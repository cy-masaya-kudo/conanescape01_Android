package jp.co.cybird.android.conanescape01.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.db.DBAdapter;
import android.content.Context;
import android.content.SharedPreferences;

public class PurchaseItem {
	static final int ITEMID_STAGE = 0;
	static final int ITEMID_3COINS = 1;
	static final int ITEMID_10COINS = 2;

	/** 課金アイテムid */
	ArrayList<String> mItemIdList = new ArrayList<String>(Arrays.asList(
			"jp.co.cybird.android.conanescape01.stage",
			"jp.co.cybird.android.conanescape01.hint3",
			"jp.co.cybird.android.conanescape01.hint10"));

	/** 価格マップ */
	HashMap<String, String> skuPrices = null;
	/** 所持コイン枚数 */
	int coinNum = -1;

	/** 価格をセット */
	public void putPrice(String itemId, String price) {
		if (skuPrices == null) {
			skuPrices = new HashMap<String, String>();
		}
		skuPrices.put(itemId, price);
	}

	/** ステージ価格取得 */
	public String getStagePrice() {
		if (skuPrices == null)
			return null;
		return skuPrices.get(mItemIdList.get(ITEMID_STAGE));
	}

	/** ヒントコインx3価格取得 */
	public String get3CoinsPrice() {
		if (skuPrices == null)
			return null;
		return skuPrices.get(mItemIdList.get(ITEMID_3COINS));
	}

	/** ヒントコインx10価格取得 */
	public String get10CoinsPrice() {
		if (skuPrices == null)
			return null;
		return skuPrices.get(mItemIdList.get(ITEMID_10COINS));
	}

	/** 価格情報をdbに保存 */
	public void savePrices(Context c) {

		if (skuPrices == null || skuPrices.size() == 0)
			return;

		DBAdapter dba = new DBAdapter(c);
		dba.openWritable();

		for (String key : skuPrices.keySet()) {
			dba.savePrice(key, skuPrices.get(key));
		}
		dba.close();

	}

	/** dbに保存した価格情報を取得 */
	public void getSavedPrices(Context c) {
		DBAdapter dba = new DBAdapter(c);
		dba.openReadable();

		skuPrices = dba.getAllPrice();
		dba.close();
	}

	/** ItemIdListの取得 */
	public ArrayList<String> getItemIdList() {
		return mItemIdList;
	}

	/** ステージの課金idを取得 */
	public String getStageItemId() {
		return mItemIdList.get(ITEMID_STAGE);
	}

	/** coin3の課金idを取得 */
	public String get3CoinsItemId() {
		return mItemIdList.get(ITEMID_3COINS);
	}

	/** coin10の課金idを取得 */
	public String get10CoinsItemId() {
		return mItemIdList.get(ITEMID_10COINS);
	}

	/** 所持コイン枚数をセット */
	public void setCoinNum(int coinNum) {
		//
		this.coinNum = coinNum;
	}

	/** 所持コイン枚数を取得 */
	public int getCoinNum() {
		return coinNum;
	}

	// /

	/** ステージ購入済みチェック */
	public static boolean isStagePurchased(Context c) {
		SharedPreferences p = c.getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE);
		boolean b = p.getBoolean(Common.PREF_KEY_STAGE_PURCHASED, false);
		return b;
	}

	/** ステージ購入フラグセット */
	public static void setStagePurchased(Context c, boolean flag) {
		SharedPreferences.Editor e = c.getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE).edit();
		e.putBoolean(Common.PREF_KEY_STAGE_PURCHASED, flag);
		e.commit();
	}
}
