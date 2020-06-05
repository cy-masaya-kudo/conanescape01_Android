package jp.co.cybird.android.conanescape01.anim;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.gui.EngineCallback.EngineEventListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class FadeAnim extends ViewAnimController {

	public FadeAnim(EngineEventListener l, GameManager gm, View node, View anim) {
		super(l, gm, node, anim);
	}

	@Override
	protected Animation createAnim() {
		// 新ノードは後からフェードイン
		Animation anim = new AlphaAnimation(0, 1);
		return anim;
	}

	@Override
	protected Animation createNodeAnim() {
		// 現nodeは先にフェードアウト
		Animation anim = new AlphaAnimation(1, 0);
		return anim;
	}

	@Override
	public void start() {
		setAnimNodeDraw();
		// 現ノードを先にフェードアウト
		animNode.reset();
		// アニメーションを開始
		nodeView.startAnimation(animNode);
	}

	@Override
	protected void onAnimEndNode() {
		// nodeViewをいったん非表示
		nodeView.setAnimation(null);
		nodeView.setVisibility(View.INVISIBLE);
		// animViewをフェードイン
		animTemp.reset();
		animView.startAnimation(animTemp);
	}

	@Override
	protected void onAnimEndAnim() {
		nodeView.setVisibility(View.VISIBLE);
		super.onAnimEndAnim();
	}
}
