package com.gc.zhbj.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gc.zhbj.activity.MainActivity;
import com.gc.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 主页下5个子页面的基类
 *
 */
public class BasePager {

    public Activity mActivity;
    // 布局对象
    public View mRootView;
    // 标题对象
    public TextView tv_title;
    // 内容
    public FrameLayout fl_content;
    // 菜单按钮
    public ImageButton btn_menu;
    //组图切换按钮
    public ImageButton btn_photo_change;

    public BasePager(Activity activity) {
        mActivity = activity;
        initViews();
    }

    /**
     * 初始化布局
     */
    public void initViews() {
        mRootView = View.inflate(mActivity, R.layout.base_pager, null);

        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
        fl_content = (FrameLayout) mRootView.findViewById(R.id.fl_content);
        btn_menu = (ImageButton) mRootView.findViewById(R.id.btn_menu);
        btn_photo_change = (ImageButton) mRootView.findViewById(R.id.btn_photo_change);

        btn_menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    /**
     * 切换SlidingMenu的状态
     *
     */
    protected void toggleSlidingMenu() {
        MainActivity mainUi = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUi.getSlidingMenu();
        // 切换状态, 显示时隐藏, 隐藏时显示
        slidingMenu.toggle();
    }

    /**
     * 初始化数据（子类根据实际情况选择是否覆写，在哪调用）
     */
    public void initData() {

    }

    /**
     * 设置侧边栏开启或关闭
     *
     * @param enable
     */
    public void setSlidingMenuEnable(boolean enable) {
        MainActivity mainUi = (MainActivity) mActivity;

        SlidingMenu slidingMenu = mainUi.getSlidingMenu();

        if (enable) {
            //允许全屏滑动开启侧边栏
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            //关闭侧边栏
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

}
