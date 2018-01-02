package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.BaseAdapter;
import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.SPHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的排名界面
 * Created by itstrong on 2016/6/8.
 */
public class RankFragment extends BaseFragment {

    private RecyclerView mRecyclerViewMonth;
    private RecyclerView mRecyclerViewYear;
    private TextView mTextMonth;
    private TextView mTextYear;

    private BaseAdapter mMonthAdapter;
    private BaseAdapter mYearAdapter;
    private List<String> mMonthNameList;
    private List<String> mYearNameList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_rank, container, false);
        mMonthNameList = new ArrayList<>();
        mYearNameList = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        mRecyclerViewMonth = (RecyclerView)mContentView.findViewById(R.id.recycler_view_month);
        mRecyclerViewYear = (RecyclerView)mContentView.findViewById(R.id.recycler_view_year);
        mTextMonth = (TextView)mContentView.findViewById(R.id.text_rank_month);
        mTextYear = (TextView)mContentView.findViewById(R.id.text_rank_year);
    }

    @Override
    public void setListener() {
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
                myViewHolder.textName.setText(mMonthNameList.get(position));
            }
        });
        mRecyclerViewYear.setAdapter(mYearAdapter);
    }

    @Override
    public void processLogic() {
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
                        int myMonthlyRanking = results.getInt("myMonthlyRanking");
                        int myAnnualRanking = results.getInt("myAnnualRanking");
                        //更新UI
                        mTextMonth.setText(myMonthlyRanking + "");
                        mTextYear.setText(myAnnualRanking + "");
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
