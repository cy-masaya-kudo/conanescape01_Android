package jp.co.cybird.escape.engine.lib.manager;

import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;

/**
 * 描画コールバック用インターフェース
 * 
 * @author S.Kamba
 */
public interface OnDrawCallback {

	/**
	 * 描画コールバック
	 * 
	 * @param Node
	 */
	public void onDrawCallback(Node node);

	/**
	 * セリフ文字列を表示
	 * 
	 * @param str
	 *            文字列
	 * @param chara_no
	 *            キャラクター番号
	 */
	public void onDrawText(String str, EventCharacter character, boolean isEnd);

	/**
	 * ヒントを表示
	 * 
	 * @param str
	 *            文字列
	 * @param chara_no
	 *            キャラクター番号
	 */
	public void onDrawHint(String str, EventCharacter character, boolean isEnd);

	/**
	 * アイテムリスト欄のサムネイルを描画
	 * 
	 * @param index
	 *            アイテム欄index
	 * @param item
	 *            Item
	 */
	public void onDrawItemThumb(int index, Node item);

	/**
	 * アイテム拡大画面を描画
	 * 
	 * @param item
	 *            Item
	 */
	public void onDrawItem(Node item);

	/**
	 * イベント用描画コールバック
	 * 
	 * @param event
	 *            イベント
	 */
	public void onDrawEvent(Event event);

	/**
	 * イベント用テキスト描画コールバック
	 * 
	 * @param str
	 *            文字列
	 * @param color
	 *            文字色
	 * @param character
	 *            キャラクター
	 */
	public void onDrawEventText(String str, int color,
			EventCharacter character, boolean isEnd);

	/**
	 * エンディング用ハッピーコイン描画コールバック
	 */
	public void onDrawEventCoin(boolean flag);

	/** エフェクト描画：フェード */
	public void onDrawEffectFade(int color, long in_duration, long out_duration);

	/** エフェクト描画：画面振動 */
	public void onDrawEffectVibration(long duration);
}
