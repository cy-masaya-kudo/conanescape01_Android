package jp.co.cybird.escape.engine.lib.data;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.cybird.escape.engine.lib.action.Action;
import jp.co.cybird.escape.engine.lib.action.Action.ActionType;
import jp.co.cybird.escape.engine.lib.condition.Condition;
import jp.co.cybird.escape.engine.lib.math.Collision;
import jp.co.cybird.escape.engine.lib.math.Position;
import jp.co.cybird.escape.engine.lib.object.Control;
import jp.co.cybird.escape.engine.lib.object.Controllable;
import jp.co.cybird.escape.engine.lib.object.ESCObject;
import jp.co.cybird.escape.engine.lib.object.ImageSet;
import jp.co.cybird.escape.engine.lib.object.MiniGame;
import jp.co.cybird.escape.engine.lib.object.Moji;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Room;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.TextSet;
import jp.co.cybird.escape.engine.lib.object.Wall;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;
import jp.co.cybird.escape.engine.lib.object.event.EventEnding;
import jp.co.cybird.escape.engine.lib.object.event.EventHint;
import jp.co.cybird.escape.engine.lib.object.event.EventOpening;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.object.item.ItemLayer;
import jp.co.cybird.escape.engine.lib.object.item.SubItem;
import jp.co.cybird.escape.engine.lib.object.layer.ItemSetLayer;
import jp.co.cybird.escape.engine.lib.object.layer.Layer;
import jp.co.cybird.escape.engine.lib.util.Consts;
import jp.co.cybird.escape.engine.lib.util.Downloader;
import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * CSVデータのDL・解凍・パース処理をするクラス
 *
 * @author S.Kamba
 */
public class CsvDataManager {

	/** ダウンロードキャッシュファイルの保存ディレクトリ名 */
	static String cashDir = "";
	/** 展開ファイルの保存ディレクトリ */
	static String dataDir = "";
	/** ダウンロード時リクエストに付与するヘッダー情報 */
	static HashMap<String, String> httpHeaders = null;
	/** zip解凍コールバック */
	static OnDecodeZipListener decodeZipListener = null;

	/** データダウンロードURL */
	String mDownloadUrl = null;
	/** キャッシュファイル名 */
	String mCashPath = null;
	/** データを解凍するフォルダ */
	String mTempDir = null;

	// パース＆返却用

	/** 画像セットリスト */
	ArrayList<ImageSet> imageSetList = null;
	/** ルームオブジェクト */
	Room room = null;
	/** ノードオブジェクトリスト */
	ArrayList<Node> nodeList = null;
	/** アイテムオブジェクトリスト */
	ArrayList<Node> itemList = null;
	/** 条件リスト */
	ArrayList<Condition> conditionList = null;
	/** テキストセットリスト */
	ArrayList<TextSet> textSetList = null;
	/** アクションリスト */
	ArrayList<Action> actionList = null;
	/** コントロールリスト */
	ArrayList<Control> controlList = null;
	/** イベントリスト */
	ArrayList<Event> eventList = null;
	/** キャラクターリスト */
	ArrayList<EventCharacter> charaList = null;

	/** ダウンローダ */
	Downloader downloader = null;
	/** 初期化キャンセルフラグ */
	boolean isCanceled = false;

	/**
	 * ダウンロードキャッシュディレクトリをセットする
	 *
	 * @param name
	 *            ディレクトリ名
	 */
	public static void setCashDir(String name) {
		cashDir = name;
	}

	/**
	 * 解凍データの展開先フォルダをセットする
	 *
	 * @param name
	 *            ディレクトリ名
	 */
	public static void setDataDir(String name) {
		dataDir = name;
	}

	/**
	 * URLからダウンロード時、httpリクエストに付けるヘッダー情報をセットする
	 *
	 * @param map
	 *            　ヘッダー情報(Key,Value)のHashMap
	 */
	public static void setRequestHeaders(HashMap<String, String> map) {
		httpHeaders = map;
	}

	/**
	 * Zip解凍コールバックを登録する
	 * 
	 * @param l
	 */
	public static void setOnDecodeZipListener(OnDecodeZipListener l) {
		decodeZipListener = l;
	}

	/**
	 * コンストラクタ
	 *
	 * @param url_path
	 *            ダウンロードURL
	 */
	public CsvDataManager(String url_path) {
		this.mDownloadUrl = url_path;
	}

	/**
	 * キャッシュ保存フォルダの設定(必須)
	 *
	 * @param path
	 *            キャッシュフォルダ
	 */
	public void setCashFileName(String filename) {
		mCashPath = cashDir + File.separator + filename;
	}

	/**
	 * 解凍データ展開用のフォルダ名設定(必須)
	 *
	 * @param dirname
	 *            フォルダ名
	 */
	public void setTempDir(String dirname) {
		mTempDir = dataDir + File.separator + dirname;
	}

	/**
	 * csvファイル読み込み
	 *
	 * @param filename
	 *            ファイル名
	 * @return 読み込んだバッファ
	 */
	protected ArrayList<String[]> loadCsv(String filename) {
		return LibUtil.loadCsv(filename, Consts.ENCODING_SHIFTJIS);
	}

	/**
	 * データ読み込み
	 *
	 * @return 成否
	 */
	public boolean readData(boolean doDownload, boolean enableCash) {
		boolean result = false;
		if (mCashPath == null) {
			//
			LibUtil.LogD("ダウンロードファイルの保存先を指定してください");
			return false;
		}
		if (mTempDir == null) {
			//
			LibUtil.LogD("解凍ファイルの保存先を指定してください");
			return false;
		}
		File unzipped = new File(mTempDir);

		// ダウンロードしない設定でも、展開データもキャッシュも無ければダウンロードする
		if (!doDownload) {
			if (!unzipped.exists() && !checkDownloadCash(mCashPath)) {
				doDownload = true;
			}
		}
		boolean dounzip = false;
		try {
			if (doDownload) {
				LibUtil.LogD("Download zip file:" + mDownloadUrl);
				// データをDL
				downloader = new Downloader();
				result = downloader.downloadUrl(mDownloadUrl, mCashPath,
						httpHeaders);
				dounzip = true;
			}
			if (isCanceled)
				return false;
			LibUtil.LogD("Download result=" + result);
			if (!result) {
				// 展開済みデータをチェック
				result = unzipped.exists();
			}
			if (!result || enableCash) {
				// DL失敗時、残っているキャッシュをチェック
				result = checkDownloadCash(mCashPath);
				dounzip = true;
			}
			if (!result) {
				return false;
			}
			if (isCanceled)
				return false;

			if (dounzip) {
				LibUtil.LogD("unzip start===");
				// すでにあれば、一旦削除する
				if (unzipped.exists()) {
					LibUtil.removeAllFile(unzipped);
				}
				// データを解凍
				if (decodeZipListener != null) {
					result = decodeZipListener.onDecodeZip(mCashPath, mTempDir);
				} else {
					result = LibUtil.unzip(mCashPath, mTempDir);
				}
			}

			if (isCanceled)
				return false;
			LibUtil.LogD("unzip finish===");

			ArrayList<String[]> csvLines = null;
			String filename = "";
			if (result) {
				// csvファイルとしてもらうことを想定。
				// ひとまずすべてメモリに展開する
				// 容量やアクセス速度に問題があればSQLiteに置いて、
				// アクティブなノード情報だけ持つなど検討する

				// 画像セットcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_IMAGESET, mTempDir);
				csvLines = loadCsv(filename);
				result = parseImageSetList(csvLines);
			}
			if (result) {
				// オブジェクトリストcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_OBJECTLIST,
						mTempDir);
				csvLines = loadCsv(filename);
				result = parseObjectList(csvLines);
			}
			if (result) {
				// 条件リストcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_CONDITIONLIST,
						mTempDir);
				csvLines = loadCsv(filename);
				result = parseConditionList(csvLines);
			}
			if (result) {
				// テキストセットリストcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_TXTSETLIST,
						mTempDir);
				csvLines = loadCsv(filename);
				result = parseTextSetList(csvLines);
			}
			if (result) {
				// アクションリストcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_ACTIONLIST,
						mTempDir);
				csvLines = loadCsv(filename);
				result = parseActionList(csvLines);
			}
			if (result) {
				// コントロールリストcsv
				filename = getCsvFilename(Consts.PREFIX_CSV_CONTROLLLIST,
						mTempDir);
				csvLines = loadCsv(filename);
				result = parseControlList(csvLines);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}

	/**
	 * cancel loading
	 */
	public void cancelLoading() {
		if (downloader != null)
			downloader.cancel();
		downloader = null;
		isCanceled = true;
	}

	/**
	 * 指定のprefixで始まるcsvファイルのフルパスを取得する
	 *
	 * @param prefix
	 * @param path
	 * @return
	 */
	private String getCsvFilename(String prefix, String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File list[] = file.listFiles();
			for (File f : list) {
				String name = getCsvFilename(prefix, f.getAbsolutePath());
				if (name != null)
					return name;
			}
		} else {
			if (file.getName().startsWith(prefix)) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	/**
	 * キャッシュファイルが存在するかチェック<br>
	 *
	 * @param path
	 * @param enableCash
	 *            キャッシュ有効フラグ
	 * @return
	 */
	boolean checkDownloadCash(String path) {
		File f = new File(path);
		// キャッシュある
		return f.exists();
	}

	/**
	 * 画像データクラス
	 *
	 */
	private class ImageData {
		public int setid = -1;
		public String filepath = null;
	}

	/**
	 * 全画像データをリストにする
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 画像データの配列
	 */
	private ArrayList<ImageData> getAllSet(ArrayList<String[]> csvLines) {
		ArrayList<ImageData> list = new ArrayList<ImageData>();
		for (String buff[] : csvLines) {
			ImageData d = new ImageData();
			try {
				// [0]のidは無視
				d.setid = Integer.valueOf(buff[1]);
				d.filepath = buff[2];
				list.add(d);
			} catch (NumberFormatException e) {
			}
		}
		return list;
	}

	/**
	 * ImageSetから該当のidを検索
	 *
	 * @param id
	 *            　
	 * @return ImageSet
	 */
	private ImageSet getImageSet(int id) {
		if (imageSetList == null)
			return null;
		for (ImageSet is : imageSetList) {
			if (is.getId() == id)
				return is;
		}
		return null;
	}

	/**
	 * 画像セットリストのパース
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 成否
	 */
	boolean parseImageSetList(ArrayList<String[]> csvLines) {

		ArrayList<ImageData> images = getAllSet(csvLines);
		imageSetList = new ArrayList<ImageSet>();

		int current_setid = -1;
		ImageSet set = null;

		for (ImageData d : images) {

			if (current_setid != d.setid) {
				// 新しいset
				if (set != null) {
					imageSetList.add(set);
				}
				set = new ImageSet();
				set.setId(d.setid);
				current_setid = d.setid;
			}
			set.addImage(d.filepath);
		}
		if (set != null) { // 最後の追加
			imageSetList.add(set);
		}
		return true;
	}

	/**
	 * ESCObjectをタイプ別に作成
	 *
	 * @param type
	 *            Room/Wall/Node/Layer/Item/ItemLayer
	 * @return オブジェクトのインスタンス
	 */
	ESCObject newObject(String type) {
		if (Consts.ROOM.equals(type)) {
			return new Room();
		} else if (Consts.WALL.equals(type)) {
			return new Wall();
		} else if (Consts.LAYER.equals(type)) {
			return new Layer();
		} else if (Consts.ITEM.equals(type)) {
			return new Item();
		} else if (Consts.ITEMLAYER.equals(type)) {
			return new ItemLayer();
		} else if (Consts.OPENING.equals(type)) {
			return new EventOpening();
		} else if (Consts.ENDING.equals(type)) {
			return new EventEnding();
		} else if (Consts.STRING.equals(type)) {
			return new Moji();
		} else if (Consts.SUBITEM.equals(type)) {
			return new SubItem();
		} else if (Consts.ITEMSETLAYER.equals(type)) {
			return new ItemSetLayer();
		} else if (Consts.CHARACTER.equals(type)) {
			return new EventCharacter();
		} else if (Consts.MINIGAME.equals(type)) {
			return new MiniGame();
		} else if (Consts.EVENT.equals(type)) {
			return new Event();
		} else if (Consts.HINTEVENT.equals(type)) {
			return new EventHint();
		} else {
			// default Node
			return new Node();
		}
	}

	/**
	 * ノードリストからRoomオブジェクトを取得
	 *
	 * @return Roomオブジェクト
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * ノードリストからNodeオブジェクトを取得
	 *
	 * @param id
	 *            オブジェクトid
	 * @return Nodeオブジェクト
	 */
	public Node getNode(int id) {
		if (nodeList == null)
			return null;
		for (Node n : nodeList) {
			if (n.getId() == id)
				return n;
		}
		return null;
	}

	/**
	 * ノードリストからLayerオブジェクトを取得
	 *
	 * @param id
	 *            オブジェクトid
	 * @return Layerオブジェクト
	 */
	public Node getLayer(int id) {
		Node node = getNode(id);
		if (node instanceof Layer) {
			return node;
		}
		//
		LibUtil.LogD("id=[" + id + "]はNodeオブジェクトではありません");
		return null;
	}

	/**
	 * アイテムリストからItemオブジェクトを検索
	 *
	 * @param id
	 *            　アイテムID
	 * @return　Itemオブジェクト
	 */
	public Node getItem(int id) {
		if (itemList == null)
			return null;

		for (Node item : itemList) {
			if (item.getId() == id)
				return item;
		}
		//
		// LibUtil.LogD("getItem::id=[" + id + "]はItem/ItemLayerオブジェクトではありません");
		return null;
	}

	/**
	 * アイテムリストからItemLayerオブジェクトを検索
	 *
	 * @param id
	 *            　アイテムID
	 * @return　Itemオブジェクト
	 */
	public ItemLayer getItemLayer(int id) {
		Node item = getItem(id);
		if (item instanceof ItemLayer) {
			return (ItemLayer) item;
		}
		//
		LibUtil.LogD("id=[" + id + "]はItemLayerオブジェクトではありません");
		return null;
	}

	/**
	 * 条件オブジェクトを取得
	 *
	 * @param id
	 *            条件id
	 * @return　Conditionオブジェクト
	 */
	public Condition getCondition(int id) {
		for (Condition c : conditionList) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	/**
	 * テキストセットを検索
	 *
	 * @param id
	 *            検索するid
	 * @return テキストセットオブジェクト
	 */
	private TextSet getTextSet(int id) {
		if (textSetList == null)
			return null;
		for (TextSet ts : textSetList) {
			if (ts.getId() == id)
				return ts;
		}
		return null;
	}

	/**
	 * 動作オブジェクトを取得
	 *
	 * @param id
	 *            動作id
	 * @return Actionオブジェクト
	 */
	public Action getAction(int id) {
		for (Action a : actionList) {
			if (a.getId() == id)
				return a;
		}
		return null;
	}

	/**
	 * オープニング・エンディングオブジェクトを取得
	 *
	 * @param id
	 *            　オブジェクトid
	 * @return オープニング・エンディングオブジェクト
	 */
	public Event getEvent(int id) {
		if (eventList == null)
			return null;
		for (Event e : eventList) {
			if (e.getId() == id)
				return e;
		}
		return null;
	}

	/**
	 * @return action list(array)
	 */
	public Action[] getActionList() {
		return actionList.toArray(new Action[actionList.size()]);
	}

	/**
	 * @return condition list(array)
	 */
	public Condition[] getConditionList() {
		return conditionList.toArray(new Condition[conditionList.size()]);
	}

	/**
	 * @return control list(array)
	 */
	public Control[] getControlList() {
		return controlList.toArray(new Control[controlList.size()]);
	}

	/**
	 * オブジェクトリストのパース
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 成否
	 */
	boolean parseObjectList(ArrayList<String[]> csvLines) {
		nodeList = new ArrayList<Node>();
		itemList = new ArrayList<Node>();

		try {
			for (int n = 0; n < csvLines.size(); n++) {
				String[] buff = csvLines.get(n);
				// 0:type[Room,Wall,Node,Layer,Item,ItemLayer,String,SubItem,Event,EventCharacter]
				String type = buff[0].trim();
				ESCObject object = newObject(type);
				// 1:id
				int id = -1;
				try {
					id = Integer.valueOf(buff[1].trim());
					object.setId(id);
				} catch (NumberFormatException e) {
					object.setId(-1); // 無効
				}
				// 2:parent_id
				int parent_id = -1;
				if (buff.length > 2) {
					try {
						parent_id = Integer.valueOf(buff[2].trim());
					} catch (NumberFormatException e) {
					}
				}
				if (nodeList != null && parent_id >= 0) {
					Node parent = null;
					if (object instanceof Item || object instanceof ItemLayer) {
						parent = getItem(parent_id);
					} else {
						parent = getNode(parent_id);
					}
					if (parent == null) {
						//
						LibUtil.LogD("parseObjectList:親アイテム/ノードがありません。n=" + n
								+ ", parent_id=[" + parent_id + "]");
						return false;
					} else {
						try {
							((Node) object).setParent(parent);
						} catch (ClassCastException e) {
							//
							LibUtil.LogD("parseObjectList:ノード以外のオブジェクトに親を設定しようとしました。n="
									+ n + ", parent_id=[" + parent_id + "]");
							return false;

						}
					}

				}
				// 3:position(lx,ly)-(rx,ry)
				if (object instanceof Layer || object instanceof ItemLayer) { // 座標があるのはLayerのみ
					Layer layer = (Layer) object;
					// 位置情報をパース
					Position position = new Position();
					if (object instanceof ItemLayer) {
						if (!position.parsePositionItemLayer(buff[3].trim())) {
							return false;
						}
					} else {
						if (!position.parsePosition(buff[3].trim())) {
							return false;
						}
					}
					// Layerオブジェクトにセット
					layer.setPosition(position);
				}
				// 4:imageset_id
				id = -1;
				if (buff.length > 4) {
					try {
						id = Integer.valueOf(buff[4].trim());
					} catch (NumberFormatException e) {
					}
					try {
						if (id >= 0) {
							if (object instanceof Event) {
								((Event) object).setImageSet(getImageSet(id));
							} else if (object instanceof EventCharacter) {
								((EventCharacter) object)
										.setImageSet(getImageSet(id));
							} else {
								((Node) object).setImageSet(getImageSet(id));
							}
						}
					} catch (ClassCastException e) {
						//
						LibUtil.LogD("parseObjectList:画像セットid[" + buff[4]
								+ "]が不正です。n=" + n);
					}
				}

				// itemのデフォルト描画indexをセット
				if (object instanceof SubItem) {
					Item parent = (Item) ((SubItem) object).getParent();
					((SubItem) object)
							.setDisplayIndex(parent.getDisplayIndex());
				} else if (object instanceof Item) {
					// 記述順にセットする
					((Item) object).setDisplayIndex(itemList.size());
				}

				// 5:初期パラメータ1[maxUseNum/image_id/初期文字列/itemSetNum/表示名]
				id = -1;
				if (buff.length > 5) {
					// 文字
					if (object instanceof Moji) {
						((Moji) object).init(buff[5].trim());
					} else if (object instanceof EventCharacter) {
						((EventCharacter) object).setName(buff[5].trim());
					} else {
						try {
							id = Integer.valueOf(buff[5].trim());
						} catch (NumberFormatException e) {
						}
						if (id > 0) {
							if (object instanceof ItemSetLayer) {
								((ItemSetLayer) object).setItemNum(id);
							} else if (object instanceof Item) {
								((Item) object).setMaxUseNum(id);
							} else if (object instanceof Layer) {
								((Node) object).setActiveImageId(id);
							} else if (object instanceof MiniGame) {
								((MiniGame) object).setType(id);
							}
						}
					}
				}
				// 6:初期パラメータ2(表示フラグ/文字色/アイテム表示index)
				if (buff.length > 6) {
					String flag = buff[6].trim();
					if ("FALSE".equals(flag.toUpperCase())) {
						if (object instanceof Layer) {
							((Node) object).flagOFF(Status.FLAG_DISP);
						}
					} else {
						if (object instanceof Moji) {
							// 文字色
							int color = LibUtil.toARGB(flag.split(","));
							((Moji) object).setColor(color);
						} else if (object instanceof Item) {
							// アイテム表示index
							try {
								int index = Integer.valueOf(flag);
								((Item) object).setDisplayIndex(index);
							} catch (NumberFormatException e) {
							}
						}
					}
				}

				// 7.初期パラメータ3(文字サイズ)
				if (buff.length > 7) {
					int size = 0;
					try {
						size = Integer.valueOf(buff[7].trim());
					} catch (NumberFormatException e) {
					}
					if (object instanceof Moji) {
						((Moji) object).setSize(size * 0.8f);
					}
				}
				if (object instanceof Item || object instanceof ItemLayer) {
					itemList.add((Node) object);
				} else if (object instanceof Room) {
					room = (Room) object;
				} else if (object instanceof Event) {
					if (eventList == null) {
						eventList = new ArrayList<Event>();
					}
					eventList.add((Event) object);
				} else if (object instanceof EventCharacter) {
					if (charaList == null) {
						charaList = new ArrayList<EventCharacter>();
					}
					charaList.add((EventCharacter) object);
				} else if (object instanceof Node) {
					nodeList.add((Node) object);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 条件リストのパース
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 成否
	 */
	boolean parseConditionList(ArrayList<String[]> csvLines) {
		conditionList = new ArrayList<Condition>(csvLines.size());
		try {
			for (int n = 0; n < csvLines.size(); n++) {
				String[] buff = csvLines.get(n);
				Condition condition = new Condition();
				// 0:id
				int id = -1;
				try {
					id = Integer.valueOf(buff[0]);
					condition.setId(id);
				} catch (NumberFormatException e) {
					//
					LibUtil.LogD("parseConditionList:idに数値以外が指定されています。n=" + n
							+ "," + LibUtil.arrayToString(buff));
				}
				// 1:type_id
				id = -1;
				if (buff.length > 1 && buff[1].length() > 0) {
					try {
						id = Integer.valueOf(buff[1]);
						condition.setType(id);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseConditionList:type_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 2:param1
				String params[] = null;
				if (buff.length > 2 && buff[2].length() > 0) {
					params = buff[2].split(",");
					condition.setParam1(params);
				}
				// 3:param2
				if (buff.length > 3 && buff[3].length() > 0) {
					params = buff[3].split(",");
					condition.setParam2(params);
				}
				// 4:object_id
				id = -1;
				if (buff.length > 4 && buff[4].length() > 0) {
					try {
						id = Integer.valueOf(buff[4]);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseConditionList:オブジェクトidに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
					if (id >= 0) {
						ESCObject object = getNode(id);
						if (object == null) {
							object = getItem(id);
						}
						if (object == null) {
							//
							LibUtil.LogD("parseConditionList:対象オブジェクトid[" + id
									+ "]が見つかりません。n=" + n);
							return false;
						}
						condition.setTarget(object);
					}
				}
				// 5:next
				id = -1;
				if (buff.length > 5 && buff[5].length() > 0) {
					try {
						id = Integer.valueOf(buff[5].trim());
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseConditionList:next_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
					if (id >= 0) {
						condition.setNext(id);
					}
				}

				conditionList.add(condition);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * テキストセットリストのパース
	 *
	 * @param csvLines
	 *            　読み込んだcsvのバッファ(行単位)
	 * @return　成否
	 */
	boolean parseTextSetList(ArrayList<String[]> csvLines) {
		textSetList = new ArrayList<TextSet>(csvLines.size());
		try {
			for (int n = 0; n < csvLines.size(); n++) {
				String[] buff = csvLines.get(n);
				TextSet ts = new TextSet();

				// 0:id
				int id = -1;
				try {
					id = Integer.valueOf(buff[0]);
					ts.setId(id);
				} catch (NumberFormatException e) {
					//
					LibUtil.LogD("parseTextSetList:idに数値以外が指定されています。n=" + n
							+ "," + LibUtil.arrayToString(buff));
				}
				// 1:id
				int chrid = -1;
				if (buff.length > 1 && buff[1].trim().length() > 0) {
					try {
						chrid = Integer.valueOf(buff[1].trim());
						ts.setCharacterId(chrid);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseTextSetList:chara_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 2:ja 3:en ...
				for (int i = 2; i < buff.length; i++) {
					ts.setString(i - 2, buff[i]);
				}
				textSetList.add(ts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * アクションリストのパース
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 成否
	 */
	boolean parseActionList(ArrayList<String[]> csvLines) {
		actionList = new ArrayList<Action>(csvLines.size());
		try {
			for (int n = 0; n < csvLines.size(); n++) {
				String[] buff = csvLines.get(n);
				Action action = new Action();
				// 0:id
				int id = -1;
				try {
					id = Integer.valueOf(buff[0]);
					action.setId(id);
				} catch (NumberFormatException e) {
					//
					LibUtil.LogD("parseActionList:idに数値以外が指定されています。n=" + n
							+ "," + LibUtil.arrayToString(buff));
				}
				// 1:type_id
				id = -1;
				if (buff.length > 1 && buff[1].length() > 0) {
					try {
						id = Integer.valueOf(buff[1]);
						action.setType(id);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseActionList:type_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 2:param1
				String params[] = null;
				if (buff.length > 2 && buff[2].length() > 0) {
					params = buff[2].split(",");
					action.setParam1(params);
				}
				// 3:param2
				if (buff.length > 3 && buff[3].length() > 0) {
					params = buff[3].split(",");
					action.setParam2(params);
				}
				// 4:object_id
				id = -1;
				if (buff.length > 4 && buff[4].length() > 0) {
					try {
						id = Integer.valueOf(buff[4]);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseActionList:オブジェクトidに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
					if (id >= 0) {
						if (action.getType() == ActionType.ACTION_SERIF
								|| action.getType() == ActionType.ACTION_HINT
								|| action.getType() == ActionType.ACTION_TEXT) {
							// セリフかヒントならテキストセットから取得
							TextSet ts = getTextSet(id);
							if (ts == null) {
								LibUtil.LogD("parseActionList:対象テキストセットid["
										+ id + "]が見つかりません。n=" + n);
								return false;
							}
							action.setTarget(ts);
						} else if (action.getType() == ActionType.ACTION_EVENT) {
							// event
							Event event = getEvent(id);
							if (event == null) {
								LibUtil.LogD("parseActionList:対象オブジェクトid[" + id
										+ "]がEventリストに見つかりません。n=" + n);
								return false;
							}
							action.setTarget(event);
						} else {
							// オブジェクト
							Node object = getNode(id);
							if (object == null) {
								object = getItem(id);
							}
							if (object == null) {

								LibUtil.LogD("parseActionList:対象オブジェクトid[" + id
										+ "]がNodeリストに見つかりません。n=" + n);
								return false;
							}
							action.setTarget(object);
						}
					}
				}
				// 5:next
				id = -1;
				if (buff.length > 5 && buff[5].length() > 0) {
					try {
						id = Integer.valueOf(buff[5].trim());
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseActionList:next_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
						return false;
					}
					if (id >= 0) {
						action.setNext(id);
					}
				}

				actionList.add(action);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 操作リストのパース
	 *
	 * @param csvLines
	 *            読み込んだcsvのバッファ(行単位)
	 * @return 成否
	 */
	boolean parseControlList(ArrayList<String[]> csvLines) {
		controlList = new ArrayList<Control>(csvLines.size());
		try {
			for (int n = 0; n < csvLines.size(); n++) {
				String[] buff = csvLines.get(n);
				Control ctrl = new Control();
				Controllable object = null;
				// 0:id
				int id = -1;
				try {
					id = Integer.valueOf(buff[0].trim());
					ctrl.setId(id);
				} catch (NumberFormatException e) {
					//
					LibUtil.LogD("parseControlList:オブジェクトidに数値以外が指定されています。n="
							+ n + "," + LibUtil.arrayToString(buff));
				}
				// 1:object_id
				id = -1;
				if (buff.length > 1 && buff[1].length() > 0) {
					try {
						id = Integer.valueOf(buff[1].trim());
						if (id == 0) {
							if (room == null) {
								LibUtil.LogD("parseControlList:対象オブジェクトid["
										+ id + "]が見つかりません。n=" + n);
								return false;
							} else {
								room.addControl(ctrl);
							}
						} else if (id > 0) {
							object = getNode(id);
							if (object == null) {
								object = getItem(id);
							}
							if (object == null) {
								object = getEvent(id);
							}
							if (object == null) {
								LibUtil.LogD("parseControlList:対象オブジェクトid["
										+ id + "]が見つかりません。n=" + n);
								return false;
							}
							object.addControl(ctrl);
						}
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseControlList:オブジェクトidに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					} catch (ClassCastException e) {
						//
						LibUtil.LogD("parseControlList:オブジェクト[" + id
								+ "]は、Nodeまたはその派生オブジェクトではありません。n=" + n);
						return false;
					}
				}
				// 2:collision
				if (buff.length > 2 && buff[2].length() > 0) {
					Collision collision = new Collision();
					if (object instanceof Item) {
						if (!collision.parsePositionItemLayer(buff[2].trim())) {
							return false;
						}
					} else {
						if (!collision.parsePosition(buff[2].trim())) {
							return false;
						}
					}
					ctrl.setCollision(collision);
				}
				// 3:type_id
				id = -1;
				if (buff.length > 3 && buff[3].length() > 0) {
					try {
						id = Integer.valueOf(buff[3].trim());
						ctrl.setType(id);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseControlList:type_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 4:condition_id
				id = -1;
				if (buff.length > 4 && buff[4].length() > 0) {
					try {
						id = Integer.valueOf(buff[4].trim());
						Condition condition = getCondition(id);
						if (condition == null) {
							//
							LibUtil.LogD("parseControlList:指定のcondition_id["
									+ id + "]が見つかりません。n=" + n);
							return false;
						}
						ctrl.setCondition(condition);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseControlList:condition_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 5:action_id
				id = -1;
				if (buff.length > 5 && buff[5].length() > 0) {
					try {
						id = Integer.valueOf(buff[5].trim());
						Action action = getAction(id);
						if (action == null) {
							//
							LibUtil.LogD("parseControlList:指定のaction_id[" + id
									+ "]が見つかりません。n=" + n);
							return false;
						}
						ctrl.setAction(action);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseControlList:action_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}
				// 6:next_id
				id = -1;
				if (buff.length > 6 && buff[6].length() > 0) {
					try {
						id = Integer.valueOf(buff[6].trim());
						ctrl.setNext(id);
					} catch (NumberFormatException e) {
						//
						LibUtil.LogD("parseControlList:next_idに数値以外が指定されています。n="
								+ n + "," + LibUtil.arrayToString(buff));
					}
				}

				controlList.add(ctrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @return get nodes array-list
	 */
	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	/**
	 * @return get items array-list
	 */
	public ArrayList<Node> getItemList() {
		return itemList;
	}

	/**
	 * @return get events array-list
	 */
	public ArrayList<Event> getEventList() {
		return eventList;
	}

	/**
	 * @return get characters array-list
	 */
	public ArrayList<EventCharacter> getCharacterList() {
		return charaList;
	}

	/**
	 * @return zip展開後のデータ保存パス
	 */
	public String getDataPath() {
		File cash = new File(mCashPath);
		String dataDir = cash.getName().substring(0,
				cash.getName().indexOf('.'));
		return mTempDir + File.separator + dataDir;
	}

	/** 拡張子mp3用のファイルフィルタ */
	FilenameFilter mMP3Filter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			boolean ret = name.endsWith(".mp3");
			return ret;
		}
	};

	/**
	 * @return SEファイルのパス
	 */

	public String[] getSEFilepath() {
		ArrayList<String> files = new ArrayList<String>();

		String data_path = getDataPath();
		String se_path = data_path + File.separator + "sound" + File.separator
				+ "SE"; // SEディレクトリ内のファイルをリストアップ
		File sedir = new File(se_path);
		if (!sedir.exists())
			return null;
		File[] list = sedir.listFiles(mMP3Filter);
		if (list == null || list.length == 0) {
			return null;
		}
		for (File f : list) {
			files.add(f.getAbsolutePath());
		}
		return LibUtil.copyToStringArray(null, files);
	}

	/**
	 * @return BGMファイルのパス
	 */
	public String getBGMFilepath() {

		String data_path = getDataPath();
		String se_path = data_path + File.separator + "sound" + File.separator
				+ "BGM";
		// BGMディレクトリ内のファイルをリストアップ
		File sedir = new File(se_path);
		if (!sedir.exists())
			return null;
		File[] list = sedir.listFiles(mMP3Filter);
		if (list != null && list.length > 0)
			return list[0].getAbsolutePath();
		return null;
	}

	/**
	 * @return BGMファイルのパス<br>
	 *         sound/BGM/(任意のフォルダ名)の1階層のみを検索
	 */
	public String[] getSubBGMFilepaths() {
		ArrayList<String> files = new ArrayList<String>();

		String data_path = getDataPath();
		String se_path = data_path + File.separator + "sound" + File.separator
				+ "BGM";
		// BGMディレクトリ内のファイルをリストアップ
		File sedir = new File(se_path);
		if (!sedir.exists())
			return null;
		File[] list = sedir.listFiles();
		if (list != null && list.length > 0) {
			for (File f : list) {
				if (f.isDirectory()) {
					ArrayList<String> l = getMp3Files(f);
					files.addAll(l);
				}
			}
		}
		return LibUtil.copyToStringArray(null, files);
	}

	/**
	 * ディレクトリ内のmp3ファイルをリストアップする<br>
	 * 階層を再帰的に辿っていないので要注意
	 * 
	 * @param dir
	 *            ディレクトリ
	 * @return mp3ファイル名のフルパスのリスト
	 */
	ArrayList<String> getMp3Files(File dir) {
		ArrayList<String> names = new ArrayList<String>();
		File[] list = dir.listFiles(mMP3Filter);
		if (list != null && list.length > 0) {
			for (File f : list) {
				names.add(f.getAbsolutePath());
			}
		}
		return names;
	}
}
