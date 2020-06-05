package jp.co.cybird.escape.engine.lib.object.event;

import jp.co.cybird.escape.engine.lib.object.ImageChanger;

/**
 * キャラクタークラス
 * 
 * @author S.Kamba
 * 
 */
public class EventCharacter extends ImageChanger {

	/** 表示名 */
	String mName = null;

	/***
	 * @param name
	 *            to set
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

}
