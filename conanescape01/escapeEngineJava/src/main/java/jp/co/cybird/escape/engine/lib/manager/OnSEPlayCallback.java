package jp.co.cybird.escape.engine.lib.manager;

/**
 * アクティブノードが変更されたときのコールバック
 * 
 * @author S.Kamba
 */
public interface OnSEPlayCallback {

	public enum SEType {
		ITEM_GET, // アイテム獲得
		ITEM_SELECT, // アイテム選択
		ITEM_ZOOM, // アイテム拡大
		ZOOM_END, // アイテム拡大終了
		ITEM_COMBINE, // アイテム合成
		SERRIF_PROCESS, // セリフ進行
	}

	public void onSEPlay(SEType se_type);
}
