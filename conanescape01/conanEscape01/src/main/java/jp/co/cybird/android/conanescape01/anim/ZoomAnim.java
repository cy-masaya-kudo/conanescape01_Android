package jp.co.cybird.android.conanescape01.anim;

import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.gui.EngineCallback.EngineEventListener;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class ZoomAnim extends ViewAnimController {

	Context context = null;

	public ZoomAnim(EngineEventListener l, GameManager gm, View node, View anim) {
		super(l, gm, node, anim);
		context = anim.getContext();
	}

	@Override
	protected Animation createAnim() {
		Animation scale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation alpha = new AlphaAnimation(0, 1);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(scale);
		set.addAnimation(alpha);
		return set;
	}

	@Override
	protected Animation createNodeAnim() {
		// Nodeはアニメーションしない
		return null;
	}

}
