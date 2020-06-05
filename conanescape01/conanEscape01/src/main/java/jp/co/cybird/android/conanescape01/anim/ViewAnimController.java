package jp.co.cybird.android.conanescape01.anim;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.EngineCallback.EngineEventListener;
import jp.co.cybird.android.escape.util.ImageViewUtil;
import jp.co.cybird.android.escape.util.LayerView;
import jp.co.cybird.escape.engine.lib.object.Node;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * GameViewのViewアニメーション制御用
 * 
 * @author S.Kamba
 *
 */
public abstract class ViewAnimController {

	float moveX = 0;

	GameManager gm = null;
	EngineEventListener listener = null;

	View nodeView;
	View animView;

	Animation animNode;
	Animation animTemp;

	AnimationListener animListenerNode = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			onAnimStartNode();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			onAnimEndNode();
		}
	};

	AnimationListener animListenerAnim = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			onAnimStartAnim();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			onAnimEndAnim();
		}
	};

	abstract protected Animation createAnim();

	abstract protected Animation createNodeAnim();

	public ViewAnimController(EngineEventListener l, GameManager gm, View node,
			View anim) {
		listener = l;
		this.gm = gm;
		nodeView = node;
		animView = anim;
		createAnimations(node.getContext());
	}

	public void delete() {
		gm = null;
		nodeView = null;
		animView = null;
	}

	protected void initDefaultParams(Animation anim) {
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(Common.NODE_ANIM_DURATION);
		anim.setFillBefore(true);
		anim.setFillAfter(true);
	}

	protected void createAnimations(Context c) {

		moveX = c.getResources().getDisplayMetrics().widthPixels;

		animTemp = createAnim();
		animNode = createNodeAnim();

		animTemp.setAnimationListener(animListenerAnim);
		initDefaultParams(animTemp);

		if (animNode != null) {
			animNode.setAnimationListener(animListenerNode);
			initDefaultParams(animNode);
			animNode.setFillAfter(false);
		}
	}

	protected void setAnimNodeDraw() {
		// アクティブノードを取得
		Node node = gm.getActiveObject();
		String filename = gm.getImagePath() + node.getImage(-1);
		if (filename != null) {
			ImageView img = (ImageView) animView.findViewById(R.id.animImage);
			ImageViewUtil.setImageBitmap(img, filename, 1);
		}
		// layerを描画
		drawLayer(node.getChildren());
	}

	public void start() {
		setAnimNodeDraw();
		// アニメーションを開始
		animTemp.reset();
		animView.startAnimation(animTemp);
		if (animNode != null) {
			animNode.reset();
			// アニメーションを開始
			nodeView.startAnimation(animNode);
		}
	}

	/** Layerを描画 */
	void drawLayer(ArrayList<Node> layers) {
		LayerView lv = (LayerView) animView.findViewById(R.id.anim_layer_view);
		lv.setVisibility(View.VISIBLE);
		lv.setGameManager(gm);
		lv.setLayerList(layers);
		lv.invalidate();
	}

	protected void onAnimStartNode() {
	}

	protected void onAnimEndNode() {
		nodeView.setAnimation(null);

	}

	protected void onAnimStartAnim() {
	}

	protected void onAnimEndAnim() {
		animView.setAnimation(null);
		gm.draw();

		animView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// nodeImageに貼り替え
				animView.setVisibility(View.INVISIBLE);
				listener.setAnimationFlag(false);
			}
		}, 1);
	}
}
