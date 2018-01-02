package com.itstrong.popularscience.fragment.competition;

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
import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏结束页面
 * Created by itstrong on 2016/6/17.
 */
public class GameOverFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRecyclerViewMonth;
    private RecyclerView mRecyclerViewYear;
    private Button mBtnBack;
    private Button mBtnContinue;

    private BaseAdapter mMonthAdapter;
    private BaseAdapter mYearAdapter;
    private List<String> mMonthNameList;
    private List<String> mYearNameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_game_over, container, false);
        mMonthNameList = new ArrayList<>();
        mYearNameList = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        mRecyclerViewMonth = (RecyclerView)mContentView.findViewById(R.id.recycler_view_month);
        mRecyclerViewYear = (RecyclerView)mContentView.findViewById(R.id.recycler_view_year);
        mBtnBack = (Button)mContentView.findViewById(R.id.btn_game_over_back);
        mBtnContinue = (Button)mContentView.findViewById(R.id.btn_game_over_continue);
    }

    @Override
    public void setListener() {
        mBtnBack.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);
        //月排名列表
        mRecyclerViewMonth.setHasFixedSize(true);
        mRecyclerViewMonth.setLayoutManager(new LinearLayoutManager(mActivity));
        mMonthAdapter = new BaseAdapter(mMonthNameList.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_rank, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyViewHolder myViewHolder = ((MyViewHolder)holder);
                switch (position) {
                    case 0:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_0);
                        break;
                    case 1:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_1);
                        break;
                    case 2:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_2);
                        break;
                }
                LogUtils.d("position:" + position + ",mMonthNameList:" + mMonthNameList.size());
                myViewHolder.textName.setText(mMonthNameList.get(position));
            }
        });
        mRecyclerViewMonth.setAdapter(mMonthAdapter);
        //年排名列表
        mRecyclerViewYear.setHasFixedSize(true);
        mRecyclerViewYear.setLayoutManager(new LinearLayoutManager(mActivity));
        mYearAdapter = new BaseAdapter(mYearNameList.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_rank, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyViewHolder myViewHolder = ((MyViewHolder)holder);
                switch (position) {
                    case 0:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_0);
                        break;
                    case 1:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_1);
                        break;
                    case 2:
                        myViewHolder.imgUserHead.setImageResource(R.mipmap.rank_2);
                        break;
                }
                myViewHolder.textName.setText(mYearNameList.get(position));
            }
        });
        mRecyclerViewYear.setAdapter(mYearAdapter);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("互动游戏");
        mActivity.setFragmentTitle("我的排名");
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_GET_RANKING, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    //解析数据
                    JSONObject jsonObject = new JSONObject(response);
                    boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
                        JSONObject results = jsonObject.getJSONObject("results");
                        JSONArray monthlyRankingTop10 = results.getJSONArray("monthlyRankingTop10");
                        for (int i = 0; i < monthlyRankingTop10.length(); i++) {
                            JSONObject user = monthlyRankingTop10.getJSONObject(i);
                            mMonthNameList.add(user.getString("name"));
                        }
                        JSONArray annualRankingTop10 = results.getJSONArray("annualRankingTop10");
                        for (int i = 0; i < annualRankingTop10.length(); i++) {
                            JSONObject user = annualRankingTop10.getJSONObject(i);
                            mYearNameList.add(user.getString("name"));
                        }
                        //更新UI
                        mMonthAdapter.setCount(mMonthNameList.size());
                        mMonthAdapter.notifyDataSetChanged();
                        mYearAdapter.setCount(mYearNameList.size());
                        mYearAdapter.notifyDataSetChanged();
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
            case R.id.btn_game_over_back:
                mActivity.switchFragmentPage(mActivity.FRAGMENT_COMPETITION);
                break;
            case R.id.btn_game_over_continue:
                mActivity.switchFragmentPage(mActivity.FRAGMENT_DAY_ANSWER);
                break;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgUserHead;
        public TextView textName;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgUserHead = (ImageView)itemView.findViewById(R.id.img_rank_user_head);
            textName = (TextView)itemView.findViewById(R.id.text_rank_name);
        }
    }
}
