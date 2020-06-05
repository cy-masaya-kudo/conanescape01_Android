package jp.co.cybird.android.escape.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SwipeView extends HorizontalScrollView {

	int currentIndex = 0;

	private LinearLayout linearLayout;
	private int dpWidth; // ディスプレイの横サイズ
	private int ivWidth; // ImageViewの横サイズ(隙間込み)
	private int pad; // 画像を真ん中に表示させるためのpadding
	private int scrollX; // ScrollViewの座標(現在)
	/** ジェスチャー用 */
	GestureDetector gestureDetector = null;

	int itemCount; // アイテム数

	public SwipeView(Context context) {
		super(context);
	}

	public SwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwipeView(Context context, AttributeSet attrs, int defs) {
		super(context, attrs, defs);
	}

	/** スクロールViewで制御するLinearLayoutをセットする */
	public void setChildLayout(LinearLayout linearLayout) {
		this.linearLayout = linearLayout;
		if (this.linearLayout == null) {
			this.linearLayout = (LinearLayout) getChildAt(0);
		}
		if (!isInEditMode()) {
			// ウィンドウマネージャのインスタンス取得
			WindowManager wm = (WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE);
			// ディスプレイのインスタンス生成
			Display disp = wm.getDefaultDisplay();
			Point p = new Point();
			disp.getSize(p);
			dpWidth = p.x;
		}

		// ジェスチャーの設定
		gestureDetector = new GestureDetector(getContext(), gestureListener);
	}

	/** gesture リスナー */
	private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {

			if (velocityX < 0) {

				// 右へフリック
				moveNextItem(1);
				return true;

			} else if (velocityX > 0) {
				// 左へフリック
				moveNextItem(-1);
				return true;
			}

			return super.onFling(event1, event2, velocityX, velocityY);
		}

	};

	/** カレントアイテムindex */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/** カレントインデックスをセット */
	public void setCurrentIndex(int index) {
		currentIndex = index;
		// その位置までスクロール
		int to = getTargetPosition(currentIndex);
		smoothScrollTo(to, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		initLayout();
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	void initLayout() {
		if (this.linearLayout == null) {
			setChildLayout(null);
		}
		if (ivWidth == 0) {
			// ディスプレイサイズとImageViewの横サイズから両端の空白を計算し挿入
			itemCount = this.linearLayout.getChildCount();
			if (itemCount == 0)
				return;

			View item = linearLayout.getChildAt(0);
			ivWidth = item.getWidth();
			if (ivWidth != 0) {
				TextView view_0 = new TextView(getContext());
				TextView view_1 = new TextView(getContext());
				pad = (int) ((dpWidth - ivWidth) / 2);
				view_0.setWidth(pad);
				view_1.setWidth(pad);
				linearLayout.addView(view_0, 0);
				linearLayout.addView(view_1, linearLayout.getChildCount());
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			postDelayed(new Runnable() {

				@Override
				public void run() {
					// if (!doFling) {
					// スクロール終了なので位置を調整
					setDisplayPosition();
					// }
				}
			}, 50);

		}

		return super.onTouchEvent(event);
	}

	private int getTargetPosition(int index) {
		int to = index == 0 ? 0
				: (pad + index * ivWidth - (dpWidth - ivWidth) / 2);
		return to;
	}

	private void setDisplayPosition() {
		scrollX = getScrollX();
		currentIndex = toIndex(); // 真ん中に表示させるべき画像のIndex
		int to = getTargetPosition(currentIndex);
		smoothScrollTo(to, 0);
	}

	private int toIndex() {
		int index = 0;
		for (int i = 0; i < itemCount; i++) {
			if (i == 0) {
				if (scrollX < pad) {
					index = i;
					break;
				}
			} else if (scrollX < pad + i * ivWidth) {
				index = i;
				break;
			}
		}
		return index;
	}

	/** 次のアイテムをカレントにセットする */
	public void moveNextItem(int move) {
		currentIndex += move;
		if (currentIndex < 0)
			currentIndex = 0;
		if (currentIndex >= itemCount)
			currentIndex = itemCount - 1;
		// その位置までスクロール
		int to = getTargetPosition(currentIndex);
		smoothScrollTo(to, 0);
	}
}
