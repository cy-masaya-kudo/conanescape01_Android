package jp.co.cybird.android.conanescape01.webapi;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

/**
 * ステージDL用のリクエストパラメータ作成用
 * 
 * @author S.Kamba
 *
 */
public class WebapiStageData extends WebapiBase {

	String stageNo = null;

	public WebapiStageData(Context context, String stageNo) {
		super(context, context.getString(R.string.stage_data));
		this.stageNo = stageNo;
	}

	public String getEncryptedParams() {
		try {
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("no", stageNo));
			return getEncryptParam(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
