package com.itstrong.popularscience.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.activity.HomeActivity;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录界面
 * Created by itstrong on 2016/6/4.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private Button btnRegister;
    private Button btnLogin;
    private EditText editUserName;
    private EditText editUserPass;

    public static LoginFragment newInstance(int TAG) {
        LoginFragment newFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TAG", TAG);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        btnLogin = (Button)mContentView.findViewById(R.id.btn_user_login);
        btnRegister = (Button)mContentView.findViewById(R.id.btn_user_register);
        editUserName = (EditText)mContentView.findViewById(R.id.edit_user_name);
        editUserPass = (EditText)mContentView.findViewById(R.id.edit_user_pass);
    }

    @Override
    public void setListener() {
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("用户登录");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_login:   //登录
                userLogin();
                break;
            case R.id.btn_user_register:    //注册
                mActivity.switchFragmentPage(mActivity.SWITCH_MY_REGISTER);
                break;
        }
    }

    /**
     * 用户登录
     */
    private void userLogin() {
        final String userName = editUserName.getText().toString();
        String userPass = editUserPass.getText().toString();
        if (userName.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入用户名");
            return;
        } else if (userPass.isEmpty()) {
            ToastUtils.showToast(mActivity, "请输入密码");
            return;
        }
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
                        ToastUtils.showToast(mActivity, "登录成功！");
                        SPHandler.putUserIsLogin(mActivity, true);
                        SPHandler.putUserName(mActivity, userName);
                        if (getArguments().getInt("TAG") == HomeActivity.FRAGMENT_COMPETITION) {
                            mActivity.switchFragmentPage(mActivity.FRAGMENT_COMPETITION);
                        } else if (getArguments().getInt("TAG") == HomeActivity.SWITCH_TAB_MY) {
                            mActivity.switchFragmentPage(mActivity.SWITCH_TAB_MY);
                        }
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
