package jp.co.cybird.escape.engine.lib.manager;

import jp.co.cybird.escape.engine.lib.action.Action.MoveType;

/**
 * アクティブノードが変更されたときのコールバック
 * 
 * @author S.Kamba
 */
public interface OnActiveChangeCallback {
	public void onActiveNodeChanged(MoveType type);

	public void onActiveItemChanged();
}
