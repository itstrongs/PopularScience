package com.itstrong.popularscience.fragment.book;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.BaseAdapter;
import com.itstrong.popularscience.DividerItemDecoration;
import com.itstrong.popularscience.R;
import com.itstrong.popularscience.bean.Book;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.AsyncImageLoader;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图书列表界面
 * Created by itstrong on 2016/6/8.
 */
public class BookListFragment extends BaseFragment {

	private RecyclerView mRecyclerView;
	private BaseAdapter mAdapter;
	private List<Book> mItemList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_book_list, container, false);
		mItemList = new ArrayList<>();
		return mContentView;
	}

	@Override
	public void findViewById() {
		mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recycler_view_book);
	}

	@Override
	public void setListener() {
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
		mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
		mAdapter = new BaseAdapter(mItemList.size(), new BaseAdapter.BaseAdapterCallback() {
			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
				return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_book, parent, false));
			}

			@Override
			public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
				Book book = mItemList.get(position);
				final MyViewHolder myViewHolder = (MyViewHolder)holder;
				myViewHolder.textTitle.setText(book.getTitle());
				new AsyncImageLoader().loadDrawable(book.getImageUrl(),
						new AsyncImageLoader.ImageCallback() {
							public void imageLoaded(Drawable imageDrawable, String imageUrl) {
								myViewHolder.imgIcon.setImageDrawable(imageDrawable);
							}

							@Override
							public void loadedFailure() {
								myViewHolder.imgIcon.setImageResource(R.mipmap.img_error);
							}
						});
			}
		});
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void processLogic() {
		mActivity.setFragmentTitle("科普阅读");
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
						for (int i = 0; i < results.length(); i++) {
							JSONObject object = results.getJSONObject(i);
							Book itemInfo = new Book();
							itemInfo.setId(object.getString("id"));
							itemInfo.setDownloadUrl(object.getString("downloadUrl"));
							itemInfo.setImageUrl(object.getString("imageUrl"));
							itemInfo.setTitle(object.getString("title"));
							mItemList.add(itemInfo);
							mAdapter.setCount(mItemList.size());
							mAdapter.notifyItemInserted(i);
						}
						LogUtils.d("mItemList:" + mItemList);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		ImageView imgIcon;
		TextView textTitle;
		TextView textContent;
		Button btnReading;

		public MyViewHolder(View view) {
			super(view);
			imgIcon = (ImageView)view.findViewById(R.id.img_read_icon);
			textTitle = (TextView)view.findViewById(R.id.text_read_title);
			textContent = (TextView)view.findViewById(R.id.text_read_content);
			btnReading = (Button)view.findViewById(R.id.btn_book_reading);
			btnReading.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			final Book item = mItemList.get(getLayoutPosition());
			String downloadUrl = item.getDownloadUrl();
			final String name = downloadUrl.substring(downloadUrl.lastIndexOf(".") + 1, downloadUrl.length());	//扩展名
			final String path = ConstantHolder.PATH_CACHE_BOOK + item.getId() + "." + name;
			LogUtils.d("path:" + path);
			File file = new File(path);
			if (!file.exists()) {
				HttpUtils.downloadFile(mActivity, downloadUrl, item.getId() + ".pdf", new HttpUtils.HttpRequestCallback() {
					@Override
					public void onSuccess(String response) {
						mActivity.switchFragment(ReadingFragment.newInstance(path), "ReadingFragment");
					}
				});
			} else {
				mActivity.switchFragment(ReadingFragment.newInstance(path), "ReadingFragment");
			}
		}
	}

	/**
	 * 打开图书文件
	 * @param path 图书路径
	 * @param name 图书扩展名
	 */
	private void openBookFile(String path, String name) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if ("txt".equals(name) || "text".equals(name)) {
			Uri uri =Uri.fromFile(new File(path));
			intent.setDataAndType(uri,"text/plain");
		} else if ("pdf".equals(name)) {
			Uri uri = Uri.fromFile(new File(path));
			intent.setDataAndType(uri, "application/pdf");
		} else if ("doc".equals(name)) {
			Uri uri = Uri.fromFile(new File(path));
			intent.setDataAndType(uri,"application/msword");
		}
		startActivity(intent);
	}
}
