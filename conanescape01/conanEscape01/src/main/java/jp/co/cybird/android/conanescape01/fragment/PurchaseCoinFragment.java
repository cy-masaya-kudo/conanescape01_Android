package jp.co.cybird.android.conanescape01.fragment;

import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity.OnPurchaseFinishedListener;
import jp.co.cybird.android.conanescape01.gui.PurchaseActivity;
import jp.co.cybird.android.escape.sound.SoundManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class PurchaseCoinFragment extends PurchaseFragmentBase implements
		OnClickListener, OnPurchaseFinishedListener {
	View root;
	boolean updatePrice = false;

	static final int COIN3 = 0;
	static final int COIN10 = 1;
	int buyMode;

	ImageButton buy3;
	ImageButton buy10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_purchase_coin, container,
				false);

		buy3 = (ImageButton) root.findViewById(R.id.btn_buy_coin3);
		buy3.setOnClickListener(this);
		buy10 = (ImageButton) root.findViewById(R.id.btn_buy_coin10);
		buy10.setOnClickListener(this);

		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		updateCoinNum();
	}

	void updateCoinNum() {

		// 所持コイン枚数表示
		int coinNum = getCoinNum();
		TextView t = (TextView) root.findViewById(R.id.text_coin_num);
		if (coinNum >= 0) {
			t.setText(getString(R.string.coin_num, coinNum));
		} else if (coinNum == -1) {
			// 取得中
			t.setText(R.string.coin_get);
		} else {
			// 取得失敗
			t.setText(R.string.err_coin_get);
		}

		// 残り枚数に応じて買える物が決まる
		if (coinNum > 0 && coinNum < 8) {
			buy3.setEnabled(true);
			buy10.setEnabled(false);
		} else if (coinNum >= 8 || coinNum < 0) {
			buy3.setEnabled(false);
			buy10.setEnabled(false);
		} else {
			buy3.setEnabled(true);
			buy10.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		playButtonSE();
		switch (v.getId()) {
		case R.id.btn_buy_coin3:
			onBuyCoin(COIN3);
			break;
		case R.id.btn_buy_coin10:
			onBuyCoin(COIN10);
			break;
		default:
			break;
		}

	}

	@Override
	public void onStart() {
		if (updatePrice) {
			updatePrices();
			updatePrice = false;
		}
		super.onStart();
	}

	/**
	 * ユーザーが保持しているコイン枚数を取得する<br>
	 * 事前(起動時など)にAPIで取得しておく必要がある<br>
	 * HintDialogにも同じ処理があるので、どこかで共通化しても良い
	 * 
	 * @return コイン枚数
	 */
	int getCoinNum() {
		PurchaseActivity a = (PurchaseActivity) getActivity();
		int coin = a.getCoinNum();
		return coin;
	}

	/**
	 * コインを購入(3枚2セット/10枚1セット)<br>
	 * HintDialogにも同じ処理があるので、どこかで共通化しても良い
	 * 
	 * @param i
	 */
	private void onBuyCoin(int buyMode) {
		this.buyMode = buyMode;
		PurchaseActivity a = (PurchaseActivity) getActivity();
		a.setOnPusrhcaseFinishedListener(this);
		a.setDoingOther(true);
		SoundManager.getInstance().pauseBGM();
		if (buyMode == COIN3) {
			a.purchaseCoin3();
		} else {
			a.purchaseCoin10();
		}
	}

	@Override
	public void updatePrices() {

		// 価格を更新
		BillingBaseActivity ac = (BillingBaseActivity) getActivity();
		if (ac == null) {
			updatePrice = true;
			return;
		}
		{
			String s = ac.get3CoinsPrice();
			TextView t = (TextView) root.findViewById(R.id.text_price3);
			if (s != null && s.length() > 0) {
				t.setText("¥" + s);
			} else {
				t.setText(R.string.noprice);
			}
		}
		{
			String s = ac.get10CoinsPrice();
			TextView t = (TextView) root.findViewById(R.id.text_price10);
			if (s != null && s.length() > 0) {
				t.setText("¥" + s);
			} else {
				t.setText(R.string.noprice);
			}
		}
	}

	@Override
	public void onPurchaseFinished(boolean result) {
		// getActivity().setResult(Common.RESULT_COIN_PURCHASED);
		updateCoinNum();
	}

}
