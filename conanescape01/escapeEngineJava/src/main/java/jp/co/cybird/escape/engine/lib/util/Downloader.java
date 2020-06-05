package jp.co.cybird.escape.engine.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Httpでファイルをダウンロードするクラス
 * 
 * @author S.Kamba
 * 
 */
public class Downloader {

	/** バッファサイズ */
	static final int BUF_SIZE = 16 * 1024;

	/** キャンセルフラグ */
	boolean isCanceled = false;

	/**
	 * 指定のURLからデータをダウンロードする
	 * 
	 * @param url
	 *            ダウンロード先URL
	 * @param cashPath
	 *            キャッシュ保存先
	 * @return boolean 成否
	 */
	public boolean downloadUrl(String url, String cashPath,
			HashMap<String, String> httpHeaders) {
		if (url == null || url.length() == 0)
			return false;
		if (cashPath == null || cashPath.length() == 0)
			return false;

		InputStream in = null;
		FileOutputStream out = null;
		// Http getでダウンロード
		try {
			URL u = new URL(url);
			HttpURLConnection hc = (HttpURLConnection) u.openConnection();
			hc.setRequestMethod("GET");
			hc.setInstanceFollowRedirects(false);
			// ヘッダーの設定(複数設定可能)
			// hc.setRequestProperty("Accept-Language", "jp");
			if (httpHeaders != null) {
				for (Map.Entry<String, String> e : httpHeaders.entrySet()) {
					hc.setRequestProperty(e.getKey(), e.getValue());
				}
			}
			// 接続
			hc.connect();
			// レスポンス
			int httpResponse = hc.getResponseCode();
			if (httpResponse != HttpURLConnection.HTTP_OK) {
				// DEBUG
				LibUtil.LogD("httpResponse=" + httpResponse + ",["
						+ hc.getResponseMessage() + "]");
			}
			// データをファイルに書き込み
			in = hc.getInputStream();
			File file = new File(cashPath); // 保存先
			out = new FileOutputStream(file, false);

			byte[] bytes = new byte[BUF_SIZE];
			int len = 0;
			while ((len = in.read(bytes)) > 0) {
				if (isCanceled) {
					hc.disconnect();
					return false;
				}
				out.write(bytes, 0, len);
			}
			hc.disconnect();
			return true;
		} catch (MalformedURLException e) {
			if (LibUtil.DEBUG) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if (LibUtil.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
		return false;
	}

	/** キャンセル */
	public void cancel() {
		isCanceled = true;
	}
}
