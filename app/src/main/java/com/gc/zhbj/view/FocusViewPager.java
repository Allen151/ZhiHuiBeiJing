package com.gc.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 父控件不拦截事件的ViewPager
 * (11个页签滑动的ViewPager)
 */
public class FocusViewPager extends ViewPager {
    public FocusViewPager(Context context) {
        super(context);
    }

    public FocusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 事件分发，请求所有父控件是否拦截事件
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentItem() != 0){
            //请求所有父控件不要拦截(true)
            getParent().requestDisallowInterceptTouchEvent(true);
        }else{
            //请求所有父控件不要拦截(false)
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return super.dispatchTouchEvent(ev);
    }
}
