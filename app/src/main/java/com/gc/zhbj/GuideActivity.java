package com.gc.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gc.zhbj.Utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手引导页
 */
public class GuideActivity extends Activity {

    //初始化图片数组id
    private static final int[] mImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private ViewPager vp_guide;
    private Button btn_begin;
    private List<ImageView> mImageViewList;
    //引导页小圆点父控件
    private LinearLayout ll_point_group;
    //小红点
    private View v_point_red;
    // 圆点间的距离
    private int mPointWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_guide);

        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        btn_begin = (Button) findViewById(R.id.btn_begin);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        v_point_red = findViewById(R.id.v_point_red);

        mImageViewList = new ArrayList<ImageView>();

        // 初始化引导页的3个页面
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            // 设置引导页背景
            image.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(image);
        }


        // 初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            // 设置引导页默认圆点
            point.setBackgroundResource(R.drawable.shape_point_gray);

            //圆点图片宽高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);

            if (i > 0) {
                // 设置圆点间隔
                params.leftMargin = 15;
            }
            // 设置圆点的大小
            point.setLayoutParams(params);
            // 将圆点添加给线性布局
            ll_point_group.addView(point);
        }

        //初始化引导页红点大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(15, 15);
        v_point_red.setLayoutParams(params);


        // 获取视图树, 对layout(布局)结束事件进行监听
        ll_point_group.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    // 当layout（布局）执行结束后回调此方法
                    @Override
                    public void onGlobalLayout() {
                        //注销对layout(布局)结束事件进行监听，防止重复调用代码
                        ll_point_group.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mPointWidth = ll_point_group.getChildAt(1).getLeft()- ll_point_group.getChildAt(0).getLeft();
                    }
                });


        //加载ViewPager适配器
        GuideAdapter guideAdapter = new GuideAdapter();
        vp_guide.setAdapter(guideAdapter);

        //ViewPager页面滑动监听(原方法setOnPageChangeListener(...))
        vp_guide.addOnPageChangeListener(new GuidePageListener());

        //按钮点击监听
        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新sp, 表示已经展示了新手引导
                PrefUtils.setBoolean(GuideActivity.this, "is_user_guide_showed", true);

                // 跳转主页面
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        });

    }


    class GuideAdapter extends PagerAdapter {
        /**
         * 数量
         *
         * @return
         */
        @Override
        public int getCount() {
            return mImageIds.length;
        }

        /**
         * 当前滑动的和要进来的是否是同一个
         * true：使用缓存
         * false：创建新的
         *
         * @param view   当前滑动的view
         * @param object 要进来的view
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 类似于BaseAdapter的getView
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView view = mImageViewList.get(position);
            container.addView(view);

            return view;
        }

        /**
         * 销毁未缓存条目（最多缓存三个条目）
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }


    /**
     * viewpager的滑动监听
     *
     * @author Kevin
     */
    class GuidePageListener implements ViewPager.OnPageChangeListener {

        // 滑动事件(参数：1，当前页面编号，2，滑动幅度[0,1)，3，滑动像素)
        @Override
        public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {
            int len = (int) (mPointWidth * positionOffset) + position * mPointWidth;
            // 获取当前红点的布局参数
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_point_red.getLayoutParams();
            // 设置左边距
            params.leftMargin = len;
            // 重新给小红点设置布局参数
            v_point_red.setLayoutParams(params);
        }

        // 某个页面被选中
        @Override
        public void onPageSelected(int position) {
            if (position == mImageIds.length - 1) {
                // 最后一个页面,显示开始体验的按钮
                btn_begin.setVisibility(View.VISIBLE);
            } else {
                //隐藏开始体验的按钮
                btn_begin.setVisibility(View.INVISIBLE);
            }
        }

        // 滑动状态发生变化
        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }

}
