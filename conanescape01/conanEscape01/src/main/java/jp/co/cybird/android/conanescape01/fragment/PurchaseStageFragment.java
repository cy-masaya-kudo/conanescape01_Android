package jp.co.cybird.android.conanescape01.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.cybird.android.billing.util.IabResult;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.billing.util.SkuDetails;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity.OnPurchaseFinishedListener;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity.OnQueryFinishedListener;
import jp.co.cybird.android.conanescape01.gui.PurchaseActivity;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.util.Debug;

public class PurchaseStageFragment extends PurchaseFragmentBase implements
        OnClickListener, OnPurchaseFinishedListener, OnQueryFinishedListener {
    View root;
    boolean updatePrice = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_purchase_stage, container,
                false);

        setStagePurchaseImage();
        return root;
    }

    /*
     * ステージ購入画像の切替
     */
    void setStagePurchaseImage() {
        ImageButton b = (ImageButton) root.findViewById(R.id.btn_buy_stage);
        b.setOnClickListener(this);
        if (isPurchasedStage()) {
            // 購入済み画像に差し替え(必ずsetBackgroundResource使用のこと)
            // b.setImageResource(R.drawable.btn_buystage_2);
            b.setEnabled(false);
            // 価格も非表示
            TextView t = (TextView) root.findViewById(R.id.text_price);
            t.setVisibility(View.INVISIBLE);
        } else {
            b.setEnabled(true);
        }
        b = (ImageButton) root.findViewById(R.id.btn_restore);
        b.setOnClickListener(this);
    }

    /**
     * ステージ購入済みか調べる<br>
     * 起動時などにAPIで取ってきておく
     *
     * @return 購入済みならtrue
     */
    boolean isPurchasedStage() {
        if (Debug.isDebugStagePurchase) {
            return true;
        }

        BillingBaseActivity a = (BillingBaseActivity) getActivity();
        return a.isStagePurchased();
    }

    @Override
    public void onClick(View v) {
        playButtonSE();
        switch (v.getId()) {
            case R.id.btn_buy_stage:
                onBuyStage();
                break;
            case R.id.btn_restore:
                onRestore();
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
     * ステージ購入処理
     */
    private void onBuyStage() {
        PurchaseActivity a = (PurchaseActivity) getActivity();
        a.setOnPusrhcaseFinishedListener(this);
        a.setDoingOther(true);
        SoundManager.getInstance().pauseBGM();
        a.purchaseStage();
    }

    /**
     * 購入復元処理
     */
    private void onRestore() {
        //
        BillingBaseActivity a = (BillingBaseActivity) getActivity();
        a.setOnQueryFinishedListener(this);
        a.setPointApiCall(true);
        try {
            a.queryItemPurchase();
        } catch (Exception e) {
            // Toast.makeText(a, R.string.err_purchase,
            // Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updatePrices() {
        BillingBaseActivity ac = (BillingBaseActivity) getActivity();
        if (ac == null) {
            updatePrice = true;
            return;
        }

        TextView t = (TextView) root.findViewById(R.id.text_price);
        if (isPurchasedStage()) {
            t.setVisibility(View.INVISIBLE);
            return;
        }
        t.setVisibility(View.VISIBLE);
        // 価格を更新
        String s = ac.getStagePrice();

        if (s != null && s.length() > 0) {
            t.setText("¥" + s);
        } else {
            t.setText(R.string.noprice);
        }
    }

    @Override
    public void onPurchaseFinished(boolean result) {
        //
        if (result) {
            getActivity().setResult(Common.RESULT_STAGE_PURCHASED);
            getActivity().finish();
        }
    }

    @Override
    public void onQueryFinished(IabResult result,
                                ArrayList<SkuDetails> itemDetailList,
                                ArrayList<Purchase> itemPurchaseList) {
        if (result.isFailure()) {
            Toast.makeText(getActivity(), R.string.err_restore,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(), R.string.restore_done, Toast.LENGTH_SHORT)
                .show();
        //
        setStagePurchaseImage();

        if (isPurchasedStage()) {
            getActivity().setResult(Common.RESULT_STAGE_PURCHASED);
            // getActivity().finish();
        }
    }
}
