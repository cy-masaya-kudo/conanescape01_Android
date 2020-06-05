package jp.co.cybird.android.conanescape01.webapi;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;
import jp.co.cybird.android.util.Debug;

/**
 * 未処理トランザクション取得API
 * 
 * @author S.Kamba
 *
 */
public class WebapiTransactions extends WebapiForConan {

	static final String KEY_POINT_ID = "point_id";

	static final String KEY_LIST = "point_transaction_list";

	String point_id;
	ArrayList<String> transactions = null;

	public WebapiTransactions(Context context) {
		super(context);
	}

	public void setPointId(String id) {
		point_id = id;
	}

	@Override
	public void execute(WebapiFinishListener l) {
		setFinishListener(l);

		// GETパラメータ
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();
		getParams.add(new BasicNameValuePair(KEY_POINT_ID,
				WebapiPoint.POINTID_CONSUME_COIN));

		getParamBundle(getParams, null);
		PointApiHelper.getUntreatedPoints(queryBundle, mCallback);
	}

	@Override
	protected boolean doParse(JSONObject json) {
		try {
			if (json.has(KEY_LIST)) {
				JSONArray array = json.getJSONArray(KEY_LIST);
				if (array == null || array.length() == 0) {
					return true;
				}
				transactions = new ArrayList<String>();
				int num = array.length();
				for (int i = 0; i < num; i++) {
					JSONObject o = array.getJSONObject(i);
					transactions.add(o.toString());
				}
			}

			return true;
		} catch (Exception e) {
			if (Debug.isDebug)
				e.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> getTransactions() {
		return transactions;
	}
}
