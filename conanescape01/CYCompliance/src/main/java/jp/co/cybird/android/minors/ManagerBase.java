/**
 * Copyright (c) 2015年 CYBIRD Co., Ltd.
 */

package jp.co.cybird.android.minors;

import android.content.Context;

import java.util.Calendar;

import jp.co.cybird.android.compliance.R;

/**
 * MinorsDialogManagerのベースクラス
 */
public abstract class ManagerBase implements MinorsDialogListener.OnAgreeListener, MinorsDialogListener.OnDeclineListener, MinorsDialogListener.OnCancelListener, MinorsDialogListener.OnDismissListener {
    protected MinorsDialogListener.OnAgreeListener mOnAgreeListener = null;
    protected MinorsDialogListener.OnDeclineListener mOnDeclineListener = null;
    protected MinorsDialogListener.OnCancelListener mOnCancelListener = null;
    protected MinorsDialogListener.OnDismissListener mOnDismissListener = null;

    protected Context mContext = null;

    protected int mEulaVer = 0;

    protected Calendar mCurrentCalendar = null;

    /**
     * 年齢入力済み、かつ親権者同意済みかどうか
     * @return true：年齢入力済み、かつ親権者同意済み、false：trueの条件以外の場合
     */
    public boolean isAgreement(){
        return MinorsPref.sharedInstance(mContext).isAgeRegist(mEulaVer)
                && MinorsPref.sharedInstance(mContext).isMinorAgreed(mEulaVer);
    }

    /**
     * 年齢入力済みかどうか
     * @return true：年齢入力済み、false：trueの条件以外の場合
     */
    public boolean isAgeRegist() {
        return MinorsPref.sharedInstance(mContext).isAgeRegist(mEulaVer);
    }

    /**
     * 親権者同意済みかどうか
     * @return true：親権者同意済み、false：trueの条件以外の場合
     */
    public boolean isMinorAgreed() {
        return MinorsPref.sharedInstance(mContext).isMinorAgreed(mEulaVer);
    }

    /**
     *
     * @return 生まれ年 ※まだ同意が取れていなかったら-1を返す
     */
    public int getBirthYear(){
        return MinorsPref.sharedInstance(mContext).getBirthYear();
    }

    /**
     *
     * @return 生まれ月 ※まだ同意が取れていなかったら-1を返す
     */
    public int getBirthMonth(){
        return  MinorsPref.sharedInstance(mContext).getBirthMonth();
    }

    /**
     *
     * @return 年齢入力日 ※まだ同意が取れていなかったらnullを返す
     */
    public String getAgeRegistDate(){
        return MinorsPref.sharedInstance(mContext).getAgeRegistDate();
    }

    /**
     *
     * @return 親権者同意日 ※まだ同意が取れていなかったらnullを返す
     */
    public String getMinorAgreementDate(){
        return MinorsPref.sharedInstance(mContext).getMinorAgreementDate();
    }

    /**
     *
     * @return 未成年か否か ※まだ同意が得られていないなどであればtrue(未成年者扱い)
     */
    public abstract boolean isMinor(Context context, Calendar currentCalendar);

    /**
     * Managerへ設定したカレンダーを取得できる
     * @return Managerへ設定したカレンダー
     */
    public Calendar getCurrentCalendar() {
        return mCurrentCalendar;
    }

    /**
     *
     * @param mEulaVer
     */
    public abstract void setEulaVer(int mEulaVer);

    /**
     * 親権者からの問合せページURL取得
     */
    public static String getParentContactUrl(Context context) {
        return context.getString(R.string.url_parent_contact);
    }

    /**
     * リクエストパラーメータ平文で取得(POPGATE暗号化必須だが、cybirdUtilityを含めたくないので平文取得のみ)
     *
     * @param uuid アプリで取得したuuid(こちらもcybirdUtilityを含めたくないのでアプリで取得して渡す)
     */
    public String getMinorsRequestParams(String uuid) {
        return MinorSender.getMinorsParams(mContext, this, uuid);
    }

    /**
     * リクエストパラメータのキー名称(このキーでパラメータに追加する)<br>
     * 正式な形は、?uid=[popgate暗号化済みUUID]&minor=[popgate暗号化済みminorsパラメータ]
     */
    public static String getMinorsKeyName() {
        return "minor";
    }

}
