package jp.co.cybird.escape.engine.lib.object;

import java.util.ArrayList;
import java.util.Stack;

/**
 * アイテム以外のすべてのオブジェクトを表すクラス<br>
 *
 * @author S.Kamba
 */
public class Node extends ImageChanger {

	/** 親オブジェクト */
	protected Node mParent = null;
	/** 子オブジェクト(Layer) */
	protected ArrayList<Node> mChildren = null;

	/** 各種フラグ(ビットシフト演算で使用する:32桁まで) */
	protected int mFlag = 0;

	/** アクションスタック:アクションに付随するパラメータを必要に応じてpushするためのスタック */
	protected Stack<Object> mActionStack = null;
	/** 経過時間計測用のタイマー開始時の時刻 */
	protected long mSavedTimer = 0;
	/** タップされた回数 */
	protected int mCount = 0;

	/** コンストラクタ */
	public Node() {
		super();

		mActionStack = new Stack<Object>();
		// 基本的にロックをかける
		mFlag = Status.FLAG_LOCKED;
		// 基本的に表示
		mFlag |= Status.FLAG_DISP;
	}

	/** @return the parent */
	public Node getParent() {
		return mParent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(Node parent) {
		this.mParent = parent;
		parent.addChild(this);
	}

	/** @return the children */
	public ArrayList<Node> getChildren() {
		return mChildren;
	}

	/**
	 * @param childrens
	 *            the children to set
	 */
	public void setChildren(ArrayList<Node> children) {
		this.mChildren = children;
	}

	/**
	 * 子オブジェクトを追加
	 *
	 * @param child
	 *            　追加する子オブジェクト
	 */
	public void addChild(Node child) {
		if (mChildren == null) {
			mChildren = new ArrayList<Node>();
		}
		// すでにいないかチェックして追加
		if (mChildren.indexOf(child) < 0) {
			mChildren.add(child);
		}
	}

	/** @return the flag */
	public int getFlag() {
		return mFlag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(int flag) {
		this.mFlag = flag;
	}

	/**
	 * ビット演算使用のフラグをONにする
	 *
	 * @param bitFlag
	 */
	public void flagON(int bitFlag) {
		mFlag |= bitFlag;
	}

	/**
	 * ビット演算使用のフラグをOFFにする
	 *
	 * @param bitFlag
	 */
	public void flagOFF(int bitFlag) {
		mFlag &= ~bitFlag;
	}

	/**
	 * フラグの状態を切り替える
	 *
	 * @param bitFlag
	 */
	public void toggleFlag(int bitFlag) {
		mFlag ^= bitFlag;
	}

	/**
	 * ビット演算使用のフラグがONかチェックする
	 *
	 * @param bitFlag
	 * @return フラグがONならTRUE
	 */
	public boolean isFlagON(int bitFlag) {
		return (mFlag & bitFlag) == 0 ? false : true;
	}

	/**
	 * @return the action stack
	 */
	public Stack<Object> getActionStack() {
		return mActionStack;
	}

	/**
	 * @param timer
	 *            to save
	 */
	public void setTimerCurrent(long timer) {
		mSavedTimer = timer;
	}

	/** @return the timer saved */
	public long getSavedTimer() {
		return mSavedTimer;
	}

	/** set mCount */
	public void setCount(int c) {
		mCount = c;
	}

	/** get mCount */
	public int getCount() {
		return mCount;
	}

	/** for save */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// 0:id, 1:activeImageId, 2:flag, 3:savedTimer, 4:tapCount
		sb.append(getId()).append(",").append(getActiveImageId()).append(",")
				.append(getFlag()).append(",").append(getSavedTimer())
				.append(",").append(getCount());
		return sb.toString();
	}

	/** resotre from saved String */
	public void restore(String buf[]) {
		// 1:activeImageId
		int id = Integer.valueOf(buf[1].trim());
		setActiveImageId(id);
		// 2:flag
		int flag = Integer.valueOf(buf[2].trim());
		setFlag(flag);
		// 3:savedTimer
		long timer = Long.valueOf(buf[3].trim());
		setTimerCurrent(timer);
		// 4:count
		int count = Integer.valueOf(buf[4].trim());
		setCount(count);
	}
}
