package jp.co.cybird.escape.engine.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.co.cybird.escape.engine.lib.util.CsvParser.parseCsvException;

/**
 * 便利系
 * 
 * @author S.Kamba
 */
public class LibUtil {
	/** デバッグモード:リリース時はfalseにすること */
	public static final boolean DEBUG = false;

	/**
	 * デバッグログ出力
	 * 
	 * @param s
	 *            　デバッグメッセージ
	 */
	public static void LogD(String s) {
		if (!DEBUG)
			return;
		// System.err.println(s); // コメントアウト
	}

	/**
	 * ArrayList<String>をString[]にコピーする<br>
	 * toArrayだとJavaのバージョンにより動かないので
	 * 
	 * @param targetArray
	 *            コピー先の配列
	 * @param fromList
	 *            コピー元のArrayList
	 */
	public static String[] copyToStringArray(String[] targetArray,
			ArrayList<String> fromList) {
		if (fromList == null)
			return null;
		if (targetArray == null || targetArray.length != fromList.size()) {
			targetArray = new String[fromList.size()];
		}
		int i = 0;
		for (String s : fromList) {
			targetArray[i++] = s;
		}
		return targetArray;
	}

	/**
	 * zipファイルを解凍する
	 * 
	 * @param zipfile
	 *            解凍するzipファイル
	 * @param dstDir
	 *            　解凍先のテンポラリフォルダ
	 * @return　成否
	 * @throws IOException
	 */
	public static boolean unzip(String zipfile, String dstDir) {
		// テンポラリディレクトリを作成する。
		File baseDir = new File(dstDir);
		if (!baseDir.mkdirs()) {
			return false;
		}
		final int BUFFER_SIZE = 2048;

		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(new File(zipfile));
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			File destFile;
			while ((entry = zis.getNextEntry()) != null) {

				destFile = new File(baseDir, entry.getName());
				if (entry.isDirectory()) {
					destFile.mkdirs();
					continue;
				} else {
					int count;
					byte data[] = new byte[BUFFER_SIZE];

					destFile.getParentFile().mkdirs();
					LogD("unzipping... :" + destFile.getAbsolutePath());

					FileOutputStream fos = new FileOutputStream(destFile);
					dest = new BufferedOutputStream(fos, BUFFER_SIZE);
					while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
						dest.write(data, 0, count);
					}

					dest.flush();
					dest.close();
					fos.close();
				}
			}
			zis.close();
			fis.close();
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogD(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LogD(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * CSVファイルを読み込む<br>
	 * 
	 * @param filename
	 *            ファイル名
	 * @param encoding
	 *            エンコード指定文字列
	 * @return パース結果の文字列配列
	 */
	public static ArrayList<String[]> loadCsv(String filename, String encoding) {
		BufferedReader br = null;
		try {
			File file = new File(filename);
			if (!file.exists() || file.isDirectory())
				return null;

			FileInputStream fin = new FileInputStream(file);
			InputStreamReader ir = new InputStreamReader(fin, encoding);
			br = new BufferedReader(ir);
			StringBuffer buff = new StringBuffer();
			// テキスト読み込み処理:行単位
			String line = null;
			while ((line = br.readLine()) != null) {
				buff.append(line);
				buff.append("\n");
			}
			// CSVを解析
			CsvParser parser = new CsvParser(buff.toString());
			return parser.parse();
		} catch (IOException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		} catch (parseCsvException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}

	/**
	 * ファイルをリカーシブルに全て削除する<br>
	 * 
	 * @param file
	 *            　削除するFileオブジェクト
	 */
	public static void removeAllFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		File list[] = file.listFiles();
		for (File f : list) {
			removeAllFile(f);
		}
		file.delete();
	}

	public static String arrayToString(String[] buff) {
		StringBuffer sb = new StringBuffer("[");
		for (String s : buff) {
			sb.append(s).append(",");
		}
		sb.deleteCharAt(sb.length() - 1).append("]");
		return sb.toString();
	}

	/**
	 * 色パラメータをパース<br>
	 * [r,g,b]の形式で記載されたものをARGBのint型で返す
	 */
	public static int toARGB(String cols[]) {
		int r, g, b, a = 255;
		try {
			r = Integer.valueOf(cols[0]);
		} catch (NumberFormatException e) {
			r = 0;
		}
		try {
			g = Integer.valueOf(cols[1]);
		} catch (NumberFormatException e) {
			g = 0;
		}
		try {
			b = Integer.valueOf(cols[2]);
		} catch (NumberFormatException e) {
			b = 0;
		}

		int argb = (a << 24) | (r << 16) | (g << 8) | (b);
		return argb;
	}
}
