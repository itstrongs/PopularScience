package com.itstrong.popularscience.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.book.BookFragment;
import com.itstrong.popularscience.fragment.book.BookListFragment;
import com.itstrong.popularscience.fragment.book.ReadingFragment;
import com.itstrong.popularscience.fragment.competition.CompetitionFragment;
import com.itstrong.popularscience.fragment.competition.EverydayFragment;
import com.itstrong.popularscience.fragment.competition.GameFragment;
import com.itstrong.popularscience.fragment.competition.GameOverFragment;
import com.itstrong.popularscience.fragment.competition.KnowledgeFragment;
import com.itstrong.popularscience.fragment.discover.DetailsFragment;
import com.itstrong.popularscience.fragment.discover.DiscoverFragment;
import com.itstrong.popularscience.fragment.discover.HandLineFragment;
import com.itstrong.popularscience.fragment.discover.LectureFragment;
import com.itstrong.popularscience.fragment.discover.LifeFragment;
import com.itstrong.popularscience.fragment.discover.ParkFragment;
import com.itstrong.popularscience.fragment.discover.VideoFragment;
import com.itstrong.popularscience.fragment.mine.CollectFragment;
import com.itstrong.popularscience.fragment.mine.CommentFragment;
import com.itstrong.popularscience.fragment.mine.LoginFragment;
import com.itstrong.popularscience.fragment.mine.MineFragment;
import com.itstrong.popularscience.fragment.mine.RankFragment;
import com.itstrong.popularscience.fragment.mine.RegisterFragment;
import com.itstrong.popularscience.fragment.mine.SettingFragment;
import com.itstrong.popularscience.fragment.mine.SignInFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

//    @InjectView(R.id.text_discover) //控件对应的ID
//            TextView textDiscover;

    private TextView textDiscover;      //发现tab
    private TextView textRead;          //阅读tab
    private TextView textContest;       //竞赛tab
    private TextView textMy;            //我的tab
    private TextView textTitle;         //标题
    private TextView btnHomeBack;       //返回按钮
    private ImageView imgHomeShare;     //分享按钮

    public static final int SWITCH_TAB_DISCOVER = 1;
    public static final int SWITCH_DISCOVER_HEAD_LINE = 10;
    public static final int SWITCH_DISCOVER_LECTURE = 11;
    public static final int SWITCH_DISCOVER_LIFE = 12;
    public static final int SWITCH_DISCOVER_VIDEO = 13;
    public static final int SWITCH_DISCOVER_PARK = 14;
    public static final int SWITCH_DISCOVER_DISPLAY = 15;

    public static final int FRAGMENT_BOOK = 2;
    public static final int FRAGMENT_BOOK_LIST = 21;
    public static final int FRAGMENT_BOOK_READING = 22;

    public static final int FRAGMENT_COMPETITION = 3;
    public static final int FRAGMENT_INTERACT_GAME = 30;
    public static final int FRAGMENT_KNOW_COMPETITION = 31;
    public static final int FRAGMENT_DAY_ANSWER = 32;
    public static final int FRAGMENT_GAME_OVER = 33;

    public static final int SWITCH_TAB_MY = 4;
    public static final int SWITCH_MY_RANK = 40;
    public static final int SWITCH_MY_SETTING = 41;
    public static final int SWITCH_MY_COMMENT = 42;
    public static final int SWITCH_MY_COLLECT = 43;
    public static final int SWITCH_MY_REGISTER = 44;    //注册页面
    public static final int SWITCH_SIGN_IN = 45;        //签到页面
    public static final int SWITCH_MY_LOGIN = 46;       //登录页面

    public static final int SWITCH_BACK_HOME = 5;

    private Context mContext;
    private FragmentManager fragmentManager;
    private Fragment fragment = null; //当前Fragment
    private boolean isFirstBackEvent = false; //是否是第一次按退出
    private int currentTabState = SWITCH_TAB_DISCOVER; //纪录当前标签状态
    private boolean isHomeFragment = true;  //是否是首页fragment

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        this.mContext = this;
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.parseColor(ConstantHolder.APP_COLOR_STYLE));
        }
        fragmentManager = getFragmentManager();
        findViewById();
        switchFragmentPage(SWITCH_TAB_DISCOVER);
    }

    private void findViewById() {
        textDiscover = (TextView) findViewById(R.id.text_discover);
        textRead = (TextView) findViewById(R.id.text_read);
        textContest = (TextView) findViewById(R.id.text_contest);
        textMy = (TextView) findViewById(R.id.text_my);
        textTitle = (TextView)findViewById(R.id.text_home_title);
        btnHomeBack = (TextView) findViewById(R.id.btn_home_back);
        imgHomeShare = (ImageView) findViewById(R.id.img_home_share);
        textDiscover.setOnClickListener(this);
        textRead.setOnClickListener(this);
        textContest.setOnClickListener(this);
        textMy.setOnClickListener(this);
        btnHomeBack.setOnClickListener(this);
        imgHomeShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_discover:
                switchFragmentPage(SWITCH_TAB_DISCOVER);
                break;
            case R.id.text_read:
                switchFragmentPage(FRAGMENT_BOOK);
                break;
            case R.id.text_contest:
                switchFragmentPage(FRAGMENT_COMPETITION);
                break;
            case R.id.text_my:
                switchFragmentPage(SWITCH_TAB_MY);
                break;
            case R.id.btn_home_back:
                if (!isHomeFragment) {
                    switchFragmentPage(SWITCH_BACK_HOME);
                }
                break;
            case R.id.img_home_share:
                if (SPHandler.getUserIsLogin(this)) {
                    switchFragmentPage(SWITCH_MY_SETTING);
                } else {
                    switchFragmentPage(SWITCH_MY_LOGIN);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFirstBackEvent) {
            isFirstBackEvent = true;
            ToastUtils.showToast(mContext, "再按一次返回键退出");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFirstBackEvent = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    public void switchFragment(Fragment fragment, String TAG) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_tab_content, fragment, TAG);
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    public void switchFragmentPage(int index, String TAG) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        isHomeFragment = false;
        switch (index) {
            case FRAGMENT_BOOK_READING:
                fragment = ReadingFragment.newInstance(TAG);
                break;
        }
        setCurrentTabState();
        transaction.replace(R.id.layout_tab_content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //过渡动画
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void switchFragmentPage(int index, String TAG, String type) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        isHomeFragment = false;
        switch (index) {
            case SWITCH_DISCOVER_DISPLAY:
                fragment = DetailsFragment.newInstance(type, TAG);
                break;
        }
        setCurrentTabState();
        transaction.replace(R.id.layout_tab_content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //过渡动画
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 切换fragment页面
     * @param index
     */
    public void switchFragmentPage(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        isHomeFragment = false;
        switch (index) {
            /** 发现页面 */
            case SWITCH_TAB_DISCOVER:
                isHomeFragment = true;
                currentTabState = SWITCH_TAB_DISCOVER;
                fragment = new DiscoverFragment();
                break;
            case SWITCH_DISCOVER_HEAD_LINE:
                fragment = new HandLineFragment();
                break;
            case SWITCH_DISCOVER_LECTURE:
                fragment = new LectureFragment();
                break;
            case SWITCH_DISCOVER_LIFE:
                fragment = new LifeFragment();
                break;
            case SWITCH_DISCOVER_VIDEO:
                fragment = new VideoFragment();
                break;
            case SWITCH_DISCOVER_PARK:
                fragment = new ParkFragment();
                break;

            /** 阅读页面 */
            case FRAGMENT_BOOK:
                isHomeFragment = true;
                currentTabState = FRAGMENT_BOOK;
                fragment = new BookFragment();
                break;
            case FRAGMENT_BOOK_LIST:
                fragment = new BookListFragment();
                break;

            /** 竞赛页面 */
            case FRAGMENT_COMPETITION:
                isHomeFragment = true;
                currentTabState = FRAGMENT_COMPETITION;
                if (isUserLogin()) {
                    fragment = new CompetitionFragment();
                } else {
                    fragment = LoginFragment.newInstance(FRAGMENT_COMPETITION);
                }
                break;
            case FRAGMENT_INTERACT_GAME:
//                fragment = AnswerFragment.newInstance(1);
                fragment = new GameFragment();
                break;
            case FRAGMENT_KNOW_COMPETITION:
//                fragment = AnswerFragment.newInstance(2);
                fragment = new KnowledgeFragment();
                break;
            case FRAGMENT_DAY_ANSWER:
//                fragment = AnswerFragment.newInstance(3);
                fragment = new EverydayFragment();
                break;
            case FRAGMENT_GAME_OVER:
                fragment = new GameOverFragment();
                break;

            /** 我的页面 */
            case SWITCH_TAB_MY:
                isHomeFragment = true;
                currentTabState = SWITCH_TAB_MY;
                if (isUserLogin()) {
                    fragment = new MineFragment();
                } else {
                    fragment = LoginFragment.newInstance(FRAGMENT_COMPETITION);
                }
                break;
            case SWITCH_MY_REGISTER:
                fragment = new RegisterFragment();
                break;
            case SWITCH_MY_RANK:
                fragment = new RankFragment();
                break;
            case SWITCH_MY_SETTING:
                fragment = new SettingFragment();
                break;
            case SWITCH_MY_COMMENT:
                fragment = new CommentFragment();
                break;
            case SWITCH_MY_COLLECT:
                fragment = new CollectFragment();
                break;
            case SWITCH_SIGN_IN:
                fragment = new SignInFragment();
                break;
            case SWITCH_MY_LOGIN:
                fragment = new LoginFragment();
                break;

            /** 返回 */
            case SWITCH_BACK_HOME:
                getFragmentManager().popBackStack();
                return;
        }
        if (isHomeFragment) {
            btnHomeBack.setVisibility(View.GONE);
        } else {
            btnHomeBack.setVisibility(View.VISIBLE);
        }
        setCurrentTabState();
        transaction.replace(R.id.layout_tab_content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //过渡动画
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 设置当前tab颜色及点击状态
     */
    private void setCurrentTabState() {
        textDiscover.setSelected(false);
        textRead.setSelected(false);
        textContest.setSelected(false);
        textMy.setSelected(false);
        textDiscover.setTextColor(Color.WHITE);
        textRead.setTextColor(Color.WHITE);
        textContest.setTextColor(Color.WHITE);
        textMy.setTextColor(Color.WHITE);
        switch (currentTabState) {
            case SWITCH_TAB_DISCOVER:
                textDiscover.setSelected(true);
                textDiscover.setTextColor(Color.parseColor(ConstantHolder.APP_COLOR_STYLE));
                break;
            case FRAGMENT_BOOK:
                textRead.setSelected(true);
                textRead.setTextColor(Color.parseColor(ConstantHolder.APP_COLOR_STYLE));
                break;
            case FRAGMENT_COMPETITION:
                textContest.setSelected(true);
                textContest.setTextColor(Color.parseColor(ConstantHolder.APP_COLOR_STYLE));
                break;
            case SWITCH_TAB_MY:
                textMy.setSelected(true);
                textMy.setTextColor(Color.parseColor(ConstantHolder.APP_COLOR_STYLE));
                break;
        }
    }

    /**
     * 判断用户是否登录
     * @return
     */
    private boolean isUserLogin() {
        return SPHandler.getUserIsLogin(mContext);
    }

    /**
     * 设置Fragment标题
     * @param title
     */
    public void setFragmentTitle(String title) {
        textTitle.setText(title);
    }

    /**
     * 设置返回按钮是否可见
     * @param flag
     */
    public void setBtnBackIsInvisible(boolean flag) {
        if (flag) {
            btnHomeBack.setVisibility(View.GONE);
        } else {
            btnHomeBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
//        ShareSDK.stopSDK(this);
        super.onDestroy();
    }
}
