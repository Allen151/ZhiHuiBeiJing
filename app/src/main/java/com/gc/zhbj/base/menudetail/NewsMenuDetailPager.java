package com.gc.zhbj.base.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gc.zhbj.MainActivity;
import com.gc.zhbj.R;
import com.gc.zhbj.base.BaseMenuDetailPager;
import com.gc.zhbj.base.newstabdetail.TabDetailPager;
import com.gc.zhbj.bean.NewsData;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;


/**
 * 菜单详情页-新闻
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements
		ViewPager.OnPageChangeListener {

	private ViewPager mViewPager;

	private ArrayList<TabDetailPager> mPagerList;
	// 页签网络数据
	private ArrayList<NewsData.NewsTabData> mNewsTabData;
	private TabPageIndicator tpi_indicator;
	private ImageButton btn_next;

	public NewsMenuDetailPager(Activity activity, ArrayList<NewsData.NewsTabData> children) {
		super(activity);

		mNewsTabData = children;
	}

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.news_menu_detail, null);
		mViewPager = (ViewPager) view.findViewById(R.id.vp_menu_detail);
		btn_next = (ImageButton) view.findViewById(R.id.btn_next);

		//初始化自定义控件ViewPagerIndicator
		tpi_indicator = (TabPageIndicator) view.findViewById(R.id.tpi_indicator);

		btn_next.setOnClickListener(new View.OnClickListener() {
			/**
			 * 跳转下一个页面
			 * @param v
			 */
			@Override
			public void onClick(View v) {
				int currentItem = mViewPager.getCurrentItem();
				mViewPager.setCurrentItem(++currentItem);
			}
		});

		// mViewPager.setOnPageChangeListener(this);
		// 注意:当viewpager和Indicator绑定时,
		// 滑动监听需要设置给Indicator而不是viewpager
		tpi_indicator.setOnPageChangeListener(this);


		return view;
	}

	@Override
	public void initData() {
		mPagerList = new ArrayList<TabDetailPager>();

		// 初始化页签数据
		for (int i = 0; i < mNewsTabData.size(); i++) {
			TabDetailPager pager = new TabDetailPager(mActivity, mNewsTabData.get(i));
			mPagerList.add(pager);
		}

		mViewPager.setAdapter(new MenuDetailAdapter());

		//ViewPagerIndicator和ViewPager绑定在一起
		tpi_indicator.setViewPager(mViewPager);
	}


	class MenuDetailAdapter extends PagerAdapter {

		//使用ViewPagerIndicator必须覆写getPageTitle方法
		@Override
		public CharSequence getPageTitle(int position) {
			return mNewsTabData.get(position).title;
		}

		@Override
		public int getCount() {
			return mPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mPagerList.get(position);
			container.addView(pager.mRootView);
			pager.initData();
			return pager.mRootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		System.out.println("onPageSelected:" + position);

		MainActivity mainUi = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUi.getSlidingMenu();

		if (position == 0) {
			//只有在第一个页面(北京), 侧边栏才允许出来
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}


}
