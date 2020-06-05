package jp.co.cybird.android.conanescape01.fragment;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.OptionActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Option Sound画面用フラグメント
 * 
 * @author S.Kamba
 * 
 */
public class SoundFragment extends OptionFragmentBase implements
		OnClickListener {

	ImageButton btnSound = null;
	ImageButton btnSe = null;
	boolean isSoundOn = true;
	boolean isSeOn = true;

	@Override
	public String getViewName() {
		return "OptionSound";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sound, container,
				false);
		btnSound = (ImageButton) rootView.findViewById(R.id.btn_sound_onoff);
		btnSound.setOnClickListener(this);
		btnSe = (ImageButton) rootView.findViewById(R.id.btn_se_onoff);
		btnSe.setOnClickListener(this);

		SharedPreferences pref = getActivity().getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE);
		isSoundOn = pref.getBoolean(Common.PREF_KEY_SOUND, true);
		isSeOn = pref.getBoolean(Common.PREF_KEY_SE, true);

		toggleButton(btnSound, isSoundOn);
		toggleButton(btnSe, isSeOn);

		return rootView;
	}

	void saveSettings() {

	}

	void savePreference() {
		// 設定を保存
		SharedPreferences.Editor e = getActivity().getSharedPreferences(
				Common.TAG, Context.MODE_PRIVATE).edit();
		e.putBoolean(Common.PREF_KEY_SOUND, isSoundOn);
		e.putBoolean(Common.PREF_KEY_SE, isSeOn);
		e.commit();

		// activityの保存内容も変更
		OptionActivity a = (OptionActivity) getActivity();
		a.getSettings();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_sound_onoff:
			isSoundOn = !isSoundOn;
			toggleButton(btnSound, isSoundOn);
			break;
		case R.id.btn_se_onoff:
			isSeOn = !isSeOn;
			toggleButton(btnSe, isSeOn);
			break;
		}
		savePreference();

		OptionActivity a = (OptionActivity) getActivity();
		if (isSoundOn) {
			a.playBGM();
		} else {
			a.pauseBGM();
		}
	}

	/** On/Offボタンをトグル */
	private void toggleButton(ImageButton button, boolean flag) {
		if (flag) {
			button.setImageResource(R.drawable.btn_on);
		} else {
			button.setImageResource(R.drawable.btn_off);
		}
	}
}
