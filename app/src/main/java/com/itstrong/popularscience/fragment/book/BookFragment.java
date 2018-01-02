package com.itstrong.popularscience.fragment.book;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.bean.Details;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.AsyncImageLoader;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BookFragment extends BaseFragment implements View.OnClickListener {

	private ImageView imgIcon;
	private TextView textTitle;
	private TextView textContent;
	private Button btnStart;
	private Button btnMore;
	private Details mDetails;
	private boolean isOnlyOneBook;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isOnlyOneBook = false;
		mContentView = inflater.inflate(R.layout.fragment_read, container, false);
		return mContentView;
	}

	@Override
	public void findViewById() {
		imgIcon = (ImageView)mContentView.findViewById(R.id.img_read_icon);
		textTitle = (TextView)mContentView.findViewById(R.id.text_read_title);
		textContent = (TextView)mContentView.findViewById(R.id.text_read_content);
		btnStart = (Button)mContentView.findViewById(R.id.btn_read_start);
		btnMore = (Button)mContentView.findViewById(R.id.btn_read_more);
	}

	@Override
	public void setListener() {
		btnStart.setOnClickListener(this);
		btnMore.setOnClickListener(this);
	}

	@Override
	public void processLogic() {
		mActivity.setFragmentTitle("科普阅读");
		mActivity.setBtnBackIsInvisible(true);
		Map<String, String> map = new HashMap<>();
		map.put("page", "1");
		map.put("count", "10");
		HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_BOOK_LIST, map, new HttpUtils.HttpRequestCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject root = new JSONObject(response);
					boolean flag = root.getBoolean("flag");
					if (flag) {
						JSONArray results = root.getJSONArray("results");
						isOnlyOneBook = results.length() == 1;
						JSONObject object = results.getJSONObject(0);
						mDetails = new Details();
						mDetails.setDownloadUrl(object.getString("downloadUrl"));
						mDetails.setId(object.getString("id"));
						mDetails.setImageUrl(object.getString("imageUrl"));
						mDetails.setTitle(object.getString("title"));
						//更新UI
						textTitle.setText(mDetails.getTitle());
						new AsyncImageLoader().loadDrawable(mDetails.getImageUrl(),
								new AsyncImageLoader.ImageCallback() {
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										imgIcon.setImageDrawable(imageDrawable);
									}

									@Override
									public void loadedFailure() {
										imgIcon.setImageResource(R.mipmap.img_error);
									}
								});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_read_start:
				mActivity.switchFragmentPage(mActivity.FRAGMENT_BOOK_READING);
				startReading();
				break;
			case R.id.btn_read_more:
				if (!isOnlyOneBook)
					mActivity.switchFragmentPage(mActivity.FRAGMENT_BOOK_LIST);
				break;
		}
	}

	/**
	 * 开始阅读
	 */
	private void startReading() {
		final String bookPath = ConstantHolder.PATH_CACHE_BOOK + mDetails.getId() + ".pdf";
		File file = new File(bookPath);
		LogUtils.d("file.exists():" + file.exists());
		if (!file.exists()) {
			HttpUtils.downloadFile(mActivity, mDetails.getDownloadUrl(), mDetails.getId() + ".pdf", new HttpUtils.HttpRequestCallback() {
				@Override
				public void onSuccess(String response) {
					mActivity.switchFragment(ReadingFragment.newInstance(bookPath), "ReadingFragment");
				}
			});
		} else {
			mActivity.switchFragment(ReadingFragment.newInstance(bookPath), "ReadingFragment");
		}
	}
}
