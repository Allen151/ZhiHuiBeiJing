package com.gc.zhbj.Utils;

import android.content.Context;

/**
 * 缓存工具类
 */
public class CacheUtils {

	/**
	 * 设置缓存，缓存json
	 * @param key 缓存json的url地址
	 * @param value String类型的json
	 * @param ctx
	 */
	public static void setCache(String key, String value, Context ctx) {
		PrefUtils.setString(ctx, key, value);
		//可以将缓存放在文件中, 文件名就是Md5(url), 文件内容是json
	}

	/**
	 * 获取缓存
	 * @param key 缓存json的url地址
	 * @param ctx 上下文
	 * @return String类型的json
	 */
	public static String getCache(String key, Context ctx) {
		return PrefUtils.getString(ctx, key, null);
	}
}
