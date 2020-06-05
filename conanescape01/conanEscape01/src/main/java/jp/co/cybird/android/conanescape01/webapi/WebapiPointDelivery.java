package jp.co.cybird.android.conanescape01.webapi;

import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

/**
 * コイン消費の完了コミット(これが成功していないと、未処理トランザクションがある状態となる)
 * 
 * @author S.Kamba
 *
 */
public class WebapiPointDelivery extends WebapiForConan {

	static final String KEY_TRANSACTION = "point_transaction";

	String pointTransaction;

	public WebapiPointDelivery(Context context) {
		super(context);

	}

	public void setPointTransaction(String s) {
		pointTransaction = s;
	}

	@Override
	public void execute(WebapiFinishListener l) {
		setFinishListener(l);

		// GETパラメータ
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();
		getParams
				.add(new BasicNameValuePair(KEY_TRANSACTION, pointTransaction));

		getParamBundle(getParams, null);
		PointApiHelper.contentsDelivery(queryBundle, mCallback);
	}

	@Override
	protected boolean parseJSON(String json) {
		// レスポンスは空？
		return true;
	}

	@Override
	protected boolean doParse(JSONObject json) {
		// レスポンスは特にない
		return true;
	}

}
