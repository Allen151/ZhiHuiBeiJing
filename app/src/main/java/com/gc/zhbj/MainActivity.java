package com.gc.zhbj;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

import com.gc.zhbj.fragment.ContentFragment;
import com.gc.zhbj.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * 主界面(继承lib里的SlidingFragmentActivity)
 */
public class MainActivity extends SlidingFragmentActivity {

    private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
    private static final String FRAGMENT_CONTENT = "fragment_content";
    private int screen_width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消Activity头标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // 设置侧边栏
        setBehindContentView(R.layout.left_menu);
        // 获取侧边栏对象
        SlidingMenu slidingMenu = getSlidingMenu();
        // 设置全屏触摸
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        // 设置预留屏幕的宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screen_width = dm.widthPixels;
        int remainingScreen = screen_width * 2 / 3;
        slidingMenu.setBehindOffset(remainingScreen);

        initFragment();
    }

    /**
     * 初始化fragment, 将fragment数据填充给布局文件
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = fm.beginTransaction();

        // 用fragment替换framelayout【参数：将 new LeftMenuFragment()放在（替换）R.id.fl_left_menu里面，
        // 第三个参数视为该替换的标记，可用标记取得对象】
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), FRAGMENT_LEFT_MENU);
        transaction.replace(R.id.fl_content, new ContentFragment(), FRAGMENT_CONTENT);

        // 提交事务
        transaction.commit();
        // 通过标记取得对象
        // Fragment leftMenuFragment = fm.findFragmentByTag(FRAGMENT_LEFT_MENU);
    }


    /**
     * 获取侧边栏fragment
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(FRAGMENT_LEFT_MENU);

        return fragment;
    }

    /**
     *  获取主页面fragment
     * @return
     */
    public ContentFragment getContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(FRAGMENT_CONTENT);

        return fragment;
    }


}
