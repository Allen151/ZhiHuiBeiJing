package com.gc.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.zhbj.MainActivity;
import com.gc.zhbj.R;
import com.gc.zhbj.base.impl.NewsCenterPager;
import com.gc.zhbj.bean.NewsData;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 侧边栏
 */
public class LeftMenuFragment extends BaseFragment {

    @ViewInject(R.id.lv_list)
    private ListView lv_list;
    private ArrayList<NewsData.NewsMenuData> mMenuList;

    // 当前被点击的菜单项
    private int mCurrentPos;
    private MenuAdapter mAdapter;

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData() {
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
                mAdapter.notifyDataSetChanged();

                setCurrentMenuDetailPager(position);
                // 隐藏侧边栏
                toggleSlidingMenu();
            }
        });
    }

    /**
     * 切换SlidingMenu的状态
     */
    protected void toggleSlidingMenu() {
        MainActivity mainUi = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUi.getSlidingMenu();
        // 切换状态, 显示时隐藏, 隐藏时显示
        slidingMenu.toggle();
    }

    /**
     * 设置当前菜单详情页
     *
     * @param position
     */
    protected void setCurrentMenuDetailPager(int position) {
        MainActivity mainUi = (MainActivity) mActivity;
        // 获取主页面fragment
        ContentFragment contentFragment = mainUi.getContentFragment();
        // 获取新闻中心页面
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        // 设置当前菜单详情页
        newsCenterPager.setCurrentMenuDetailPager(position);
    }

    // 设置网络数据
    public void setMenuData(NewsData newsData) {
        // System.out.println("侧边栏拿到数据:" + data);
        mMenuList = newsData.data;
        mAdapter = new MenuAdapter();
        lv_list.setAdapter(mAdapter);
    }

    /**
     * 侧边栏数据适配器
     */
    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public NewsData.NewsMenuData getItem(int position) {
            return mMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.list_menu_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            NewsData.NewsMenuData newsMenuData = getItem(position);
            tv_title.setText(newsMenuData.title);

            if (mCurrentPos == position) {// 判断当前绘制的view是否被选中
                // 显示红色
                tv_title.setEnabled(true);
            } else {
                // 显示白色
                tv_title.setEnabled(false);
            }

            return view;
        }

    }

}

