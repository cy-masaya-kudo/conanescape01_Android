package jp.co.cybird.escape.engine.lib.object.event;

/**
 * ヒント用イベントクラス
 * 
 * @author S.Kamba
 *
 */
public class EventHint extends Event {

	// アプリに返す用の任意の文字列
	String strPayload = null;

	/** 任意の文字列セット */
	public void setPayloadString(String str) {
		strPayload = str;
	}

	/** セットした文字列を取得 */
	public String getPayloadString() {
		return strPayload;
	}
}
