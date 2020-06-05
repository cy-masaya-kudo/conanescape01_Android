package jp.co.cybird.android.conanescape01.webapi;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.cybird.android.api.param.ParamHelper;
import jp.co.cybird.android.api.point.PointApiCallback;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Debug;

/**
 * ポイントAPI アクセス用テンプレートクラス
 * 
 * @author S.Kamba
 * 
 */
public abstract class WebapiForConan extends WebapiBase {

	static final String KEY_STATUS = "status";
	static final String KEY_ERR_MSG = "error_msg";

	String err_msg = null;

	WebapiFinishListener mListener = null;

	Bundle queryBundle = null;

	public WebapiForConan(Context context) {
		super(context, "");
	}

	/** リスナ登録 */
	public void setFinishListener(WebapiFinishListener l) {
		mListener = l;
	}

	/**
	 * エラーメッセージを取得
	 * 
	 * @return message
	 */
	public String getErrorMessage() {
		return err_msg;
	}

	/**
	 * エラーメッセージをセット
	 * 
	 * @param resId
	 *            メッセージ文字列リソースid
	 */
	protected void setErrorMessage(int resId) {
		err_msg = context.getString(resId);
	}

	/** 実行開始処理 */
	public abstract void execute(WebapiFinishListener l);

	/** パラメータBundleを作成 */
	public Bundle getParamBundle(ArrayList<NameValuePair> get_params,
			ArrayList<NameValuePair> post_params) {
		try {
			ParamHelper helper = new ParamHelper(context);
			queryBundle = helper.getParamBundle(get_params, post_params);

			return queryBundle;
		} catch (Exception e) {
			if (Debug.isDebug)
				e.printStackTrace();
		}
		return null;
	}

	/** parseMain */
	protected boolean parseJSON(String json) {
		boolean result = false;
		if (json == null) {
			// サーバーに接続できてない
			setErrorMessage(R.string.err_server);
			return result;
		}
		try {
			JSONObject o = new JSONObject(json);
			int status = o.getInt(KEY_STATUS);
			if (status != 0) {
				err_msg = o.getString(KEY_ERR_MSG);
				return false;
			}

			result = doParse(o);
		} catch (Exception e) {
			if (Debug.isDebug)
				e.printStackTrace();
			setErrorMessage(R.string.err_json);
			return false;
		}
		return result;
	}

	/**
	 * parse実際処理
	 * 
	 * @return　成否
	 */
	abstract protected boolean doParse(JSONObject json);

	/** PointAPIコールバック */
	PointApiCallback mCallback = new PointApiCallback() {
		public void onFinished(String result) {
			boolean r = parseJSON(result);
			if (mListener != null) {
				mListener.onFinish(r);
			}
		}
	};
}
