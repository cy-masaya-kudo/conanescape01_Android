package jp.co.cybird.android.compliance;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import jp.co.cybird.android.agreement.AgreementDialog;
import jp.co.cybird.android.agreement.BaseAgreementDialog;
import jp.co.cybird.android.minors.ManagerBase;
import jp.co.cybird.android.minors.MinorsDialogManager;
import jp.co.cybird.android.minors.MinorsPref;
import jp.co.cybird.android.util.Uid;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

import jp.co.cybird.android.conanescape01.R;

/**
 * Created by S.Kamba on 2015/10/27.
 * 親権者問い合わせ関係の共通処理など(cybirdUtility必要)をまとめたもの
 */
public class AgreementUtil {

    public static AgreementDialog newAgreementDialog(Context context) {
        return new AgreementDialog(context, getEulaVersion(context),
                context.getString(R.string.url_agreement), true);
    }

    public static MinorsDialogManager newMinorsManager(Context context, FragmentManager fragmentManager) {
        return new MinorsDialogManager(context, getEulaVersion(context), fragmentManager);
    }

    /**
     * 利用規約バージョン
     */
    public static int getEulaVersion(Context c) {
        return c.getResources().getInteger(R.integer.eula_version);
    }

    /**
     * 年齢確認規約バージョン
     */
    public static int getMinorsEulaVersion(Context c) {
        return c.getResources().getInteger(R.integer.minors_eula_version);
    }

    /**
     * 利用規約ＵＲＬの取得
     *
     * @param c
     * @return
     */
    public static String getAgreementUrl(Context c) {
        return c.getString(R.string.url_agreement);
    }

    /**
     * 通常問い合わせページへのリクエストに付与する年齢確認用のパラメータをPOPGATE暗号化して取得する
     *
     * @param context             　コンテキスト
     * @param minorsDialogManager 　MinorsDialogManager(nullを渡すとminorsパラメータは空になります)
     * @return POPGATE暗号化済みパラメータ(key=xxxの形になっている。？や＆は付いていない)
     */
    public static String getMinorsParamEncrypt(Context context, ManagerBase minorsDialogManager) {
        String uuid = Uid.getCyUserId(context);
        String minorsParams = null;
        if (minorsDialogManager != null) {
            minorsParams = minorsDialogManager.getMinorsRequestParams(uuid);
        }
        StringBuilder sb = new StringBuilder(ManagerBase.getMinorsKeyName()).append("=");
        if (minorsParams != null) {
            sb.append(Codec.encode(minorsParams));
        }
        return sb.toString();
    }

    /**
     * 親権者お問い合わせページへのリクエストを取得する
     *
     * @param context             コンテキスト
     * @param minorsDialogManager MinorsDialogManager(nullを渡すとminorsパラメータは空になります)
     * @return urlとパラメータのセット
     */
    public static String getParentContactRequestEncrypt(Context context, ManagerBase minorsDialogManager) {
        String uuid = Uid.getCyUserId(context);
        String url = ManagerBase.getParentContactUrl(context);
        String minorsParams = null;
        if (minorsDialogManager != null) {
            minorsParams = minorsDialogManager.getMinorsRequestParams(uuid);
        }
        StringBuilder sb = new StringBuilder(url).append("?id=").append(Codec.encode(uuid));
        sb.append("&").append(ManagerBase.getMinorsKeyName()).append("=");
        if (minorsParams != null) {
            sb.append(Codec.encode(minorsParams));
        }
        String request = sb.toString();
        return request;
    }

    /**
     * 同意フラグ、年齢設定などをすべて削除<br>
     *
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    @SuppressWarnings("unchecked")
    public static void clearData(Context context) {

        SharedPreferences pref;
        SharedPreferences.Editor e;

        Class c = null;
        try {
            // 規約同意関連クリア
            c = BaseAgreementDialog.class;
            Class<?>[] types = {Context.class, int.class, String.class, String.class, String.class, String.class};
            Constructor<BaseAgreementDialog> con = c.getDeclaredConstructor(types);
            con.setAccessible(true);
            BaseAgreementDialog obj = con.newInstance(context, 1, "", null, null, null);
            Field f = c.getDeclaredField("PREF_FILE_NAME");
            f.setAccessible(true);
            String prefName = (String) f.get(obj);
            if (prefName != null) {
                pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                e = pref.edit();
                e.clear().commit();
            }

            // 年齢確認関連クリア
            c = MinorsPref.class;
            f = c.getDeclaredField("A");
            f.setAccessible(true);
            prefName = (String) f.get(null);
            if (prefName != null) {
                pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                e = pref.edit();
                e.clear().commit();
            }
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }
}
