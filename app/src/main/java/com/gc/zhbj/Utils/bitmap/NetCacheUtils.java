package com.gc.zhbj.Utils.bitmap;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * 网络缓存（图片压缩）
 */
public class NetCacheUtils {

	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils,
			MemoryCacheUtils memoryCacheUtils) {
		mLocalCacheUtils = localCacheUtils;
		mMemoryCacheUtils = memoryCacheUtils;
	}

	/**
	 * 从网络下载图片
	 * 
	 * @param imageView
	 * @param imageUrl
	 */
	public void getBitmapFromNet(ImageView imageView, String imageUrl) {

		// 将url和imageview绑定，用于多图先后（url）共用ImageView时，ImageView与Url的匹配检验
		imageView.setTag(imageUrl);

		// 启动AsyncTask,参数会在doInbackground中获取
		new BitmapTask().execute(imageView, imageUrl);
	}

	/**
	 * Handler和线程池的封装
	 * 
	 * 第一个泛型: 参数类型 第二个泛型: 更新进度的泛型, 第三个泛型是onPostExecute的返回结果
	 */
	class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

		private ImageView imageView;
		private String imageUrl;

		/**
		 * 后台耗时方法在此执行, 子线程
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			imageView = (ImageView) params[0];
			imageUrl = (String) params[1];

			return downloadBitmap(imageUrl);
		}

		/**
		 * 更新进度, 主线程
		 */
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * 耗时方法结束后,执行该方法, 主线程
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				String bindUrl = (String) imageView.getTag();

				if (imageUrl.equals(bindUrl)) {
					// 确保图片设定给了正确的imageview
					imageView.setImageBitmap(result);
					// 将图片保存在本地
					mLocalCacheUtils.setBitmapToLocal(imageUrl, result);
					// 将图片保存在内存
					mMemoryCacheUtils.setBitmapToMemory(imageUrl, result);
					Log.i("test", "从网络获取图片");
				}
			}
		}
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap downloadBitmap(String url) {

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream inputStream = conn.getInputStream();
				
				//图片压缩处理
				BitmapFactory.Options option = new BitmapFactory.Options();
				//宽高都压缩为原来的二分之一, 此参数需要根据图片要展示的大小来动态设置
				option.inSampleSize = 2;
				//设置图片格式
//				option.inPreferredConfig = Bitmap.Config.RGB_565;
				option.inPreferredConfig = Bitmap.Config.ARGB_4444;

				Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, option);
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}

		return null;
	}

}
