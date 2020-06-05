package jp.co.cybird.escape.engine.lib.object.layer;

import java.util.Stack;

import jp.co.cybird.escape.engine.lib.object.Node;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.item.Item;

/**
 * アイテム設置用の管理オブジェクト
 * 
 * @author S.Kamba
 * 
 */
public class ItemSetLayer extends Node {

	/** 設置されたアイテムの保存配列 */
	int[] itemIdArray = null;

	/** アイテム数をセット */
	public void setItemNum(int num) {
		itemIdArray = new int[num];
		mActionStack = new Stack<Object>();
		mActionStack.setSize(num);
		for (int i = 0; i < num; i++) {
			itemIdArray[i] = -1;
			mActionStack.set(i, -1);
		}
	}

	/** アイテムをセット */
	public void putItem(int index, int item_id, Item item) {
		if (mActionStack == null || itemIdArray == null)
			return;
		if (index >= mActionStack.size() || index >= itemIdArray.length)
			return;
		mActionStack.set(index, item_id);
		itemIdArray[index] = item.getId();
		// 対応レイヤーを探す
		Node l = mChildren.get(index);
		// 表示ON
		l.flagON(Status.FLAG_DISP);
		// 画像idを切り替える
		l.setActiveImageId(item_id);
	}

	/** アイテムを回収 */
	public void removeItem(int index) {
		if (mActionStack == null || itemIdArray == null)
			return;
		if (index >= mActionStack.size() || index >= itemIdArray.length)
			return;
		mActionStack.set(index, -1);
		itemIdArray[index] = -1;

		// 対応レイヤーを探す
		Node l = mChildren.get(index);
		// 表示オフ
		l.flagOFF(Status.FLAG_DISP);
	}

	/** アイテム配列の指定されたインデクスのアイテムIDを取得する */
	public int getItemId(int index) {
		if (itemIdArray == null || index >= itemIdArray.length)
			return -1;
		return itemIdArray[index];
	}

	/** アイテム配列対応レイヤの画像idを変更する */
	public void changeImage(int index, int newImageId) {
		if (mActionStack == null || itemIdArray == null)
			return;
		if (index >= mActionStack.size() || index >= itemIdArray.length)
			return;
		int start;
		int end;
		if (index < 0) {
			start = 0;
			end = mActionStack.size();
		} else {
			start = index;
			end = index + 1;
		}
		for (int i = start; i < end; i++) {
			// 対応レイヤーを探す
			Node l = mChildren.get(i);
			// // 表示ON
			// l.flagON(Status.FLAG_DISP); // やらない
			// 画像idを切り替える
			if (newImageId < 0) {
				// item_idに対応した値にする
				int item_id = (Integer) mActionStack.get(i);
				l.setActiveImageId(item_id);
			} else {
				l.setActiveImageId(newImageId);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (itemIdArray != null) {
			for (int i : itemIdArray) {
				sb.append(",");
				sb.append(i);
			}
		}
		if (mActionStack != null) {
			for (int i = 0; i < mActionStack.size(); i++) {
				sb.append(",");
				sb.append(mActionStack.get(i));
			}
		}
		return sb.toString();
	}

	@Override
	public void restore(String[] buf) {
		super.restore(buf);

		if (buf.length < 6)
			return;
		if (itemIdArray == null)
			return;
		if (mActionStack == null)
			return;
		int bufindex = 5;
		for (int i = 0; i < itemIdArray.length; i++, bufindex++) {
			itemIdArray[i] = Integer.valueOf(buf[bufindex].trim());
		}
		for (int i = 0; i < mActionStack.size(); i++, bufindex++) {
			int id = Integer.valueOf(buf[bufindex].trim());
			mActionStack.set(i, id);
		}
	}
}
