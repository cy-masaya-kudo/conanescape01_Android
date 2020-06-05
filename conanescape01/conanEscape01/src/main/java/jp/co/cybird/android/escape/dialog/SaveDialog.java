package jp.co.cybird.android.escape.dialog;

import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * セーブ確認ダイアログ
 * 
 * @author S.Kamba
 *
 */
public class SaveDialog extends BaseDialogFragment implements
		DialogInterface.OnClickListener {

	GameManager gm = null;
	EscApplication app = null;
	OnSaveFinishListener listener = null;

	public interface OnSaveFinishListener {
		public void onSaveFinish(boolean backToTop);
	}

	public void setOnSaveFinishListener(OnSaveFinishListener l) {
		listener = l;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		app = (EscApplication) getActivity().getApplication();
		gm = app.getGameManager();

		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setMessage(R.string.alert_save);
		b.setPositiveButton(R.string.alert_save_positive, this);
		b.setNegativeButton(R.string.alert_save_negative, this);
		return b.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		boolean backToTop = false;
		if (which == DialogInterface.BUTTON_POSITIVE) {
			backToTop = true;
		}
		if (listener != null) {
			listener.onSaveFinish(backToTop);
		}
	}

}
