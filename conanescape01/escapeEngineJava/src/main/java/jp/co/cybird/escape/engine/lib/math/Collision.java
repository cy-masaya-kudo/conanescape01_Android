package jp.co.cybird.escape.engine.lib.math;

import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * 当たり判定クラス
 *
 * @author S.Kamba
 *
 */
public class Collision extends Position {

	/** ゲーム画面幅 */
	static int windowW = 0;
	/** ゲーム画面高さ */
	static int windowH = 0;
	/** 判定格子X個数 */
	static int collisionBoxXNum = 0;
	/** 判定格子Y個数 */
	static int collisionBoxYNum = 0;

	/**
	 * ゲーム画面サイズをセット
	 *
	 * @param w
	 *            幅
	 * @param h
	 *            高さ
	 */
	public static void setWindowSize(int w, int h) {
		windowH = h;
		windowW = w;
	}

	/**
	 * 判定格子個数をセット
	 *
	 * @param x
	 *            横方向個数
	 * @param y
	 *            縦方向個数
	 */
	public static void setCollisionBoxSize(int x, int y) {
		collisionBoxXNum = x;
		collisionBoxYNum = y;
	}

	public Collision() {
		super();
	}

	public Collision(Position p) {
		this.mPositionType = p.mPositionType;
		this.mRect = p.mRect;
	}

	@Override
	public boolean parsePosition(String buff) {
		if (buff.startsWith("[")) {
			// 座標
			super.parsePosition(buff);
		} else if (buff.contains(",")) {
			// カンマ区切りがある場合は当たり判定
			String nums[] = buff.split(",");
			int minX = 9999, minY = 9999;
			int maxX = 0, maxY = 0;
			// 判定格子Noが複数
			for (String strNo : nums) {
				try {
					int no = Integer.valueOf(strNo.trim()) - 1;
					// windowSizeから計算
					int xx = no % collisionBoxXNum;
					int yy = no / collisionBoxXNum;

					int x = windowW / collisionBoxXNum * xx;
					int y = windowH / collisionBoxYNum * yy;
					if (x < minX)
						minX = x;
					if (y < minY)
						minY = y;
					if (x > maxX)
						maxX = x;
					if (y > maxY)
						maxY = y;
				} catch (NumberFormatException e) {
					//
					LibUtil.LogD("当たり判定の判定格子Noの指定が正しくありません。[" + buff + "]");
					return false;
				}
			}
			int w = windowW / collisionBoxXNum;
			int h = windowH / collisionBoxYNum;

			mRect = new Rect(minX, minY, maxX + w, maxY + h);
		} else {
			try {
				// 判定格子Noがひとつ
				int no = Integer.valueOf(buff.trim()) - 1;
				// windowSizeから計算
				int xx = no % collisionBoxXNum;
				int yy = no / collisionBoxXNum;

				int w = windowW / collisionBoxXNum;
				int h = windowH / collisionBoxYNum;
				int x = w * xx;
				int y = h * yy;
				mRect = new Rect(x, y, x + w, y + h);
			} catch (NumberFormatException e) {
				// 通常の位置指定
				return super.parsePosition(buff);
			}
		}
		return true;
	}

}
