package jp.co.cybird.escape.engine.lib.manager;

/**
 * イベント関連コールバック
 * 
 * @author S.Kamba
 */
public interface OnEventCallback {
	/** 途中イベント開始 */
	public void onStartEvent();

	/** 途中イベント終了 */
	public void onFinishEvent();

	public void onFinishHint(String payloadString);

	public void onFailHint(String payloadString);
}
