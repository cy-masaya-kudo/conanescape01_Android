package jp.co.cybird.android.conanescape01.webapi;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.cybird.android.api.point.PointApiHelper;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Debug;

public class WebapiGetCoinNum extends WebapiForConan {

	static final String KEY_POINTS = "points";

	protected int coin_num = 0;

	public WebapiGetCoinNum(Context context) {
		super(context);
	}

	public void execute(WebapiFinishListener l) {
		setFinishListener(l);
		getParamBundle(null, null);
		PointApiHelper.getPoints(queryBundle, mCallback);
	}

	@Override
	protected boolean doParse(JSONObject json) {
		try {
			coin_num = json.getInt(KEY_POINTS);
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

}
