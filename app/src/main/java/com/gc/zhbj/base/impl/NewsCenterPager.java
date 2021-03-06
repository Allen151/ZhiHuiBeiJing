package com.gc.zhbj.base.impl;


import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gc.zhbj.Utils.CacheUtils;
import com.gc.zhbj.activity.MainActivity;
import com.gc.zhbj.base.BaseMenuDetailPager;
import com.gc.zhbj.base.BasePager;
import com.gc.zhbj.base.menudetail.InteractMenuDetailPager;
import com.gc.zhbj.base.menudetail.NewsMenuDetailPager;
import com.gc.zhbj.base.menudetail.PhotoMenuDetailPager;
import com.gc.zhbj.base.menudetail.TopicMenuDetailPager;
import com.gc.zhbj.bean.NewsData;
import com.gc.zhbj.fragment.LeftMenuFragment;
import com.gc.zhbj.global.GlobalContants;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;


/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {

    // 4个菜单详情页的集合
    private ArrayList<BaseMenuDetailPager> mPagers;
    private NewsData mNewsData;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        // 修改标题
        tv_title.setText("新闻");
        //侧边栏可用
        setSlidingMenuEnable(true);

        //读取缓存
        String cache = CacheUtils.getCache(GlobalContants.CATEGORIES_URL, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            // 如果缓存存在,直接解析数据, 无需访问网路
            parseData(cache);
        }
        // 不管有没有缓存, 都获取最新数据, 保证数据最新
        getDataFromServer();
    }

    /**
     * 从服务器获取数据
     */
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();

        // 使用xutils发送请求(参数：1，请求方式，2，请求地址，3，请求结果回调)，都是在主线程中调用
        utils.send(HttpRequest.HttpMethod.GET, GlobalContants.CATEGORIES_URL, new RequestCallBack<String>() {

            // 访问成功
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                //json字符串
                String result = (String) responseInfo.result;
                System.out.println("返回结果:" + result);

                parseData(result);

                // 设置缓存
                CacheUtils.setCache(GlobalContants.CATEGORIES_URL, result, mActivity);
            }

            // 访问失败
            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg , Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }

        });
    }

    /**
     * 解析网络数据
     *
     * @param result json字符串
     */
    protected void parseData(String result) {
        Gson gson = new Gson();
        //把json解析，封装在NewsData类中
        mNewsData = gson.fromJson(result, NewsData.class);
        System.out.println("解析结果:" + mNewsData);

        // 刷新测边栏的数据
        MainActivity mainUi = (MainActivity) mActivity;
        LeftMenuFragment leftMenuFragment = mainUi.getLeftMenuFragment();
        leftMenuFragment.setMenuData(mNewsData);

        // 准备4个菜单详情页
        mPagers = new ArrayList<BaseMenuDetailPager>();
        mPagers.add(new NewsMenuDetailPager(mActivity, mNewsData.data.get(0).children));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotoMenuDetailPager(mActivity, btn_photo_change));
        mPagers.add(new InteractMenuDetailPager(mActivity));

        // 设置菜单详情页-新闻为默认当前页
        setCurrentMenuDetailPager(0);
    }

    /**
     * 设置当前菜单详情页
     */
    public void setCurrentMenuDetailPager(int position) {
        // 获取当前要显示的菜单详情页
        BaseMenuDetailPager baseMenuDetailPager = mPagers.get(position);
        // 清除之前的布局
        fl_content.removeAllViews();
        // 将菜单详情页的布局设置给帧布局
        fl_content.addView(baseMenuDetailPager.mRootView);

        // 设置当前页的标题
        NewsData.NewsMenuData menuData = mNewsData.data.get(position);
        tv_title.setText(menuData.title);

        // 初始化当前页面的数据
        baseMenuDetailPager.initData();

        //是否显示组图按钮
        if (baseMenuDetailPager instanceof PhotoMenuDetailPager) {
            btn_photo_change.setVisibility(View.VISIBLE);
        } else {
            btn_photo_change.setVisibility(View.GONE);
        }
    }


}
