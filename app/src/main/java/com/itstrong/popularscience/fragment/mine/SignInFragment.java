package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 签到界面
 * Created by itstrong on 2016/6/8.
 */
public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView recyclerCalendar;
    private ImageView imgSignInOnOff;
    private TextView textDate;
    private TextView textScore;
    private String[] record;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        recyclerCalendar = (RecyclerView)mContentView.findViewById(R.id.recycler_view_calendar);
        imgSignInOnOff = (ImageView)mContentView.findViewById(R.id.img_sign_in_on_off);
        textDate = (TextView)mContentView.findViewById(R.id.text_sign_in_date);
        textScore = (TextView)mContentView.findViewById(R.id.text_sign_in_score);
        recyclerCalendar.setHasFixedSize(true);
        recyclerCalendar.setLayoutManager(new GridLayoutManager(mActivity, 7));
        recyclerCalendar.setAdapter(new MyAdapter());
    }

    @Override
    public void setListener() {
        imgSignInOnOff.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!today.equals(SPHandler.getIsRemindToday(mActivity))) {
            SPHandler.putIsRemindToday(mActivity, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            ToastUtils.showToast(mActivity, "今日签到 + 5");
            submitScore(5);
        }
        mActivity.setFragmentTitle("签到赚积分");
        textDate.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        LogUtils.d("getWeekOfMonth" + getWeekOfMonth());
        LogUtils.d("getDayOfMonth" + getDayOfMonth());
        String signInValue = SPHandler.getUserIsSignIn(mActivity);
        record = signInValue.split(",");
        String temp = "";
        for (String str : record) {
            temp += str;
        }
        LogUtils.d("签到纪录:" + temp);
        if (SPHandler.getUserIsSignInRemind(mActivity)) {
            imgSignInOnOff.setImageResource(R.mipmap.sign_in_down);
        } else {
            imgSignInOnOff.setImageResource(R.mipmap.sign_in_off);
        }
        displayScore();
    }

    /**
     * 显示积分
     */
    private void displayScore() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_GET_RANKING, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
                        JSONObject results = jsonObject.getJSONObject("results");
                        textScore.setText(results.getString("myScore"));
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
            case R.id.img_sign_in_on_off:
                if (SPHandler.getUserIsSignInRemind(mActivity)) {
                    imgSignInOnOff.setImageResource(R.mipmap.sign_in_off);
                    SPHandler.putUserIsSignInRemind(mActivity, false);
                } else {
                    imgSignInOnOff.setImageResource(R.mipmap.sign_in_down);
                    SPHandler.putUserIsSignInRemind(mActivity, true);
                }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private int day = 1;
        private int week = getWeekOfMonth();
        private int totalDay = getDayOfMonth();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from
                    (mActivity).inflate(R.layout.item_recycler_sign_in, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position >= week - 1 && position <= totalDay + week - 2) {
                holder.textTime.setText("" + day);
                day++;
            }
            for (String str : record) {
                if (str.equals(position - week + 2 + "")) {
                    holder.imgFlag.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 35;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textTime;
            ImageView imgFlag;

            public MyViewHolder(View view) {
                super(view);
                textTime = (TextView) view.findViewById(R.id.text_sign_in_day);
                imgFlag = (ImageView) view.findViewById(R.id.text_sign_in_flag);
            }
        }
    }

    /**
     * 获取当月第一天是星期几
     * @return
     */
    private int getWeekOfMonth() {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        int w = cal.get(Calendar.DAY_OF_WEEK) + 2;
        return w < 0 ? 0 : w;
    }

    /**
     * 获取当月的天数
     * @return
     */
    private int getDayOfMonth(){
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        return aCalendar.getActualMaximum(Calendar.DATE);
    }
}
