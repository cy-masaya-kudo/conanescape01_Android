package jp.co.cybird.android.util;

import android.util.Log;

import jp.co.cybird.android.conanescape01.Common;

/**
 * Android 汎用処理クラス
 *
 * @author S.Kamba
 */
public class Debug {


    /**
     * デバッグフラグ:本番はfalseにすること
     */
    public static final boolean isDebug = false;
    /**
     * 当たり判定表示フラグ：本番はfalseにすること
     */
    public static final boolean showCollision = false;
    /**
     * assetローカルフォルダデータのzip暗号化フラグ:本番はtrueにすること
     */
    public static final boolean isAssetEncrypt = true;
    /**
     * デバッグ用ステージ購入済みフラグ:本番はfalseにすること
     */
    public static final boolean isDebugStagePurchase = true;
    /**
     * デバッグ用ヒント購入可能フラグ:本番はfalseにすること
     */
    public static final boolean isDebugHint10Enable = true;

    /**
     * ログ出力(isDebug=falseの場合は出力されません)
     *
     * @param s ログメッセージ
     */
    public static void logD(String s) {
        Log.d(Common.TAG, s); // リリース時コメントアウトすること
    }
}
