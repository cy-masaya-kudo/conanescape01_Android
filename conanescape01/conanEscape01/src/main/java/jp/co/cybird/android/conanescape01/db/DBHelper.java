package jp.co.cybird.android.conanescape01.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jp.co.cybird.android.util.Debug;

public class DBHelper extends SQLiteOpenHelper {

	static final String DATABASE_NAME = "conan02";
	static final int DATABASE_VERSION = 1;
	static final String TABLE_NAME = "items";
	static final String COL_ID = "_id";
	static final String COL_ITEMID = "itemid";
	static final String COL_PRICE = "price";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " //
				+ COL_ITEMID + " TEXT NOT NULL, " //
				+ COL_PRICE + " TEXT )");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newViersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	/**
	 * Insert
	 *
	 * @param db
	 * @param data
	 */
	public long insert(SQLiteDatabase db, String itemId, String price) {
		db.beginTransaction();

		long insert_id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(COL_ITEMID, itemId);
			values.put(COL_PRICE, price);
			insert_id = db.insertOrThrow(TABLE_NAME, null, values);
			db.setTransactionSuccessful();

		} catch (Exception e) {
			if (Debug.isDebug) {
				e.printStackTrace();
			}
			insert_id = 0;
		} finally {
			db.endTransaction();
		}
		return insert_id;
	}

	/**
	 * update price
	 *
	 * @param db
	 * @param data
	 */
	public int updatePrice(SQLiteDatabase db, String itemId, String price) {
		db.beginTransaction();

		int update_num = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(COL_PRICE, price);

			update_num = db.update(TABLE_NAME, values, COL_ITEMID + "= ?",
					new String[] { itemId });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			if (Debug.isDebug) {
				e.printStackTrace();
			}
			update_num = 0;
		} finally {
			db.endTransaction();
		}
		return update_num;
	}

	/**
	 * query with itemId
	 *
	 * @param db
	 * @param itemId
	 * @return cursor
	 */
	public Cursor getPrice(SQLiteDatabase db, String itemId) {
		return db.query(TABLE_NAME, new String[] { COL_PRICE }, COL_ITEMID
				+ "= ?", new String[] { itemId }, null, null, null);
	}

	/**
	 * query all
	 *
	 * @param db
	 * @return cursor
	 */
	public Cursor getAll(SQLiteDatabase db) {
		return db.query(TABLE_NAME, null, null, null, null, null, null);
	}
}
