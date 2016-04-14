package com.gc.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.gc.zhbj.R;

/**
 * 下拉刷新的ListView
 */
public class RefreshListView extends ListView {

    private View mHeaderView;

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
    }

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(mHeaderView);

        mHeaderView.measure(0, 0);
        int mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        //隐藏头布局
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
    }

}