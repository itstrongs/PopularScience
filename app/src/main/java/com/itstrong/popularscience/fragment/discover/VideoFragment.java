package com.itstrong.popularscience.fragment.discover;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 科普视频页面
 * Created by itstrong on 2016/6/1.
 */
public class VideoFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private BaseAdapter mAdapter;
    private List<Details> mDetailsList;

    private boolean isDownloading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_video, container, false);
        mDetailsList = new ArrayList<>();
        isDownloading = false;
        return mContentView;
    }

    @Override
    public void findViewById() {
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recycler_view_video);
    }

    @Override
    public void setListener() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
        mAdapter = new BaseAdapter(mDetailsList.size(), new BaseAdapter.BaseAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_video, parent, false));
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
                Details details = mDetailsList.get(position);
                LogUtils.d("details:" + details);
                final MyViewHolder myViewHolder = (MyViewHolder)holder;
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
                Set<String> videoInfo = SPHandler.getVideoInfo(mActivity);
                if (videoInfo != null) {
                    for (String info : videoInfo) {
                        if (info.equals(details.getId())) {
                            myViewHolder.btnDownload.setText(" 已下载");
                            myViewHolder.btnDownload.setClickable(false);
                            ViewGroup.LayoutParams params = myViewHolder.textProgress.getLayoutParams();
                            params.width = myViewHolder.btnDownload.getLayoutParams().width;
                            myViewHolder.textProgress.setLayoutParams(params);
                        }
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("科普视频");
        Map<String, String> map = new HashMap<>();
        map.put("type", "4");
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
                            mDetailsList.add(details);
                            mAdapter.setCount(mDetailsList.size());
                            mAdapter.notifyItemInserted(i);
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
        TextView textProgress;
        Button btnDownload;
        Button btnPlay;

        public MyViewHolder(View view) {
            super(view);
            imgIcon = (ImageView) view.findViewById(R.id.img_video_icon);
            textTitle = (TextView) view.findViewById(R.id.text_video_title);
            textContent = (TextView) view.findViewById(R.id.text_video_content);
            textProgress = (TextView) view.findViewById(R.id.text_video_progress);
            btnDownload = (Button) view.findViewById(R.id.btn_video_download);
            btnPlay = (Button) view.findViewById(R.id.btn_video_play);
            btnDownload.setOnClickListener(this);
            btnPlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_video_download:
                    if (isDownloading) {
                        downloadVideo(getLayoutPosition());
                    }
                    break;
                case R.id.btn_video_play:
                    playVideo(getLayoutPosition());
                    break;
            }
        }

        /**
         * 下载视频
         */
        private void downloadVideo(final int position) {
            HashMap<String, String> map = new HashMap<>();
            map.put("videoId", mDetailsList.get(position).getId() + "");
            HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_VIDEO_DOWNLOAD, map, new HttpUtils.HttpRequestCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject root = new JSONObject(response);
                        final boolean flag = root.getBoolean("flag");
                        if (flag) {
                            JSONObject results = root.getJSONObject("results");
                            String data = results.getString("data");
                            String id = results.getString("id");
                            OkHttpUtils.get().url(data).build().execute(new FileCallBack(ConstantHolder.PATH_CACHE_VIDEO, id + ".mp4") {
                                @Override
                                public void inProgress(float progress) {
                                    int width = btnDownload.getLayoutParams().width;
                                    ViewGroup.LayoutParams params = textProgress.getLayoutParams();
                                    params.width = (int) (width * progress);
                                    textProgress.setLayoutParams(params);
                                }

                                @Override
                                public void onError(Request request, Exception e) {
                                    LogUtils.d("下载失败！");
                                }

                                @Override
                                public void onResponse(File file) {
                                    LogUtils.d("下载成功！" + file.getAbsolutePath());
                                    ToastUtils.showToast(mActivity, "下载成功！");
                                    btnDownload.setText(" 已下载");
                                    btnDownload.setClickable(false);
                                    btnDownload.setTextColor(Color.GRAY);
                                    SPHandler.putVideoInfo(mActivity, mDetailsList.get(position).getId());
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 播放视频
     */
    private void playVideo(int position) {
        String id = mDetailsList.get(position).getId();
        Uri uri = Uri.parse(ConstantHolder.PATH_CACHE_VIDEO + id + ".mp4");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }
}
