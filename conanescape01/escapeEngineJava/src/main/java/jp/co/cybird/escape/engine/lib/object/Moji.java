package jp.co.cybird.escape.engine.lib.object;

import java.util.Arrays;
import java.util.List;

import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * 文字オブジェクトクラス
 * 
 * @author S.Kamba
 * 
 */
public class Moji extends Node {

	/** デフォルトカラー */
	public static final int DEFAULT_COLOR = 0xff00ff00;
	/** 文字サイズ比率 */
	static float sizeRatio = 1.f;

	/** 文字列 */
	char mBuffer[] = null;
	/** 色 */
	int mColor = DEFAULT_COLOR;
	/** サイズ */
	float mSize = 0.f;

	/** かなループ用index */
	int mLoopIndex[] = null;

	/** ひらがな文字セット */
	static final List<Character> HiraganaChars = Arrays.asList('あ', 'い', 'う',
			'え', 'お', 'か', 'き', 'く', 'け', 'こ', 'さ', 'し', 'す', 'せ', 'そ', 'た',
			'ち', 'つ', 'て', 'と', 'な', 'に', 'ぬ', 'ね', 'の', 'は', 'ひ', 'ふ', 'へ',
			'ほ', 'ま', 'み', 'む', 'め', 'も', 'や', 'ゆ', 'よ', 'ら', 'り', 'る', 'れ',
			'ろ', 'わ', 'を', 'ん');
	/** カタカナ文字セット */
	static final List<Character> KatakanaChars = Arrays.asList('ア', 'イ', 'ウ',
			'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス', 'セ', 'ソ', 'タ',
			'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ', 'ヘ',
			'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ', 'リ', 'ル', 'レ',
			'ロ', 'ワ', 'ヲ', 'ン');
	/** かな文字数 */
	static final int KanaLength = HiraganaChars.size();

	/** フォントサイズ比率をセット */
	public static void setSizeRatio(float r) {
		sizeRatio = r;
	}

	/** 初期化 */
	public void init(String str) {
		mBuffer = str.toCharArray();
		if (isHiragana() || isKatakana()) {
			mLoopIndex = new int[str.length()];
		}
	}

	/** バッファにひらがながあるかチェック */
	boolean isHiragana() {
		for (char c : mBuffer) {
			if (HiraganaChars.contains(c))
				return true;
		}
		return false;
	}

	/** バッファにカタカナがあるかチェック */
	boolean isKatakana() {
		for (char c : mBuffer) {
			if (KatakanaChars.contains(c))
				return true;
		}
		return false;
	}

	/** 文字色をセット */
	public void setColor(int color) {
		mColor = color;
	}

	/** 文字サイズをセット */
	public void setSize(float size) {
		mSize = size * sizeRatio;
	}

	/** 文字を変更 */
	public void change(int index, char chr) {
		if (mBuffer == null)
			return;
		if (index >= mBuffer.length)
			return;
		mBuffer[index] = chr;
	}

	/** 一致比較 */
	public boolean check(String str) {
		if (mBuffer == null)
			return false;

		String buf = String.valueOf(mBuffer);
		return buf.equals(str);
	}

	/** 文字取得 */
	public char[] getBuffer() {
		return mBuffer;
	}

	/** 色を取得 */
	public int getColor() {
		return mColor;
	}

	/** サイズを取得 */
	public float getSize() {
		return mSize;
	}

	/**
	 * range内の文字群で文字をループさせる
	 * 
	 * @param range
	 *            [a-z][A-Z][z-a][Z-A][0-9][9-0][あ-ん][ん-あ][ア-ン][ン-ア]
	 * @param index
	 */
	public void loopChar(String range, int index) {
		if (mBuffer == null)
			return;
		if (index >= mBuffer.length)
			return;
		if (mLoopIndex == null) {
			// アルファベットまたは数字なので連続範囲内から取得
			char nowChar = mBuffer[index];
			char nextChar = getNextCharLoop(range, nowChar);
			mBuffer[index] = nextChar;
		} else if (range.equals("[あ-ん]")) {
			// ひらがな：昇順
			if (++mLoopIndex[index] >= KanaLength) {
				mLoopIndex[index] = 0;
			}
			mBuffer[index] = HiraganaChars.get(mLoopIndex[index]);
		} else if (range.equals("[ん-あ]")) {
			// ひらがな：降順
			if (--mLoopIndex[index] < 0) {
				mLoopIndex[index] = KanaLength - 1;
			}
			mBuffer[index] = HiraganaChars.get(mLoopIndex[index]);
		} else if (range.equals("[ア-ン]")) {
			// カタカナ：昇順
			if (++mLoopIndex[index] >= KanaLength) {
				mLoopIndex[index] = 0;
			}
			mBuffer[index] = KatakanaChars.get(mLoopIndex[index]);
		} else if (range.equals("[ン-ア]")) {
			// カタカナ：降順
			if (--mLoopIndex[index] < 0) {
				mLoopIndex[index] = KanaLength - 1;
			}
			mBuffer[index] = KatakanaChars.get(mLoopIndex[index]);
		} else {
			// 無いはずだが一応数字かアルファベットで
			char nowChar = mBuffer[index];
			char nextChar = getNextCharLoop(range, nowChar);
			mBuffer[index] = nextChar;
		}
	}

	/**
	 * range内の文字群で文字をループさせる
	 * 
	 * @param range
	 * @param nowChar
	 * @return
	 */
	static char getNextCharLoop(String range, char nowChar) {
		char next = (char) (nowChar + 1);
		char prev = (char) (nowChar - 1);
		if ("[A-Z]".equals(range)) {
			// アルファベットループ
			if (next > 'Z') {
				next = 'A';
			}
			return next;
		} else if ("[Z-A]".equals(range)) {
			// アルファベットループ降順
			if (prev < 'A')
				prev = 'Z';
			return prev;
		} else if ("[0-9]".equals(range)) {
			// 数字ループ
			if (next > '9') {
				next = '0';
			}
			return next;
		} else if ("[9-0]".equals(range)) {
			// 数字ループ降順
			if (prev < '0') {
				prev = '9';
			}
			return prev;
		}
		// rangeの指定がおかしい
		LibUtil.LogD("onMojiLoop:Rangeの指定がおかしいです。");
		return nowChar;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(",").append(getBuffer());
		return sb.toString();
	}

	@Override
	public void restore(String[] buf) {
		super.restore(buf);
		if (buf.length > 5) {
			init(buf[5].trim());
		}
	}
}
