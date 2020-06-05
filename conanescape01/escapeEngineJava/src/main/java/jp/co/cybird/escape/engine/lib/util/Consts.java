package jp.co.cybird.escape.engine.lib.util;

/**
 * 汎用的な定数系
 *
 * @author S.Kamba
 */
public class Consts {
	/** エンコード:SJIS */
	public static final String ENCODING_SHIFTJIS = "Shift_JIS";
	/** エンコード:UTF8 */
	public static final String ENCODING_UTF8 = "utf8";
	/** csvデリミタ */
	public static final String REGEX_CSV_DELIMITER = ",";

	/** csvファイルprefix:imageset */
	public static final String PREFIX_CSV_IMAGESET = "imagelist_";
	/** csvファイルprefix:objectlist */
	public static final String PREFIX_CSV_OBJECTLIST = "objectlist_";
	/** csvファイルprefix:conditionlist */
	public static final String PREFIX_CSV_CONDITIONLIST = "conditionlist_";
	/** csvファイルprefix:actionlist */
	public static final String PREFIX_CSV_ACTIONLIST = "actionlist_";
	/** csvファイルprefix:controlllist */
	public static final String PREFIX_CSV_CONTROLLLIST = "controllist_";
	/** csvファイルprefix:textlist */
	public static final String PREFIX_CSV_TXTSETLIST = "textlist_";

	public static final String ROOM = "Room";
	public static final String WALL = "Wall";
	public static final String Node = "Node";
	public static final String LAYER = "Layer";
	public static final String ITEM = "Item";
	public static final String ITEMLAYER = "ItemLayer";
	public static final String OPENING = "Opening";
	public static final String ENDING = "Ending";
	public static final String STRING = "String";
	public static final String SUBITEM = "SubItem";
	public static final String ITEMSETLAYER = "ItemSetLayer";
	public static final String CHARACTER = "Character";
	public static final String MINIGAME = "MiniGame";
	public static final String EVENT = "Event";
	public static final String HINTEVENT = "HintEvent";
	/** アイテム最大数 */
	public static final int ITEM_NUM_MAX = 8;
}
