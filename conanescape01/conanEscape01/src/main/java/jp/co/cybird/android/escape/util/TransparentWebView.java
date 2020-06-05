package jp.co.cybird.android.escape.util;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TransparentWebView extends WebView {

	public TransparentWebView(Context context) {
		super(context);
	}

	public TransparentWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TransparentWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init() {
		init(null);
	}

	public interface OnWebViewClickListener {
		public void onClick(String url);
	}

	OnWebViewClickListener mOnViewClick = null;

	public void setOnViewClickListener(OnWebViewClickListener l) {
		mOnViewClick = l;
	}

	public void init(WebViewClient client) {
		setBackgroundColor(Color.TRANSPARENT);
		setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		if (client == null) {
			client = new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					view.setVisibility(View.INVISIBLE);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (mOnViewClick != null) {
						mOnViewClick.onClick(url);
						return true;
					} else {
						return false;
					}
				}
			};
		}
		setWebViewClient(client);
	}
}
