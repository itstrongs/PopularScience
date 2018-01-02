package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;

import java.util.Calendar;

/**
 * 我的页面
 * Created by itstrong on 2016/6/8.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

	private TextView imgMyRank;
	private TextView imgMySetting;
	private TextView imgMyComment;
	private TextView imgMyCollect;
	private TextView textSignIn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_my, container, false);
		return mContentView;
	}

	@Override
	public void findViewById() {
		imgMyRank = (TextView)mActivity.findViewById(R.id.text_my_rank);
		imgMySetting = (TextView)mActivity.findViewById(R.id.text_my_setting);
		imgMyComment = (TextView)mActivity.findViewById(R.id.text_my_comment);
		imgMyCollect = (TextView)mActivity.findViewById(R.id.text_my_collect);
		textSignIn = (TextView)mActivity.findViewById(R.id.text_sign_in);
	}

	@Override
	public void setListener() {
		imgMyRank.setOnClickListener(this);
		imgMySetting.setOnClickListener(this);
		imgMyComment.setOnClickListener(this);
		imgMyCollect.setOnClickListener(this);
		textSignIn.setOnClickListener(this);
	}

	@Override
	public void processLogic() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.text_my_rank:
				mActivity.switchFragmentPage(mActivity.SWITCH_MY_RANK);
				break;
			case R.id.text_my_setting:
				mActivity.switchFragmentPage(mActivity.SWITCH_MY_SETTING);
				break;
			case R.id.text_my_comment:
				mActivity.switchFragmentPage(mActivity.SWITCH_MY_COMMENT);
				break;
			case R.id.text_my_collect:
				mActivity.switchFragmentPage(mActivity.SWITCH_MY_COLLECT);
				break;
			case R.id.text_sign_in:
				mActivity.switchFragmentPage(mActivity.SWITCH_SIGN_IN);
				putUserIsSignIn();
		}
	}

	/**
	 * 存储签到信息
	 */
	private void putUserIsSignIn() {
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String value = SPHandler.getUserIsSignIn(mActivity);
		LogUtils.d("存储签到信息:" + value + "day" + day);
		String str[] = value.split(",");
		boolean flag = true;
		for(String s : str) {
			if (s.equals(day + "")) {
				flag = false;
			}
		}
		if (flag) {
			SPHandler.putUserIsSignIn(mActivity, day);
		}
	}
}
