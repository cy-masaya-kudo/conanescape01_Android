package jp.co.cybird.android.api.param;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;
import com.gency.cybirdid.CybirdCommonUserId;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * ApiHelperを呼ぶ際のパラメータ作成支援用クラス<br>
 * <br>
 * (1)共通ヘッダ用のBundle<br>
 * (2)GET用のBundle（独自のパラメータと共通パラメータを付けてURLEncode+Popgate暗号化し、param=XXXの形にしたもの）<br>
 * (3)POST用のBundle<br>
 * これらを持ったqueryBundleを作成する
 * 
 * @author S.Kamba
 *
 */
public class ParamHelper {

	static final boolean isDebug = false;

	Context context;

	//
	// Common URI parameters
	String appl_ver = null;
	String os_name = null;
	String os_ver = null;
	String device_name = null;
	String x_cy_identify = null;
	String app_id = null;

	protected String url_params = null;
	protected String encrypted_params = null;

	public ParamHelper(Context c) {
		context = c;

		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			appl_ver = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// 2) OS name
		os_name = "android";
		// 3) OS version
		os_ver = android.os.Build.VERSION.RELEASE;
		// 4) Device name
		device_name = android.os.Build.HARDWARE;
		// 5) UUID
		try {
			x_cy_identify = CybirdCommonUserId.get(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isDebug) {
			x_cy_identify = "DEBUG00-0000-0000-0000-000000000000";
		}
		// 6) APP_ID
		app_id = context.getPackageName();
	}

	private void encrypt() throws UnsupportedEncodingException {
		if ((this.url_params != null) && (this.url_params.length() > 0)) {
			this.encrypted_params = Codec.encode(this.url_params.toString());
		}
	}

	/**
	 * GETパラメータを暗号化済みで取得<br>
	 * Bundle形式では要らないが文字列で欲しい場合のメソッド
	 * 
	 * @param params
	 *            GETパラメータ(API独自のもののみでOK)
	 * @return　URLEncode+POPGATE暗号化済みパラメータ文字列
	 * @throws Exception
	 */
	public String getEncryptParams(ArrayList<NameValuePair> params)
			throws Exception {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();

		//
		// Set common params
		if (params != null && params.size() > 0) {
			for (NameValuePair param : params) {
				pairs.add(param);
			}
		}
		//
		// Set common params
		if (appl_ver != null) {
			pairs.add(new BasicNameValuePair("ver", appl_ver));
		}
		//
		if (os_name != null) {
			pairs.add(new BasicNameValuePair("os", os_name));
		}
		//
		if (os_ver != null) {
			pairs.add(new BasicNameValuePair("os_ver", os_ver));
		}
		//
		if (device_name != null) {
			pairs.add(new BasicNameValuePair("device", device_name));
		}
		//
		if (app_id != null) {
			pairs.add(new BasicNameValuePair("app_id", app_id));
		}
		// URLEncode
		url_params = URLEncodedUtils.format(pairs, "utf-8");
		//
		// パラメータを暗号化する
		this.encrypt();
		return encrypted_params;
	}

	/**
	 * パラメータBundleを作成
	 * 
	 * @param get_params
	 *            GETパラメータ(API独自のもののみでOK)
	 * @param post_params
	 *            POSTパラメータ
	 * @return　ヘッダー用Bundle/get用Bundle/post用Bundleを含んだBundle
	 */
	public Bundle getParamBundle(ArrayList<NameValuePair> get_params,
			ArrayList<NameValuePair> post_params) {
		try {
			// header Bundle
			Bundle headerBundle = new Bundle();
			headerBundle.putString("X-Cy-Identify",
					Codec.encode(this.x_cy_identify));

			// get Bundle
			Bundle getBundle = new Bundle();
			getEncryptParams(get_params);
			getBundle.putString("param", encrypted_params);

			// post Bundle
			Bundle postBundle = new Bundle();
			if (post_params != null) {
				for (NameValuePair p : post_params) {
					postBundle.putString(p.getName(), p.getValue());
				}
			}

			// query Bundle
			Bundle queryBundle = new Bundle();
			queryBundle.putBundle(PointApiHelper.KEY_BUNDLE_HEADER,
					headerBundle);
			queryBundle.putBundle(PointApiHelper.KEY_BUNDLE_GET, getBundle);
			queryBundle.putBundle(PointApiHelper.KEY_BUNDLE_POST, postBundle);

			return queryBundle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
