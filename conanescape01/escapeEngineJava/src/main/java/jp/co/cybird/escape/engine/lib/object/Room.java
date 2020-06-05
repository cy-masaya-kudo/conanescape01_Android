package jp.co.cybird.escape.engine.lib.object;

import java.util.ArrayList;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.Control.ControlType;
import jp.co.cybird.escape.engine.lib.object.event.Event;
import jp.co.cybird.escape.engine.lib.object.event.EventCharacter;
import jp.co.cybird.escape.engine.lib.object.event.EventEnding;
import jp.co.cybird.escape.engine.lib.object.event.EventHint;
import jp.co.cybird.escape.engine.lib.object.event.EventOpening;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.object.item.SubItem;

/**
 * 部屋オブジェクト<br>
 * すべてのルートになるオブジェクト
 *
 * @author S.Kamba
 */
public class Room extends Controllable {

	/** アクティブなオブジェクト */
	Node mActiveNode = null;
	/** アクティブなアイテム */
	Item mActiveItem = null;
	/** アイテム詳細画面で表示中のアイテムインデクス */
	Item mZoomingItem = null;

	/** Nodeオブジェクトリスト */
	Node[] mNodes = null;
	/** アイテムリスト */
	Node[] mItems = null;
	/** イベントリスト */
	Event[] mEvents = null;
	/** キャラクターリスト */
	EventCharacter[] mCharacters = null;
	/** アクティブなイベント */
	Event mActiveEvent = null;

	/** コンストラクタ */
	public Room() {
		super();
		mId = 0;
	}

	/** @return the activeObject */
	public Node getActiveObject() {
		return mActiveNode;
	}

	/**
	 * @param activeObject
	 *            the activeObject to set
	 */
	public void setActiveObject(Node activeObject) {
		this.mActiveNode = activeObject;
	}

	/** @return the activeItem */
	public Item getActiveItem() {
		return mActiveItem;
	}

	/**
	 * @param item
	 *            the active Item to set
	 */
	public void setActiveItem(Item item) {
		this.mActiveItem = item;
	}

	/**
	 * @return index of the active item
	 */
	public int getItemIndex(Item item) {
		if (item == null)
			return -1;
		for (int i = 0; i < mItems.length; i++) {
			if (item == mItems[i]) {
				return i;
			}
		}
		return -1;
	}

	/** @return the item array */
	public Node[] getNodeArray() {
		return mNodes;
	}

	/**
	 * @param itemList
	 *            the itemList to set
	 */
	public void setNodeList(ArrayList<Node> list) {
		this.mNodes = list.toArray(new Node[list.size()]);
	}

	/** @return the event array */
	public Event[] getEventArray() {
		return mEvents;
	}

	/**
	 * @param eventList
	 *            the eventList to set
	 */
	public void setEventList(ArrayList<Event> list) {
		if (list == null)
			return;
		mEvents = list.toArray(new Event[list.size()]);
	}

	/**
	 * idを指定してアイテムオブジェクトを取得
	 *
	 * @param id
	 *            オブジェクトid
	 * @return Itemオブジェクト
	 */
	public Node getNode(int id) {
		if (mNodes == null)
			return null;
		for (Node n : mNodes) {
			if (n.getId() == id)
				return n;
		}
		return null;
	}

	/**
	 * アイテムの数を取得
	 *
	 * @return アイテムの数
	 */
	int getItemNum() {
		if (mItems == null)
			return 0;
		return mItems.length;
	}

	/** @return the item array */
	public Node[] getItemArray() {
		return mItems;
	}

	/**
	 * @param itemList
	 *            the itemList to set
	 */
	public void setItemList(ArrayList<Node> itemList) {
		this.mItems = itemList.toArray(new Node[itemList.size()]);
	}

	/**
	 * idを指定してアイテムオブジェクトを取得
	 *
	 * @param id
	 *            アイテムid
	 * @return Itemオブジェクト
	 */
	public Node getItem(int id) {
		if (mItems == null)
			return null;
		for (Node i : mItems) {
			if (i.getId() == id)
				return i;
		}
		return null;
	}

	/**
	 * クリック処理
	 *
	 * @param x
	 *            ,y クリック座標
	 */
	public void onClick(GameManagerBase manager, int x, int y) {
		// ActiveNodeのコントロールを実行
		mActiveNode.runControll(manager, ControlType.CLICK, x, y);
	}

	/**
	 * アイテム拡大画面クリック処理
	 *
	 * @param x
	 *            ,y クリック座標
	 */
	public void onClickItem(GameManagerBase manager, int x, int y) {
		// ZoomingItemのコントロールを実行
		if (mZoomingItem == null)
			return;
		mZoomingItem.runControll(manager, ControlType.CLICK, x, y);
	}

	/**
	 * アイテム拡大画面終了
	 *
	 * @param manager
	 */
	public void onItemClose(GameManagerBase manager) {
		// ZoomingItemのコントロールを実行
		if (mZoomingItem == null)
			return;
		mZoomingItem.runControll(manager, ControlType.ITEM_CLOSE, 0, 0);
	}

	/**
	 * タッチ中の処理
	 *
	 * @param x
	 *            ,y クリック座標
	 */
	public void onTouchDown(GameManagerBase manager, int x, int y) {
		// ActiveNodeのコントロールを実行
		mActiveNode.runControll(manager, ControlType.TOUCH_DOWN, x, y);
	}

	/**
	 * タッチ終わりの処理
	 *
	 * @param x
	 *            ,y クリック座標
	 */
	public void onTouchUp(GameManagerBase manager, int x, int y) {
		// ActiveNodeのコントロールを実行
		mActiveNode.runControll(manager, ControlType.TOUCH_UP, x, y);
	}

	/**
	 * イベント中クリック処理
	 *
	 * @param manager
	 * @param x
	 *            クリック座標x
	 * @param y
	 *            クリック座標y
	 */
	public void onClickEvent(GameManagerBase manager, int x, int y) {
		if (mActiveEvent != null) {
			mActiveEvent.runControll(manager, ControlType.CLICK, x, y);
		}
	}

	/**
	 * 保存処理
	 */
	public String[] save() {
		String saveTexts[] = new String[2];

		saveTexts[0] = objectsBackup(mNodes);
		saveTexts[1] = objectsBackup(mItems);

		return saveTexts;
	}

	/** バックアップ文字列取得 */
	private String objectsBackup(Node[] objects) {
		StringBuffer sb = new StringBuffer();
		for (Node node : objects) {
			sb.append(node.toString()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * FIXME:暫定版　保存データの復元処理
	 *
	 * @param objects
	 *            復元するNodeのリスト(Nodes/Items)
	 * @param lines
	 *            保存されていたcsvデータ
	 * @return 成否
	 */
	private boolean objectsRestore(Node[] objects, ArrayList<String[]> lines) {
		int i = 0;
		for (Node node : objects) {
			String[] buf = lines.get(i);
			// 0:id
			int id = Integer.valueOf(buf[0].trim());
			if (node.getId() != id) {
				Node sub = getItem(id);
				if (sub instanceof Item || sub instanceof SubItem) {
					// 置換済みのSubItem対応
					// replaceItem((SubItem) sub);
					node = sub;
				} else {
					System.err.println("NodeIdが保存データと一致しません");
					return false;
				}
			}
			node.restore(buf);
			i++;
		}
		return true;
	}

	/**
	 * Nodeオブジェクトリストの復元
	 *
	 * @param lines
	 *            　保存されたCSVデータ
	 * @return　成否
	 */
	public boolean restoreNodes(ArrayList<String[]> lines) {
		return objectsRestore(mNodes, lines);
	}

	/**
	 * Itemオブジェクトリストの復元
	 *
	 * @param lines
	 *            　保存されたCSVデータ
	 * @return　成否
	 */
	public boolean restoreItems(ArrayList<String[]> lines) {
		if (!objectsRestore(mItems, lines))
			return false;
		// 置換済みアイテムを考慮して並び替え
		Node newList[] = new Node[mItems.length];
		for (int i = 0; i < lines.size(); i++) {
			String[] buf = lines.get(i);
			// 0:id
			int id = Integer.valueOf(buf[0].trim());
			newList[i] = getItem(id);
		}
		mItems = newList;
		return true;
	}

	/**
	 * アイテムをインデクスから取得
	 *
	 * @param index
	 * @return
	 */
	public Node getItemFromIndex(int index) {
		if (mItems == null)
			return null;
		if (index >= mItems.length)
			return null;
		return mItems[index];
	}

	/**
	 * @param index
	 *            the zooming Item to set
	 */
	public void setZoomingItem(int index) {
		if (index >= mItems.length)
			return;
		this.mZoomingItem = (Item) mItems[index];
	}

	/**
	 * @param item
	 *            the zoom Item to set
	 */
	public void setZoomingItem(Item item) {
		this.mZoomingItem = item;
	}

	/** @return the zooming Item */
	public Item getZoomingItem() {
		return mZoomingItem;
	}

	/**
	 * @return index of the zoom item
	 */
	public int getZoomingItemIndex() {
		if (mZoomingItem == null)
			return -1;
		for (int i = 0; i < mItems.length; i++) {
			if (mZoomingItem == mItems[i]) {
				return i;
			}
		}
		return -1;
	}

	/** 実行中のイベントを取得 */
	public Event getRunningEvent() {
		return mActiveEvent;
	}

	/** 実行中イベントをセット */
	public void setRunningEvent(Event e) {
		mActiveEvent = e;
	}

	/**
	 * オープニングイベントを取得
	 *
	 * @return Event of Opening
	 */
	public Event getOpening() {
		if (mEvents == null)
			return null;
		// 0番目が必ずオープニング
		if (mEvents[0] instanceof EventOpening)
			return mEvents[0];
		return null;
	}

	/**
	 * エンディングイベントを取得
	 *
	 * @param index
	 *            エンディングindex(1 based)
	 * @return Event of Ending
	 */
	public Event getEnding(int index) {
		if (mEvents == null)
			return null;
		int endingCount = 0;
		for (Event e : mEvents) {
			if (e instanceof EventEnding) {
				if (endingCount == (index - 1))
					return e;
			}
		}
		return null;
	}

	/** 親アイテムとサブアイテムを入れ替える */
	public void replaceItem(SubItem subitem) {
		Item parent = (Item) subitem.getParent();
		int subindex = getItemIndex(subitem);
		int index = getItemIndex(parent);
		if (index >= 0) {
			mItems[index] = subitem;
		}
		// 元のアイテムをサブアイテムの位置に残す
		if (subindex >= 0) {
			mItems[subindex] = parent;
			parent.flagON(Status.FLAG_USED);
		}
	}

	/** キャラクターリストをセット */
	public void setCharacterList(ArrayList<EventCharacter> list) {
		if (list == null)
			return;
		mCharacters = list.toArray(new EventCharacter[list.size()]);
	}

	/** 指定idのキャラクタオブジェクトを取得 */
	public EventCharacter getCharacter(int chara_id) {
		if (mCharacters == null)
			return null;
		for (EventCharacter c : mCharacters) {
			if (c.getId() == chara_id) {
				return c;
			}
		}
		return null;
	}

	/** ヒントイベントオブジェクトを取得 */
	public Event getHintEvent() {
		for (Event e : mEvents) {
			if (e instanceof EventHint)
				return e;
		}
		return null;
	}

}
