package jp.co.cybird.android.compliance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import jp.co.cybird.android.conanescape01.R;

/**
 * Created by S.Kamba on 2015/10/27.
 * 問い合わせ先選択ダイアログ<br>
 * 必ずnewInstanceメソッドで実体化して下さい
 */
public class DialogContactSelect extends DialogFragment {
    public static final int REQUEST_CODE = 100;
    public static final String TAG = "ContactSelect";

    public interface OnContactSelectListener {
        /**
         * 問い合わせ先選択コールバック
         *
         * @param isNormalContact 通常問い合わせの場合true/falseは親権者用問い合わせ
         */
        void onContactSelected(boolean isNormalContact);
    }

    /**
     * 実体化
     *
     * @param fragment 呼び出し元のフラグメント。OnContactSelectListenerを実装していること。
     * @return　DialogContactSelectオブジェクト
     */
    public static DialogContactSelect newInstance(Fragment fragment) {
        if (fragment == null || !(fragment instanceof OnContactSelectListener)) {
            return null;
        }
        DialogContactSelect d = new DialogContactSelect();
        d.setTargetFragment(fragment, REQUEST_CODE);
        return d;
    }

    /**
     * 実体化
     *
     * @param activity 呼び出し元のActivity。OnContactSelectListenerを実装していること。
     * @return　DialogContactSelectオブジェクト
     */
    public static DialogContactSelect newInstance(Activity activity) {
        if (activity == null || !(activity instanceof OnContactSelectListener)) {
            return null;
        }
        DialogContactSelect d = new DialogContactSelect();
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String items[] = getResources().getStringArray(R.array.contacts);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
                Fragment fragment = getTargetFragment();
                if (fragment instanceof OnContactSelectListener) {
                    ((OnContactSelectListener) fragment).onContactSelected(i == 0);
                }
                Activity activity = getActivity();
                if (activity instanceof OnContactSelectListener) {
                    ((OnContactSelectListener) activity).onContactSelected(i == 0);
                }
            }
        });
        return builder.create();
    }
}
