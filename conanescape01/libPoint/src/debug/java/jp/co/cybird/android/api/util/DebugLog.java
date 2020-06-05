package jp.co.cybird.android.api.util;


import android.util.Log;

public class DebugLog {

	static final boolean isDebug = true;
	static final String TAG = "LibPoint";

	public static void d(String s) {
		if (isDebug) {
			 Log.d(TAG, s);
		}
	}

	public static void e(String s) {
		if (isDebug) {
			 Log.e(TAG, s);
		}
	}

	public static void i(String s) {
		if (isDebug) {
			 Log.i(TAG, s);
		}
	}

	public static void w(String s) {
		if (isDebug) {
			 Log.w(TAG, s);
		}
	}
}
