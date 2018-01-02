package com.itstrong.popularscience.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by itstrong on 2016/6/13.
 */
public class ToastUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
