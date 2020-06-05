package jp.co.cybird.android.conanescape01.gui;

import java.util.ArrayList;

import jp.co.cybird.android.billing.util.IabResult;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.billing.util.SkuDetails;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.fragment.PurchaseCoinFragment;
import jp.co.cybird.android.conanescape01.fragment.PurchaseFragmentBase;
import jp.co.cybird.android.conanescape01.fragment.PurchaseStageFragment;
import jp.co.cybird.android.escape.util.Tracking;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * 購入画面
 * 
 * @author S.Kamba
 *
 */
public class PurchaseActivity extends BillingBaseActivity {

	/** savedInstance用キー */
	static final String KEY_BUYMODE = "buymode";

	static final String TAG_STAGE = "stage";
	static final String TAG_COIN = "coin";

	/** 購入モード:ステージ */
	static final int BUYMODE_STAGE = 0;
	/** 購入モード:コイン */
	static final int BUYMODE_COIN = 1;

	/** 購入モード */
	int buyMode = BUYMODE_STAGE;

	/** スイッチボタン:ステージ */
	ImageButton btnSwitchStage;
	/** スイッチボタン:コイン */
	ImageButton btnSwitchCoin;

	String fragmentTag = null;

	boolean fromGameActivity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);

		if (savedInstanceState == null) {
			Fragment fragment = new PurchaseStageFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment, TAG_STAGE).commit();
			fragmentTag = TAG_STAGE;

		} else {
			buyMode = savedInstanceState.getInt(KEY_BUYMODE, 0);
		}

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getBooleanExtra(Common.KEY_FROM_GAGME, false)) {
				// ゲームから呼ばれた
				fromGameActivity = true;
			}
		}

		btnSwitchStage = (ImageButton) findViewById(R.id.btn_switch_stage);
		btnSwitchCoin = (ImageButton) findViewById(R.id.btn_switch_coin);

		// ネットワーク状況をチェック
		if (checkConnect()) {
			callCoinNumAPI();
		} else {
			;
		}

		setResult(RESULT_CANCELED);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// GoogleAnalytics
		Tracking.sendView("Purchase");
	}

	@Override
	protected void onResume() {
		switchBuyModeButtons(buyMode);
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//
		outState.putInt(KEY_BUYMODE, buyMode);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		int c = getFragmentManager().getBackStackEntryCount();
		if (c == 0) {
			setDoingFlagClearParent();
		}
		stopBGM = false;
		super.onBackPressed();
	}

	/** 閉じるボタン */
	public void onClose(View v) {
		playCloseSE();
		setDoingFlagClearParent();
		stopBGM = false;
		finish();
	}

	/** スイッチボタンクリック処理 */
	public void onClick(View v) {
		playButtonSE();
		switch (v.getId()) {
		case R.id.btn_switch_stage:
			if (buyMode != BUYMODE_STAGE) {
				// ステージ購入フラグメントに切替
				changeFragment(new PurchaseStageFragment(), TAG_STAGE);
				buyMode = BUYMODE_STAGE;
				switchBuyModeButtons(buyMode);
			}
			break;
		case R.id.btn_switch_coin:
			if (buyMode != BUYMODE_COIN) {
				// コイン購入フラグメントに切替
				changeFragment(new PurchaseCoinFragment(), TAG_COIN);
				buyMode = BUYMODE_COIN;
				switchBuyModeButtons(buyMode);
			}
			break;
		default:
			break;
		}
	}

	/** 購入モードスイッチボタンのアクティブ切り替え */
	void switchBuyModeButtons(int mode) {
		if (mode == BUYMODE_STAGE) {
			btnSwitchStage.setEnabled(false);
			btnSwitchCoin.setEnabled(true);
		} else {
			btnSwitchStage.setEnabled(true);
			btnSwitchCoin.setEnabled(false);
		}
	}

	/** Fragmentを切り替える:backボタンでは戻らない */
	public void changeFragment(PurchaseFragmentBase f, String tag) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, f, tag);
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();

		fragmentTag = tag;
		f.updatePrices();
	}

	private void setDoingFlagClearParent() {
		if (fromGameActivity) {
			// GameActivity g = GameActivity.getInstance();
			// g.setDoingOther(false);
		} else {
			MainActivity a = MainActivity.getInstance();
			a.setDoingOther(false);
		}

	}

	// // Billing

	@Override
	public void onQueryItemFinished(IabResult result,
			ArrayList<SkuDetails> itemDetailList,
			ArrayList<Purchase> itemPurchaseList) {
		super.onQueryItemFinished(result, itemDetailList, itemPurchaseList);

		// Fragmentに価格更新を通達
		PurchaseFragmentBase fragment = (PurchaseFragmentBase) getFragmentManager()
				.findFragmentByTag(fragmentTag);
		if (fragment != null)
			fragment.updatePrices();
	}

	@Override
	public void onPurchaseItemFinished(IabResult result, Purchase purchase) {
		doingOther = false;
		super.onPurchaseItemFinished(result, purchase);
	}
}
