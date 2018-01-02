package com.itstrong.popularscience.utils;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by itstrong on 2016/6/4.
 */
public class SPHandler {

    private static final String SP_NAME = "sp_name";
    /** 存储用户是否登录 */
    private static final String USER_IS_LOGIN = "user_is_login";
    /** 存储用户是否签到 */
    private static final String USER_IS_SIGN_IN = "user_is_sign_in";
    /** 存储用户是否开启签到提醒 */
    private static final String USER_IS_SIGN_IN_REMIND = "user_is_sign_in_remind";
    /** 存储今日是否提醒签到 */
    private static final String SP_IS_REMIND_TODAY = "sp_is_remind_today";
    /** 存储用户名 */
    private static final String USER_NAME = "user_name";
    /** 存储用户ID */
    private static final String USER_ID = "user_id";
    /** 存储已下载视频信息 */
    private static final String SP_VIDEO_INFO = "sp_video_info";

    public static void putUserIsLogin(Context context, Boolean isLogin) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(USER_IS_LOGIN, isLogin).commit();
    }

    public static boolean getUserIsLogin(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(USER_IS_LOGIN, false);
    }

    public static void putUserIsSignIn(Context context, int day) {
        String value = getUserIsSignIn(context);
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(USER_IS_SIGN_IN, value + day + ",").commit();
    }

    public static String getUserIsSignIn(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(USER_IS_SIGN_IN, "");
    }

    public static void putUserIsSignInRemind(Context context, Boolean isRemind) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(USER_IS_SIGN_IN_REMIND, isRemind).commit();
    }

    public static boolean getUserIsSignInRemind(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(USER_IS_SIGN_IN_REMIND, false);
    }

    public static void putIsRemindToday(Context context, String isRemind) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(SP_IS_REMIND_TODAY, isRemind).commit();
    }

    public static String getIsRemindToday(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SP_IS_REMIND_TODAY, "");
    }

    public static void putUserName(Context context, String userName) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(USER_NAME, userName).commit();
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(USER_NAME, "");
    }

    public static void putUserId(Context context, String userId) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(USER_ID, userId).commit();
    }

    public static String getUserId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(USER_ID, "");
    }

    public static void putVideoInfo(Context context, String id) {
        Set<String> info = getVideoInfo(context);
        if (info == null) {
            info = new HashSet<>();
        }
        info.add(id);
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putStringSet(SP_VIDEO_INFO, info).commit();
    }

    public static Set<String> getVideoInfo(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getStringSet(SP_VIDEO_INFO, null);
    }
}
