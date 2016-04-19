package com.gc.zhbj.Utils.bitmap;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.gc.zhbj.R;

/**
 * 自定义图片加载类（三级缓存）
 */
public class MyBitmapUtils {
    NetCacheUtils mNetCacheUtils;
    LocalCacheUtils mLocalCacheUtils;
    MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    /**
     * 加载图片
     * @param imageView 图片布局
     * @param imageUrl 图片Url
     */
    public void display(ImageView imageView, String imageUrl) {
        // 设置默认加载图片
        imageView.setImageResource(R.drawable.news_pic_default);

        Bitmap bitmap = null;

        // 从内存读取
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(imageUrl);
        if (bitmap != null) {
            Log.i("test","从内存获取图片");
            imageView.setImageBitmap(bitmap);
            return;
        }

        // 从本地读取
        bitmap = mLocalCacheUtils.getBitmapFromLocal(imageUrl);
        if (bitmap != null) {
            Log.i("test","从本地获取图片");
            imageView.setImageBitmap(bitmap);
            // 将图片保存在内存
            mMemoryCacheUtils.setBitmapToMemory(imageUrl, bitmap);
            return;
        }

        // 从网络读取
        mNetCacheUtils.getBitmapFromNet(imageView, imageUrl);
    }
}
