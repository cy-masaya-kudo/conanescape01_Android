package jp.co.cybird.escape.engine.lib.object;

import java.util.ArrayList;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.Control.ControlType;

/**
 * コントロールを持つクラス
 * 
 * @author S.Kamba
 * 
 */
public class Controllable extends ESCObject {
	/** コントロールオブジェクトリスト */
	ArrayList<Control> mControls = null;
	/** コントロールの親:Moveで移動する前のNode */
	Node mControlParent = null;

	/** @return the controll array */
	public ArrayList<Control> getControls() {
		return mControls;
	}

	/**
	 * @param controlList
	 *            the controll list to set
	 */
	public void setControlList(ArrayList<Control> controlList) {
		this.mControls = controlList;
	}

	/**
	 * コントロールを追加
	 * 
	 * @param ctrl
	 *            Controlオブジェクト
	 */
	public void addControl(Control ctrl) {
		if (mControls == null)
			mControls = new ArrayList<Control>();
		mControls.add(ctrl);
	}

	/**
	 * インデクスを指定してアイテムオブジェクトを取得
	 * 
	 * @param index
	 *            index
	 * @return Itemオブジェクト
	 */
	public Control getControll(int index) {
		if (mControls == null || index >= mControls.size())
			return null;
		return mControls.get(index);
	}

	/** @return the ControlParent */
	public Node getControlParent() {
		return mControlParent;
	}

	/**
	 * @param mControlParent
	 *            the mControlParent to set
	 */
	public void setControlParent(Node controlParent) {
		this.mControlParent = controlParent;
	}

	/**
	 * コントロールを実行
	 * 
	 * @param type
	 *            コントロールタイプ(ControlType)
	 * @param x
	 *            タップ座標x
	 * @param y
	 *            タップ座標y
	 */
	public boolean runControll(GameManagerBase manager, ControlType type,
			int x, int y) {
		if (mControls == null)
			return false;
		manager.setActionEnableShowingSerif(false);
		for (Control ctrl : mControls) {
			if (ctrl.getType().equals(type)) {
				if (ctrl.run(manager, x, y)) {
					return true; // 1つ実行したら終了
				}
			}
		}
		return false;
	}
}
