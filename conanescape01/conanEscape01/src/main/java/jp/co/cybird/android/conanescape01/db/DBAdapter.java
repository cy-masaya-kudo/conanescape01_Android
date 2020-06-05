package jp.co.cybird.android.conanescape01.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import jp.co.cybird.android.util.Debug;

/**
 * DBアクセス用ラッパークラス
 *
 * @author S.Kamba
 *
 */
public class DBAdapter {

	protected final Context context;
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;

	public DBAdapter(Context context) {
		this.context = context;
		dbHelper = new DBHelper(this.context);
	}

	/** DB Open */
	public DBAdapter openWritable() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/** DB Open read only */
	public DBAdapter openReadable() {
		db = dbHelper.getReadableDatabase();
		return this;
	}

	/** DB Close */
	public void close() {
		dbHelper.close();
		db = null;
	}

	/**
	 * @param itemId
	 * @param price
	 */
	public void savePrice(String itemId, String price) {
		if (db == null)
			return;

		if (dbHelper.updatePrice(db, itemId, price) <= 0) {
			// 無かったので追加
			long id = dbHelper.insert(db, itemId, price);
			if (Debug.isDebug) {
				Debug.logD("insert db:itemId=" + itemId + " db:id=" + id
						+ " price=" + price);
			}
		} else {
			if (Debug.isDebug) {
				Debug.logD("update db:itemId=" + itemId + " price=" + price);
			}

		}
	}

	/**
	 * @param itemId
	 * @return price
	 */
	public String getPrice(String itemId) {
		Cursor c = dbHelper.getPrice(db, itemId);
		if (c == null || c.getCount() == 0) {
			c.close();
			return null;
		}

		c.moveToFirst();
		String s = c.getString(0);
		c.close();
		return s;
	}

	/**
	 * @return Stage list
	 */
	public HashMap<String, String> getAllPrice() {
		Cursor c = dbHelper.getAll(db);
		if (c == null || c.getCount() == 0) {
			c.close();
			return null;
		}

		HashMap<String, String> map = new HashMap<String, String>();

		int num = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < num; i++) {
			int columnIndex = 1;
			String itemId = c.getString(columnIndex++);
			String price = c.getString(columnIndex++);
			map.put(itemId, price);
			c.moveToNext();
		}
		c.close();
		return map;
	}
}
