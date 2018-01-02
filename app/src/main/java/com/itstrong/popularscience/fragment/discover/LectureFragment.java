package com.itstrong.popularscience.fragment.discover;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.itstrong.popularscience.bean.Details;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.AsyncImageLoader;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科普讲堂界面
 * Created by itstrong on 2016/6/8.
 */
public class LectureFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private BaseAdapter mAdapter;
    private List<Details> mItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_lecture, container, false);
        mItemList = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        mSwipeRefresh = (SwipeRefreshLayout)mContentView.findViewById(R.id.layout_swipe_refresh);
        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recycler_lecture);
    }

    @Override
    public void setListener() {
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
        mAdapter = new BaseAdapter(mItemList.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_head_line, parent, false));
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
                Details details = mItemList.get(position);
                final MyViewHolder myViewHolder = ((MyViewHolder)holder);
                myViewHolder.textTitle.setText(details.getTitle());
                new AsyncImageLoader().loadDrawable(details.getImageUrl(),
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
        mActivity.setFragmentTitle("科普讲堂");
        Map<String, String> map = new HashMap<>();
        map.put("type", "2");
        map.put("page", "1");
        map.put("count", "10");
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_DISCOVER_LIST, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    boolean flag = root.getBoolean("flag");
                    if (flag) {
                        JSONArray results = root.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject object = results.getJSONObject(i);
                            Details details = new Details();
                            details.setId(object.getString("id"));
                            details.setImageUrl(object.getString("imageUrl"));
                            details.setTitle(object.getString("title"));
                            mItemList.add(details);
                        }
                        LogUtils.d("mItemList:" + mItemList);
                        mAdapter.setCount(mItemList.size());
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
                ToastUtils.showToast(mActivity, "刷新成功！");
            }
        }, 1000);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgIcon;
        TextView textTitle;
        TextView textContent;
        Button btnDetail;

        public MyViewHolder(View view) {
            super(view);
            imgIcon = (ImageView)view.findViewById(R.id.img_head_line_icon);
            textTitle = (TextView)view.findViewById(R.id.text_head_line_title);
//            textContent = (TextView)view.findViewById(R.id.text_head_line_content);
            btnDetail = (Button)view.findViewById(R.id.btn_head_line_detail);
            btnDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_DISPLAY, mItemList.get(getLayoutPosition()).getId(), "2");
        }
    }
}
