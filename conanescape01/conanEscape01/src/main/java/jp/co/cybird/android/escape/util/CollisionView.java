package jp.co.cybird.android.escape.util;

import java.util.ArrayList;

import jp.co.cybird.escape.engine.lib.math.Collision;
import jp.co.cybird.escape.engine.lib.math.Rect;
import jp.co.cybird.escape.engine.lib.object.Control;
import jp.co.cybird.escape.engine.lib.object.Node;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CollisionView extends View {

	Node mActiveNode = null;

	public CollisionView(Context context) {
		super(context);
	}

	public CollisionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CollisionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param mActiveNode
	 *            セットする mActiveNode
	 */
	public void setActiveNode(Node node) {
		this.mActiveNode = node;
	}

	/* (非 Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(0x3300ff00);

		if (mActiveNode == null)
			return;

		Rect prev = null;
		ArrayList<Control> controls = mActiveNode.getControls();
		if (controls == null)
			return;
		for (Control ctrl : controls) {
			Collision c = ctrl.getCollision();
			if (c == null)
				continue;

			Rect rc = c.getRect();
			if (rc == null)
				continue;
			if (rc.equals(prev)) { // 同じものは描かない
				continue;
			}
			canvas.drawRect(rc.getLeft(), rc.getTop(),
					rc.getLeft() + rc.getWidth(), rc.getTop() + rc.getHeight(),
					paint);
			prev = rc;
		}

		// int[] location = new int[2];
		// this.getLocationOnScreen(location);
		// LibUtil.LogD("CollisionView locaion.x=" + location[0] +
		// ", location.y="
		// + location[1]);
	}
}
