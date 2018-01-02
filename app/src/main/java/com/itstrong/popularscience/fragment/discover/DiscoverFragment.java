package com.itstrong.popularscience.fragment.discover;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 发现界面
 * Created by itstrong on 2016/6/4.
 */
public class DiscoverFragment extends BaseFragment implements View.OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_discover, container, false);
		return mContentView;
	}

	@Override
	public void findViewById() {
		mContentView.findViewById(R.id.text_discover_head_line).setOnClickListener(this);
		mContentView.findViewById(R.id.text_discover_lecture).setOnClickListener(this);
		mContentView.findViewById(R.id.text_discover_life).setOnClickListener(this);
		mContentView.findViewById(R.id.text_discover_video).setOnClickListener(this);
		mContentView.findViewById(R.id.text_discover_park).setOnClickListener(this);
	}

	@Override
	public void setListener() { }

	@Override
	public void processLogic() {
		mActivity.setFragmentTitle("科普房山");
		mActivity.setBtnBackIsInvisible(true);
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (!today.equals(SPHandler.getIsRemindToday(mActivity)) && SPHandler.getUserIsSignInRemind(mActivity)) {
			showRemindDialog();
		}
		Map<String, String> map = new HashMap<>();
		map.put("", "");
		HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_CAROUSEL_IMAGE, map, new HttpUtils.HttpRequestCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject root = new JSONObject(response);
					if (root.getBoolean("flag")) {
						JSONArray results = root.getJSONArray("results");
						for (int i = 0; i < results.length(); i++) {
							String imageUrl = results.getString(i);
							if (!imageUrl.isEmpty()) {
								LogUtils.d("imageUrl:" + results.getString(i));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 签到提醒对话框
	 */
	private void showRemindDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage("今日还未签到，是否立即签到？").setTitle("签到提醒");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mActivity.switchFragmentPage(mActivity.SWITCH_SIGN_IN);
				ToastUtils.showToast(mActivity, "今日签到 + 5");
				submitScore(5);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
		SPHandler.putIsRemindToday(mActivity, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.text_discover_head_line:
				mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_HEAD_LINE);
				break;
			case R.id.text_discover_lecture:
				mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_LECTURE);
				break;
			case R.id.text_discover_life:
				mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_LIFE);
				break;
			case R.id.text_discover_video:
				mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_VIDEO);
				break;
			case R.id.text_discover_park:
				mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_PARK);
				break;
		}
	}
}
