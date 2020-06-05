package jp.co.cybird.android.escape.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity;
import jp.co.cybird.android.conanescape01.gui.ConanActivityBase;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.util.Debug;

public class BaseDialogFragment extends DialogFragment {
	boolean isPlaySE = true;
	boolean isPlayBGM = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSettings();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected void getSettings() {
		SharedPreferences pref = getActivity().getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE);
		isPlaySE = pref.getBoolean(Common.PREF_KEY_SE, true);
		isPlayBGM = pref.getBoolean(Common.PREF_KEY_SOUND, true);
	}

	public void playBGM() {
		if (isPlayBGM) {
			SoundManager.getInstance().startBGM();
		}
	}

	public void pauseBGM() {
		if (isPlayBGM) {
			SoundManager.getInstance().pauseBGM();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Debug.logD("DialogFragment:onDismiss");
		Activity a = getActivity();

		if (a instanceof ConanActivityBase) {
			ConanActivityBase g = (ConanActivityBase) a;
			g.setDoingOther(false);
			g.setStopBGM(true);
		} else if (a instanceof BillingBaseActivity) {
			BillingBaseActivity g = (BillingBaseActivity) a;
			g.setDoingOther(false);
			g.setStopBGM(true);
		}
		super.onDismiss(dialog);
	}

	@Override
	public void onResume() {
		Debug.logD("DialogFragment:onResume");
		if (isPlayBGM)
			playBGM();
		super.onResume();
	}

	@Override
	public void onPause() {
		Debug.logD("DialogFragment:onPause");
		Activity a = getActivity();

		boolean doingOther = false;

		if (a instanceof ConanActivityBase) {
			ConanActivityBase g = (ConanActivityBase) a;
			doingOther = g.getDoingOther();
		} else if (a instanceof BillingBaseActivity) {
			BillingBaseActivity g = (BillingBaseActivity) a;
			doingOther = g.getDoingOther();
		}

		if (doingOther) {
			// ダイアログを閉じないで非表示になった
			if (isPlayBGM)
				pauseBGM();
		} else {
			if (a instanceof ConanActivityBase) {
				ConanActivityBase g = (ConanActivityBase) a;
				g.setDoingOther(false);
				g.setStopBGM(true);

			} else if (a instanceof BillingBaseActivity) {
				BillingBaseActivity g = (BillingBaseActivity) getActivity();
				g.setDoingOther(false);
				g.setStopBGM(true);
			}
		}
		super.onPause();
	}
}
