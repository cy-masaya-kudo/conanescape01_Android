/**
 *
 */
package jp.co.cybird.android.conanescape01.webapi;

import android.content.Context;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import jp.co.cybird.android.api.param.ParamHelper;
import jp.co.cybird.android.util.Debug;

/**
 * API呼び出しクラスのひな形
 * 
 * @author S.Kamba
 *
 */
public class WebapiBase {

	protected final String TAG = "WebApiBase";
	protected String api_domain = null;
	//
	//
	// protected Activity activity;
	protected Context context;
	//
	//
	private Boolean isOK = false;

	/** @return the isOK */
	public Boolean isOK() {
		return this.isOK;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 *            アプリケーションコンテキスト
	 * @param api_domain
	 *            WEBAPIを提供するサーバのドメイン名
	 */
	public WebapiBase(Context context, String api_domain) {
		//
		//
		this.context = context;
		//
		// Host name which is provied WebApi services.
		this.api_domain = api_domain;
	}

	/**
	 * GETパラメータに共通パラメータを付与して暗号化済みパラメータを取得
	 * 
	 * @param list
	 *            API独自のGETパラメータ
	 * @return 暗号化済みパラメータ
	 */
	public String getEncryptParam(ArrayList<NameValuePair> list) {
		ParamHelper helper = new ParamHelper(context);
		try {
			String param_str = helper.getEncryptParams(list);
			return param_str;
		} catch (Exception e) {
			if (Debug.isDebug)
				e.printStackTrace();
		}
		return null;
	}
}
