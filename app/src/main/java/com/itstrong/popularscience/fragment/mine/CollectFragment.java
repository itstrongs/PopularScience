package com.itstrong.popularscience.fragment.mine;

import android.graphics.drawable.Drawable;
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
import com.itstrong.popularscience.DividerItemDecoration;
import com.itstrong.popularscience.R;
import com.itstrong.popularscience.bean.Details;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.AsyncImageLoader;
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
 * 我的收藏界面
 * Created by itstrong on 2016/6/8.
 */
public class CollectFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private TextView mTextTip;
    private BaseAdapter mAdapter;
    private List<Details> mDetailses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_collect, container, false);
        mDetailses = new ArrayList<>();
        return mContentView;
    }

    @Override
    public void findViewById() {
        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recycler_collect_list);
        mTextTip = (TextView)mContentView.findViewById(R.id.text_collect_tip);
    }

    @Override
    public void setListener() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
        mAdapter = new BaseAdapter(mDetailses.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_collect, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final MyViewHolder myViewHolder = (MyViewHolder)holder;
                Details details = mDetailses.get(position);
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
                mActivity.switchFragmentPage(mActivity.SWITCH_DISCOVER_DISPLAY,
                        mDetailses.get(position).getId(), mDetailses.get(position).getType());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("我的收藏");
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_MY_COLLECT, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    boolean flag = root.getBoolean("flag");
                    if (flag) {
                        JSONArray results = root.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject item = results.getJSONObject(i);
                            Details details = new Details();
                            details.setId(item.getString("id"));
                            details.setImageUrl(item.getString("imageUrl"));
                            details.setSubtitle(item.getString("subtitle"));
                            details.setTitle(item.getString("title"));
                            details.setType(item.getString("type"));
                            mDetailses.add(details);
                        }
                        LogUtils.d(mDetailses.toString());
                        if (mDetailses.size() == 0) {
                            mTextTip.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.setCount(mDetailses.size());
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgIcon;
        TextView textTitle;
        TextView textContent;
        ImageView imgDelete;

        public MyViewHolder(View view) {
            super(view);
            imgIcon = (ImageView)view.findViewById(R.id.img_park_icon);
            textTitle = (TextView)view.findViewById(R.id.text_park_title);
            textContent = (TextView)view.findViewById(R.id.text_park_content);
            imgDelete = (ImageView)view.findViewById(R.id.img_collect_delete);
            imgDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", SPHandler.getUserId(mActivity));
            map.put("type", mDetailses.get(getLayoutPosition()).getType());
            map.put("id", mDetailses.get(getLayoutPosition()).getId());
            LogUtils.d(map.toString());
            HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_DELETE_COLLECT, map, new HttpUtils.HttpRequestCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject root = new JSONObject(response);
                        if (root.getBoolean("flag")) {
                            ToastUtils.showToast(mActivity, "删除成功！");
                            mDetailses.remove(getLayoutPosition());
                            mAdapter.setCount(mDetailses.size());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast(mActivity, "删除失败！" + root.getBoolean("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
