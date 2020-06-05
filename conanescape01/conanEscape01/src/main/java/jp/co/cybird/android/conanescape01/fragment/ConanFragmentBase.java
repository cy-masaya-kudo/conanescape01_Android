package jp.co.cybird.android.conanescape01.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.util.Debug;

public class ConanFragmentBase extends Fragment {

	boolean isPlayBGM = true;
	boolean isPlaySE = true;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		getSettings();
	}

	/** preferenceに保存された設定を取得 */
	void getSettings() {
		Activity a = getActivity();
		if (a == null) {
			Debug.logD("ConanFragmentBase#getSettings ::: Activity is null. TOO fast to call this method.");
			return;
		}
		SharedPreferences pref = getActivity().getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE);
		isPlayBGM = pref.getBoolean(Common.PREF_KEY_SOUND, true);
		isPlaySE = pref.getBoolean(Common.PREF_KEY_SE, true);
	}

	/** system se play */
	protected void playButtonSE() {
		if (isPlaySE) {
			SoundManager.getInstance().playButtonSE();
		}
	}
}
