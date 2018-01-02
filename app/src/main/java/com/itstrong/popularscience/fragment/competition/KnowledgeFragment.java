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
import com.itstrong.popularscience.bean.Question;
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
 * 知识竞赛
 * Created by itstrong on 2016/6/14.
 */
public class KnowledgeFragment extends BaseFragment implements View.OnClickListener {

    private TextView textTimer;
    private TextView textContent;
    private RadioButton radioOption0;
    private RadioButton radioOption1;
    private RadioButton radioOption2;
    private RadioButton radioOption3;
    private Button btnQuit;

    private List<RadioButton> mRadioButtonList;
    private List<Question> mQuestions;
    private int stage;
    private int score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_know_competition, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        textTimer = (TextView)mContentView.findViewById(R.id.text_game_timer);
        textContent = (TextView)mContentView.findViewById(R.id.text_game_content);
        radioOption0 = (RadioButton)mContentView.findViewById(R.id.radio_know_option_0);
        radioOption1 = (RadioButton)mContentView.findViewById(R.id.radio_know_option_1);
        radioOption2 = (RadioButton)mContentView.findViewById(R.id.radio_know_option_2);
        radioOption3 = (RadioButton)mContentView.findViewById(R.id.radio_know_option_3);
        btnQuit = (Button)mContentView.findViewById(R.id.btn_knowledge_quit);
    }

    @Override
    public void setListener() {
        radioOption0.setOnClickListener(this);
        radioOption1.setOnClickListener(this);
        radioOption2.setOnClickListener(this);
        radioOption3.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stage = 0;
        score = 0;
        mQuestions = new ArrayList<>();
        mRadioButtonList = new ArrayList<>();
        mRadioButtonList.add(radioOption0);
        mRadioButtonList.add(radioOption1);
        mRadioButtonList.add(radioOption2);
        mRadioButtonList.add(radioOption3);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("知识竞赛");
        Map<String, String> map = new HashMap<>();
        map.put("UserId", SPHandler.getUserId(mActivity));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_KNOWLEDGE_CONTEST, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("flag")) {
                        //解析数据
                        JSONArray results = jsonObject.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject item = results.getJSONObject(i);
                            List<String> options = new ArrayList<>();
                            JSONArray option = item.getJSONArray("option");
                            for (int j = 0; j < option.length(); j++) {
                                options.add(option.getString(j));
                            }
                            Question question = new Question();
                            question.setAnswer(item.getString("answer"));
                            question.setContent(item.getString("content"));
                            question.setId(item.getString("id"));
                            question.setOption(options);
                            mQuestions.add(question);
                        }
                        LogUtils.d(mQuestions.toString());
                        //更新UI
                        updateUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String choose = "";
        switch (v.getId()) {
            case R.id.btn_knowledge_quit:
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
        String answer = mQuestions.get(stage).getAnswer();
        if (choose.equals(answer)) {
            ToastUtils.showToast(mActivity, "回答正确！积分 + 1");
        } else {
            ToastUtils.showToast(mActivity, "回答错误！" + "正确答案为：" + answer);
        }
        stage++;
        if (stage < mQuestions.size()) {
            updateUI();
        } else {
            showOverDialog();
        }
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
     * 更新UI
     */
    private void updateUI() {
        textTimer.setText(stage + 1 + " / " + mQuestions.size());
        Question question = mQuestions.get(stage);
        textContent.setText(question.getContent());
        for (int i = 0; i < question.getOption().size(); i++) {
            mRadioButtonList.get(i).setText(question.getOption().get(i));
            mRadioButtonList.get(i).setChecked(false);
        }
    }

    /**
     * 显示答题结束对话框
     */
    private void showOverDialog() {
        submitScore(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("答题完毕，共获得 " + score + " 积分！").setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.switchFragmentPage(mActivity.FRAGMENT_GAME_OVER);
            }
        });
        builder.create().show();
    }
}
