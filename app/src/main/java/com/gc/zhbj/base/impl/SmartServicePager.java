package com.gc.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.gc.zhbj.base.BasePager;


/**
 * 智慧服务
 * 
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 修改标题
		tv_title.setText("生活");
		// 隐藏菜单按钮
		btn_menu.setVisibility(View.GONE);
		//关闭侧边栏
		setSlidingMenuEnable(false);

		TextView text = new TextView(mActivity);
		text.setText("智慧服务");
		text.setTextColor(Color.RED);
		text.setTextSize(25);
		text.setGravity(Gravity.CENTER);

		// 向FrameLayout中动态添加布局
		fl_content.addView(text);
	}

}
