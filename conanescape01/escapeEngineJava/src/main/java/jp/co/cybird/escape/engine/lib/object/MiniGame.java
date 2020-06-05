package jp.co.cybird.escape.engine.lib.object;

import java.util.Arrays;

import jp.co.cybird.escape.engine.lib.minigame.MiniGameRunner;

/**
 * ミニゲームクラス
 *
 * @author S.Kamba
 *
 */
public class MiniGame extends Node {

	int mType = 0;
	boolean isCleared = false;
	MiniGameRunner mRunner = null;
	String mRestoreData[] = null;

	/** ゲームタイプセット */
	public void setType(int type) {
		mType = type;
	}

	/** クリアフラグセット */
	public void setClearFlag(boolean flag) {
		isCleared = flag;
	}

	/** クリアフラグ取得 */
	public boolean isCleared() {
		return isCleared;
	}

	/** Runnerのセット */
	public void setRunner(MiniGameRunner r) {
		mRunner = r;
	}

	/** 状態保存処理 */
	public String getSaveString() {
		if (mRunner != null)
			return mRunner.getSaveString();
		return null;
	}

	/** 復元用データの取得 */
	public String[] getRestoreData() {
		return mRestoreData;
	}

	/** 復元用データのセット */
	public void setRestoreData(String[] data) {
		mRestoreData = data;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(",").append(isCleared);
		String s = getSaveString();
		if (s != null) {
			sb.append(",").append(s);
		}
		return sb.toString();
	}

	@Override
	public void restore(String[] buf) {
		super.restore(buf);
		if (buf.length > 5) {
			isCleared = buf[5].trim().equals("true") ? true : false;
		}
		if (buf.length > 6) {
			String[] b = Arrays.copyOfRange(buf, 6, buf.length);
			setRestoreData(b);
		}
	}
}
