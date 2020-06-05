package jp.co.cybird.escape.engine.lib.math;

import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * 位置情報クラス
 * 
 * @author S.Kamba
 * 
 */
public class Position {
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String BOTTOM = "BOTTOM";
	public static final String FULL = "FULL";

	/**
	 * 座標タイプ<br>
	 * LayerやControllの当たり判定で使用
	 */
	public enum PositionType {
		/** 座標タイプ：なし */
		NONE, //
		/** 座標タイプ：どこでも */
		FULL, //
		/** 座標タイプ：左 */
		LEFT, //
		/** 座標タイプ：右 */
		RIGHT, //
		/** 座標タイプ：下 */
		BOTTOM, //
		/** 座標タイプ：座標 */
		COORD,
	}

	/** 位置タイプ */
	protected PositionType mPositionType = PositionType.NONE;
	/** 座標 */
	protected Rect mRect = null;

	static float ratioX;
	static float ratioY;
	static float itemRatioX;
	static float itemRatioY;
	static int itemOffsetX;
	static int itemOffsetY;

	/**
	 * 元画像サイズとのピクセル比率
	 * 
	 * @param x
	 *            比率x
	 * @param y
	 *            比率y
	 */
	public static void setRatioToOriginalSize(float x, float y) {
		ratioX = x;
		ratioY = y;
	}

	/**
	 * アイテム画面の元画像サイズとのピクセル比率
	 * 
	 * @param x
	 * @param y
	 */
	public static void setItemZoomScreenRatioToOriginalSize(float x, float y) {
		itemRatioX = x;
		itemRatioY = y;
	}

	/**
	 * アイテム拡大画面におけるスクリーン座標のオフセット
	 * 
	 * @param x
	 *            オフセットx
	 * @param y
	 *            オフセットy
	 */
	public static void setItemZoomScreenPositionOffset(int x, int y) {
		itemOffsetX = x;
		itemOffsetY = y;
	}

	public Position(PositionType type, Rect rect) {
		mPositionType = type;
		mRect = rect;
	}

	public Position() {
	}

	/**
	 * @return the mPositionType
	 */
	public PositionType getPositionType() {
		return mPositionType;
	}

	/**
	 * @param mPositionType
	 *            the mPositionType to set
	 */
	public void setPositionType(PositionType type) {
		this.mPositionType = type;
	}

	/**
	 * @return the mRect
	 */
	public Rect getRect() {
		return mRect;
	}

	/**
	 * @param mRect
	 *            the mRect to set
	 */
	public void setRect(Rect rect) {
		this.mRect = rect;
	}

	/**
	 * csvの位置情報をパース
	 * 
	 * @param buff
	 *            csv文字列<br>
	 *            LEFT,RIGHT,BOTTOM,FULLまたは<br>
	 *            [lx,ly,rx,ry]
	 */
	public boolean parsePosition(String buff) {

		if (LEFT.equals(buff)) {
			setPositionType(PositionType.LEFT);
		} else if (RIGHT.equals(buff)) {
			setPositionType(PositionType.RIGHT);
		} else if (BOTTOM.equals(buff)) {
			setPositionType(PositionType.BOTTOM);
		} else if (FULL.equals(buff)) {
			setPositionType(PositionType.FULL);
		} else if (buff.length() > 0) {
			try {
				setPositionType(PositionType.COORD);
				// 　分解
				String s = buff.substring(1, buff.length() - 1);
				String coords[] = s.split(",");
				int lx = (int) (Integer.valueOf(coords[0].trim()) * ratioX + 0.5f);
				int ly = (int) (Integer.valueOf(coords[1].trim()) * ratioY + 0.5f);
				int rx = (int) (Integer.valueOf(coords[2].trim()) * ratioX + 0.5f);
				int ry = (int) (Integer.valueOf(coords[3].trim()) * ratioY + 0.5f);
				// 矩形
				mRect = new Rect(lx, ly, rx, ry);
			} catch (Exception e) {
				LibUtil.LogD("座標指定方法が間違っています。[" + buff + "]");
				return false;
			}
		}
		return true;
	}

	/**
	 * csvの位置情報をパース:ItemLayer専用
	 * 
	 * @param buff
	 *            csv文字列<br>
	 *            LEFT,RIGHT,BOTTOM,FULLまたは<br>
	 *            [lx,ly,rx,ry]
	 */
	public boolean parsePositionItemLayer(String buff) {

		try {
			setPositionType(PositionType.COORD);
			// 　分解
			String s = buff.substring(1, buff.length() - 1);
			String coords[] = s.split(",");
			int lx = (int) (Integer.valueOf(coords[0].trim()) * itemRatioX
					+ itemOffsetX + 0.5f);
			int ly = (int) (Integer.valueOf(coords[1].trim()) * itemRatioY
					+ itemOffsetY + 0.5f);
			int rx = (int) (Integer.valueOf(coords[2].trim()) * itemRatioX
					+ itemOffsetX + 0.5f);
			int ry = (int) (Integer.valueOf(coords[3].trim()) * itemRatioY
					+ itemOffsetY + 0.5f);
			// 矩形
			mRect = new Rect(lx, ly, rx, ry);
		} catch (Exception e) {
			LibUtil.LogD("座標指定方法が間違っています。[" + buff + "]");
			return false;
		}
		return true;
	}

	/**
	 * 当たり判定
	 * 
	 * @param x
	 *            ,y　判定したい座標
	 * @return 当たっていればtrue
	 */
	public boolean isHit(int x, int y) {
		if (mRect == null)
			return true;
		return mRect.isIntersect(x, y);
	}

}
