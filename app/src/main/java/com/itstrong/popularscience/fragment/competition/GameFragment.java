package com.itstrong.popularscience.fragment.competition;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
 * 互动游戏界面
 * Created by itstrong on 2016/6/8.
 */
public class GameFragment extends BaseFragment implements View.OnClickListener {

    private TextView textContent;
    private TextView textTimer;
    private RadioButton radioOption0;
    private RadioButton radioOption1;
    private RadioButton radioOption2;
    private RadioButton radioOption3;
    private Button btnGameQuit;

    private String answer;           //游戏答案
    private String content;          //游戏问题
    private List<String> options;    //游戏选项

    private List<RadioButton> radioList;
    private Handler mHandler = new Handler();
    private int time = 100 * 100;   //计时器时间

    private List<RadioButton> mRadioButtonList;
    private List<Question> mQuestions;
    private int stage;
    private int score;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_game, container, false);
        options = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        textContent = (TextView)mActivity.findViewById(R.id.text_game_content);
        textTimer = (TextView)mActivity.findViewById(R.id.text_game_timer);
        radioOption0 = (RadioButton)mActivity.findViewById(R.id.radio_game_option_0);
        radioOption1 = (RadioButton)mActivity.findViewById(R.id.radio_game_option_1);
        radioOption2 = (RadioButton)mActivity.findViewById(R.id.radio_game_option_2);
        radioOption3 = (RadioButton)mActivity.findViewById(R.id.radio_game_option_3);
        btnGameQuit = (Button)mActivity.findViewById(R.id.btn_game_quit);
    }

    @Override
    public void setListener() {
        radioOption0.setOnClickListener(this);
        radioOption1.setOnClickListener(this);
        radioOption2.setOnClickListener(this);
        radioOption3.setOnClickListener(this);
        btnGameQuit.setOnClickListener(this);
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
        mHandler.postDelayed(runnable, 1000);
        mActivity.setFragmentTitle("互动游戏");
        radioList = new ArrayList<>();
        radioList.add(radioOption0);
        radioList.add(radioOption1);
        radioList.add(radioOption2);
        radioList.add(radioOption3);
        Map<String, String> map = new HashMap<>();
        map.put("UserId", SPHandler.getUserId(mActivity));
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_INTERACTIVE_GAME, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
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

    /**
     * 更新UI
     */
    private void updateUI() {
        Question question = mQuestions.get(stage);
        answer = question.getAnswer();
        textContent.setText(question.getContent());
        for (int i = 0; i < question.getOption().size(); i++) {
            mRadioButtonList.get(i).setText(question.getOption().get(i));
            mRadioButtonList.get(i).setChecked(false);
        }
    }

    /**
     * 定时器
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time == 0) {
                ToastUtils.showToast(mActivity, "时间到了，游戏结束");
                showTipDialog();
            } else {
                textTimer.setText(time / 100 + ":" + (time % 100));
                mHandler.postDelayed(this, 10);
                time--;
            }
        }
    };

    @Override
    public void onClick(View v) {
        RadioButton radioButton = null;
        switch (v.getId()) {
            case R.id.radio_game_option_0:
                radioButton = radioOption0;
                break;
            case R.id.radio_game_option_1:
                radioButton = radioOption1;
                break;
            case R.id.radio_game_option_2:
                radioButton = radioOption2;
                break;
            case R.id.radio_game_option_3:
                radioButton = radioOption3;
                break;
            case R.id.btn_game_quit:
                showQuitDialog();
                return;
        }
        if (answer.equals(radioButton.getText().subSequence(0, 1))) {
            ToastUtils.showToast(mActivity, "回答正确，进入下一题");
            if (stage < mQuestions.size() - 1) {
                stage++;
                score++;
                updateUI();
            } else {
                showTipDialog();
            }
        } else {
            ToastUtils.showToast(mActivity, "回答错误，游戏结束");
            showTipDialog();
        }
    }

    /**
     * 显示答题结束对话框
     */
    private void showTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("答题完毕，共获得 " + score + " 积分！").setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandler.removeCallbacks(runnable);
                mActivity.switchFragmentPage(mActivity.FRAGMENT_GAME_OVER);
            }
        });
        builder.create().show();
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
                mHandler.removeCallbacks(runnable);
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
}
