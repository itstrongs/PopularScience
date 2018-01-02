package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册页面
 * Created by itstrong on 2016/6/5.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = "RegisterFragment";

    private Button btnUserSubmit;
    private EditText editUserName;
    private EditText editUserPass;
    private EditText editUserPassAgain;
    private EditText editUserNickname;
    private EditText editUserEmail;

    private String userName;
    private String userPass;
    private String userPassAgain;
    private String userNickname;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_register, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        btnUserSubmit = (Button)mContentView.findViewById(R.id.btn_register_submit);
        editUserName = (EditText)mContentView.findViewById(R.id.edit_user_name);
        editUserPass = (EditText)mContentView.findViewById(R.id.edit_user_pass);
        editUserPassAgain = (EditText)mContentView.findViewById(R.id.edit_user_pass_again);
        editUserNickname = (EditText)mContentView.findViewById(R.id.edit_user_nickname);
        editUserEmail = (EditText)mContentView.findViewById(R.id.edit_user_email);
    }

    @Override
    public void setListener() {
        btnUserSubmit.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("用户注册");
    }

    @Override
    public void onClick(View v) {
        if (validityCheck()) {
            userRegisterSubmit();
        }
    }

    /**
     * 用户输入合法性检查
     * @return
     */
    private boolean validityCheck() {
        userName = editUserName.getText().toString();
        userPass = editUserPass.getText().toString();
        userPassAgain = editUserPassAgain.getText().toString();
        userNickname = editUserNickname.getText().toString();
        userEmail = editUserEmail.getText().toString();
        if (userName.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入用户名");
            return false;
        } else if (userPass.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入密码");
            return false;
        }
        else if (!userPass.equals(userPassAgain)) {
            ToastUtils.showToast(mActivity, "两次密码输入不一致");
            return false;
        }
        else if (userNickname.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入昵称");
            return false;
        }
        else if (userEmail.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入邮箱");
            return false;
        }
        return true;
    }

    /**
     * 提交用户注册
     */
    private void userRegisterSubmit() {
        OkHttpUtils.post()
                .url(ConstantHolder.URL_USER_REGISTER)
                .addParams("userName", userName)
                .addParams("password", userPass)
                .addParams("email", userEmail)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d(TAG, "onFailure:" + request.body().toString());
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean flag = jsonObject.getBoolean("flag");
                    Log.d(TAG, "flag:" + flag);
                    if (flag) {
                        ToastUtils.showToast(mActivity, "注册成功！");
                        SPHandler.putUserName(mActivity, userName);
                        SPHandler.putUserIsLogin(mActivity, true);
                        login();
                        mActivity.switchFragmentPage(mActivity.SWITCH_TAB_MY);
                    } else {
                        ToastUtils.showToast(mActivity, "注册失败！" + jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login() {
        Map<String, String> map = new HashMap<>();
        map.put("UserName", userName);
        map.put("password", userPass);
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_USER_LOGIN, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
                        JSONObject results = jsonObject.getJSONObject("results");
                        String userId = results.getString("id");
                        SPHandler.putUserId(mActivity, userId);
                        SPHandler.putUserIsLogin(mActivity, true);
                    } else {
                        LogUtils.d("登录失败！" + jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
