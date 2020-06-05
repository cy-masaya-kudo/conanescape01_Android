package jp.co.cybird.android.util;

import android.content.Context;

import com.gency.cybirdid.CybirdCommonUserId;

/**
 * Android 汎用処理クラス
 *
 * @author S.Kamba
 */
public class Uid {

    /***
     * UUIDの取得<br>
     * isDebug=true時は、固定文字列が返ります
     *
     * @param c
     *            　コンテキスト
     * @return UUID文字列
     */
    public static String getCyUserId(Context c) {
        String uuid = null;
        try {
            uuid = CybirdCommonUserId.get(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Debug.logD("UUID="+uuid);
        return uuid;
    }
}
