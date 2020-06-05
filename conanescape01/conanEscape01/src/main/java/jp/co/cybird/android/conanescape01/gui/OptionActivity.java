package jp.co.cybird.android.conanescape01.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.fragment.OptionMenuFragment;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.util.Debug;

public class OptionActivity extends ConanActivityBase {
	boolean fromGameActivity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		Intent intent = getIntent();

		if (intent != null) {
			if (intent.getBooleanExtra(Common.KEY_FROM_GAGME, false)) {
				// ゲームから呼ばれた
				fromGameActivity = true;
			}
		}
		ImageButton b = (ImageButton) findViewById(R.id.btn_top);
		if (fromGameActivity) {
			b.setVisibility(View.VISIBLE);
		} else {
			b.setVisibility(View.INVISIBLE);
		}
		setFirstFragment(savedInstanceState);
		showBackButton(false);
	}

	protected void setFirstFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new OptionMenuFragment()).commit();
		}

	}

	protected String getScreenName() {
		return "Option";
	}

	@Override
	protected void onStart() {
		super.onStart();
		// GoogleAnalytics
		Tracking.sendView(getScreenName());
	}

	/**
	 * フラグメント遷移
	 * 
	 * @param f
	 */
	public void move(Fragment f) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, f, null);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
	}

	/**
	 * 戻るボタンの表示制御
	 * 
	 * @param enabled
	 */
	public void showBackButton(boolean enabled) {
		ImageButton closeBtn = (ImageButton) findViewById(R.id.btn_close);
		ImageButton backBtn = (ImageButton) findViewById(R.id.btn_back);

		if (enabled) {
			closeBtn.setVisibility(View.INVISIBLE);
			backBtn.setVisibility(View.VISIBLE);
		} else {
			closeBtn.setVisibility(View.VISIBLE);
			backBtn.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		int c = getFragmentManager().getBackStackEntryCount();
		if (c == 0) {
			setDoingFlagClearParent();
		}
		stopBGM = false;
		super.onBackPressed();
	}

	private void setDoingFlagClearParent() {
		if (fromGameActivity) {
			GameActivity g = GameActivity.getInstance();
			g.setDoingOther(false);
		} else {
			MainActivity a = MainActivity.getInstance();
			a.setDoingOther(false);
		}

	}

	/**
	 * ボタンクリック処理
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			playButtonSE();
			FragmentManager fm = getFragmentManager();
			fm.popBackStack();
			break;
		case R.id.btn_close:
			playCloseSE();
			stopBGM = false;
			setDoingFlagClearParent();
			finish();
			break;
		}
	}

	public void onTop(View v) {
		if (!fromGameActivity)
			return;

		if (isPlaySE)
			SoundManager.getInstance().playButtonSE();

		stopBGM = false;
		doingOther = true;
		BackTopAlertFragment f = new BackTopAlertFragment();
		f.show(getFragmentManager(), "back");
	}

	/**
	 * トップへ戻る確認ダイアログ
	 */
	public static final class BackTopAlertFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
			b.setMessage(R.string.backto_top);
			b.setNegativeButton(android.R.string.cancel, null);
			b.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							OptionActivity activity = (OptionActivity) getActivity();
							activity.setStopBGM(false);
							activity.setResult(Common.RESULT_BACKTOTOP);
							activity.finish();
						}
					});
			return b.create();
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			Debug.logD("DialogFragment:onDismiss");
			OptionActivity activity = (OptionActivity) getActivity();
			activity.doingOther = false;
			super.onDismiss(dialog);
		}

		@Override
		public void onResume() {
			Debug.logD("DialogFragment:onResume");
			OptionActivity activity = (OptionActivity) getActivity();
			if (activity.isPlayBGM)
				activity.playBGM();
			super.onResume();
		}

		@Override
		public void onPause() {
			Debug.logD("DialogFragment:onPause");
			OptionActivity activity = (OptionActivity) getActivity();
			if (activity.doingOther) {
				// ダイアログを閉じないで非表示になった
				if (activity.isPlayBGM)
					activity.pauseBGM();
			} else {
				activity.setDoingOther(false);
				activity.setStopBGM(true);
			}
			super.onPause();
		}
	}

	public void playBGM() {
		if (isPlayBGM) {
			SoundManager.getInstance().startBGM();
		}
	}

	public void pauseBGM() {
		SoundManager.getInstance().pauseBGM();
	}

}
