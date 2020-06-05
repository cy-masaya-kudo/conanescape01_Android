package jp.co.cybird.android.escape.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * スクロールイベントキャッチが可能なScrollView
 *
 * @author S.Kamba
 *
 */
public class EventCatchableScrollView extends ScrollView {

	ScrollViewListener mOnScrollListener = null;

	public EventCatchableScrollView(Context context) {
		super(context);
	}

	public EventCatchableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EventCatchableScrollView(Context context, AttributeSet attrs,
			int defs) {
		super(context, attrs, defs);
	}

	/**
	 * スクロールイベントリスナを登録
	 * 
	 * @param listener
	 */
	public void setOnScrollListener(ScrollViewListener listener) {
		mOnScrollListener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		//
		super.onScrollChanged(l, t, oldl, oldt);
		// リスナー呼び出し
		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(l, t, oldl, oldt);
		}
	}
}
