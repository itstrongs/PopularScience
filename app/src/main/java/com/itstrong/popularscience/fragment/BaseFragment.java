package com.itstrong.popularscience.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.itstrong.popularscience.activity.HomeActivity;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by itstrong on 2016/6/1.
 */
public abstract class BaseFragment extends Fragment {

    protected HomeActivity mActivity;
    protected View mContentView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (HomeActivity)getActivity();
        findViewById();
        setListener();
        processLogic();
    }

    /**
     * 设置布局控件
     */
    public abstract void findViewById();

    /**
     * 设置控件监听
     */
    public abstract void setListener();

    /**
     * 处理业务逻辑
     */
    public abstract void processLogic();

    /**
     * 提交积分
     */
    public void submitScore(int score) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        map.put("score", score + "");
        map.put("type", "3");
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_SUBMIT_SCORE, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtils.d("积分提交成功！");
            }
        });
    }
}
