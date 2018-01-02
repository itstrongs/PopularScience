package com.itstrong.popularscience.fragment.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 详情页面
 * Created by itstrong on 2016/6/19.
 */
public class DetailsFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTextTitle;
    private TextView mTextDate;
    private TextView mTextCollect;
    private TextView mTextShare;
    private WebView mWebView;

    private String titles;
    private String content;

    public static DetailsFragment newInstance(String type, String id) {
        DetailsFragment newFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("id", id);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_web_view, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        mTextTitle = (TextView)mContentView.findViewById(R.id.text_web_view_title);
        mTextDate = (TextView)mContentView.findViewById(R.id.text_details_date);
        mTextCollect = (TextView)mContentView.findViewById(R.id.text_details_collect);
        mTextShare = (TextView)mContentView.findViewById(R.id.text_details_share);
        mWebView = (WebView)mContentView.findViewById(R.id.web_view);
    }

    @Override
    public void setListener() {
        mTextCollect.setOnClickListener(this);
        mTextShare.setOnClickListener(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUseWideViewPort(true);//關鍵點
        webSettings.setLoadWithOverviewMode(true);
//        mWebView.setInitialScale(39);
//        mWebView.setWebViewClient(new HelloWebViewClient());
    }

    @Override
    public void processLogic() {
        String title = "科普头条";
        switch (getArguments().getString("type")) {
            case "1":
                title = "科普头条";
                break;
            case "2":
                title = "科普讲堂";
                break;
            case "3":
                title = "科普生活";
                break;
            case "4":
                title = "科普视频";
                break;
            case "5":
                title = "科普公园";
                break;
        }
        mActivity.setFragmentTitle(title);
        Map<String, String> map = new HashMap<>();
        map.put("type", getArguments().getString("type"));
        map.put("discoveryId", getArguments().getString("id"));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_DISCOVER_CONTENT, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    boolean flag = root.getBoolean("flag");
                    if (flag) {
                        JSONObject results = root.getJSONObject("results");
                        content = results.getString("content");
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(results.getLong("time")));
                        titles = results.getString("title");
                        mTextTitle.setText(titles);
                        mTextDate.setText(date);
                        mWebView.loadDataWithBaseURL("about:blank", content, "text/html", "utf-8", null);
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
            case R.id.text_details_collect:
                if (SPHandler.getUserIsLogin(mActivity)) {
                    addCollect();
                } else {
                    ToastUtils.showToast(mActivity, "请先登录！");
                }
                break;
            case R.id.text_details_share:
//                showShare();
                break;
        }
    }

    /**
     * 添加收藏
     */
    private void addCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        map.put("type", getArguments().getString("type"));
        map.put("id", getArguments().getString("id"));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_ADD_COLLECT, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ToastUtils.showToast(mActivity, "收藏成功！");
            }
        });
    }

    /**
     * Web视图
     */
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 显示分享内容
     */
//    private void showShare() {
//        ShareSDK.initSDK(mActivity);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("科普房山");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(titles);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
////        oks.setImagePath(ConstantHolder.PATH_CACHE_IMG + "user_head_img.png");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//        // 启动分享GUI
//        oks.show(mActivity);
//    }
}
