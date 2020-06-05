package jp.co.cybird.escape.engine.lib.object;

import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * テキストのセット
 * 
 * @author S.Kamba
 * 
 */
public class TextSet extends ESCObject {

	/** セリフ文字の表示秒数(ミリ) */
	public static final int SHOW_SERIF_TIME = 2 * 1000;
	/** ヒント文字の表示秒数(ミリ) */
	public static final int SHOW_HINT_TIME = 10 * 1000;
	/** セリフ文字のdelay表示秒数(ミリ) */
	public static final int SHOW_SERIF_DELAYTIME = 10;

	/** 言語タイプ：日本語 */
	public static final int LANGUAGE_JAPANESE = 0;
	/** 言語タイプ：英語 */
	public static final int LANGUAGE_ENGLISH = 1;

	/** 対応言語数：２ */
	public static final int LANGUAGE_TYPE_NUM = 2;

	/** デフォルトテキスト色 */
	public static final int DEFAULT_COLOR = 0xffffffff;

	/** 言語設定 */
	static int mLanguage = LANGUAGE_JAPANESE;

	/** 言語別文字列 */
	String mTexts[] = null;

	/** キャラクターid */
	int mCharaId = -1;
	/** 色 */
	int mColor = 0;

	/**
	 * 文字列をセット
	 * 
	 * @param ja
	 *            日本語用文字列
	 * @param en
	 *            英語用文字列
	 */
	public void setStrings(String... texts) {
		if (texts == null)
			return;
		if (texts.length != LANGUAGE_TYPE_NUM) {
			LibUtil.LogD("TextSet::setStrings  対応言語数(" + LANGUAGE_TYPE_NUM
					+ ")と異なるサイズです。[" + texts.length + "]");
		}
		mTexts = texts;
	}

	/**
	 * 日本語文字列をセット
	 * 
	 * @param language
	 *            言語(LANGUAGE_JAPANESE/LANGUAGE_ENGLISH/etc)
	 * @param s
	 *            文字列
	 */
	public void setString(int language, String s) {
		if (mTexts == null)
			mTexts = new String[LANGUAGE_TYPE_NUM];
		if (language >= LANGUAGE_TYPE_NUM) {
			LibUtil.LogD("TextSet::setString  対応言語数(" + LANGUAGE_TYPE_NUM
					+ ")と異なるサイズです。[" + language + "]");
		}
		mTexts[language] = s;
	}

	/**
	 * 言語別に文字列を取得
	 * 
	 * @param language
	 * @return
	 */
	public String getString(int language) {
		if (language >= LANGUAGE_TYPE_NUM)
			return null;
		if (mTexts == null)
			return null;
		return mTexts[language];
	}

	/**
	 * キャラクターidをセット
	 * 
	 * @param id
	 */
	public void setCharacterId(int id) {
		mCharaId = id;
	}

	/**
	 * @return　キャラクターid
	 */
	public int getCharacterID() {
		return mCharaId;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return mColor;
	}

	/**
	 * @param c
	 *            the color to set
	 */
	public void setColor(int c) {
		mColor = c;
	}

	/**
	 * 言語を設定する
	 * 
	 * @param language
	 *            　言語(LANGUAGE_JAPANESE/LANGUAGE_ENGLISH/etc)
	 */
	public static void setLanguage(int language) {
		mLanguage = language;
	}

	/**
	 * 言語設定を取得する
	 * 
	 * @return 言語設定
	 */
	public static int getLanguage() {
		return mLanguage;
	}

}
