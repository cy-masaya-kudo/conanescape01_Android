package jp.co.cybird.android.conanescape01;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.util.Debug;

public class EscApplication extends Application {
	GameManager gameManager = null;
	boolean newgame = true;
	ArrayList<Stage> mStageList = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Tracking.init(this);
	}

	public void setGameManager(GameManager m) {
		gameManager = m;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public void setNewGameFlag(boolean flag) {
		newgame = flag;
	}

	public boolean isNewGame() {
		return newgame;
	}

	/**
	 * プリファレンスをチェックして初回起動か確認
	 * 
	 * @return 初回起動ならtrue
	 */
	public boolean checkIsFirstRun() {
		SharedPreferences pref = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		boolean isFirst = pref.getBoolean(Common.PREF_KEY_FIRST, true);
		return isFirst;
	}

	/**
	 * 初回起動済みフラグを更新
	 * 
	 * @param flag
	 */
	public void setFirstRunPreference(boolean flag) {
		SharedPreferences pref = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		SharedPreferences.Editor e = pref.edit();
		e.putBoolean(Common.PREF_KEY_FIRST, flag);
		e.commit();
	}

	/**
	 * ステージ情報リストを保管
	 * 
	 * @param floors
	 */
	public void setStageList(ArrayList<Stage> floors) {
		mStageList = floors;
	}

	/** ステージ情報リストを取得 */
	public ArrayList<Stage> getStageList() {
		if (mStageList == null) {
			mStageList = initStageList();
		}
		return mStageList;
	}

	/**
	 * デフォルトリストの取得
	 * 
	 * @return
	 */
	ArrayList<Stage> initStageList() {
		// 現在のバージョンと最後に起動した時のバージョンを比較して、
		// 異なればファイルの更新を強制する
		int ver = getVersionCode();
		int last_ver = getLastLaunchVersion();
		boolean update = false;
		if (ver != last_ver) {
			update = true;
		}

		if (Debug.isDebug) {
			update = true;
		}

		ArrayList<Stage> list = Stage.initStageList(this);

		for (Stage s : list) {
			s.need_update = update;
		}
		return list;
	}

	/** 現在のアプリバージョンコードを取得 */
	public int getVersionCode() {
		try {
			PackageInfo pkg = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return pkg.versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	/**
	 * 最後に起動した時のバージョンを取得
	 * 
	 * @return version
	 */
	public int getLastLaunchVersion() {
		SharedPreferences pref = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		int last_ver = pref.getInt(Common.PREF_KEY_VERSION, 0);
		return last_ver;
	}

	/**
	 * 最後に起動した時のバージョンを保存
	 */
	public void setLastLaunchVersion(int version) {
		SharedPreferences pref = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		SharedPreferences.Editor e = pref.edit();
		e.putInt(Common.PREF_KEY_VERSION, version);
		e.commit();

	}

	/**
	 * 保存されているStageNoを取得
	 * 
	 * @return StageNo
	 */
	public int getSavedStageNo() {
		SharedPreferences p = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		int savedNo = p.getInt(Common.PREF_KEY_STAGENO, -1);
		return savedNo;
	}

	/**
	 * SaveデータのStageNoを保存
	 * 
	 * @param stageNo
	 */
	public void setSavedStageNo(int stageNo) {
		SharedPreferences pref = getSharedPreferences(Common.TAG, MODE_PRIVATE);
		SharedPreferences.Editor e = pref.edit();
		e.putInt(Common.PREF_KEY_STAGENO, stageNo);
		e.commit();
	}

	/**
	 * 指定のStageNoのステージを返す
	 * 
	 * @param stageNo
	 * @return Stage
	 */
	public Stage getStage(int stageNo) {
		if (mStageList == null)
			return null;
		for (Stage s : mStageList) {
			if (s.stageNo == stageNo)
				return s;
		}
		return null;
	}

}
