package jp.co.cybird.escape.engine.lib.object;

import jp.co.cybird.escape.engine.lib.action.Action;
import jp.co.cybird.escape.engine.lib.condition.Condition;
import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.math.Collision;

/**
 * ゲーム内のオブジェクトを操作するためのコントロールクラス
 *
 * @author S.Kamba
 */
public class Control extends ESCObject {

	/** 操作タイプ */
	public enum ControlType {
		NULL,
		/** クリック */
		CLICK,
		/** タッチダウン */
		TOUCH_DOWN,
		/** タッチアップ */
		TOUCH_UP,
		/** 右へフリック */
		FLICK_RIGHT,
		/** 左へフリック */
		FLICK_LEFT,
		/** 下へフリック */
		FLICK_DOWN,
		/** ヒントボタン */
		HINT,
		// /** 初期化 */
		// INIT,
		/** イベント開始 */
		RUN_EVENT,
		/** アイテム拡大画面クローズ */
		ITEM_CLOSE,
		/** ミニゲームクリア */
		CLEAR_MINIGAME,
	}

	/** 操作タイプ */
	ControlType mType = ControlType.NULL;
	/** 当たり判定 */
	Collision mCollision;
	/** 条件 */
	Condition mCondition;
	/** 動作セット */
	Action mAction;
	/** 次の操作id */
	int mNextId = -1;
	/** 次の操作オブジェクト */
	Control mNext = null;

	/**
	 * コンストラクタ
	 *
	 * @param _id
	 *            id
	 */
	public Control() {
	}

	public void setType(ControlType type) {
		mType = type;
	}

	public void setType(int type_id) {
		mType = ControlType.values()[type_id];
	}

	public ControlType getType() {
		return mType;
	}

	/** @return the collision */
	public Collision getCollision() {
		return mCollision;
	}

	/**
	 * @param collision
	 *            the collision to set
	 */
	public void setCollision(Collision collision) {
		this.mCollision = collision;
	}

	/** @return the condition */
	public Condition getCondition() {
		return mCondition;
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	public void setCondition(Condition condition) {
		this.mCondition = condition;
	}

	/** @return the action */
	public Action getAction() {
		return mAction;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(Action action) {
		this.mAction = action;
	}

	/**
	 * @param id
	 *            the next id to set
	 */
	public void setNext(int id) {
		mNextId = id;
	}

	/**
	 * 操作の実行<br>
	 * 必要であれば当たり判定を行う
	 *
	 * @param x
	 *            位置座標x
	 * @param y
	 *            位置座標y
	 * @return 実行したらtrue
	 */
	public boolean run(GameManagerBase manager, int x, int y) {
		boolean act = false;
		if (ControlType.FLICK_RIGHT.equals(mType)
				|| ControlType.FLICK_LEFT.equals(mType)
				|| ControlType.FLICK_DOWN.equals(mType)) {
			// 条件をチェック
			if (mCondition == null || mCondition.doCheck(manager)) {
				if (mAction != null) {
					mAction.run(manager);
				}
				act = true;
			}
		} else if (mCollision == null || mCollision.isHit(x, y)) {
			// 条件をチェック
			if (mCondition == null || mCondition.doCheck(manager)) {
				if (mAction != null) {
					mAction.run(manager);
				}
				act = true;
			}
		}
		if (act) {
			if (mNextId >= 0) {
				if (mNext == null) {
					mNext = manager.findControl(mNextId);
				}
				if (mNext != null) {
					mNext.run(manager, x, y);
				}
			}
		}

		return act;
	}
}
