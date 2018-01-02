package com.itstrong.popularscience.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Map;

/**
 * 网络工具类
 * Created by itstrong on 2016/6/14.
 */
public class HttpUtils {

    private static ProgressBar progressBar;

    /**
     * POST请求接口
     * @param url
     * @param form
     * @param requestCallback
     */
    public static void getServerDataForPost(final Context context, String url, Map<String, String> form, final HttpRequestCallback requestCallback) {
        OkHttpUtils.post().url(url).params(form).build().execute(new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtils.w("请求失败:" + request.body().toString());
                ToastUtils.showToast(context, "请检查网络连接");
            }

            @Override
            public void onResponse(String response) {
                LogUtils.d("请求结果:" + response);
                requestCallback.onSuccess(response);
            }
        });
    }

    /**
     * 请求成功回调
     */
    public interface HttpRequestCallback {
        void onSuccess(String response);
    }

    /**
     * 网络是否连接判断
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 下载文件接口
     * @param activity
     * @param url
     * @param fileName
     * @param callback
     */
    public static void downloadFile(Activity activity, String url, String fileName, final HttpRequestCallback callback) {
        createProgress(activity);
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(ConstantHolder.PATH_CACHE_BOOK, fileName) {
            @Override
            public void inProgress(float progress) { }

            @Override
            public void onError(Request request, Exception e) {
                LogUtils.d("下载失败！");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(File file) {
                LogUtils.d("下载成功！" + file.getAbsolutePath());
                callback.onSuccess(file.getAbsolutePath());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private static void createProgress(Activity activity) {
        if (progressBar == null) {
            progressBar = new ProgressBar(activity);
            RelativeLayout layout = new RelativeLayout(activity);
            layout.addView(progressBar);
            layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            activity.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        progressBar.setVisibility(View.VISIBLE);
    }
}