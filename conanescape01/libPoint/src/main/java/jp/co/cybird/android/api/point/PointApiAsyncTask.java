/**
 * 
 */
package jp.co.cybird.android.api.point;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.cybird.android.api.util.DebugLog;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;

/**
 * @author c1758
 *
 */
public class PointApiAsyncTask extends AsyncTask<Bundle, Integer, String> {
	static final String TAG = "PointApiAsyncTask";

	private PointApiCallback callback = null;

	BufferedInputStream mInputStream;
	BufferedOutputStream mOutputStream;

	/**
	 * 
	 */
	public PointApiAsyncTask(PointApiCallback _callback) {
		this.callback = _callback;
	}

	/*
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    callback.preExecute();
	}
	*/

	@Override
	protected void onCancelled() {
		super.onCancelled();

		try {
			if (mOutputStream != null) {
				mOutputStream.close();
			}
			if (mInputStream != null) {
				mInputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		callback.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		callback.onFinished(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		callback.onProgressUpdate(values[0]);
	}

	@Override
	protected String doInBackground(Bundle... parameters) {
		Bundle queryBundle = parameters[0];
		Bundle apiBundle = parameters[1];

		Bundle getBundle = queryBundle.getBundle(PointApiHelper.KEY_BUNDLE_GET);
		Bundle headerBundle = queryBundle
				.getBundle(PointApiHelper.KEY_BUNDLE_HEADER);
		Bundle postBundle = queryBundle
				.getBundle(PointApiHelper.KEY_BUNDLE_POST);
		Bundle downloadBundle = queryBundle
				.getBundle(PointApiHelper.KEY_BUNDLE_DOWNLOAD);

		// パラメーター文字列をURLエンコーディングする
		DebugLog.d("getBundle.keySet():" + getBundle.keySet());
		ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();
		for (String key : getBundle.keySet()) {
			getParams
					.add(new BasicNameValuePair(key, getBundle.getString(key)));
		}
		String getParamString = URLEncodedUtils.format(getParams, "utf-8");

		String serverUrl = apiBundle.getString(PointApiHelper.KEY_SERVER_URL);
		String apiUrl = apiBundle.getString(PointApiHelper.KEY_API);

		StringBuilder uri = new StringBuilder(serverUrl + apiUrl + "?"
				+ getParamString);

		// String httpMethod = apiBundle.getString(PointApiHelper.KEY_METHOD);
		// boolean isGetMethod = httpMethod.equals(PointApiHelper.METHOD_GET);

		// HttpGet request = new HttpGet(uri.toString());
		HttpPost request = new HttpPost(uri.toString());

		// ヘッダー情報をセットする
		DebugLog.d("headerBundle.keySet():" + headerBundle.keySet());
		for (String key : headerBundle.keySet()) {
			request.setHeader(key, headerBundle.getString(key));
		}

		// Set some headers to inform server about the type of the content
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		String resultString = null;

		try {
			// POSTパラメーター情報をセットする
			DebugLog.d("postBundle.keySet():" + postBundle.keySet());

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			for (String key : postBundle.keySet()) {
				jsonObject.accumulate(key, postBundle.getString(key));
			}
			// convert JSONObject to JSON to String
			String json = jsonObject.toString();
			// set json to StringEntity
			StringEntity se = new StringEntity(json, "utf-8");
			// set httpPost Entity
			request.setEntity(se);

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(request);

			HashMap<String, String> headerMap = convertHeadersToHashMap(response
					.getAllHeaders());
			DebugLog.d(headerMap.toString());
			DebugLog.d(response.getFirstHeader("Content-Type").getValue());

			int status = response.getStatusLine().getStatusCode();
			Header conentTypeHeader = response.getFirstHeader("Content-Type");
			if (HttpStatus.SC_OK == status
					&& conentTypeHeader.getValue().equalsIgnoreCase(
							"application/force-download")) {
				// ファイル応答の場合

				// ファイル名を取得する
				Header contentDispositionHeader = response
						.getFirstHeader("Content-disposition");
				DebugLog.d(contentDispositionHeader.getValue());
				// String attachment = "";

				try {
					String pathName = downloadBundle
							.getString(PointApiHelper.KEY_PATHNAME);
					String fileName = downloadBundle
							.getString(PointApiHelper.KEY_FILENAME);
					if (pathName == null || fileName == null) {
						// fileName = attachment;
						throw new Exception("no path or file name!");
					}

					// ディレクトリを作成
					File path = new File(pathName);
					if (!path.exists())
						path.mkdir();
					// ファイルを作成
					File file = new File(path + "/" + fileName);
					if (file.exists()) {
						DebugLog.d("file length:" + file.length()
								+ " last modified:" + file.lastModified());
					}
					// 入力
					mInputStream = new BufferedInputStream(response.getEntity()
							.getContent());
					// 出力
					mOutputStream = new BufferedOutputStream(
							new FileOutputStream(file));

					int i = 0;
					byte buffer[] = new byte[10240];

					while ((i = mInputStream.read(buffer)) != -1) {
						mOutputStream.write(buffer, 0, i);
					}

					// 書き込み
					mOutputStream.flush();
				} finally {
					if (mOutputStream != null) {
						mOutputStream.close();
					}
					if (mInputStream != null) {
						mInputStream.close();
					}
				}
				// 成功した場合は結果を返す
				resultString = "{ \"status\":0 }";
			} else {
				// JSON応答の場合
				resultString = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				DebugLog.d(resultString);
			}
		} catch (Exception e) {
			e.printStackTrace();

			resultString = null;
		}

		return resultString;
	}

	public synchronized void sleep(long msec) {
		try {
			wait(msec);
		} catch (InterruptedException e) {
		}
	}

	private HashMap<String, String> convertHeadersToHashMap(Header[] headers) {
		HashMap<String, String> result = new HashMap<String, String>(
				headers.length);
		for (Header header : headers) {
			result.put(header.getName(), header.getValue());
		}
		return result;
	}

}
