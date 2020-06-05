package jp.co.cybird.escape.engine.lib.object;


/**
 * 脱出ゲーム基本オブジェクトクラス<br>
 * すべてのオブジェクトはここから派生させる
 * 
 * @author S.Kamba
 * 
 */
public class ESCObject {

	/** オブジェクトID */
	int mId;

	/**
	 * コンストラクタ
	 * 
	 * @param _id オブジェクトID
	 */
	public ESCObject() {
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return mId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.mId = id;
	}

}
