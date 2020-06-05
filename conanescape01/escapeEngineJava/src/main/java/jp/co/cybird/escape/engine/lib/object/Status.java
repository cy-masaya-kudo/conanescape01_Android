package jp.co.cybird.escape.engine.lib.object;

/**
 * オブジェクト状態フラグ関連クラス
 * 
 * @author S.Kamba
 * 
 */
public class Status {
	/** オブジェクト状態フラグ：なし */
	public static final int FLAG_NONE = 0;
	/** オブジェクト状態フラグ：ロック中 */
	public static final int FLAG_LOCKED = (1 << 0);
	/** オブジェクト状態フラグ：取得済み(Only Item) */
	public static final int FLAG_GOT = (1 << 2);
	/** オブジェクト状態フラグ：表示(Only Layer/ItemLayer) */
	public static final int FLAG_DISP = (1 << 3);
	/** オブジェクト状態フラグ：初期化済み */
	// public static final int FLAG_INIT = (1 << 4);

	/** アイテム使用済みフラグ */
	public static final int FLAG_USED = (1 << 5);
	/** オブジェクト状態フラグ：親と置換済み */
	public static final int FLAG_REPLACED = (1 << 6);

	/** user defined flags:8bit only */
	public static final int FLAG_USER_TOP = (1 << 24);

}
