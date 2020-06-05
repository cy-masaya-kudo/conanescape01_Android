package jp.co.cybird.escape.engine.lib.object.layer;

import jp.co.cybird.escape.engine.lib.math.Position;
import jp.co.cybird.escape.engine.lib.math.Position.PositionType;
import jp.co.cybird.escape.engine.lib.math.Rect;
import jp.co.cybird.escape.engine.lib.object.Node;

/**
 * Nodeオブジェクトに重ねて表示するLayerオブジェクトクラス
 * 
 * @author S.Kamba
 * 
 */
public class Layer extends Node {

	Position mPosition;

	/**
	 * コンストラクタ
	 */
	public Layer() {
		super();
	}

	/**
	 * @param pos the Position to set
	 */
	public void setPosition(Position pos) {
		mPosition = pos;
	}

	/**
	 * @return the mPosition
	 */
	public Position getPosition() {
		return mPosition;
	}

	/**
	 * @return the PositionType
	 */
	public PositionType getPositionType() {
		return mPosition.getPositionType();
	}

	/**
	 * @return the Rect
	 */
	public Rect getRect() {
		return mPosition.getRect();
	}

	/**
	 * 当たり判定
	 * 
	 * @param x,y　判定したい座標
	 * @return　当たっていればTRUE
	 */
	public boolean isHit(int x, int y) {
		return mPosition.isHit(x, y);
	}
}
