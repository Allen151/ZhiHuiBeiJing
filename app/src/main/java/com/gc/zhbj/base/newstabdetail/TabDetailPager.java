package com.gc.zhbj.base.newstabdetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.gc.zhbj.base.BaseMenuDetailPager;
import com.gc.zhbj.bean.NewsData;

/**
 * 页签详情页
 * 
 * @author Kevin
 * 
 */
public class TabDetailPager extends BaseMenuDetailPager {

	NewsData.NewsTabData mTabData;
	private TextView tvText;

	public TabDetailPager(Activity activity, NewsData.NewsTabData newsTabData) {
		super(activity);
		mTabData = newsTabData;
	}

	@Override
	public View initViews() {
		tvText = new TextView(mActivity);
		tvText.setText("页签详情页");
		tvText.setTextColor(Color.RED);
		tvText.setTextSize(25);
		tvText.setGravity(Gravity.CENTER);
		return tvText;
	}

	@Override
	public void initData() {
		tvText.setText(mTabData.title);
	}

}
