package jp.co.cybird.android.escape.util;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.escape.engine.lib.object.Moji;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.layer.ItemSetLayer;
import jp.co.cybird.escape.engine.lib.object.layer.Layer;
import jp.co.cybird.escape.engine.lib.util.LibUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LayerView extends View {
	/** 最大フォントサイズ **/
	protected static final int MAX_FONT_SIZE = 72;
	/** 最小フォントサイズ **/
	protected static final int MIN_FONT_SIZE = 2;
	/** デフォルトフォントサイズ **/
	protected static final int DEF_FONT_SIZE = 12;

	GameManager gm = null;
	ArrayList<Node> mLayerList = null;
	ArrayList<Bitmap> mImageList = null;
	float[] mPos = null;

	Paint paint;

	public LayerView(Context context) {
		super(context);
	}

	public LayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param gm
	 *            GameManager to set
	 */
	public void setGameManager(GameManager gm) {
		this.gm = gm;
	}

	protected int addBitmapList(ArrayList<Node> layers, int posSize) {

		for (Node n : layers) {
			if (n instanceof Moji) {
				Moji moji = (Moji) n;
				posSize = Math.max(posSize, moji.getChildren().size());
				if (moji.getSize() == 0.f) {
					int size = getFontSize(moji);
					moji.setSize(size);
				}
			} else if (n instanceof Layer) {
				Layer layer = (Layer) n;
				String img_filename = gm.getImagePath() + layer.getImage(-1);
				Bitmap image = BitmapFactory.decodeFile(img_filename);
				mImageList.add(image);
			} else if (n instanceof ItemSetLayer) {
				posSize = addBitmapList(n.getChildren(), posSize);
			}
		}
		return posSize;
	}

	/**
	 * @param layer
	 *            セットする Layer
	 */
	public void setLayerList(ArrayList<Node> layers) {
		paint = new Paint();

		mLayerList = layers;
		int posSize = 0;
		if (layers != null && layers.size() > 0) {
			mImageList = new ArrayList<Bitmap>();

			posSize = addBitmapList(layers, posSize);
		}
		if (posSize > 0) {
			mPos = new float[posSize * 2];
		}
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mLayerList == null || mLayerList.size() == 0)
			return;

		drawLayers(canvas, mLayerList, 0);
	}

	protected int drawLayers(Canvas canvas, ArrayList<Node> layers,
			int bitmapIndex) {
		for (int i = 0; i < layers.size(); i++) {
			Node object = layers.get(i);
			if (object instanceof Layer) {
				drawImage(canvas, paint, (Layer) object,
						mImageList.get(bitmapIndex++));
			} else if (object instanceof Moji) {
				drawText(canvas, paint, (Moji) object);
			} else if (object instanceof ItemSetLayer) {
				bitmapIndex = drawLayers(canvas, object.getChildren(),
						bitmapIndex);
			}
		}
		return bitmapIndex;
	}

	/** イメージを描画 */
	protected void drawImage(Canvas canvas, Paint paint, Layer layer, Bitmap img) {
		if (layer.isFlagON(Status.FLAG_DISP)) {
			jp.co.cybird.escape.engine.lib.math.Rect rc = layer.getRect();
			if (rc == null)
				return;
			if (img == null)
				return;
			// 矩形を指定してbitmapを描画
			Rect src = new Rect();
			src.left = 0;
			src.top = 0;
			src.right = img.getWidth();
			src.bottom = img.getHeight();
			Rect dst = new Rect();
			dst.left = rc.getLeft();
			dst.top = rc.getTop();
			dst.right = rc.getLeft() + rc.getWidth();
			dst.bottom = rc.getTop() + rc.getHeight();
			canvas.drawBitmap(img, src, dst, paint);
		}
	}

	/** 文字を描画 */
	private void drawText(Canvas canvas, Paint paint, Moji moji) {

		if (moji.isFlagON(Status.FLAG_DISP)) {
			char buff[] = moji.getBuffer();
			ArrayList<Node> list = moji.getChildren();

			if (buff.length != list.size()) {
				LibUtil.LogD("文字オブジェクトの文字数と子レイヤーの数が合いません");
			}

			paint.setAntiAlias(true);
			paint.setColor(moji.getColor());
			paint.setTextSize(moji.getSize());
			paint.setTextAlign(Paint.Align.CENTER);

			int i = 0;
			FontMetrics fm = paint.getFontMetrics();
			for (Node n : list) {
				try {
					Layer l = (Layer) n;
					jp.co.cybird.escape.engine.lib.math.Rect rc = l.getRect();
					if (rc != null) {
						mPos[i] = rc.getLeft() + rc.getWidth() / 2;
						float centerY = rc.getTop() + rc.getHeight() / 2.f;
						mPos[i + 1] = centerY - (fm.ascent + fm.descent) / 2;
					}
				} catch (ClassCastException e) {
				}
				i += 2;
			}
			// 指定した位置に各文字を描画
			// canvas.drawPosText(buff, 0, list.size(), mPos, paint);
			// 出ない端末があるのでこっち
			for (i = 0; i < list.size(); i++) {
				canvas.drawText(String.valueOf(buff[i]), mPos[i * 2],
						mPos[i * 2 + 1], paint);
			}
		}
	}

	/**
	 * 最適なフォントサイズを計算する
	 */
	protected int getFontSize(Moji moji) {
		int fontsize = MAX_FONT_SIZE;
		char buff[] = moji.getBuffer();
		Layer l = (Layer) moji.getChildren().get(0); // 矩形サイズは先頭のものを使う。最小のものを探すとかはしない
		jp.co.cybird.escape.engine.lib.math.Rect rc = l.getRect();

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(moji.getColor());
		for (char c : buff) {
			int n = fontTestXY(c, rc.getWidth(), rc.getHeight(), paint);
			fontsize = Math.min(fontsize, n); // 一番小さいものに合わせる
		}

		return fontsize;
	}

	/** 指定した幅、高さに入りきるフォントの最大サイズを返す **/
	protected int fontTestXY(char target, int width, int height, Paint tester) {
		for (int i = MAX_FONT_SIZE; i > MIN_FONT_SIZE; i = i - 2) {
			tester.setTextSize(i);
			FontMetrics fontMetrics = tester.getFontMetrics();
			if ((height >= (int) (Math.abs(fontMetrics.ascent) + fontMetrics.descent))
					&& (width >= tester.measureText(String.valueOf(target)))) {
				return i;
			}
		}
		return DEF_FONT_SIZE;
	}
}
