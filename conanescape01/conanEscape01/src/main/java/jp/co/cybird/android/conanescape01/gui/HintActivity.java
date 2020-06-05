package jp.co.cybird.android.conanescape01.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.cybird.android.billing.util.IabResult;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.billing.util.SkuDetails;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity.OnPurchaseFinishedListener;
import jp.co.cybird.android.conanescape01.webapi.WebapiFinishListener;
import jp.co.cybird.android.conanescape01.webapi.WebapiGetCoinNum;
import jp.co.cybird.android.conanescape01.webapi.WebapiPoint;
import jp.co.cybird.android.conanescape01.webapi.WebapiPointConsume;
import jp.co.cybird.android.conanescape01.webapi.WebapiTransactions;
import jp.co.cybird.android.escape.dialog.BaseDialogFragment;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.util.Debug;

/**
 * Hint購入画面
 *
 * @author S.Kamba
 */
public class HintActivity extends BillingBaseActivity implements
        OnClickListener, OnPurchaseFinishedListener {

    static final int COIN3 = 0;
    static final int COIN10 = 1;

    ImageButton buy3;
    ImageButton buy10;
    ImageButton buyHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        buyHint = (ImageButton) findViewById(R.id.btn_buyhint);
        buyHint.setOnClickListener(this);

        buy3 = (ImageButton) findViewById(R.id.btn_buy_coin3);
        buy3.setOnClickListener(this);
        buy10 = (ImageButton) findViewById(R.id.btn_buy_coin10);
        buy10.setOnClickListener(this);

        ImageButton b = (ImageButton) findViewById(R.id.btn_close);
        b.setOnClickListener(this);

        Button debugButton = (Button) findViewById(R.id.btn_buy_debug);
        if (Debug.isDebugHint10Enable) {
            debugButton.setOnClickListener(this);
        } else {
            debugButton.setVisibility(View.GONE);
        }

        updateCoinNum();

        // ネットワーク状況をチェック
        if (checkConnect()) {
            callCoinNumAPI();
        }

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // GoogleAnalytics
        Tracking.sendView("Hint");
    }

    @Override
    protected void callCoinNumAPI() {
        final WebapiGetCoinNum webapi = new WebapiGetCoinNum(this);
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                if (result) {
                    //
                    purchaseItem.setCoinNum(webapi.getCoinNum());
                } else {
                    purchaseItem.setCoinNum(-2);
                    // error
                    complain(webapi.getErrorMessage());
                }
                updateCoinNum();
            }
        });
    }

    void updateCoinNum() {
        // 所持コイン枚数により購入を制限
        int coinNum = getCoinNum();
        if (coinNum > 0 && coinNum < 8) {
            buy3.setEnabled(true);
            buy10.setEnabled(false);
            buyHint.setEnabled(true);
        } else if (coinNum >= 8 || coinNum < 0) {
            buy3.setEnabled(false);
            buy10.setEnabled(false);
            buyHint.setEnabled(true);
        } else {
            buy3.setEnabled(true);
            buy10.setEnabled(true);
            buyHint.setEnabled(true);
        }

        TextView t = (TextView) findViewById(R.id.text_coin_num);
        if (coinNum >= 0) {
            t.setText(getString(R.string.coin_num, coinNum));
        } else if (coinNum == -1) {
            // 取得中
            t.setText(R.string.coin_get);
        } else {
            // 取得失敗
            t.setText(R.string.err_coin_get);
        }
    }

    void updatePrices() {
        // 価格を反映
        {
            TextView t = (TextView) findViewById(R.id.text_price3);
            String price = purchaseItem.get3CoinsPrice();
            if (price != null && price.length() > 0) {
                t.setText("¥" + price);
            } else {
                t.setText(getString(R.string.noprice));
            }
        }
        {
            TextView t = (TextView) findViewById(R.id.text_price10);
            String price = purchaseItem.get10CoinsPrice();
            if (price != null && price.length() > 0) {
                t.setText("¥" + price);
            } else {
                t.setText(getString(R.string.noprice));
            }
        }
    }

    @Override
    protected void onFinishedCoinNumApi() {
        super.onFinishedCoinNumApi();
        // コイン枚数取得終わったので表示更新
        updateCoinNum();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_buyhint:
                playButtonSE();
                onBuyHint();
                break;
            case R.id.btn_buy_coin3:
                playButtonSE();
                onBuyCoin(0);
                break;
            case R.id.btn_buy_coin10:
                playButtonSE();
                onBuyCoin(1);
                break;
            case R.id.btn_buy_debug:
                if (Debug.isDebugHint10Enable) {
                    // debugのみ
                    onDebugBuyCoin();
                }
                break;
            default:
                playCloseSE();
                setDoingFlagClearParent();
                stopBGM = false;
                // 閉じる
                finish();
                break;
        }
    }

    /**
     * コインを消費してヒントを購入
     */
    private void onBuyHint() {
        if (getCoinNum() <= 0) {
            Toast.makeText(this, "コインがありません。", Toast.LENGTH_SHORT).show();
            return;
        }
        // 未処理トランザクションを確認する あれば無料で表示。無ければ消費
        final WebapiTransactions webapi = new WebapiTransactions(this);
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                if (result) {
                    ArrayList<String> list = webapi.getTransactions();
                    if (list != null && list.size() > 0) {
                        // 一つ目を消費する
                        callHint(list.get(0));
                    } else {
                        alertHint();
                        // callPointConsumeApi();
                    }
                } else {
                    alert("コインの状態が確認できません。");
                }
            }
        });
    }

    /**
     * ポイント消費API呼び出し
     */
    void callPointConsumeApi() {
        // コイン消費API呼び出しなど
        final WebapiPointConsume webapi = new WebapiPointConsume(this);
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                if (result) {
                    String pointTransaction = webapi.getPointTransactino();
                    callHint(pointTransaction);
                } else {
                    alert(getString(R.string.err_hint_consume));
                }
            }
        });
    }

    /**
     * コインを購入(3枚2セット/10枚1セット) <br>
     * PurchaseCoinFragmentにも同じ処理があるので、どこかで共通化しても良い
     */
    private void onBuyCoin(int buyMode) {
        setOnPusrhcaseFinishedListener(this);
        if (buyMode == COIN3) {
            purchaseCoin3();
        } else {
            purchaseCoin10();
        }
    }

    /**
     * ヒントを実際に表示する<br>
     * コイン消費が成功した場合に呼んでください
     */
    void callHint(String pointTransaction) {

        Intent data = new Intent();
        data.putExtra(Common.KEY_POINT_TRANSACTION, pointTransaction);
        setResult(Common.RESULT_DOHINT, data);
        setDoingFlagClearParent();
        stopBGM = false;
        finish();
    }

    @Override
    public void onQueryItemFinished(IabResult result,
                                    ArrayList<SkuDetails> itemDetailList,
                                    ArrayList<Purchase> itemPurchaseList) {
        //
        super.onQueryItemFinished(result, itemDetailList, itemPurchaseList);

        // 価格更新
        updatePrices();
    }

    @Override
    public void onPurchaseFinished(boolean result) {
        updateCoinNum();
    }

    // // for debug
    private void onDebugBuyCoin() {
        if (!Debug.isDebugHint10Enable)
            return;
        final WebapiPoint webapi = new WebapiPoint(this);
        webapi.setPointId(WebapiPoint.POINTID_DEBUG_COIN);
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                if (result) {
                    purchaseItem.setCoinNum(webapi.getCoinNum());
                    updateCoinNum();
                } else {
                    alert(webapi.getErrorMessage());
                }
            }
        });
    }

    private void setDoingFlagClearParent() {
        GameActivity g = GameActivity.getInstance();
        g.setDoingOther(false);
    }

    void alertHint() {
        setDoingOther(true);
        HintConsumeDialog dlg = new HintConsumeDialog();
        dlg.show(getFragmentManager(), "HintAlert");
    }

    public static class HintConsumeDialog extends BaseDialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setMessage(R.string.hint_alert);
            b.setNegativeButton(android.R.string.cancel, null);
            b.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HintActivity activity = (HintActivity) getActivity();
                            if(activity != null) {
                                activity.callPointConsumeApi();
                            }
                        }
                    });
            b.setCancelable(true);
            setCancelable(true);
            return b.create();
        }
    }
}
