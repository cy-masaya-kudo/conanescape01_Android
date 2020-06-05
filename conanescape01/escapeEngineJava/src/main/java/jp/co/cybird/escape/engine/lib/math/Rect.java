package jp.co.cybird.escape.engine.lib.math;

/**
 * 2D矩形を表すクラス
 * 
 * @author S.Kamba
 * 
 */
public class Rect {

	/** 左上座標X */
	int x;
	/** 左上座標Y */
	int y;
	/** 幅 */
	int width;
	/** 高さ */
	int height;

	/**
	 * コンストラクタ
	 * 
	 * @param lx
	 *            , ly　左上座標
	 * @param rx
	 *            , ry　右下座標
	 */
	public Rect(int lx, int ly, int rx, int ry) {
		this.x = lx;
		this.y = ly;
		this.width = rx - lx;
		this.height = ry - ly;
	}

	/**
	 * 左上座標Xを取得
	 * 
	 * @return 左上座標X
	 */
	public int getLeft() {
		return x;
	}

	/**
	 * 左上座標Yを取得
	 * 
	 * @return 左上座標Y
	 */
	public int getTop() {
		return y;
	}

	/**
	 * 幅を取得
	 * 
	 * @return　幅
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 高さを取得
	 * 
	 * @return 高さ
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 当たり判定
	 * 
	 * @param x
	 *            ,y　判定したい座標
	 * @return　座標が矩形内にあればtrue
	 */
	public boolean isIntersect(int x, int y) {
		if (x < this.x)
			return false;
		if (x > (this.x + width))
			return false;
		if (y < this.y)
			return false;
		if (y > (this.y + height))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("(");
		sb.append(x).append(",");
		sb.append(y).append(",");
		sb.append(width).append(",");
		sb.append(height);
		sb.append(")");
		return sb.toString();
	}

	/** @return 座標などが同じならtrue */
	public boolean equals(Rect r) {
		if (r == null)
			return false;
		if (this.x != r.x)
			return false;
		if (this.y != r.y)
			return false;
		if (this.width != r.width)
			return false;
		if (this.height != r.height)
			return false;
		return true;
	}
}
