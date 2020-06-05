package jp.co.cybird.android.conanescape01.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Uid;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

/**
 * Option Web画面用フラグメント
 * 
 * @author S.Kamba
 * 
 */
public abstract class WebFragment extends OptionFragmentBase {

	/** 遷移先URLを取得：抽象メソッド */
	public abstract String getUrl();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_web, container,
				false);

		WebView web = (WebView) rootView.findViewById(R.id.webView);
		// 背景透過
		// web.setBackgroundColor(Color.TRANSPARENT);
		// web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		// ページ読込
		loadUrl(web);

		return rootView;
	}

	/**
	 * urlを読込
	 * 
	 * @param web
	 **/
	protected void loadUrl(WebView web) {
		Context context = getActivity().getApplicationContext();
		// URL
		String url = getUrl();
		// UUID
		String x_cy_identify = Uid.getCyUserId(context);
		// HEADER
		Map<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders.put("X-Cy-Identify", Codec.encode(x_cy_identify));
		// load
		web.loadUrl(url, extraHeaders);
	}
}
