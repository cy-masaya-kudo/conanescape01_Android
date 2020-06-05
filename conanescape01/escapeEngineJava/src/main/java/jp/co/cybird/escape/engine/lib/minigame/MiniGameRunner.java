package jp.co.cybird.escape.engine.lib.minigame;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.MiniGame;

/**
 * MiniGame実行用インターフェース
 * 
 * @author S.Kamba
 * 
 */
public interface MiniGameRunner {

	/** 実行 */
	public void run(GameManagerBase gm, MiniGame game);

	/** 保存用文字列取得 */
	public String getSaveString();

	/** セーブデータから復元 */
	public void restoreFromSave(String buff[], MiniGame game);
}
