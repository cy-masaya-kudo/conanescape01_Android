package jp.co.cybird.escape.engine.lib.object;

import jp.co.cybird.escape.engine.lib.action.Action;

/**
 * キーフレームクラス<br>
 * すべての要素がpublic
 * 
 * @author S.Kamba
 * 
 */
public class KeyFrame {
	public int no = -1;
	public Integer imageId = null;
	public int text_display_time = 0;
	public Action action = null;

	public KeyFrame() {
	}
}
