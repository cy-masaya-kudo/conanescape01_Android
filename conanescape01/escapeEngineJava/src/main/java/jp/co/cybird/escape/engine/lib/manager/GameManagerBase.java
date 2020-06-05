package jp.co.cybird.escape.engine.lib.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jp.co.cybird.escape.engine.lib.action.Action;
import jp.co.cybird.escape.engine.lib.action.Action.MoveType;
import jp.co.cybird.escape.engine.lib.condition.Condition;
import jp.co.cybird.escape.engine.lib.data.CsvDataManager;
import jp.co.cybird.escape.engine.lib.manager.OnSEPlayCallback.SEType;
import jp.co.cybird.escape.engine.lib.math.Collision;
import jp.co.cybird.escape.engine.lib.math.Position;
import jp.co.cybird.escape.engine.lib.minigame.MiniGameRunner;
import jp.co.cybird.escape.engine.lib.object.Control;
import jp.co.cybird.escape.engine.lib.object.Control.ControlType;
import jp.co.cybird.escape.engine.lib.object.MiniGame;
import jp.co.cybird.escape.engine.lib.object.Moji;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Room;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.TextSet;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;
import jp.co.cybird.escape.engine.lib.object.event.EventEnding;
import jp.co.cybird.escape.engine.lib.object.event.EventHint;
import jp.co.cybird.escape.engine.lib.object.event.EventOpening;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.object.item.ItemLayer;
import jp.co.cybird.escape.engine.lib.object.item.SubItem;
import jp.co.cybird.escape.engine.lib.util.Consts;
import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * ゲーム全体の管理クラス
 *
 * @author S.Kamba
 */
public abstract class GameManagerBase {

	public static boolean drawAllItems = false;

	/** 動作データ(後から検索する可能性があるのでリストを保持) */
	protected Action[] actionList = null;
	/** 条件データ(後から検索する可能性があるのでリストを保持) */
	protected Condition[] conditionList = null;
	/** 操作データ(後から検索する可能性があるのでリストを保持 */
	protected Control[] controlList = null;
	/** SEデータ */
	protected String[] seFilepaths = null;
	/** BGMデータ */
	protected String bgmFilepath = null;
	/** BGMデータ:sub */
	protected String[] subBgmFilepaths = null;

	/** 展開データフォルダ */
	protected String dataPath = null;

	/** ステージ番号 */
	protected int mStageNo = 0;
	/** Roomオブジェクト */
	protected Room mRoom = null;

	/** アイテム詳細画面制御 */
	protected boolean isItemDetailShowing = false;

	/** アクティブ状態変更コールバック */
	OnActiveChangeCallback onActiveChangeCallback = null;
	/** 描画コールバック */
	OnDrawCallback onDrawCallback = null;
	/** SEコールバック */
	OnSEPlayCallback onSECallback = null;
	/** イベントコールバック */
	OnEventCallback onEventCallback = null;

	/** オープニング・エンディングイベント実施中 */
	protected boolean isRunningEvent = false;
	/** セリフ表示中 */
	protected boolean isShowingSerif = false;
	/** セリフ表示中動作有効フラグ */
	protected boolean isActionEnableShowingSerif = false;
	/** ヒント表示中 */
	protected boolean isShowingHint = false;

	/** CsvManager */
	CsvDataManager manager = null;

	/** serif表示制御 */
	SerifDisplay mSerifDisplay = new SerifDisplay();
	EventHint hintEvent = null;

	/** アイテム欄の数 */
	int itemAreaNum = Consts.ITEM_NUM_MAX;
	/** サムネイル表示中アイテムの管理 */
	Node thumbItems[] = new Item[Consts.ITEM_NUM_MAX];

	/** miniゲーム用 */
	boolean isRunningMiniGame = false;
	MiniGame runningMiniGame = null;
	MiniGameRunner miniGmaeRunner = null;

	/** 直前の操作タイプを保存 */
	ControlType lastControlType = ControlType.NULL;

	/**
	 * コンストラクタ
	 *
	 * @param room
	 *            Roomオブジェクト
	 */
	public GameManagerBase() {
	}

	/**
	 * 言語をセット
	 *
	 * @param language
	 *            言語(LANGUAGE_JAPANESE/LANGUAGE_ENGLISH/etc)
	 */
	public void setLanguage(int language) {
		TextSet.setLanguage(language);
	}

	/**
	 * アイテム欄の数をセット
	 *
	 * @param num
	 *            　アイテム欄の数
	 */
	public void setItemAreaNum(int num) {
		itemAreaNum = num;
	}

	/**
	 * アイテム欄の数を取得
	 *
	 * @return アイテム欄の数
	 */
	public int getItemAreaNum() {
		return itemAreaNum;
	}

	/**
	 * データマネージャのフォルダ関係を初期化する<br>
	 * キャッシュフォルダとデータフォルダをセットしてください
	 */
	public abstract void initDataManagerDirs();

	/**
	 * Timer終了時の処理をUIスレッドで行うためのスレッド開始処理
	 *
	 * @param delay
	 *            タイマー時間（ミリ秒）
	 * @param callback
	 *            timer終了時に処理するコールバック
	 */
	public abstract void startTimerDoneOnUIThread(int delay,
			OnTimerDoneOnUIThread callback);

	/**
	 * ゲーム終了
	 */
	public abstract void gameFinish();

	/**
	 * 初期化
	 *
	 * @param dataSrc
	 *            データ(path)
	 * @param doDownload
	 *            サーバーからDLフラグ。 falseなら展開済みデータを読み込む
	 * @param enableCash
	 *            キャッシュ有効フラグ
	 */
	public boolean initialize(String dataSrc, boolean doDownload,
			boolean enableCash) {

		initDataManagerDirs();

		// データをダウンロードして解析
		manager = new CsvDataManager(dataSrc);
		manager.setCashFileName("data_" + mStageNo + ".zip");
		manager.setTempDir("stage_" + mStageNo);
		dataPath = manager.getDataPath();
		boolean result = manager.readData(doDownload, enableCash);
		if (manager == null)
			return false;
		if (result) {
			mRoom = manager.getRoom();
			mRoom.setNodeList(manager.getNodeList());
			mRoom.setItemList(manager.getItemList());
			mRoom.setEventList(manager.getEventList());
			mRoom.setCharacterList(manager.getCharacterList());
			initControl();
			actionList = manager.getActionList(); // 動作リスト保存
			conditionList = manager.getConditionList(); // 条件リスト保存
			controlList = manager.getControlList(); // 操作リスト保存
			seFilepaths = manager.getSEFilepath(); // SEファイル名保存
			bgmFilepath = manager.getBGMFilepath(); // BGMファイル名保存
			subBgmFilepaths = manager.getSubBGMFilepaths(); // BGMファイル名保存

			// ヒント用イベントを保存
			hintEvent = (EventHint) mRoom.getHintEvent();
		}
		return result;
	}

	/**
	 * cancel initialize
	 */
	public void cancelLoading() {
		if (manager != null) {
			manager.cancelLoading();
		}
		manager = null;
	}

	/**
	 * セーブデータから復元処理:FIXME:暫定
	 *
	 * @param nodeSaveFilename
	 *            ノード保存データファイルのフルパス
	 * @param itemSaveFilename
	 *            アイテム保存データファイルのフルパス
	 * @return 成否
	 */
	protected boolean restore(String nodeSaveFilename, String itemSaveFilename) {
		// ファイルを読み込み
		ArrayList<String[]> lines = LibUtil.loadCsv(nodeSaveFilename,
				Consts.ENCODING_UTF8);
		boolean result = mRoom.restoreNodes(lines);
		if (!result)
			return result;
		lines = LibUtil.loadCsv(itemSaveFilename, Consts.ENCODING_UTF8);
		result = mRoom.restoreItems(lines);
		return result;
	}

	/** @return the stageNo */
	public int getStageNo() {
		return mStageNo;
	}

	/**
	 * @param stageNo
	 *            the stageNo to set
	 */
	public void setStageNo(int stageNo) {
		this.mStageNo = stageNo;
	}

	/**
	 * Roomオブジェクトを取得
	 *
	 * @return
	 */
	public Room getRoom() {
		return mRoom;
	}

	/** コントロールを初期化 */
	public void initControl() {
		Node defaultWall = mRoom.getNode(1); // default壁はid=1に必ず置く
		setActiveObject(defaultWall, null);
	}

	/** 未初期化なら初期化処理を実行する */
	/*
	 * void _initNodes(Node activeObject) { // if
	 * (!activeObject.isFlagON(Status.FLAG_INIT)) {
	 * activeObject.runControll(this, ControlType.INIT, 0, 0);
	 * activeObject.flagON(Status.FLAG_INIT); ArrayList<Node> children =
	 * activeObject.getChildren(); if (children != null && children.size() > 0)
	 * { for (Node node : children) { if (!node.isFlagON(Status.FLAG_INIT)) {
	 * node.runControll(this, ControlType.INIT, 0, 0);
	 * node.flagON(Status.FLAG_INIT); } } } } }
	 */
	/**
	 * @param activeObject
	 *            the activeObject to set
	 */
	public void setActiveObject(Node activeObject, MoveType moveType) {
		mRoom.setActiveObject(activeObject);
		// _initNodes(activeObject);
		if (onActiveChangeCallback != null) {
			onActiveChangeCallback.onActiveNodeChanged(moveType);
		}
	}

	/**
	 * 描画コールバックを登録
	 *
	 * @param callback
	 *            　コールバックメソッド
	 */
	public void setOnDrawCallback(OnDrawCallback callback) {
		this.onDrawCallback = callback;
	}

	/** @return the isItemDetailShowing */
	public boolean isItemDetailShowing() {
		return isItemDetailShowing;
	}

	/** @return オープニングかエンディングが実行中ならtrue */
	public boolean isEventRunning() {
		if (isRunningEvent)
			return true;
		return false;
	}

	/**
	 * @param callback
	 *            コールバックメソッド
	 */
	public void setOnSECallback(OnSEPlayCallback callback) {
		this.onSECallback = callback;
	}

	public void setItemDetailShowing(boolean isItemDetailShowing,
			int display_index) {
		setItemDetailShowing(isItemDetailShowing, display_index, null, true);
	}

	/**
	 * @param isItemDetailShowing
	 *            the isItemDetailShowing to set
	 */
	public void setItemDetailShowing(boolean isItemDetailShowing,
			int display_index, Item item, boolean se_play_flag) {
		this.isItemDetailShowing = isItemDetailShowing;
		// Item item = null;
		if (!isItemDetailShowing) {
			// アクションを実行
			mRoom.onItemClose(this);
			// いったんアクティブアイテムをクリアする
			mRoom.setActiveItem(null);
			drawItemList();
			// テキストクリア
			drawText("", null, true);
			isShowingSerif = false;

			// 拡大終了を再生
			if (onSECallback != null) {
				onSECallback.onSEPlay(SEType.ZOOM_END);
			}

		} else {
			if (item == null) {
				item = getItemFromDisplayIndex(display_index);
			} else {
				putItemToDisplayIndex(display_index, item);
			}
			Item oldItem = mRoom.getZoomingItem();
			if (oldItem != null && oldItem != item) {
				mRoom.onItemClose(this);
			}
			// 拡大音を再生
			if (se_play_flag && onSECallback != null) {
				onSECallback.onSEPlay(SEType.ITEM_ZOOM);
			}

		}
		setZoomingItem(item);
	}

	/**
	 * アクティブノード変更コールバックを登録
	 *
	 * @param callback
	 *            コールバック
	 */
	public void setOnActiveChangeCallback(OnActiveChangeCallback callback) {
		this.onActiveChangeCallback = callback;
	}

	/** 描画 */
	public void draw() {
		if (mRoom == null)
			return;
		if (onDrawCallback != null) {
			onDrawCallback.onDrawCallback(getActiveObject());
		}
		if (isItemDetailShowing) {
			// アイテム詳細画面を描画
			onDrawCallback.onDrawItem(mRoom.getZoomingItem());
		}
	}

	/**
	 * アイテム欄を描画<br>
	 * 描画するのはサムネイルサイズである
	 *
	 * @param callback
	 */
	@SuppressWarnings("unused")
	public void drawItemList() {
		Node[] items = mRoom.getItemArray();
		boolean[] flags = new boolean[Consts.ITEM_NUM_MAX];
		for (int i = 0; i < Consts.ITEM_NUM_MAX; i++) {
			thumbItems[i] = null;
		}
		for (int i = 0; i < items.length; i++) {
			Node item = items[i];

			if (item instanceof ItemLayer) {
				// アイテムレイヤーは描画しない
			} else if (item instanceof SubItem
					&& !item.isFlagON(Status.FLAG_REPLACED)) {
				// サブアイテムは親と入れ替え済みでなければ表示しない
			} else if (item.isFlagON(Status.FLAG_GOT)
					&& !item.isFlagON(Status.FLAG_USED)) {
				int drawIndex = ((Item) item).getDisplayIndex();

				// 獲得済みだけ表示する
				onDrawCallback.onDrawItemThumb(drawIndex, item);
				flags[drawIndex] = true;
				thumbItems[drawIndex] = item;
			} else {
				int drawIndex = ((Item) item).getDisplayIndex();
				if (drawIndex >= itemAreaNum)
					continue;

				if (LibUtil.DEBUG && drawAllItems) {
					if (!flags[drawIndex]) {
						onDrawCallback.onDrawItemThumb(drawIndex, item);
						flags[drawIndex] = true;
						thumbItems[drawIndex] = item;
					}
				} else {
					if (!flags[drawIndex]) {
						onDrawCallback.onDrawItemThumb(drawIndex, null);
						// thumbItems[drawIndex] = item;
					}
				}
			}
		}
	}

	/**
	 * セリフ文字列を描画<br>
	 * 言語別に対応された文字列を取得している<br>
	 * 文字列がSJISなので、日本語・英語以外の対応は厳しい<br>
	 * 対応するには、csvをUTF8で出力する必要がある
	 *
	 * @param string
	 * @param chara_no
	 *            キャラクター番号
	 */
	public void drawText(String string, EventCharacter chara, boolean isEnd) {
		onDrawCallback.onDrawText(string, chara, isEnd);
	}

	/**
	 * ヒントを描画
	 *
	 * @param string
	 */
	public void drawHint(String string, EventCharacter chara, boolean isEnd) {
		onDrawCallback.onDrawHint(string, chara, isEnd);
	}

	/**
	 * SE再生コールバック呼び出し
	 */
	public void onSEPlay(SEType se_type) {
		if (onSECallback != null)
			onSECallback.onSEPlay(se_type);
	}

	/**
	 * タッチ中処理
	 *
	 * @param x
	 *            , y タッチしているスクリーン座標
	 */
	public void onTouchDown(int x, int y) {
		if (mRoom == null)
			return;

		if (isItemDetailShowing) {
			//
		} else if (isSerifShowing() && !isActionEnableShowingSerif) {
			//
		} else {
			mRoom.onTouchDown(this, x, y);
		}
	}

	/**
	 * タッチ終わり処理
	 *
	 * @param x
	 *            , y タッチしているスクリーン座標
	 */
	public void onTouchUp(int x, int y) {
		if (mRoom == null)
			return;
		if (isItemDetailShowing) {
			//
		} else if (isSerifShowing() && !isActionEnableShowingSerif) {
			//
		} else {
			mRoom.onTouchUp(this, x, y);
		}
	}

	/**
	 * クリック処理
	 *
	 * @param x
	 *            , y クリック座標
	 */
	public void onClick(int x, int y) {
		lastControlType = ControlType.NULL;
		if (mRoom == null)
			return;
		if (isRunningEvent) {
			if (isSerifShowing()) {
				if (mSerifDisplay.isEnd()) {
					// セリフ表示完了していれば消す
					clearSerif();
					// セリフ表示完了していればイベントを進行
					processEvent();
				} else {
					// セリフ表示中ならスキップ
					skipSerif();
				}
			} else {
				processEvent();
			}
		} else if (isSerifShowing() && !isActionEnableShowingSerif) {
			if (mSerifDisplay.isEnd()) {
				// セリフ表示完了していれば消す
				clearSerif();
			} else {
				// セリフ表示中ならスキップ
				skipSerif();
			}
		} else if (isHintShowing()) {
			if (mSerifDisplay.isEnd()) {
				clearSerif();
				if (hintEvent != null)
					hintEvent.runControll(this, ControlType.CLICK, x, y);
			} else {
				// セリフ表示中ならスキップ
				skipSerif();
			}
		} else if (isItemDetailShowing) {
			mRoom.onClickItem(this, x, y);
		} else {
			lastControlType = ControlType.CLICK;
			mRoom.onClick(this, x, y);
		}
	}

	// イベントを進める
	void processEvent() {
		try {
			mRoom.onClickEvent(this, 0, 0);
		} catch (Exception e) {
			mSerifDisplay.setEnd(true);
		}
	}

	/**
	 * アイテム欄クリック
	 *
	 * @param index
	 *            クリックしたアイテム欄のindex
	 */
	public void onClickItem(int displayIndex) {
		lastControlType = ControlType.NULL;
		if (mRoom == null)
			return;
		Item item = null;
		if (displayIndex >= 0 && displayIndex < getItemAreaNum()) {
			item = (Item) thumbItems[displayIndex];
		}
		//
		if (item == null) {
			// active itemクリア
			mRoom.setActiveItem(null);
		} else if (isSerifShowing() || isHintShowing() || isEventRunning()) {
			// なにもしない？
		} else {
			if (getActiveItem() == item) {
				// アクティブを解除
				mRoom.setActiveItem(null);
			} else {
				mRoom.setActiveItem(item);

				// アイテム選択音再生
				if (onSECallback != null) {
					onSECallback.onSEPlay(SEType.ITEM_SELECT);
				}
			}
		}

	}

	/** 右へフリック */
	public void onFlickRight() {
		lastControlType = ControlType.NULL;
		if (isHintShowing() || isEventRunning()) {
			return;
		} else if (isShowingSerif) {
			clearSerif();
		} else {
			Node node = mRoom.getActiveObject();
			lastControlType = ControlType.FLICK_RIGHT;
			node.runControll(this, ControlType.FLICK_RIGHT, 0, 0);
		}

	}

	/** 左へフリック */
	public void onFlickLeft() {
		lastControlType = ControlType.NULL;
		if (isHintShowing() || isEventRunning()) {
			return;
		} else if (isShowingSerif) {
			clearSerif();
		} else {
			Node node = mRoom.getActiveObject();
			lastControlType = ControlType.FLICK_LEFT;
			node.runControll(this, ControlType.FLICK_LEFT, 0, 0);
		}
	}

	/** 下へフリック */
	public void onFlickDown() {
		lastControlType = ControlType.NULL;
		if (isHintShowing() || isEventRunning()) {
			return;
		} else if (isShowingSerif) {
			clearSerif();
		} else if (isItemDetailShowing) {
			setItemDetailShowing(false, -1);
		} else {
			Node node = mRoom.getActiveObject();
			if (isRunningMiniGame) {
				miniGameStop();
			}
			lastControlType = ControlType.FLICK_DOWN;
			node.runControll(this, ControlType.FLICK_DOWN, 0, 0);
		}

	}

	/** HINTボタン */
	public void onHint(String payloadString) {
		if (isShowingSerif) {

		} else {
			setShowingHint(true);
			if (hintEvent != null) {
				hintEvent.init();
				hintEvent.setPayloadString(payloadString);
			}
			boolean runned = mRoom.runControll(this, ControlType.HINT, 0, 0);
			if (!runned) {
				onFailHint();
			} else {
				if (onEventCallback != null) {
					onEventCallback.onStartEvent();
				}
			}
		}
	}

	/** オリジナルのピクセルサイズとの比率 */
	public void setRatioToOriginalSize(float x, float y) {
		Position.setRatioToOriginalSize(x, y);
	}

	/** ゲームウィンドウサイズをセット */
	public void setWindowSize(int w, int h) {
		Collision.setWindowSize(w, h);
	}

	/**
	 * 判定ボックスの縦横分割数をセット
	 *
	 * @param i
	 *            判定ボックスX方向分割数
	 * @param j
	 *            判定ボックスY方向分割数
	 */
	public void setCollisionBoxNum(int i, int j) {
		Collision.setCollisionBoxSize(i, j);
	}

	/**
	 * アイテム拡大画面におけるスクリーン座標比率
	 *
	 * @param x
	 *            オフセットx
	 * @param y
	 *            オフセットy
	 */
	public void setItemZoomScreenRatio(float x, float y) {
		Position.setItemZoomScreenRatioToOriginalSize(x, y);
	}

	/**
	 * アイテム拡大画面におけるスクリーン座標のオフセット
	 *
	 * @param x
	 *            オフセットx
	 * @param y
	 *            オフセットy
	 */
	public void setItemZoomScreenPositionOffset(int x, int y) {
		Position.setItemZoomScreenPositionOffset(x, y);
	}

	/**
	 * font size 比率
	 */
	public void setFontSizeRatio(float density) {
		Moji.setSizeRatio(density);
	}

	/**
	 * データを解凍したフォルダを取得
	 *
	 * @return
	 */
	public String getDataPath() {
		return dataPath;
	}

	/**
	 * 画像の格納パスを取得
	 *
	 * @return
	 */
	public String getImagePath() {
		return dataPath + File.separator;
	}

	/**
	 * アクティブオブジェクトを取得
	 *
	 * @return
	 */
	public Node getActiveObject() {
		return mRoom.getActiveObject();
	}

	/** get the index of Active Item */
	public int getItemIndex(Item item) {
		return mRoom.getItemIndex(item);
	}

	/** get the active item */
	public Item getActiveItem() {
		return mRoom.getActiveItem();
	}

	/**
	 * @param index
	 *            the index to set active
	 */
	public void setActiveItemFromDisplayIndex(int index) {
		Item item = getItemFromDisplayIndex(index);
		setActiveItem(item);
	}

	Item getItemFromDisplayIndex(int index) {
		if (index >= getItemAreaNum())
			return null;

		return (Item) thumbItems[index];
	}

	void putItemToDisplayIndex(int index, Item item) {
		if (index >= getItemAreaNum())
			return;
		thumbItems[index] = item;
	}

	public void setActiveItem(Item item) {
		mRoom.setActiveItem(item);
		if (onActiveChangeCallback != null) {
			onActiveChangeCallback.onActiveItemChanged();
		}
	}

	/**
	 * @param index
	 */
	public void setZoomingItem(Item item) {
		mRoom.setZoomingItem(item);
	}

	/**
	 * アイテム獲得しているかチェック
	 *
	 * @param Item
	 * @return 獲得していればtrue
	 */
	public boolean isItemGet(Item item) {
		if (item.isFlagON(Status.FLAG_GOT) && !item.isFlagON(Status.FLAG_USED))
			return true;
		return false;
	}

	/**
	 * 指定のIDの動作を探す
	 *
	 * @param id
	 *            動作id
	 * @return Action
	 */
	public Action findAction(int id) {
		for (Action a : actionList) {
			if (a.getId() == id)
				return a;
		}
		return null;
	}

	/**
	 * 指定のIDの条件を探す
	 *
	 * @param id
	 *            条件id
	 * @return Condition
	 */
	public Condition findCondition(int id) {
		for (Condition c : conditionList) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	/**
	 * 指定のIDの操作セットを返す
	 *
	 * @param id
	 *            　操作id
	 * @return Control
	 */
	public Control findControl(int id) {
		for (Control c : controlList) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	/**
	 * 保存処理
	 */
	public boolean save(String nodeSaveFilename, String itemSaveFilename) {
		String str[] = mRoom.save();
		// ファイルに保存
		File f = new File(nodeSaveFilename);
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
			fw.write(str[0]);
			fw.flush();
			fw.close();

			f = new File(itemSaveFilename);
			fw = new FileWriter(f);
			fw.write(str[1]);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** ゲーム開始 */
	public void gameStart() {
		draw();
		drawItemList();
	}

	/**
	 * オープニングの実施
	 */
	public void startOpening() {
		Event opening = mRoom.getOpening();
		if (opening == null) {
			gameStart();
		} else {
			isRunningEvent = true;
			opening.init();
			mRoom.setRunningEvent(opening);
			opening.runControll(this, ControlType.RUN_EVENT, 0, 0);
			drawEvent();

			if (onEventCallback != null) {
				onEventCallback.onStartEvent();
			}
		}
	}

	/**
	 * エンディング開始
	 *
	 * @param endingNo
	 */
	public void startEnding(int endingNo) {
		Event ending = mRoom.getEnding(endingNo);
		if (ending == null) {
			gameFinish();
		} else {
			isRunningEvent = true;
			ending.init();
			mRoom.setRunningEvent(ending);
			ending.runControll(this, ControlType.RUN_EVENT, 0, 0);
			drawEvent();

			if (onEventCallback != null) {
				onEventCallback.onStartEvent();
			}

		}
	}

	/**
	 * イベント用画面描画
	 *
	 * @param image_name
	 *            画像ファイル名
	 */
	public void drawEvent() {
		if (mRoom == null)
			return;
		if (onDrawCallback != null) {
			onDrawCallback.onDrawEvent(mRoom.getRunningEvent());
		}
	}

	/**
	 * イベント用テキスト表示
	 *
	 * @param string
	 *            表示する文字列
	 * @param color
	 *            文字色
	 */
	public void drawEventText(String string, int color, EventCharacter chara,
			boolean isEnd) {
		if (onDrawCallback != null) {
			onDrawCallback.onDrawEventText(string, color, chara, isEnd);
		}

	}

	/** エンディング用のコインを描画 */
	public void drawCoin(boolean flag) {
		if (onDrawCallback != null) {
			onDrawCallback.onDrawEventCoin(flag);
		}
	}

	/** セリフ表示中フラグ変更 */
	public void setSerifShowing(boolean flag) {
		isShowingSerif = flag;
	}

	/** セリフ表示中フラグ取得 */
	public boolean isSerifShowing() {
		return isShowingSerif;
	}

	/** セリフクリア */
	public void clearSerif() {
		setSerifShowing(false);
		drawText("", null, true);
	}

	/** フラグクリア */
	public void reset() {
		isItemDetailShowing = false;
		isShowingSerif = false;
	}

	/** イベントテキストの実行保留アクションをセットする */
	public void setHoldAction(Action action) {
		Event event = mRoom.getRunningEvent();
		if (event != null) {
			event.setHoldingAction(action);
		} else {
			// Hintの場合
			hintEvent = (EventHint) mRoom.getHintEvent();
			if (hintEvent != null)
				hintEvent.setHoldingAction(action);
		}
	}

	/** 実行中のイベントを取得する */
	public Event getRunningEvent() {
		if (isHintShowing()) {
			return hintEvent;
		}
		return mRoom.getRunningEvent();
	}

	/** イベントを終了 */
	public void eventFinish() {
		isRunningEvent = false;
		Event event = getRunningEvent();
		drawEventText("", 0, null, true);
		if (onEventCallback != null) {
			onEventCallback.onFinishEvent();
		}

		if (event instanceof EventOpening) {
			gameStart();
		} else if (event instanceof EventEnding) {
			gameFinish();
		} else {
			if (event instanceof EventHint) {
				// ヒント終了をコールバック
				onFinishHint();
			}
			gameContinue();
		}
		mRoom.setRunningEvent(null);
	}

	/** 親アイテムの位置にサブアイテムを入れ替える */
	public void replaceItem(SubItem subitem) {
		mRoom.replaceItem(subitem);
	}

	/** セリフ表示中の動作を許可するフラグのセット */
	public void setActionEnableShowingSerif(boolean flag) {
		isActionEnableShowingSerif = flag;
	}

	/**
	 * @return セリフ表示中の動作を許可するかどうか
	 */
	public boolean isActionEnableShowingSerif() {
		return isActionEnableShowingSerif;
	}

	/** キャラクターidからオブジェクトを取得 */
	public EventCharacter getCharacter(int characterID) {
		return mRoom.getCharacter(characterID);
	}

	/** セリフ表示開始:文字を1文字ずつ表示する */
	public void startSerifShowing(TextSet ts) {
		if (ts == null) {
			return;
		}
		try {
			mSerifDisplay.init(ts);
			mSerifDisplay.start(this);
		} catch (Exception e) {
			mSerifDisplay.setEnd(true);
		}
	}

	/** セリフ表示スキップ:1文字ずつ表示中の文字列をすべて表示する */
	public void skipSerif() {
		try {
			mSerifDisplay.setDiaplayAll(this);
		} catch (Exception e) {
			// ひとまず終わらせる
			mSerifDisplay.setEnd(true);
		}
	}

	/** ミニゲーム実行中フラグ */
	public boolean isRunningMiniGame() {
		return isRunningMiniGame;
	}

	/** ミニゲーム実行インターフェースをセット */
	public void setMiniGameRunner(MiniGameRunner r) {
		miniGmaeRunner = r;
	}

	/** ミニゲーム開始 */
	public void miniGameRun(MiniGame mini) {
		//
		runningMiniGame = mini;
		isRunningMiniGame = true;
		if (miniGmaeRunner != null) {
			miniGmaeRunner.run(this, mini);
		}
	}

	/** ミニゲーム停止 */
	public void miniGameStop() {
		runningMiniGame = null;
		isRunningMiniGame = false;
	}

	/** 途中イベント発生 */
	public void startEvent(Event event) {
		isRunningEvent = true;
		event.init();
		mRoom.setRunningEvent(event);
		event.runControll(this, ControlType.RUN_EVENT, 0, 0);
		drawEvent();
		if (onEventCallback != null) {
			onEventCallback.onStartEvent();
		}
	}

	/** イベント終了後ゲーム再開 */
	public void gameContinue() {
		isRunningEvent = false;
		mRoom.setRunningEvent(null);
		setShowingHint(false);
		draw();
	}

	/** SE再生 */
	abstract public void playSE(String se_name);

	/** BGM再生 */
	abstract public void playBGM(String bgm_name);

	/** 最後の操作タイプ取得 */
	public ControlType getLastControlType() {
		return lastControlType;
	}

	/** ヒント表示フラグをセット */
	public void setShowingHint(boolean flag) {
		isShowingHint = flag;
	}

	/** ヒント表示フラグ取得 */
	public boolean isHintShowing() {
		return isShowingHint;
	}

	/** イベント関連コールバックをセット */
	public void setOnEventCallback(OnEventCallback callback) {
		onEventCallback = callback;
	}

	/** ヒント終了時処理 */
	void onFinishHint() {
		if (onEventCallback == null)
			return;
		onEventCallback.onFinishHint(hintEvent != null ? hintEvent
				.getPayloadString() : null);
	}

	/** ヒントエラー時処理 */
	public void onFailHint() {
		setShowingHint(false);
		if (onEventCallback == null)
			return;
		onEventCallback.onFailHint(hintEvent != null ? hintEvent
				.getPayloadString() : null);
	}

	OnTimerDoneOnUIThread mEffectDoneCallback = null;

	/** エフェクト描画；フェード */
	public void onDrawEffectFade(int color, int in_duration, int out_duration,
			OnTimerDoneOnUIThread callback) {
		mEffectDoneCallback = callback;
		if (onDrawCallback != null) {
			onDrawCallback.onDrawEffectFade(color, in_duration, out_duration);
		}
	}

	/** エフェクト描画：画面振動 */
	public void onEffectVibration(int duration, OnTimerDoneOnUIThread callback) {
		mEffectDoneCallback = callback;
		if (onDrawCallback != null) {
			onDrawCallback.onDrawEffectVibration(duration);
		}
	}

	/** エフェクト完了を通知 */
	public void setEffectFinished() {
		if (mEffectDoneCallback != null) {
			mEffectDoneCallback.onTimerDone(this);
		}
	}

	/** SEファイルのフルパスを取得 */
	public String getSEFilePath(String name) {
		String data_path = getDataPath();
		String path = data_path + File.separator + "sound" + File.separator
				+ "SE";
		String fullpath = path + File.separator + name;
		return fullpath;
	}

	/** BGMファイルのフルパスを取得 */
	public String getBGMFilePath(String name) {
		String data_path = getDataPath();
		String path = data_path + File.separator + "sound" + File.separator
				+ "BGM";
		String fullpath = path + File.separator + name;
		return fullpath;
	}
}
