package jp.co.cybird.android.conanescape01.webapi;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Debug;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

/**
 * ポイント付与API
 * 
 * @author S.Kamba
 *
 */
public class WebapiPoint extends WebapiForConan {

	public static final String POINTID_3COINS = "jp.co.cybird.conanescape01.hint3";
	public static final String POINTID_10COINS = "jp.co.cybird.conanescape01.hint10";
	public static final String POINTID_CONSUME_COIN = "jp.co.cybird.conanescape01.hint";

	public static final String POINTID_DEBUG_COIN = "jp.co.cybird.conanescape01.add.hint10";

	static final String KEY_POINT_ID = "point_id";
	// static final String KEY_RECEIPT = "receipt";
	static final String KEY_SIGNATURE = "signature";
	static final String KEY_DATA = "signedData";
	static final String KEY_POINTS = "points";
	static final String KEY_TRANSACTION = "point_transaction";

	static final String PREF_POINT_TRANSACTION = "PointTransaction";

	protected String point_id;
	protected Purchase purchaseData;

	protected int coin_num = 0;
	protected String pointTransaction;

	public WebapiPoint(Context context) {
		super(context);
	}

	public void setPointId(String id) {
		point_id = id;
	}

	public void setPurchaseData(Purchase p) {
		purchaseData = p;
	}

	@Override
	public void execute(WebapiFinishListener l) {
		setFinishListener(l);

		// GETパラメータ
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();
		getParams.add(new BasicNameValuePair(KEY_POINT_ID, point_id));

		// POSTパラメータ
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		if (purchaseData != null) {
			postParams.add(new BasicNameValuePair(KEY_SIGNATURE, purchaseData
					.getSignature()));
			String signedData = Codec.encode(purchaseData.getOriginalJson());
			postParams.add(new BasicNameValuePair(KEY_DATA, signedData));
		}

		getParamBundle(getParams, postParams);
		PointApiHelper.pointTrade(queryBundle, mCallback);
	}

	@Override
	protected boolean doParse(JSONObject json) {
		try {
			coin_num = json.getInt(KEY_POINTS);
			JSONObject transction = json.getJSONObject(KEY_TRANSACTION);
			pointTransaction = transction.toString();

			// ポイントトランザクション情報を保存
			// savePointTransaction();

		} catch (JSONException e) {
			if (Debug.isDebug)
				e.printStackTrace();
			setErrorMessage(R.string.err_json);
			return false;
		}
		return true;
	}

	/** コイン枚数を取得 */
	public int getCoinNum() {
		return coin_num;
	}

	/** ポイントトランザクション情報を保存 */
	protected void savePointTransaction() {
		SharedPreferences.Editor e = context.getSharedPreferences(
				PREF_POINT_TRANSACTION, Context.MODE_PRIVATE).edit();
		e.putString("t", pointTransaction);
		e.commit();
	}

	/** 保存済みポイントトランザクション情報を取得 */
	protected String getSavedPointTransaction() {
		SharedPreferences pref = context.getSharedPreferences(
				PREF_POINT_TRANSACTION, Context.MODE_PRIVATE);
		String s = pref.getString("t", null);
		if (s.length() == 0)
			return null;
		return s;
	}

	/** Purchase情報を取得 */
	public Purchase getPurchase() {
		return purchaseData;
	}

	/** ポイントトランザクション情報を取得 */
	public String getPointTransactino() {
		return pointTransaction;
	}
}
