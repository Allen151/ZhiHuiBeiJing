package com.gc.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 下拉刷新的ListView
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener,AdapterView.OnItemClickListener{

    // 下拉刷新状态
    private static final int STATE_PULL_REFRESH = 0;
    // 松开刷新状态
    private static final int STATE_RELEASE_REFRESH = 1;
    // 正在刷新状态
    private static final int STATE_REFRESHING = 2;
    // 当前状态
    private int mCurrrentState = STATE_PULL_REFRESH;

    //当前是否正在处于加载更多
    private boolean isLoadingMore = false;

    private View mHeaderView;
    private View mFooterView;

    // 滑动起点的y坐标
    private int startY = -1;

    private int mHeaderViewHeight;
    private int mFooterViewHeight;

    private TextView tv_title;
    private TextView tv_time;
    private ImageView iv_arr;
    private ProgressBar pb_progress;
    private RotateAnimation animUp;
    private RotateAnimation animDown;

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(mHeaderView);

        mHeaderView.measure(0, 0);
        //获得头布局的测量高度
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        //隐藏头布局
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        tv_title = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tv_time = (TextView) mHeaderView.findViewById(R.id.tv_time);
        iv_arr = (ImageView) mHeaderView.findViewById(R.id.iv_arr);
        pb_progress = (ProgressBar) mHeaderView.findViewById(R.id.pb_progress);

        initArrowAnim();
        tv_time.setText("最后刷新时间:" + getCurrentTime());
    }

    /*
	 * 初始化脚布局
	 */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
        this.addFooterView(mFooterView);

        mFooterView.measure(0, 0);
        //获得脚布局的测量高度
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        //隐藏脚布局
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    // 确保startY有效
                    startY = (int) ev.getRawY();
                }

                if (mCurrrentState == STATE_REFRESHING) {
                    // 正在刷新时不做处理
                    break;
                }

                int endY = (int) ev.getRawY();
                // 竖直移动偏移量
                int dy = endY - startY;

                // 只有下拉并且当前是第一个item,才允许下拉刷新
                if (dy > 0 && getFirstVisiblePosition() == 0) {
                    // 计算padding
                    int padding = dy - mHeaderViewHeight;
                    // 设置当前padding
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding >= 0 && mCurrrentState != STATE_RELEASE_REFRESH) {
                        // 状态改为松开刷新
                        mCurrrentState = STATE_RELEASE_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrrentState != STATE_PULL_REFRESH) {
                        // 改为下拉刷新状态
                        mCurrrentState = STATE_PULL_REFRESH;
                        refreshState();
                    }

                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                // 重置
                startY = -1;

                if (mCurrrentState == STATE_RELEASE_REFRESH) {
                    // 状态变为正在刷新
                    mCurrrentState = STATE_REFRESHING;
                    // 首行显示头布局
                    mHeaderView.setPadding(0, 0, 0, 0);
                    refreshState();
                } else if (mCurrrentState == STATE_PULL_REFRESH) {
                    // 隐藏头布局
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 刷新下拉控件的布局
     */
    private void refreshState() {
        switch (mCurrrentState) {
            case STATE_PULL_REFRESH:
                tv_title.setText("下拉刷新");
                iv_arr.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                iv_arr.startAnimation(animDown);
                break;
            case STATE_RELEASE_REFRESH:
                tv_title.setText("松开刷新");
                iv_arr.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                iv_arr.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tv_title.setText("正在刷新...");
                // 必须先清除动画,才能隐藏
                iv_arr.clearAnimation();
                iv_arr.setVisibility(View.INVISIBLE);
                pb_progress.setVisibility(View.VISIBLE);

                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 初始化箭头动画
     */
    private void initArrowAnim() {
        // 箭头向上动画
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(300);
        animUp.setFillAfter(true);

        // 箭头向下动画
        animDown = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(300);
        animDown.setFillAfter(true);

    }

    OnRefreshListener mListener;
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 外部监听接口（下拉刷新和上拉加载）
     */
    public interface OnRefreshListener {
        /**
         * 下拉刷新
         */
        public void onRefresh();

        /**
         * 上拉加载下一页数据
         */
        public void onLoadMore();
    }


    /**
     * 收起下拉刷新/上拉刷新的控件
     * @param success  从服务器获取数据成功否
     */
    public void onRefreshComplete(boolean success) {
        if (isLoadingMore) {
            // 正在加载更多...
            // 隐藏脚布局
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
            isLoadingMore = false;
        } else {
            //准备收起该布局，重置为“下拉刷新”
            mCurrrentState = STATE_PULL_REFRESH;
            tv_title.setText("下拉刷新");
            iv_arr.setVisibility(View.VISIBLE);
            pb_progress.setVisibility(View.INVISIBLE);

            // 隐藏头布局
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

            if (success) {
                tv_time.setText("最后刷新时间:" + getCurrentTime());
            }
        }
    }

    /**
     * 获取当前时间
     */
    public String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
            //闲置或者是飞速滑动时（都是手离开屏幕时）

            if (getLastVisiblePosition() == getCount() - 1 && !isLoadingMore) {
                // 滑动到最后 且 上拉刷新没有在加载更多时

                System.out.println("到底了.....");
                // 显示脚布局
                mFooterView.setPadding(0, 0, 0, 0);
                // 改变listview显示位置，显示当前位置
                setSelection(getCount() - 1);

                isLoadingMore = true;

                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    OnItemClickListener mItemClickListener;

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(this);

        mItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(parent, view, position - getHeaderViewsCount(), id);
        }
    }
}
