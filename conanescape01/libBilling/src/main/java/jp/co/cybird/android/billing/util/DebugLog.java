package jp.co.cybird.android.billing.util;


public class DebugLog {

    static final boolean isDebug = false;
    static final String TAG = "LibBilling";

    public static void d(String s) {
        if (isDebug) {
            // Log.d(TAG, s);
        }
    }

    public static void e(String s) {
        if (isDebug) {
            // Log.e(TAG, s);
        }
    }

    public static void i(String s) {
        if (isDebug) {
            // Log.i(TAG, s);
        }
    }

    public static void w(String s) {
        if (isDebug) {
            // Log.w(TAG, s);
        }
    }
}
