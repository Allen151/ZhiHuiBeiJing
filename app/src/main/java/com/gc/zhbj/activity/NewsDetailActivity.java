package com.gc.zhbj.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.gc.zhbj.R;


/**
 * 新闻详情页
 */
public class NewsDetailActivity extends Activity {

    private WebView wv_web;
    private ImageButton btn_back;
    private ImageButton btn_size;
    private ImageButton btn_share;
    private ProgressBar pb_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消Activity头标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);

        wv_web = (WebView) findViewById(R.id.wv_web);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_size = (ImageButton) findViewById(R.id.btn_size);
        btn_share = (ImageButton) findViewById(R.id.btn_share);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);

        String url = getIntent().getStringExtra("url");

        WebSettings settings = wv_web.getSettings();
        // 表示支持js
        settings.setJavaScriptEnabled(true);
        // 显示放大缩小按钮
        settings.setBuiltInZoomControls(true);
        // 支持双击缩放
        settings.setUseWideViewPort(true);

        wv_web.setWebViewClient(new WebViewClient() {

            /**
             * 网页开始加载
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("网页开始加载");
                pb_progress.setVisibility(View.VISIBLE);
            }

            /**
             * 网页加载结束
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("网页加载结束");
                pb_progress.setVisibility(View.GONE);
            }

            /**
             * 所有跳转的链接都会在此方法中回调
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 可以在每次跳转时拦截做自定义处理
                System.out.println("跳转url:" + url);
                view.loadUrl(url);

                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }
        });
//        上一页、下一页
//        wv_web.goBack();
//        wv_web.goForward();

        wv_web.loadUrl(url);
    }
}
