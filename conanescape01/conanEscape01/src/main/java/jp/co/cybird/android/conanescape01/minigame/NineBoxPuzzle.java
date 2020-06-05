package jp.co.cybird.android.conanescape01.minigame;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.escape.util.ImageViewUtil;
import jp.co.cybird.escape.engine.lib.math.Rect;
import jp.co.cybird.escape.engine.lib.minigame.NineBoxPuzzleBase;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 9マスパズルゲーム
 *
 * @author S.Kamba
 *
 */
public class NineBoxPuzzle extends NineBoxPuzzleBase {

	ImageView draggingImageView = null;
	int imgH = -1;

	final int answer[] = { 0, 1, 7, 2, 4, 3, 8, 5, 6 };

	public NineBoxPuzzle(GameManager manager, ImageView draggingImageView) {
		super(manager);

		this.draggingImageView = draggingImageView;
		draggingImageView.setVisibility(View.INVISIBLE);
	}

	/** タッチイベント */
	public boolean onTouchEvent(MotionEvent event, int nodeImageViewOffset[]) {
		int action = event.getAction();
		int x = (int) (event.getX() - nodeImageViewOffset[0] + 0.5f);
		int y = (int) (event.getY() - nodeImageViewOffset[1] + 0.5f);

		if (imgH < 0) {
			// ImageViewの大きさをマス目Layerの大きさに合わせる
			ViewGroup.LayoutParams p = draggingImageView.getLayoutParams();
			Rect rc = boxes[0].layer.getPosition().getRect();
			imgH = p.height = rc.getHeight();
			p.width = rc.getWidth();
			draggingImageView.requestLayout();
		}

		if (action == MotionEvent.ACTION_DOWN) {
			onTouchDown(x, y);
		} else if (action == MotionEvent.ACTION_MOVE) {
			onTouchMove(x, y);
		} else if (action == MotionEvent.ACTION_UP) {
			onTouchUp(x, y);
		}
		return true;
	}

	@Override
	protected void setDragImage(String filename) {
		// ImageViewに画像をセット
		ImageViewUtil.setImageBitmap(draggingImageView, filename, 1);
	}

	@Override
	protected void drawDragImage(boolean dispFlag, int x, int y) {
		if (dispFlag) {
			// ImageViewを表示
			draggingImageView.setVisibility(View.VISIBLE);
			// 指の位置より右上に表示されるようにする
			draggingImageView.setTranslationX(x);
			draggingImageView.setTranslationY(y - imgH * 2 / 3);
		} else {
			draggingImageView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onPlayCorrectSound() {
		// TODO
	}

	@Override
	protected final int[] getAnswers() {
		return answer;
	}
}
