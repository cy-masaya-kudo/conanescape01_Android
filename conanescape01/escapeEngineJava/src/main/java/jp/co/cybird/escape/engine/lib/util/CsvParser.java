package jp.co.cybird.escape.engine.lib.util;

import java.util.ArrayList;

/**
 * CSVのパースクラス
 * 
 * @author S.Kamba
 * 
 */
public class CsvParser {

	private String mBuffer = null;

	@SuppressWarnings("serial")
	public class parseCsvException extends Exception {
		public parseCsvException() {
			super();
		}

		public parseCsvException(String msg) {
			super(msg);
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param buffer　解析するバッファ
	 */
	public CsvParser(String buffer) {
		mBuffer = buffer;
	}

	/**
	 * コンストラクタ
	 */
	public CsvParser() {
	}

	/**
	 * 解析バッファをセット
	 * 
	 * @param buffer
	 */
	public void setBuffer(String buffer) {
		mBuffer = buffer;
	}

	/**
	 * 解析を実行
	 * 
	 * @return 行、カラム単位に分割された文字列のArrayList
	 * @throws parseCsvException
	 */
	public ArrayList<String[]> parse() throws parseCsvException {
		if (mBuffer == null)
			throw new parseCsvException("Null Buffer.");

		ArrayList<String[]> csvData = new ArrayList<String[]>();

		// 1レコードずつに分割
		String[] rowDatas = splitRecords();

		// 1レコードずつ分解
		for (String row : rowDatas) {
			// 項目に分解
			String[] data = row.split(DELIMITER);
			// ダミー置き換え文字を置換
			int i = 0;
			for (String d : data) {
				d = d.replace(DMYSTR_DQUAT, '"');
				d = d.replace(DMYSTR_CRLF, '\n');
				d = d.replace(DMYSTR_COMMA, ',');
				data[i++] = d;
			}

			// for (String rr : data) {
			// System.out.printf(rr + "  ||  ");
			// }
			// System.out.print("\n");
			csvData.add(data);
		}
		return csvData;
	}

	private static final String DELIMITER = ",";
	private static final char DMYSTR_DQUAT = 0x01;
	private static final char DMYSTR_CRLF = 0x02;
	private static final char DMYSTR_COMMA = 0x03;

	/**
	 * レコード単位に分割
	 * 
	 * @return レコード配列
	 */
	private String[] splitRecords() {
		// ダブルクウォートを置き換える
		// ＆""で囲まれた中の改行を置き換える
		StringBuffer buffer = new StringBuffer();
		Boolean oddQuat = false; // 奇数フラグ
		for (int i = 0; i < mBuffer.length(); i++) {
			char c = mBuffer.charAt(i);
			switch (c) {
			case '"': // ダブルクウォート
				if (i > 0 && !oddQuat && mBuffer.charAt(i - 1) == '"') {
					// 直前が"でかつ偶数個目の"のとき、文字列としてのダブルクウォート
					// 一旦ダミー文字で置き換える(あとで置換)
					buffer.append(DMYSTR_DQUAT);
				} else {
					// 足さない？
				}
				oddQuat = !oddQuat;
				break;
			case '\n': // 改行
				if (oddQuat) {
					// クウォートが奇数なら区切りではなく文字列としての改行
					// 一旦ダミー文字で置き換える(あとで置換)
					buffer.append(DMYSTR_CRLF);
				} else {
					// 新レコード
					buffer.append(c); // あとでLFでsplitするので入れる
					oddQuat = false;
				}
				break;
			case '\r':
				break; // CRは無視(多分ないはず)
			case ',': // カンマ
				if (oddQuat) {
					// クウォートが奇数なら区切りではなく文字列としてのカンマ
					// 一旦ダミー文字で置き換える(あとで置換)
					buffer.append(DMYSTR_COMMA);
				} else {
					buffer.append(c);
				}
				break;
			default:
				buffer.append(c);
				break;
			}
		}
		String s = new String(buffer);
		// 余分な領域を削除
		return s.split("\n");
	}

}