package com.gc.zhbj.base.newstabdetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.zhbj.activity.NewsDetailActivity;
import com.gc.zhbj.R;
import com.gc.zhbj.Utils.PrefUtils;
import com.gc.zhbj.base.BaseMenuDetailPager;
import com.gc.zhbj.bean.NewsData;
import com.gc.zhbj.view.RefreshListView;
import com.gc.zhbj.bean.TabData;
import com.gc.zhbj.global.GlobalContants;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


/**
 * 页签详情页
 */
public class TabDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

    NewsData.NewsTabData mTabData;

    private String mUrl;

    private TabData tabData;

    @ViewInject(R.id.vp_news)
    private ViewPager vp_news;

    // 头条新闻的标题
    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    // 头条新闻数据集合
    private ArrayList<TabData.TopNewsData> mTopNewsList;

    // 头条新闻位置指示器
    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;

    // 新闻列表
    @ViewInject(R.id.lv_list)
    private RefreshListView lv_list;

    // 新闻数据集合
    private ArrayList<TabData.TabNewsData> mNewsList;

    private NewsAdapter mNewsAdapter;
    private String mMoreUrl;


    public TabDetailPager(Activity activity, NewsData.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalContants.SERVER_URL + mTabData.url;
    }

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.tab_detail_pager, null);
        // 加载头布局
        View headerView = View.inflate(mActivity, R.layout.list_header_topnews, null);

        ViewUtils.inject(this, view);
        ViewUtils.inject(this, headerView);

        // 将头条新闻以头布局的形式加给listview
        lv_list.addHeaderView(headerView);

        //设置下拉刷新监听
        lv_list.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                if (mMoreUrl != null) {
                    getMoreDataFromServer();
                } else {
                    Toast.makeText(mActivity, "最后一页了", Toast.LENGTH_SHORT).show();
                    // 收起加载更多的布局，此处false无意义
                    lv_list.onRefreshComplete(false);
                }
            }
        });

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("被点击:" + position);
                // 在本地记录已读状态，记录已读消息的id
                String ids = PrefUtils.getString(mActivity, "read_ids", "");
                String readId = mNewsList.get(position).id;
                if (!ids.contains(readId)) {
                    ids = ids + readId + ",";
                    PrefUtils.setString(mActivity, "read_ids", ids);
                }

                // mNewsAdapter.notifyDataSetChanged();
                // 实现局部界面刷新, 这个view就是被点击的item布局对象
                changeReadState(view);

                // 跳转新闻详情页
                Intent intent = new Intent();
                intent.setClass(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", mNewsList.get(position).url);
                mActivity.startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 改变已读新闻的颜色
     */
    private void changeReadState(View view) {
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setTextColor(Color.GRAY);
    }

    @Override
    public void initData() {
        getDataFromServer();
    }

    /**
     * 通过服务器取得数据
     */
    private void getDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //json字符串
                String result = (String) responseInfo.result;
                System.out.println("页签详情页返回结果:" + result);

                parseData(result, false);

                lv_list.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                error.printStackTrace();

                lv_list.onRefreshComplete(false);
            }
        });
    }

    /**
     * 从服务器加载下一页数据
     */
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = (String) responseInfo.result;

                parseData(result, true);

                lv_list.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                error.printStackTrace();

                lv_list.onRefreshComplete(false);
            }
        });
    }


    /**
     * 解析网络数据
     *
     * @param result json字符串
     * @param isMore 是否加载下一页
     */
    protected void parseData(String result, boolean isMore) {
        Gson gson = new Gson();
        //把json解析，封装在TabData类中
        tabData = gson.fromJson(result, TabData.class);
        System.out.println("页签详情解析:" + tabData);

        // 处理下一页链接
        String more = tabData.data.more;
        if (!TextUtils.isEmpty(more)) {
            mMoreUrl = GlobalContants.SERVER_URL + more;
        } else {
            mMoreUrl = null;
        }

        if (!isMore) {
            mTopNewsList = tabData.data.topnews;

            mNewsList = tabData.data.news;

            if (mTopNewsList != null) {
                vp_news.setAdapter(new TopNewsAdapter());
                mIndicator.setViewPager(vp_news);
                //设置ViewPagerIndicator跳跃显示
                mIndicator.setSnap(true);

                mIndicator.setOnPageChangeListener(this);

                // 让指示器重新定位到第一个点
                mIndicator.onPageSelected(0);

                tv_title.setText(mTopNewsList.get(0).title);
            }

            if (mNewsList != null) {
                mNewsAdapter = new NewsAdapter();
                lv_list.setAdapter(mNewsAdapter);
            }

//            // 自动轮播条显示
//            if (mHandler == null) {
//                mHandler = new Handler() {
//                    public void handleMessage(android.os.Message msg) {
//                        int currentItem = mViewPager.getCurrentItem();
//
//                        if (currentItem < mTopNewsList.size() - 1) {
//                            currentItem++;
//                        } else {
//                            currentItem = 0;
//                        }
//
//                        mViewPager.setCurrentItem(currentItem);// 切换到下一个页面
//                        mHandler.sendEmptyMessageDelayed(0, 3000);// 继续延时3秒发消息,
//                        // 形成循环
//                    }
//
//                    ;
//                };
//
//                mHandler.sendEmptyMessageDelayed(0, 3000);// 延时3秒后发消息
//            }

        } else {
            // 如果是加载下一页,需要将数据追加给原来的集合
            ArrayList<TabData.TabNewsData> news = tabData.data.news;
            mNewsList.addAll(news);
            mNewsAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 头条新闻适配器
     */
    class TopNewsAdapter extends PagerAdapter {

        private BitmapUtils utils;

        public TopNewsAdapter() {
            //xUtils 包中的方法
            utils = new BitmapUtils(mActivity);
            // 设置默认图片
            utils.configDefaultLoadingImage(R.drawable.topnews_item_default);
        }

        @Override
        public int getCount() {
            return tabData.data.topnews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView image = new ImageView(mActivity);
            // 基于控件大小填充图片
            image.setScaleType(ImageView.ScaleType.FIT_XY);

            TabData.TopNewsData topNewsData = mTopNewsList.get(position);
            // 传递imagView对象和图片地址
            utils.display(image, topNewsData.topimage);

            container.addView(image);

            System.out.println("instantiateItem....." + position);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 新闻列表的适配器
     */
    class NewsAdapter extends BaseAdapter {

        private BitmapUtils utils;

        public NewsAdapter() {
            utils = new BitmapUtils(mActivity);
            //默认图片
            utils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public TabData.TabNewsData getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_news_item, null);
                holder = new ViewHolder();
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TabData.TabNewsData item = getItem(position);

            holder.tv_title.setText(item.title);
            holder.tv_date.setText(item.pubdate);

            utils.display(holder.iv_pic, item.listimage);

            //是否标记为已读
            String ids = PrefUtils.getString(mActivity, "read_ids", "");
            if (ids.contains(getItem(position).id)) {
                holder.tv_title.setTextColor(Color.GRAY);
            } else {
                holder.tv_title.setTextColor(Color.BLACK);
            }

            return convertView;
        }

    }

    static class ViewHolder {
        public TextView tv_title;
        public TextView tv_date;
        public ImageView iv_pic;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TabData.TopNewsData topNewsData = mTopNewsList.get(position);
        tv_title.setText(topNewsData.title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
