package jp.co.cybird.escape.engine.lib.manager;


/**
 * Timer付きアクションの終了時のUI処理を行うためのコールバック用インターフェース
 * 
 * @author S.Kamba
 * 
 */
public interface OnTimerDoneOnUIThread {
	public void onTimerDone(GameManagerBase manager);
}
