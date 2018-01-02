package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 意见排名界面
 * Created by itstrong on 2016/6/8.
 */
public class CommentFragment extends BaseFragment implements View.OnClickListener {

    private Button btnSubmit;
    private EditText editComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_comment, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        btnSubmit = (Button)mContentView.findViewById(R.id.btn_comment_submit);
        editComment = (EditText)mContentView.findViewById(R.id.edit_comment_content);
    }

    @Override
    public void setListener() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("意见和评论");
    }

    @Override
    public void onClick(View v) {
        String comment = editComment.getText().toString();
        if (comment.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入您的意见");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        map.put("content", comment);
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_USER_FEEDBACK, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ToastUtils.showToast(mActivity, "您的意见已提交！");
                mActivity.switchFragmentPage(mActivity.SWITCH_TAB_MY);
            }
        });
    }
}
