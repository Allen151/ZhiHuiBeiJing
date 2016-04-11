package com.gc.zhbj.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.gc.zhbj.R;
import com.gc.zhbj.base.BasePager;
import com.gc.zhbj.base.impl.GovAffairsPager;
import com.gc.zhbj.base.impl.HomePager;
import com.gc.zhbj.base.impl.NewsCenterPager;
import com.gc.zhbj.base.impl.SettingPager;
import com.gc.zhbj.base.impl.SmartServicePager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 主页内容
 */
public class ContentFragment extends BaseFragment {

    @ViewInject(R.id.rg_group)
    private RadioGroup rg_group;
    @ViewInject(R.id.vp_content)
    private ViewPager vp_content;

    private ArrayList<BasePager> mPagerList;

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
//      rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

        //注入view和事件
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData() {
        //初始化默认选择
        rg_group.check(R.id.rb_home);

        // 初始化5个子页面
        mPagerList = new ArrayList<BasePager>();

        mPagerList.add(new HomePager(mActivity));
        mPagerList.add(new NewsCenterPager(mActivity));
        mPagerList.add(new SmartServicePager(mActivity));
        mPagerList.add(new GovAffairsPager(mActivity));
        mPagerList.add(new SettingPager(mActivity));

        vp_content.setAdapter(new ContentAdapter());

        // 监听RadioGroup的选择事件
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        // mViewPager.setCurrentItem(0);// 设置当前页面
                        vp_content.setCurrentItem(0, false);// 去掉切换页面的动画
                        break;
                    case R.id.rb_news:
                        vp_content.setCurrentItem(1, false);// 设置当前页面
                        break;
                    case R.id.rb_smart:
                        vp_content.setCurrentItem(2, false);// 设置当前页面
                        break;
                    case R.id.rb_gov:
                        vp_content.setCurrentItem(3, false);// 设置当前页面
                        break;
                    case R.id.rb_setting:
                        vp_content.setCurrentItem(4, false);// 设置当前页面
                        break;

                    default:
                        break;
                }
            }
        });

        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        mPagerList.get(0).initData();// 初始化首页数据

    }

    /**
     * 自定义 ViewPager 适配器
     */
    class ContentAdapter extends PagerAdapter{
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
            BasePager pager = mPagerList.get(position);
            container.addView(pager.mRootView);
            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
