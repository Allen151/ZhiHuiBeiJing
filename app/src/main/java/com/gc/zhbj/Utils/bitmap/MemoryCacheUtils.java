package com.gc.zhbj.Utils.bitmap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 内存缓存
 */
public class MemoryCacheUtils {
	// 软引用SoftReference
	// private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new HashMap<String, SoftReference<Bitmap>>();
	private LruCache<String, Bitmap> mMemoryCache;

	public MemoryCacheUtils() {
		// 当前app能分配的最大内存：Runtime.getRuntime().maxMemory()
		long maxMemory = Runtime.getRuntime().maxMemory() / 8;
		mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 获取图片占用内存大小
				int byteCount = value.getByteCount();
				return byteCount;
			}
		};
	}

	/**
	 * 从内存读
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromMemory(String url) {
		// SoftReference<Bitmap> softReference = mMemoryCache.get(url);
		// if (softReference != null) {
		// Bitmap bitmap = softReference.get();
		// return bitmap;
		// }
		return mMemoryCache.get(url);
	}

	/**
	 * 写内存
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void setBitmapToMemory(String url, Bitmap bitmap) {
		// SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bitmap);
		// mMemoryCache.put(url, softReference);
		mMemoryCache.put(url, bitmap);
	}
}
