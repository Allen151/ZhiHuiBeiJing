package com.gc.zhbj.base.menudetail;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.zhbj.R;
import com.gc.zhbj.Utils.CacheUtils;
import com.gc.zhbj.Utils.bitmap.MyBitmapUtils;
import com.gc.zhbj.base.BaseMenuDetailPager;
import com.gc.zhbj.bean.PhotosData;
import com.gc.zhbj.global.GlobalContants;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * 菜单详情页-组图
 */
public class PhotoMenuDetailPager extends BaseMenuDetailPager {

	private ListView lv_photo;
	private GridView gv_photo;
	private ArrayList<PhotosData.PhotoInfo> mPhotoList;
	private PhotoAdapter photoAdapter;
	private ImageButton btn_photo_change;

	public PhotoMenuDetailPager(Activity activity, ImageButton btnPhoto) {
		super(activity);

		this.btn_photo_change = btnPhoto;

		btn_photo_change.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeDisplay();
			}
		});
	}

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.menu_photo_pager, null);

		lv_photo = (ListView) view.findViewById(R.id.lv_photo);
		gv_photo = (GridView) view.findViewById(R.id.gv_photo);

		return view;
	}

	@Override
	public void initData() {

		String cache = CacheUtils.getCache(GlobalContants.PHOTOS_URL, mActivity);

		if (!TextUtils.isEmpty(cache)) {

		}

		getDataFromServer();
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpRequest.HttpMethod.GET, GlobalContants.PHOTOS_URL, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = (String) responseInfo.result;
						parseData(result);
						// 设置缓存
						CacheUtils.setCache(GlobalContants.PHOTOS_URL, result, mActivity);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
						error.printStackTrace();
					}
				});
	}

	protected void parseData(String result) {
		Gson gson = new Gson();
		PhotosData photosData = gson.fromJson(result, PhotosData.class);
		// 获取组图列表集合
		mPhotoList = photosData.data.news;

		if (mPhotoList != null) {
			photoAdapter = new PhotoAdapter();
			lv_photo.setAdapter(photoAdapter);
			gv_photo.setAdapter(photoAdapter);
		}
	}

	class PhotoAdapter extends BaseAdapter {
//		private MyBitmapUtils bitmapUtils;

		private BitmapUtils bitmapUtils;

		public PhotoAdapter() {
//			myBitmapUtils = new MyBitmapUtils();
			bitmapUtils = new BitmapUtils(mActivity);
			bitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
		}

		@Override
		public int getCount() {
			return mPhotoList.size();
		}

		@Override
		public PhotosData.PhotoInfo getItem(int position) {
			return mPhotoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_photo_item, null);

				holder = new ViewHolder();
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PhotosData.PhotoInfo item = getItem(position);

			holder.tv_title.setText(item.title);

			bitmapUtils.display(holder.iv_pic, item.listimage);

			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tv_title;
		public ImageView iv_pic;
	}

	// 是否是列表展示
	private boolean isListDisplay = true;

	/**
	 * 切换组图按钮展现方式
	 */
	private void changeDisplay() {
		if (isListDisplay) {
			isListDisplay = false;
			lv_photo.setVisibility(View.GONE);
			gv_photo.setVisibility(View.VISIBLE);

			btn_photo_change.setImageResource(R.drawable.icon_pic_list_type);

		} else {
			isListDisplay = true;
			lv_photo.setVisibility(View.VISIBLE);
			gv_photo.setVisibility(View.GONE);

			btn_photo_change.setImageResource(R.drawable.icon_pic_grid_type);
		}
	}
}
