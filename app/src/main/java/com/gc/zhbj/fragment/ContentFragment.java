package com.gc.zhbj.fragment;

import android.view.View;

import com.gc.zhbj.R;

/**
 * 主页内容
 */
public class ContentFragment extends BaseFragment {
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        return view;
    }
}
