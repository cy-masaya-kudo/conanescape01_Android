package jp.co.cybird.android.conanescape01.webapi;

import java.util.ArrayList;

import jp.co.cybird.android.api.point.PointApiHelper;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

/**
 * 購入Verify
 * 
 * @author S.Kamba
 *
 */
public class WebapiVerify extends WebapiPoint {

	public WebapiVerify(Context context) {
		super(context);
	}

	@Override
	public void execute(WebapiFinishListener l) {
		setFinishListener(l);

		// GETパラメータ
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();

		// POSTパラメータ
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		if (purchaseData != null) {
			postParams.add(new BasicNameValuePair(KEY_SIGNATURE, purchaseData
					.getSignature()));
			String signedData = Codec.encode(purchaseData.getToken());
			postParams.add(new BasicNameValuePair(KEY_DATA, signedData));
		}

		getParamBundle(getParams, postParams);
		PointApiHelper.contentsVerify(queryBundle, mCallback);

	}

	@Override
	protected boolean doParse(JSONObject json) {
		return true;
	}

}
