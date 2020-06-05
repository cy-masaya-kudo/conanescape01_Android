package jp.co.cybird.android.conanescape01.webapi;

import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

/**
 * ポイント消費API
 * 
 * @author S.Kamba
 *
 */
public class WebapiPointConsume extends WebapiPoint {

	public WebapiPointConsume(Context context) {
		super(context);
	}

	@Override
	public void execute(WebapiFinishListener l) {
		setFinishListener(l);

		// GETパラメータ
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();
		getParams
				.add(new BasicNameValuePair(KEY_POINT_ID, POINTID_CONSUME_COIN));

		// POSTパラメータ
		// ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		// if (purchaseData != null) {
		// postParams.add(new BasicNameValuePair(KEY_SIGNATURE, purchaseData
		// .getSignature()));
		// String signedData = Codec.encode(purchaseData.getToken());
		// postParams.add(new BasicNameValuePair(KEY_DATA, signedData));
		// }

		getParamBundle(getParams, null);
		PointApiHelper.pointTrade(queryBundle, mCallback);
	}

}
