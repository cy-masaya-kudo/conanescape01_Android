package jp.co.cybird.android.escape.util;

import jp.co.cybird.android.conanescape01.R;
import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * GoogleAnalytics Traking用
 * 
 * @author S.Kamba
 * 
 */
public class Tracking {

	static boolean isTrackingEnable = true;

	// //////////////////
	// Google Analytics用
	static Tracker mTracker = null;

	/**
	 * 初期化
	 * 
	 * @param app
	 *            アプリケーションクラス
	 */
	public static void init(Application app) {
		if (mTracker == null) {
			// String PROPERTY_ID = app.getResources().getString(
			// R.string.ga_trackingID);
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(app);
			// analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
			Tracker t = analytics.newTracker(R.xml.analytics);
			t.enableAdvertisingIdCollection(false);
			mTracker = t;
		}
	}

	/**
	 * トラッカー取得
	 * 
	 * @return
	 */
	static synchronized Tracker getTracker() {
		return mTracker;
	}

	/**
	 * Screenビューを報告
	 * 
	 * @param name
	 */
	public static void sendView(String name) {
		if (!isTrackingEnable)
			return;
		Tracker t = getTracker();
		t.setScreenName(name);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * GoogleService利用可能か
	 * 
	 * @return
	 */
	public static boolean isTrackingEnabled() {
		return isTrackingEnable;
	}

	/**
	 * GoogleService利用可能フラグセット
	 * 
	 * @param flag
	 */
	public static void setTrackingEnabled(boolean flag) {
		isTrackingEnable = flag;
	}
}
