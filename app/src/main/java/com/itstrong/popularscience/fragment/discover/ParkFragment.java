package com.itstrong.popularscience.fragment.discover;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.BaseAdapter;
import com.itstrong.popularscience.R;
import com.itstrong.popularscience.bean.Details;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.AsyncImageLoader;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科普公园界面
 * Created by itstrong on 2016/6/5.
 */
public class ParkFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;

    private BaseAdapter mAdapter;
    private List<Details> mItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_park, container, false);
        mItemList = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        mSwipeRefresh = (SwipeRefreshLayout)mContentView.findViewById(R.id.layout_swipe_refresh);
        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recycler_view_park);
    }

    @Override
    public void setListener() {
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new BaseAdapter(mItemList.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_park, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
        mAdapter.setOnItemClickLitener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_DISPLAY, mItemList.get(position).getId(), "5");
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("科普公园");
        Map<String, String> map = new HashMap<>();
        map.put("type", "5");
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
                            mAdapter.setCount(mItemList.size());
                            mAdapter.notifyItemInserted(i);
                        }
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

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView textTitle;
        TextView textContent;

        public MyViewHolder(View view) {
            super(view);
            imgIcon = (ImageView)view.findViewById(R.id.img_park_icon);
            textTitle = (TextView)view.findViewById(R.id.text_park_title);
            textContent = (TextView)view.findViewById(R.id.text_park_content);
        }
    }
}
