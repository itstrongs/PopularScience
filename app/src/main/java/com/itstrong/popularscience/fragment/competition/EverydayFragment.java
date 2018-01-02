package com.itstrong.popularscience.fragment.competition;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日一答页面
 * Created by itstrong on 2016/6/14.
 */
public class EverydayFragment extends BaseFragment implements View.OnClickListener {

    private TextView textContent;
    private RadioButton radioOption0;
    private RadioButton radioOption1;
    private RadioButton radioOption2;
    private RadioButton radioOption3;
    private Button btnQuit;

    private List<RadioButton> mRadioButtonList;
    private String answer;
    private int score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_day_answer, container, false);
        score = 0;
        return mContentView;
    }

    @Override
    public void findViewById() {
        textContent = (TextView)mContentView.findViewById(R.id.text_day_content);
        radioOption0 = (RadioButton)mContentView.findViewById(R.id.radio_game_option_0);
        radioOption1 = (RadioButton)mContentView.findViewById(R.id.radio_game_option_1);
        radioOption2 = (RadioButton)mContentView.findViewById(R.id.radio_game_option_2);
        radioOption3 = (RadioButton)mContentView.findViewById(R.id.radio_game_option_3);
        btnQuit = (Button)mContentView.findViewById(R.id.btn_everyday_quit);
    }

    @Override
    public void setListener() {
        radioOption0.setOnClickListener(this);
        radioOption1.setOnClickListener(this);
        radioOption2.setOnClickListener(this);
        radioOption3.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        initData();
        mActivity.setFragmentTitle("每日一答");
        Map<String, String> map = new HashMap<>();
        map.put("UserId", SPHandler.getUserId(mActivity));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_DAY_ANSWER, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
                        //解析数据
                        JSONObject results = jsonObject.getJSONObject("results");
                        answer = results.getString("answer");
                        String content = results.getString("content");
                        JSONArray option = results.getJSONArray("option");
                        List<String> optionList = new ArrayList<>();
                        for (int i = 0; i < option.length(); i++) {
                            optionList.add(option.getString(i));
                        }
                        LogUtils.d(optionList.toString());
                        //更新UI
                        textContent.setText(content);
                        for (int i = 0; i < optionList.size(); i++) {
                            mRadioButtonList.get(i).setText(optionList.get(i));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        mRadioButtonList = new ArrayList<>();
        mRadioButtonList.add(radioOption0);
        mRadioButtonList.add(radioOption1);
        mRadioButtonList.add(radioOption2);
        mRadioButtonList.add(radioOption3);
    }

    @Override
    public void onClick(View v) {
        String choose = "";
        switch (v.getId()) {
            case R.id.btn_everyday_quit:
                showQuitDialog();
                return;
            case R.id.radio_game_option_0:
                choose = "A";
                break;
            case R.id.radio_game_option_1:
                choose = "B";
                break;
            case R.id.radio_game_option_2:
                choose = "C";
                break;
            case R.id.radio_game_option_3:
                choose = "D";
                break;
        }
        if (choose.equals(answer)) {
            ToastUtils.showToast(mActivity, "回答正确！+5积分");
            score = 5;
        } else {
            ToastUtils.showToast(mActivity, "回答错误，正确答案为：" + answer);
        }
        showOverDialog();
    }

    /**
     * 显示提示对话框
     */
    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("答题未结束，确定退出？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.switchFragmentPage(mActivity.FRAGMENT_COMPETITION);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 显示答题结束对话框
     */
    private void showOverDialog() {
        submitScore(score);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("答题完毕，共获得 " + score + "积分").setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.switchFragmentPage(mActivity.FRAGMENT_GAME_OVER);
            }
        });
        builder.create().show();
    }
}
