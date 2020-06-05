package jp.co.cybird.android.escape.dialog;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingDialog extends DialogFragment {

	private static final String KEY_MESSAGE = "message";

	private int messageResId = R.string.loading_stage;

	public LoadingDialog() {
	}

	@SuppressWarnings("SameParameterValue")
	static public LoadingDialog newInstance(int messageResId) {
		LoadingDialog d = new LoadingDialog();
		Bundle args = new Bundle();
		args.putInt(KEY_MESSAGE, messageResId);
		d.setArguments(args);
		return d;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());

		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey(KEY_MESSAGE)) {
				messageResId = args.getInt(KEY_MESSAGE);
			}
		}
		dialog.setMessage(getString(messageResId));
		// setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (getActivity() != null) {
		getActivity().setResult(Common.RESULT_LOAD_ERROR);
		getActivity().finish();
		}
		super.onCancel(dialog);
	}
}
