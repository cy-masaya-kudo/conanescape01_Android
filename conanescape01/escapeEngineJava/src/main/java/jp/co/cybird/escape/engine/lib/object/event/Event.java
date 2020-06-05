package jp.co.cybird.escape.engine.lib.object.event;

import jp.co.cybird.escape.engine.lib.action.Action;
import jp.co.cybird.escape.engine.lib.object.ImageChanger;

/**
 * イベントシーンクラス<br>
 * オープニングやエンディング用
 * 
 * @author S.Kamba
 * 
 */
public class Event extends ImageChanger {

	/** 保留されているアクション */
	Action mHoldingAction = null;

	/** 終了フラグ */
	boolean isEnded = false;

	/**
	 * 初期化
	 */
	public void init() {
		setActiveImageId(0);
		mHoldingAction = null;
		isEnded = false;
	}

	/** 保留中アクションをセット */
	public void setHoldingAction(Action action) {
		mHoldingAction = action;
	}

	/** 保留中アクションを取得 */
	public Action getHoldingAction() {
		return mHoldingAction;
	}

	/** イベント終了フラグのセット */
	public void setFinishFlag(boolean flag) {
		isEnded = flag;
	}

	/** 終了フラグを取得 */
	public boolean isEnded() {
		return isEnded;
	}
}
