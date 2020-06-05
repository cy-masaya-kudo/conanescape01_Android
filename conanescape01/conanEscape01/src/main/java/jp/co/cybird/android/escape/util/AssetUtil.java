package jp.co.cybird.android.escape.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class AssetUtil {

	/**
	 * assetesフォルダからファイルを指定のフォルダにコピー<br>
	 * 1Mを超えるファイルはzipファイルしかasstes下には置けないので注意。
	 * 
	 * @param dstPath
	 *            コピー先のパス
	 * @param src_assetPath
	 *            コピー元のassetsファイルのファイル名
	 */
	public static boolean copyFromAssets(Context context, String dstPath,
			String src_assetPath) {
		AssetManager am = context.getAssets();

		FileOutputStream fos = null;
		InputStream in = null;
		try {
			in = am.open(src_assetPath);
			fos = new FileOutputStream(dstPath);
			int len = 0;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
}
