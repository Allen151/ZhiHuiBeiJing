package com.gc.zhbj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.gc.zhbj.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;


/**
 * 新闻详情页
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener {

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

        btn_back.setOnClickListener(this);
        btn_size.setOnClickListener(this);
        btn_share.setOnClickListener(this);

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
                Log.i("i", "网页开始加载");
                pb_progress.setVisibility(View.VISIBLE);
            }

            /**
             * 网页加载结束
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("i", "网页加载结束");
                pb_progress.setVisibility(View.GONE);
            }

            /**
             * 所有跳转的链接都会在此方法中回调
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 可以在每次跳转时拦截做自定义处理
                Log.i("i", "跳转url" + url);
                view.loadUrl(url);

                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }
        });

        wv_web.setWebChromeClient(new WebChromeClient() {

            /**
             * 进度发生变化
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("i", "加载进度:" + newProgress);
                super.onProgressChanged(view, newProgress);
            }

            /**
             * 获取网页标题
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i("i", "网页标题:" + title);
                super.onReceivedTitle(view, title);
            }
        });
//        上一页、下一页
//        wv_web.goBack();
//        wv_web.goForward();

        wv_web.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_size:
                showChooseDialog();
                break;
            case R.id.btn_share:
                showShare();
                break;

            default:
                break;
        }
    }

    // 记录当前选中的item, 点击确定前临时记录
    private int mCurrentChooseItem;
    // 记录当前选中的item, 点击确定后记录
    private int mCurrentItem = 2;

    /**
     * 显示选择对话框
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        builder.setTitle("字体设置");
        builder.setSingleChoiceItems(items, mCurrentItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("i", "选中:" + which);
                mCurrentChooseItem = which;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                WebSettings settings = wv_web.getSettings();
                switch (mCurrentChooseItem) {
                    case 0:
//                      settings.setTextSize(WebSettings.TextSize.LARGEST);
                        settings.setTextZoom(200);
                        break;
                    case 1:
//                      settings.setTextSize(WebSettings.TextSize.LARGER);
                        settings.setTextZoom(150);
                        break;
                    case 2:
//                      settings.setTextSize(WebSettings.TextSize.NORMAL);
                        settings.setTextZoom(100);
                        break;
                    case 3:
//                      settings.setTextSize(WebSettings.TextSize.SMALLER);
                        settings.setTextZoom(75);
                        break;
                    case 4:
//                      settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        settings.setTextZoom(50);
                        break;

                    default:
                        break;
                }

                mCurrentItem = mCurrentChooseItem;
            }
        });

        builder.setNegativeButton("取消", null);

        builder.show();
    }

    /**
     * 分享(ShareSDK)
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.ssdk_oks_share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.baidu.com");

        // 启动分享GUI
        oks.show(this);
    }
}
