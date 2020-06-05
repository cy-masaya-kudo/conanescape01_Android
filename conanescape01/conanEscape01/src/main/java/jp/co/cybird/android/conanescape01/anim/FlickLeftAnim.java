package jp.co.cybird.android.conanescape01.anim;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.gui.EngineCallback.EngineEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class FlickLeftAnim extends ViewAnimController {

	public FlickLeftAnim(EngineEventListener l, GameManager gm, View node,
			View anim) {
		super(l, gm, node, anim);
	}

	@Override
	protected Animation createAnim() {
		// 左フリック用アニメーション
		Animation anim = new TranslateAnimation(moveX, 0, 0, 0);
		return anim;
	}

	@Override
	protected Animation createNodeAnim() {
		Animation anim = new TranslateAnimation(0, -moveX, 0, 0);
		return anim;
	}

}
