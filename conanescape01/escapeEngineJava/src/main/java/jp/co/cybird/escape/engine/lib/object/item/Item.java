package jp.co.cybird.escape.engine.lib.object.item;

import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Status;

/**
 * 取得可能なアイテムオブジェクトクラス<br>
 * 
 * @author S.Kamba
 * 
 */
public class Item extends Node {

	/** アイテム欄表示インデクス */
	int mDisplayIndex = 0;
	/** 使用回数制限 */
	int mMaxUseNum = -1; // 基本は無制限
	/** 使用回数 */
	int mUsedNum = 0;

	/**
	 * コンストラクタ
	 */
	public Item() {
		super();
	}

	/**
	 * @return the displayIndex
	 */
	public int getDisplayIndex() {
		return mDisplayIndex;
	}

	/**
	 * @param displayIndex
	 *            the displayIndex to set
	 */
	public void setDisplayIndex(int displayIndex) {
		this.mDisplayIndex = displayIndex;
	}

	/**
	 * @return the maxUseNum
	 */
	public int getMaxUseNum() {
		return mMaxUseNum;
	}

	/**
	 * @param maxUseNum
	 *            the maxUseNum to set
	 */
	public void setMaxUseNum(int maxUseNum) {
		this.mMaxUseNum = maxUseNum;
	}

	/**
	 * @return the useNum
	 */
	public int getUsedNum() {
		return mUsedNum;
	}

	/**
	 * @param useNum
	 *            the useNum to set
	 */
	public void setUsedNum(int useNum) {
		this.mUsedNum = useNum;
		if (mMaxUseNum >= 0 && mUsedNum >= mMaxUseNum) {
			flagON(Status.FLAG_USED);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		// , 5:maxUseNum, 6:usedNum
		sb.append(",").append(getMaxUseNum()).append(",").append(getUsedNum());
		return sb.toString();
	}

	@Override
	public void restore(String[] buf) {
		super.restore(buf);
		// 5:maxUseNum
		if (buf.length > 5) {
			int num = Integer.valueOf(buf[5].trim());
			setMaxUseNum(num);
		}
		// 6:usedNum
		if (buf.length > 6) {
			int num = Integer.valueOf(buf[6].trim());
			setUsedNum(num);
		}
	}
}
