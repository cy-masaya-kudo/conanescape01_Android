/**
 * 
 */
package jp.co.cybird.android.api.point;

import jp.co.cybird.android.api.util.DebugLog;

/**
 * @author c1758
 *
 */
public class PointApiCallback {
	static final String TAG = "PointApiCallback";

	public void onProgressUpdate(int progress) {
		DebugLog.d("onProgressUpdate");
	}

	public void onFinished(String result) {
		DebugLog.d("onFinished");
	}

	public void onCancelled() {
		DebugLog.d("onCancelled");
	}

}
