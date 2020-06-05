package jp.co.cybird.escape.engine.lib.action;

import java.util.Stack;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.manager.OnTimerDoneOnUIThread;
import jp.co.cybird.escape.engine.lib.manager.OnSEPlayCallback.SEType;
import jp.co.cybird.escape.engine.lib.object.ESCObject;
import jp.co.cybird.escape.engine.lib.object.MiniGame;
import jp.co.cybird.escape.engine.lib.object.Moji;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Room;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.TextSet;
import jp.co.cybird.escape.engine.lib.object.Control.ControlType;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.object.item.SubItem;
import jp.co.cybird.escape.engine.lib.object.layer.ItemSetLayer;
import jp.co.cybird.escape.engine.lib.util.LibUtil;

/**
 * 動作クラス<br>
 * オブジェクトがタップされた時の動作を管理するクラス
 *
 * @author S.Kamba
 */
public class Action extends ESCObject {

	static boolean ActionLockFlag = false;

	/** アクションタイプ定義 */
	public enum ActionType {
		ACTION_NULL, // 特にない
		ACTION_MOVE, // 移動
		ACTION_BACK, // 戻る
		ACTION_ENDING, // エンディング
		ACTION_CHANGE, // 画像変更
		ACTION_CHANGEANIMATION, // 画像切替アニメーション
		ACTION_LOOP, // 画像ループ
		ACTION_LOCK, // ロックフラグ変更
		ACTION_DISP, // 表示フラグ変更
		ACTION_STACK, // スタックにPUSH
		ACTION_ITEM, // アイテムフラグを変更
		ACTION_USENUM, // アイテム使用回数加算
		ACTION_TIMER, // タイマー
		ACTION_SERIF, // メッセージが出る
		ACTION_SERIF_CLEAR, // メッセージを消去
		ACTION_HINT, // ヒント用
		ACTION_TEXT, // オープニング・エンディング用テキスト
		ACTION_COIN, // エンディング用・ハッピーコイン表示
		ACTION_PROCESS_EVENT, // イベントを進める
		ACTION_EVENT_IMAGE, // イベント画像変更
		ACTION_ITEM_PUT, // アイテム設置
		ACTION_ITEM_REMOVE, // 設置アイテム回収
		ACTION_MOJILOOP, // 文字ループ
		ACTION_ITEMPUT_IMAGE, // 設置アイテム画像変更
		ACTION_EVENT, // 途中イベント発生
		ACTION_SE, // SE再生
		ACTION_TAPCOUNT, // タップ回数
		ACTION_EFFECT_FADE, // フェードエフェクト
		ACTION_EFFECT_VIBERATION, // 画面振動エフェクト
		ACTION_BGM, // BGM再生
	}

	/** 移動タイプ */
	public enum MoveType {
		FLICK_LEFT, FLICK_RIGHT, MOVE_FADE, MOVE_ZOOM, MOVE_NONE,
	}

	/** アクションタイプ */
	ActionType mType = ActionType.ACTION_NULL;
	/** パラメータ1 */
	String[] mParam1 = null;
	/** パラメータ2 */
	String[] mParam2 = null;
	/** 実際に処理を反映する対象オブジェクト */
	ESCObject mTarget = null;
	/** 次のアクションId */
	int mNextId = -1;
	/** 次のアクション */
	Action mNextAction = null;
	/** カレントインデクス：連続処理系用 */
	int currentId = 0;

	/** アクションを禁止する */
	public static void setActionLock(boolean flag) {
		ActionLockFlag = flag;
	}

	/**
	 * コンストラクタ
	 *
	 * @param _id
	 *            オブジェクトID
	 * @param type
	 *            アクションタイプ
	 */
	public Action() {
	}

	public void setType(ActionType type) {
		mType = type;
	}

	public void setType(int type_id) {
		mType = ActionType.values()[type_id];
	}

	public ActionType getType() {
		return mType;
	}

	/**
	 * @return the mParameters
	 */
	public String[] getParam1() {
		return mParam1;
	}

	/**
	 * @return the mParameters
	 */
	public String[] getParam2() {
		return mParam2;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParam1(String[] parameters) {
		mParam1 = parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParam2(String[] parameters) {
		mParam2 = parameters;
	}

	/** @return the target */
	public ESCObject getTarget() {
		return mTarget;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(ESCObject target) {
		this.mTarget = target;
	}

	/** @return the next */
	public int getNext() {
		return mNextId;
	}

	/**
	 * @param next
	 *            the next to set
	 */
	public void setNext(int next_id) {
		this.mNextId = next_id;
	}

	/** アクションの実行 */
	public void run(GameManagerBase manager) {
		if (ActionLockFlag)
			return;

		boolean doNext = true;
		switch (mType) {
		case ACTION_MOVE:
			doNext = onMove(manager);
			break;
		case ACTION_BACK:
			doNext = onBack(manager);
			break;
		case ACTION_ENDING:
			doNext = onEnding(manager);
			break;
		case ACTION_CHANGE:
			doNext = onChange(manager);
			break;
		case ACTION_CHANGEANIMATION:
			doNext = onChangeAnimation(manager);
			break;
		case ACTION_LOOP:
			doNext = onLoop(manager);
			break;
		case ACTION_LOCK:
			doNext = onLock(manager);
			break;
		case ACTION_DISP:
			doNext = onDsip(manager);
			break;
		case ACTION_STACK:
			doNext = onStack(manager);
			break;
		case ACTION_ITEM:
			doNext = onItemFlagChange(manager);
			break;
		case ACTION_USENUM:
			doNext = onItemUseNum(manager);
			break;
		case ACTION_TIMER:
			doNext = onTimer(manager);
			break;
		case ACTION_SERIF:
			doNext = onSerif(manager);
			break;
		case ACTION_SERIF_CLEAR:
			doNext = onSerifClear(manager);
			break;
		case ACTION_HINT:
			doNext = onHint(manager);
			break;
		case ACTION_TEXT:
			doNext = onText(manager);
			break;
		case ACTION_COIN:
			doNext = onCoin(manager);
			break;
		case ACTION_PROCESS_EVENT:
			doNext = onProcessEvent(manager);
			break;
		case ACTION_EVENT_IMAGE:
			doNext = onEventImage(manager);
			break;
		case ACTION_ITEM_PUT:
			doNext = onItemPut(manager);
			break;
		case ACTION_ITEM_REMOVE:
			doNext = onItemRemove(manager);
			break;
		case ACTION_MOJILOOP:
			doNext = onMojiLoop(manager);
			break;
		case ACTION_ITEMPUT_IMAGE:
			doNext = onItemPutImage(manager);
			break;
		case ACTION_EVENT:
			doNext = onEvent(manager);
			break;
		case ACTION_SE:
			doNext = onPlaySE(manager);
			break;
		case ACTION_TAPCOUNT:
			doNext = onTapCount(manager);
			break;
		case ACTION_EFFECT_FADE:
			doNext = onEffectFade(manager);
			break;
		case ACTION_EFFECT_VIBERATION:
			doNext = onEffectVibration(manager);
			break;
		case ACTION_BGM:
			doNext = onPlayBGM(manager);
			break;
		case ACTION_NULL:
		default:
			// 何もしない
			break;
		}
		if (doNext) { // Timer動作した場合のみ、falseになる
			// 次の動作
			runNext(manager);
		}
	}

	/** 次の動作を実行 */
	void runNext(GameManagerBase manager) {
		if (mNextId >= 0) {
			if (mNextAction == null) {
				mNextAction = manager.findAction(mNextId);
			}
			if (mNextAction != null) {
				if (manager.isEventRunning()
						&& mNextAction.mType.equals(ActionType.ACTION_TEXT)) {
					manager.setHoldAction(mNextAction);
					return;
				}
				if (manager.isHintShowing()
						&& mNextAction.mType.equals(ActionType.ACTION_HINT)) {
					manager.setHoldAction(mNextAction);
					return;
				}
				mNextAction.run(manager);
			}
		}
	}

	/** Node移動 */
	boolean onMove(GameManagerBase manager) {
		Room room = manager.getRoom();
		Node node = room.getActiveObject();
		if (mTarget != null && mTarget instanceof Node) {
			Node targetNode = (Node) mTarget;
			targetNode.setControlParent(node);

			MoveType mt = MoveType.MOVE_ZOOM;
			if (mParam1 != null) {
				String m = mParam1[0].toLowerCase();
				if ("fade".equals(m)) {
					mt = MoveType.MOVE_FADE;
				} else if ("none".equals(m)) {
					mt = MoveType.MOVE_NONE;
				}
			} else {
				ControlType type = manager.getLastControlType();
				if (type == ControlType.FLICK_DOWN)
					mt = null;
				else if (type == ControlType.FLICK_LEFT)
					mt = MoveType.FLICK_LEFT;
				else if (type == ControlType.FLICK_RIGHT)
					mt = MoveType.FLICK_RIGHT;
			}
			if (node instanceof MiniGame) {
				// miniGameStop
				manager.miniGameStop();
			}
			if (mTarget instanceof MiniGame) {
				// miniGame開始
				manager.miniGameRun((MiniGame) mTarget);
			}
			manager.setActiveObject(targetNode, mt);
		}
		return true;
	}

	/** Node戻る */
	boolean onBack(GameManagerBase manager) {
		Room room = manager.getRoom();
		try {
			int backNum = Integer.valueOf(mParam1[0]);
			Node node = room.getActiveObject();
			for (int i = 0; i < backNum; i++) {
				node = node.getControlParent();
			}
			manager.setActiveObject(node, null);
		} catch (NumberFormatException e) {
			//
			LibUtil.LogD(e.getMessage());
		}
		return true;
	}

	/** ending */
	boolean onEnding(GameManagerBase manager) {
		try {
			int endingNo = Integer.valueOf(mParam1[0]);
			manager.startEnding(endingNo);
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		}

		return false;
	}

	/** 画像切替実施 */
	void changeImageId(final GameManagerBase manager) {
		int newImgId = Integer.valueOf(mParam1[0]);
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		node.setActiveImageId(newImgId);
		// 再描画発行
		if (node instanceof Item) {
			manager.drawItemList();
		}
		manager.draw();
	}

	/**
	 * 画像切替をUIスレッド上で行うためのコールバック用
	 */
	OnTimerDoneOnUIThread mOnChangedItemId = new OnTimerDoneOnUIThread() {
		@Override
		public void onTimerDone(GameManagerBase manager) {
			setActionLock(false);
			changeImageId(manager);
			runNext(manager);
		}
	};

	/** 画像切替 */
	boolean onChange(final GameManagerBase manager) {

		try {
			//
			if (mParam2 != null) {
				// 変更までの秒数指定あり
				int val = (int) (Double.valueOf(mParam2[0]) * 1000);
				setActionLock(true);
				manager.startTimerDoneOnUIThread(val, mOnChangedItemId);
				return false;
			} else {
				// すぐに変更
				changeImageId(manager);
			}
		} catch (NumberFormatException e) {
			//
			LibUtil.LogD(e.getMessage());
		}
		return true;
	}

	/**
	 * 画像切替をUIスレッド上で行うためのコールバック用
	 */
	OnTimerDoneOnUIThread mOnChangedItemIdAnimation = new OnTimerDoneOnUIThread() {
		@Override
		public void onTimerDone(GameManagerBase manager) {
			try {
				int anim_num = mParam1.length;
				if (currentId >= anim_num) {
					// 最後なので次を実行する
					setActionLock(false);
					runNext(manager);
				} else {
					// 画像を切り替える
					changeImageIdAnimation(manager);
					// 次のアニメーションタイマー
					int val = (int) (Double.valueOf(mParam2[0]) * 1000);
					manager.startTimerDoneOnUIThread(val,
							mOnChangedItemIdAnimation);

				}

			} catch (NumberFormatException e) {
				//
				LibUtil.LogD(e.getMessage());
			}
		}
	};

	/** sub:animation */
	void changeImageIdAnimation(GameManagerBase manager) {
		int newImgId = Integer.valueOf(mParam1[currentId]);
		Node node = (Node) mTarget;
		if (mTarget == null) {
			node = manager.getActiveObject();
		}
		node.setActiveImageId(newImgId);
		// 再描画発行
		manager.draw();
		currentId++;

	}

	/** 連続画像切替アニメーション */
	boolean onChangeAnimation(final GameManagerBase manager) {

		try {
			currentId = 0;
			// 最初の画像変更
			changeImageIdAnimation(manager);
			// 変更までの秒数指定あり
			int val = (int) (Double.valueOf(mParam2[0]) * 1000);
			setActionLock(true);
			manager.startTimerDoneOnUIThread(val, mOnChangedItemIdAnimation);
			return false;
		} catch (NumberFormatException e) {
			//
			LibUtil.LogD(e.getMessage());
		}
		return true;
	}

	/** 画像切替loop */
	boolean onLoop(GameManagerBase manager) {
		try {
			int max = Integer.valueOf(mParam1[0]);
			int add = Integer.valueOf(mParam2[0]);
			Node node = (Node) mTarget;
			if (mTarget == null) {
				node = manager.getActiveObject();
			}
			int newImgId = node.getActiveImageId() + add;
			if (newImgId < 0)
				newImgId = max - 1;
			if (newImgId >= max)
				newImgId = 0;

			// 画像変更
			node.setActiveImageId(newImgId);
			// 再描画発行
			manager.draw();
		} catch (NumberFormatException e) {
			//
			LibUtil.LogD(e.getMessage());
		}
		return true;
	}

	/** ロックフラグ変更 */
	boolean onLock(GameManagerBase manager) {
		Node target = (Node) mTarget;
		if (target == null) {
			target = manager.getActiveObject();
		}
		if (mParam1[0].toUpperCase().equals("TRUE")) {
			target.flagON(Status.FLAG_LOCKED);
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			target.flagOFF(Status.FLAG_LOCKED);
		}
		return true;
	}

	/** 表示フラグ変更 */
	boolean onDsip(GameManagerBase manager) {
		Node target = (Node) mTarget;
		if (mParam1[0].toUpperCase().equals("TRUE")) {
			target.flagON(Status.FLAG_DISP);
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			target.flagOFF(Status.FLAG_DISP);
		}
		manager.draw();
		return true;
	}

	/** 行動スタック */
	boolean onStack(GameManagerBase manager) {
		Node target = (Node) mTarget;
		if (target == null) {
			target = manager.getActiveObject();
		}
		Stack<Object> stack = target.getActionStack();
		if (mParam1[0].toUpperCase().equals("CLEAR")) {
			stack.clear();
		} else {
			try {
				Integer number = Integer.valueOf(mParam1[0]);
				stack.push(number);
			} catch (NumberFormatException e) {
				System.err.println("Action::onStack - パラメータ1が数値ではありません。");
			}
		}
		return true;
	}

	/** アイテムフラグ変更 */
	boolean onItemFlagChange(GameManagerBase manager) {
		Item target = (Item) mTarget;

		if (mParam1[0].toUpperCase().equals("TRUE")) {
			target.flagON(Status.FLAG_GOT);
			target.flagOFF(Status.FLAG_USED); // 使用済みフラグは削除する
			if (target instanceof SubItem) {
				if (!target.isFlagON(Status.FLAG_REPLACED)) { // 未置換の場合
					// サブアイテムだった場合、親の位置に入れ替える
					manager.replaceItem((SubItem) target);
					target.flagON(Status.FLAG_REPLACED);
				}
			}
			boolean showDetail = true;
			if (mParam2 != null && mParam2[0].toUpperCase().equals("FALSE")) {
				// Param2に明示的にFALSEがある場合のみ、獲得画面を表示しない
				showDetail = false;
			}
			if (showDetail) {
				manager.setActiveItem(target);
				// int index = manager.getItemIndex(target);
				int index = target.getDisplayIndex();
				manager.onSEPlay(SEType.ITEM_GET);
				manager.setItemDetailShowing(true, index, (Item) target, false);
			} else {
				manager.setActiveItem(null);
			}
			manager.draw();
			manager.drawItemList();
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			// target.mFlag &= ~Status.FLAG_GOT;
			// 一度獲得したアイテムの獲得フラグを消したくないので、使用済みフラグを立てる
			target.flagON(Status.FLAG_USED);
			Item active = manager.getActiveItem();
			if (active == target) {
				manager.setActiveItem(null);
			}
			manager.drawItemList();
		}
		return true;
	}

	/** アイテム使用回数加算 */
	boolean onItemUseNum(GameManagerBase manager) {
		Item target = (Item) mTarget;

		try {
			int add = Integer.parseInt(mParam1[0]);
			int used = target.getUsedNum();
			target.setUsedNum(used + add);
			if (target == manager.getActiveItem()) {
				if (target.isFlagON(Status.FLAG_USED))
					manager.setActiveItem(null);
			}
			manager.drawItemList();
		} catch (NumberFormatException e) {

		}
		return true;
	}

	/** タイマー：経過時間参照用なので、ここでシステムタイマーを取得しておく */
	boolean onTimer(GameManagerBase manager) {
		Node target = (Node) mTarget;
		if (target == null) {
			target = manager.getActiveObject();
		}
		if (mParam1 != null) {
			if ("0".equals(mParam1[0])) {
				target.setTimerCurrent(0);
			}
		} else if (target.getSavedTimer() == 0) {
			long cur = System.currentTimeMillis();
			target.setTimerCurrent(cur);
		}
		return true;
	}

	/**
	 * 画像切替をUIスレッド上で行うためのコールバック用
	 */
	OnTimerDoneOnUIThread mOnTextShowDone = new OnTimerDoneOnUIThread() {
		@Override
		public void onTimerDone(GameManagerBase manager) {
			manager.drawText("", null, true);
		}
	};

	/** セリフ表示 */
	boolean onSerif(GameManagerBase manager) {

		if (mTarget != null && mTarget instanceof TextSet) {
			TextSet ts = (TextSet) mTarget;
			manager.setSerifShowing(true);
			manager.startSerifShowing(ts);
			if (mParam1 != null && "TRUE".equals(mParam1[0].toUpperCase())) {
				manager.setActionEnableShowingSerif(true);
			}
		}
		return true;
	}

	/** セリフ消去 */
	boolean onSerifClear(GameManagerBase manager) {
		manager.drawText("", null, true);
		manager.setSerifShowing(false);
		return true;
	}

	/** ヒント表示 */
	boolean onHint(GameManagerBase manager) {

		if (mTarget != null && mTarget instanceof TextSet) {
			TextSet ts = (TextSet) mTarget;
			if (ts == null) {
				manager.onFailHint();
			} else {
				manager.setShowingHint(true);
				try {
					manager.startSerifShowing(ts);
				} catch (Exception e) {
					manager.onFailHint();
				}
			}
		}
		return true;
	}

	/** オープニング・エンディング用テキスト */
	boolean onText(GameManagerBase manager) {

		if (mTarget != null && mTarget instanceof TextSet) {
			TextSet ts = (TextSet) mTarget;
			int color = TextSet.DEFAULT_COLOR;
			if (mParam1 != null) {
				color = LibUtil.toARGB(mParam1);
			}
			ts.setColor(color);
			manager.setSerifShowing(true);
			manager.startSerifShowing(ts);
		}
		return true;
	}

	/** エンディング用・ハッピーコイン表示 */
	boolean onCoin(GameManagerBase manager) {
		manager.drawCoin(true);
		return true;
	}

	/** イベント進行 */
	boolean onProcessEvent(GameManagerBase manager) {
		Event event = manager.getRunningEvent();
		if (event.isEnded()) {
			manager.eventFinish();
		} else {
			Action action = event.getHoldingAction();
			if (action == null) {
				// 終わり
				event.setFinishFlag(true);
				manager.eventFinish();
			} else {
				action.run(manager);
				if (action.mNextAction == null) {
					// 終わり
					event.setFinishFlag(true);
				}
			}
			// セリフ音を再生
			manager.onSEPlay(SEType.SERRIF_PROCESS);
		}
		return true;
	}

	/** イベント画像変更 */
	boolean onEventImage(GameManagerBase manager) {
		Event event = manager.getRunningEvent();
		try {
			// すぐに変更
			int newImgId = Integer.valueOf(mParam1[0]);
			event.setActiveImageId(newImgId);
			// 再描画発行
			manager.drawEvent();
		} catch (NumberFormatException e) {
			//
			LibUtil.LogD(e.getMessage());
		}
		return true;
	}

	/** アイテム設置 */
	private boolean onItemPut(GameManagerBase manager) {
		try {
			ItemSetLayer target = (ItemSetLayer) mTarget;
			Item item = manager.getActiveItem();
			// 指定の場所にput
			int index = Integer.valueOf(mParam1[0]);
			int item_id = Integer.valueOf(mParam2[0]);
			target.putItem(index, item_id, item);
			// 置いたアイテムは使用済み
			item.flagON(Status.FLAG_USED);
			// アクティブアイテムをクリアする
			manager.setActiveItem(null);
			// 再描画
			manager.draw();
			manager.drawItemList();
		} catch (NumberFormatException e) {
		} catch (ClassCastException e) {

		}
		return true;
	}

	/** 設置済みアイテム回収 */
	private boolean onItemRemove(GameManagerBase manager) {
		try {
			ItemSetLayer target = (ItemSetLayer) mTarget;
			// 指定の場所からremove
			int index = Integer.valueOf(mParam1[0]);
			int id = target.getItemId(index);
			Node item = manager.getRoom().getItem(id);
			target.removeItem(index);
			// 置いたアイテムを獲得済みに戻す
			item.flagOFF(Status.FLAG_USED);
			item.flagON(Status.FLAG_GOT);
			// 再描画
			manager.draw();
			manager.drawItemList();
		} catch (NumberFormatException e) {
		} catch (ClassCastException e) {

		}
		return true;
	}

	/** 文字ループ */
	boolean onMojiLoop(GameManagerBase manager) {
		Moji moji = null;
		try {
			moji = (Moji) mTarget;
		} catch (ClassCastException e) {
			return false;
		}
		if (moji == null)
			return false;
		String range = mParam1[0].trim();
		try {
			int index = Integer.valueOf(mParam2[0]);
			moji.loopChar(range, index);
			manager.draw();
		} catch (NumberFormatException e) {
		}
		return true;
	}

	/** 設置アイテム画像変更 */
	boolean onItemPutImage(GameManagerBase manager) {
		try {
			ItemSetLayer target = (ItemSetLayer) mTarget;
			int index = Integer.valueOf(mParam1[0]);
			int newImageId = Integer.valueOf(mParam2[0]);
			target.changeImage(index, newImageId);
			manager.draw();
		} catch (ClassCastException e) {
			LibUtil.LogD("設置アイテム画像変更のターゲットオブジェクトにはItemSetLayer"
					+ "オブジェクトを指定して下さい");
		}
		return true;
	}

	/** 途中イベント発生 */
	boolean onEvent(GameManagerBase manager) {
		try {
			Event event = (Event) mTarget;
			manager.startEvent(event);
		} catch (ClassCastException e) {
			LibUtil.LogD("イベント発生のターゲットオブジェクトにはEvent" + "オブジェクトを指定して下さい");
		}
		return false;
	}

	/** SE再生 */
	boolean onPlaySE(GameManagerBase manager) {
		String se_name = mParam1[0];
		String path = manager.getSEFilePath(se_name);
		manager.playSE(path);
		return true;
	}

	/** タップ回数 */
	boolean onTapCount(GameManagerBase manager) {
		Node target = (Node) mTarget;
		if (target == null) {
			target = manager.getActiveObject();
		}
		if (mParam1 != null) {
			String s = mParam1[0];
			if ("0".equals(s)) {
				target.setCount(0);
			}
		} else {
			int c = target.getCount();
			target.setCount(c + 1);
		}
		return true;
	}

	/**
	 * EffectをUIスレッド上で行うためのコールバック用
	 */
	OnTimerDoneOnUIThread mOnEffectDone = new OnTimerDoneOnUIThread() {
		@Override
		public void onTimerDone(GameManagerBase manager) {
			setActionLock(false);
			runNext(manager);
		}
	};

	/** fadeエフェクト */
	boolean onEffectFade(GameManagerBase manager) {
		int color = -1;
		if (mParam1 != null) {
			try {
				// 色を取得
				color = LibUtil.toARGB(mParam1);
			} catch (Exception e) {
				if (LibUtil.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		int in_duration = 1000;
		int out_duration = 1000;
		if (mParam2 != null) {
			try {
				float param = Float.parseFloat(mParam2[0]);
				// フェードイン時間を取得
				in_duration = (int) (1000 * param);
			} catch (Exception e) {
				if (LibUtil.DEBUG) {
					e.printStackTrace();
				}
			}
			try {
				float param = Float.parseFloat(mParam2[0]);
				// フェードアウト時間を取得
				out_duration = (int) (1000 * param);
			} catch (Exception e) {
				if (LibUtil.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		// 次の動作をロック
		setActionLock(true);
		manager.setHoldAction(this);
		// エフェクト描画開始を指示
		manager.onDrawEffectFade(color, in_duration, out_duration,
				mOnEffectDone);
		return false;
	}

	/** 画面振動エフェクト */
	boolean onEffectVibration(GameManagerBase manager) {

		int duration = 1000;
		if (mParam1 != null) {
			try {
				float param = Float.parseFloat(mParam1[0]);
				// 時間を取得
				duration = (int) (1000 * param);
			} catch (Exception e) {
				if (LibUtil.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		// 次の動作をロック
		setActionLock(true);
		manager.setHoldAction(this);
		// エフェクト描画開始を指示
		manager.onEffectVibration(duration, mOnEffectDone);
		return false;
	}

	/** BGM再生 */
	boolean onPlayBGM(GameManagerBase manager) {
		String bgm_name = mParam1[0];
		String path = manager.getBGMFilePath(bgm_name);
		manager.playBGM(path);
		return true;
	}
}