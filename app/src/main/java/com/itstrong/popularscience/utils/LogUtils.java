package com.itstrong.popularscience.utils;

import android.util.Log;

public class LogUtils {

	private static String TAG = "itstrongs";

	public static void d(String msg) {
		Log.d(TAG, msg);
	}

	public static void w(String msg) {
		Log.w(TAG, msg);
	}
}
