package com.gc.zhbj.base.impl;


import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.gc.zhbj.base.BasePager;


/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {


    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        // 修改标题
        tv_title.setText("新闻中心");
        //开启侧边栏
        setSlidingMenuEnable(true);

        TextView text = new TextView(mActivity);
        text.setText("新闻");
        text.setTextColor(Color.RED);
        text.setTextSize(25);
        text.setGravity(Gravity.CENTER);

        // 向FrameLayout中动态添加布局
        fl_content.addView(text);
    }


}
