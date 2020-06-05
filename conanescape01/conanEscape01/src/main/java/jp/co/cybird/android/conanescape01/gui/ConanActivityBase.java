package jp.co.cybird.android.conanescape01.gui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.util.Debug;

public class ConanActivityBase extends Activity {

	/** BGM再生フラグ */
	boolean isPlayBGM = true;
	/** SE再生フラグ */
	boolean isPlaySE = true;
	/** BGM停止フラグ */
	boolean stopBGM = true;
	/** 他の画面呼び出し中フラグ */
	boolean doingOther = false;

	@Override
	protected void onStart() {
		getSettings();

		super.onStart();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Debug.logD(getClass().getSimpleName()
                + ":onWindowFocusChanged:hasFocus=" + hasFocus + " stopBGM="
                + stopBGM + " doingOther=" + doingOther);
		// onResumeだとスクリーン点灯からのロック画面表示時にきてしまう機種がある
		// ↑4.1以降の修正らしい
		// のでここでやる
		if (hasFocus) {
			if (!doingOther) {
				// 再生再開
				if (isPlayBGM && stopBGM) {
					SoundManager.getInstance().startBGM();
				}
				stopBGM = true;
			}
		} else {
			if (!doingOther) {
				// BGM再生停止
				if (isPlayBGM && stopBGM) {
					SoundManager.getInstance().pauseBGM();
				}
			}
		}
	}

	/** preferenceに保存された設定を取得 */
	public void getSettings() {
		SharedPreferences pref = getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE);
		isPlayBGM = pref.getBoolean(Common.PREF_KEY_SOUND, true);
		isPlaySE = pref.getBoolean(Common.PREF_KEY_SE, true);
	}

	/** SE再生フラグを取得 */
	public boolean isPlaySE() {
		return isPlaySE;
	}

	/** BGM再生フラグを取得 */
	public boolean isPlayBGM() {
		return isPlayBGM;
	}

	/** ボタンSE再生 */
	public void playButtonSE() {
		if (isPlaySE) {
			SoundManager.getInstance().playButtonSE();
		}
	}

	/** closeSE再生 */
	public void playCloseSE() {
		if (isPlaySE) {
			SoundManager.getInstance().playCloseSE();
		}
	}

	/** FocusOut/In時BGMをstop/playするかのフラグをセット */
	public void setStopBGM(boolean flag) {
		stopBGM = flag;
	}

	/** 他の画面を実行中フラグセット */
	public void setDoingOther(boolean flag) {
		doingOther = flag;
	}

	/** 他の画面を実行中フラグ */
	public boolean getDoingOther() {
		return doingOther;
	}
}
