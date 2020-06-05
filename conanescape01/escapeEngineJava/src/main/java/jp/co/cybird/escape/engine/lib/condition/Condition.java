package jp.co.cybird.escape.engine.lib.condition;

import java.util.Stack;

import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.ESCObject;
import jp.co.cybird.escape.engine.lib.object.Moji;
import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.object.layer.ItemSetLayer;

/**
 * 条件クラス
 * 
 * @author S.Kamba
 * 
 */
public class Condition extends ESCObject {

	/** 条件チェックタイプ */
	enum CheckType {
		NULL,
		/** ロックフラグ */
		LOCK,
		/** アイテム所有 */
		ITEM_HAVING,
		/** アイテム使用回数 */
		ITEM_USENUM,
		/** 表示フラグ */
		DISP_FLAG,
		/** アクティブアイテム */
		ITEM_ACTIVE,
		/** 画像ID */
		IMAGE_ID,
		/** 押下順 */
		STACK,
		/** 移動回数 */
		MOVE_COUNT,
		/** 経過時間 */
		TIMER,
		/** アイテム設置場所 */
		ITEM_PUT,
		/** 文字一致 */
		MOJI,
		/** カウント */
		TAP_COUNT,
	}

	/** チェックタイプ */
	CheckType mType = CheckType.NULL;
	/** チェック対象オブジェクト */
	ESCObject mTarget = null;
	/** パラメータ1 */
	String[] mParam1 = null;
	/** パラメータ2 */
	String[] mParam2 = null;
	/** 次にチェックするコンディションId */
	int mNextId = -1;
	/** 次にチェックするコンディション */
	Condition mNextCondition = null;

	/**
	 * コンストラクタ
	 */
	public Condition() {
	}

	/**
	 * @return the mType
	 */
	public CheckType getType() {
		return mType;
	}

	/**
	 * @param mType
	 *            the mType to set
	 */
	public void setType(CheckType type) {
		this.mType = type;
	}

	public void setType(int type_id) {
		mType = CheckType.values()[type_id];
	}

	/**
	 * @return the mTarget
	 */
	public ESCObject getTarget() {
		return mTarget;
	}

	/**
	 * @param mTarget
	 *            the mTarget to set
	 */
	public void setTarget(ESCObject target) {
		this.mTarget = target;
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

	/**
	 * @return the next condition id
	 */
	public int getNext() {
		return mNextId;
	}

	/**
	 * @param mNext
	 *            the next to set
	 */
	public void setNext(int id) {
		this.mNextId = id;
	}

	/**
	 * 条件チェック
	 * 
	 * @param
	 * @return TRUE or FALSE
	 */
	public boolean doCheck(GameManagerBase manager) {
		boolean result = false;
		switch (mType) {
		case LOCK:
			result = onLock(manager);
			break;
		case ITEM_HAVING:
			result = onItemHave(manager);
			break;
		case ITEM_USENUM:
			result = onItemUseCount(manager);
			break;
		case DISP_FLAG:
			result = onDispFlag(manager);
			break;
		case ITEM_ACTIVE:
			result = onItemActive(manager);
			break;
		case IMAGE_ID:
			result = onImageId(manager);
			break;
		case STACK:
			result = onActionStack(manager);
			break;
		case MOVE_COUNT:
			result = onMoveCount(manager);
			break;
		case TIMER:
			result = onTimer(manager);
			break;
		case ITEM_PUT:
			result = onItemPut(manager);
			break;
		case MOJI:
			result = onMoji(manager);
			break;
		case TAP_COUNT:
			result = onTapCount(manager);
			break;
		default:
			break;
		}
		if (result) {
			// 次の条件をチェック
			result = checkNext(manager);
		}
		return result;
	}

	/**
	 * 次の動作を実行
	 * 
	 * @return
	 */
	boolean checkNext(GameManagerBase manager) {
		if (mNextId >= 0) {
			if (mNextCondition == null) {
				mNextCondition = manager.findCondition(mNextId);
			}
			if (mNextCondition != null) {
				return mNextCondition.doCheck(manager);
			}
			return false;
		}
		return true;
	}

	/** ロックフラグをチェック */
	private boolean onLock(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		if (mParam1[0].toUpperCase().equals("TRUE")) {
			return node.isFlagON(Status.FLAG_LOCKED);
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			return !node.isFlagON(Status.FLAG_LOCKED);
		}
		return false;
	}

	/** アイテム所有フラグをチェック */
	private boolean onItemHave(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		if (mParam1[0].toUpperCase().equals("TRUE")) {
			return node.isFlagON(Status.FLAG_GOT);
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			return !node.isFlagON(Status.FLAG_GOT);
		}
		return false;
	}

	/** アイテム使用回数をチェック */
	private boolean onItemUseCount(GameManagerBase manager) {
		try {
			Item item = (Item) mTarget;
			int used = item.getUsedNum();
			int num = Integer.valueOf(mParam2[0]);
			if (mParam1[0].equals("=")) {
				if (used == num) {
					return true;
				}
				return false;
			} else if (mParam1[0].equals(">")) {
				if (used > num) {
					return true;
				}
				return false;
			} else if (mParam1[0].equals(">=")) {
				if (used >= num) {
					return true;
				}
				return false;
			} else if (mParam1[0].equals("<")) {
				if (used < num) {
					return true;
				}
				return false;
			} else if (mParam1[0].equals("<=")) {
				if (used <= num) {
					return true;
				}
				return false;
			}
		} catch (NumberFormatException e) {
			System.err.println("Condition::onItemUseCount ->"
					+ "アイテム使用回数チェックに指定されている数値パラメータ２が不正です。");
		} catch (ClassCastException e) {
			System.err.println("Condition::onItemUseCount ->"
					+ "アイテム使用回数チェックに指定されているオブジェクトがアイテムオブジェクトではありません。");
		}
		return false;
	}

	/** 表示フラグをチェック */
	private boolean onDispFlag(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		if (mParam1[0].toUpperCase().equals("TRUE")) {
			return node.isFlagON(Status.FLAG_DISP);
		} else if (mParam1[0].toUpperCase().equals("FALSE")) {
			return !node.isFlagON(Status.FLAG_DISP);
		}
		return false;
	}

	/** アクティブアイテムかチェック */
	private boolean onItemActive(GameManagerBase manager) {
		try {
			Item item = (Item) mTarget;
			if (item == manager.getActiveItem()) {
				return true;
			}
		} catch (ClassCastException e) {
			System.err.println("Condition::onItemActive ->"
					+ "アクティブアイテムチェックに指定されているオブジェクトがアイテムオブジェクトではありません。");
		}
		return false;
	}

	/** 画像idチェック */
	private boolean onImageId(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		try {
			int active_id = node.getActiveImageId();
			for (int i = 0; i < mParam1.length; i++) {
				int check_id = Integer.valueOf(mParam1[i]);
				if (active_id == check_id) {
					return true;
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("Condition::onImageId ->"
					+ "画像Idチェックに指定されているパラメータ2が数値ではありません。");
		}
		return false;
	}

	/** 動作スタック */
	private boolean onActionStack(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		Stack<Object> stack = node.getActionStack();
		int num = mParam1.length;
		int len = stack.size();
		if (len < num)
			return false;
		int i = 0;
		for (; i < num; i++) {
			Object pop = stack.get(len - i - 1);
			if (pop instanceof Integer) {
				Integer number = Integer.valueOf(mParam1[num - i - 1]);
				if (number != (Integer) pop) {
					return false;
				}
			} else {
				return false;
			}
		}
		if (i == num) {
			return true;
		}
		return false;
	}

	/** 移動回数(アクションスタックに積まれた回数) */
	private boolean onMoveCount(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		Stack<Object> stack = node.getActionStack();
		try {
			int num = Integer.valueOf(mParam1[0]);
			if (stack.size() > num) {
				return true;
			}
		} catch (NumberFormatException e) {
		}
		return false;
	}

	/** 経過時間 */
	private boolean onTimer(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		long saved = node.getSavedTimer();
		if (saved == 0)
			return false;
		long current = System.currentTimeMillis();
		int time = Integer.valueOf(mParam1[0]) * 1000;
		if ((current - saved) > time) {
			return true;
		}
		return false;
	}

	/** アイテム設置場所 */
	private boolean onItemPut(GameManagerBase manager) {
		try {
			ItemSetLayer target = (ItemSetLayer) mTarget;
			Stack<Object> stack = target.getActionStack();
			for (int i = 0; i < mParam1.length; i++) {
				int id = (Integer) stack.get(i);
				if (id != Integer.valueOf(mParam1[i])) {
					return false;
				}
			}
			return true;
		} catch (NumberFormatException e) {
		} catch (ClassCastException e) {
		}
		return false;
	}

	/** 文字一致 */
	private boolean onMoji(GameManagerBase manager) {
		Moji moji = null;
		try {
			moji = (Moji) mTarget;
		} catch (ClassCastException e) {
			return false;
		}
		if (moji == null)
			return false;
		return moji.check(mParam1[0]);
	}

	/** タップ回数カウント */
	private boolean onTapCount(GameManagerBase manager) {
		Node node = (Node) mTarget;
		if (node == null) {
			node = manager.getActiveObject();
		}
		try {
			int count = Integer.valueOf(mParam1[0]);
			if (count == node.getCount())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
