package jp.co.cybird.android.api.point;

import android.os.Bundle;

public class PointApiHelper {
	static final String TAG = "PointApiHelper";
	
	public static final String KEY_BUNDLE_HEADER = "headerBundle";
	public static final String KEY_BUNDLE_GET = "getBundle";
	public static final String KEY_BUNDLE_POST = "postBundle";
	public static final String KEY_BUNDLE_DOWNLOAD = "downloadBundle";

	public static final String KEY_PATHNAME = "path";
	public static final String KEY_FILENAME = "file";


	static final String METHOD_GET = "GET";
	static final String METHOD_POST = "POST";

	static final String KEY_SERVER_URL = "serverUrl";
	static final String KEY_API = "api";
	static final String KEY_METHOD = "method";


    /**
     * ユーザ所有ポイント数を取得する
     *
     */
    static public PointApiAsyncTask getPoints(Bundle queryBundle, PointApiCallback callback) {
    	
        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_POINT_URL);
        apiBundle.putString(KEY_METHOD, METHOD_GET);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

    /**
     * ポイント処理（追加、消費）
     *
     */
    static public PointApiAsyncTask pointTrade(Bundle queryBundle, PointApiCallback callback) {

        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_POINT_DEAL_URL);
        apiBundle.putString(KEY_METHOD, METHOD_GET);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

    /**
     * 未処理ポイントトランザクションを取得する
     *
     */
    static public PointApiAsyncTask getUntreatedPoints(Bundle queryBundle, PointApiCallback callback) {

        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_POINT_UNTREATED_URL);
        apiBundle.putString(KEY_METHOD, METHOD_GET);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

    /**
     * コンテンツ配信
     *
     */
    static public PointApiAsyncTask contentsDelivery(Bundle queryBundle, PointApiCallback callback) {

        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_CONTENTS_DELIVERY_URL);
        apiBundle.putString(KEY_METHOD, METHOD_GET);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

    /**
     * 課金情報ベリファイ
     *
     */
    static public PointApiAsyncTask contentsVerify(Bundle queryBundle, PointApiCallback callback) {

        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_CONTENTS_VERIFY_URL);
        apiBundle.putString(KEY_METHOD, METHOD_POST);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

    /**
     * コンテンツダウンロード
     *
     */
    static public PointApiAsyncTask contentsDownload(Bundle queryBundle, PointApiCallback callback) {

        Bundle apiBundle = new Bundle();
        apiBundle.putString(KEY_SERVER_URL, Server.SERVER_URL);
        apiBundle.putString(KEY_API, Server.API_CONTENTS_DOWNLOAD_URL);
        apiBundle.putString(KEY_METHOD, METHOD_GET);

        PointApiAsyncTask task = new PointApiAsyncTask(callback);
		task.execute(queryBundle, apiBundle);

    	return task;
    }

}
