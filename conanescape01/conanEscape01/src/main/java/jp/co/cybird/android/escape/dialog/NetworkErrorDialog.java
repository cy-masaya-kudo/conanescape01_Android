package jp.co.cybird.android.escape.dialog;

import jp.co.cybird.android.conanescape01.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class NetworkErrorDialog extends BaseDialogFragment {

	public interface OnDismissListener {
		public void onDismiss();
	}

	OnDismissListener listener = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle(R.string.err_title).setMessage(R.string.err_network)
				.setNegativeButton(R.string.close, null);

		return b.create();
	}

	public void setOnDismissListener(OnDismissListener l) {
		listener = l;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (listener != null) {
			listener.onDismiss();
		}
	}
}
