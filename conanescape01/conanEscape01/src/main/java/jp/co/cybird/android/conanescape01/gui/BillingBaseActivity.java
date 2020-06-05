package jp.co.cybird.android.conanescape01.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import jp.co.cybird.android.billing.BaseActivity;
import jp.co.cybird.android.billing.util.IabHelper;
import jp.co.cybird.android.billing.util.IabResult;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.billing.util.SkuDetails;
import jp.co.cybird.android.compliance.AgreementUtil;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.model.PurchaseItem;
import jp.co.cybird.android.conanescape01.webapi.WebapiFinishListener;
import jp.co.cybird.android.conanescape01.webapi.WebapiGetCoinNum;
import jp.co.cybird.android.conanescape01.webapi.WebapiPoint;
import jp.co.cybird.android.conanescape01.webapi.WebapiVerify;
import jp.co.cybird.android.escape.dialog.NetworkErrorDialog;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.NetworkUtil;
import jp.co.cybird.android.minors.MinorsDialogListener;
import jp.co.cybird.android.minors.MinorsDialogListener.OnAgreeListener;
import jp.co.cybird.android.minors.MinorsDialogListener.OnDeclineListener;
import jp.co.cybird.android.minors.MinorsDialogManager;
import jp.co.cybird.android.util.Debug;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

public class BillingBaseActivity extends BaseActivity {

    public interface OnPurchaseFinishedListener {
        public void onPurchaseFinished(boolean result);
    }

    public interface OnQueryFinishedListener {
        public void onQueryFinished(IabResult result,
                                    ArrayList<SkuDetails> itemDetailList,
                                    ArrayList<Purchase> itemPurchaseList);
    }

    boolean isPlayBGM = true;
    boolean isPlaySE = true;
    /**
     * BGM停止フラグ
     */
    boolean stopBGM = true;
    /**
     * 他の画面呼び出し中フラグ
     */
    boolean doingOther = false;

    ProgressDialog progress;
    boolean isCallPointApi = false;

    // LoadingDialog loadingDialog = null;

    ArrayList<SkuDetails> mItemDetailList = null;
    // ArrayList<Purchase> mItemPurchaseList = null;

    PurchaseItem purchaseItem = new PurchaseItem();

    OnPurchaseFinishedListener mOnPurchaseListener = null;
    OnQueryFinishedListener mOnQueryListener = null;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // loadingDialog = new LoadingDialog();
        // loadingDialog.show(getFragmentManager(), "loading");
        // ①課金のための初期処理を行う
        String encKey = Common.getEncryptAPIKey();
        String base64EncodedPublicKey = Codec.decode(encKey);
        startSetup(base64EncodedPublicKey);
    }

    ;

    @Override
    protected void onStart() {
        getSettings();

        super.onStart();
    }

    @Override
    protected void onStop() {
        // if (loadingDialog != null) {
        // loadingDialog.dismissAllowingStateLoss();
        // loadingDialog = null;
        // }
        super.onStop();
    }

    public void setPointApiCall(boolean flag) {
        isCallPointApi = flag;
    }

    // /// Billing

    // ②課金初期処理終了後にアプリ側の処理を行う
    @Override
    public void onSetupFinished(IabResult result) {
        Debug.logD("Setup finished.");

        // Is it a failure?
        if (result.isFailure()) {
            complain("GooglePlay課金サービスの初期化に失敗しました。: " + result);
            return;
        }
        Debug.logD("Setup successful. Querying inventory...");

        // ③アイテム詳細を取得する
        queryItemDetail(purchaseItem.getItemIdList());
    }

    // ④課金アイテムリスト取得後にアプリ側の処理を行う
    @Override
    public void onQueryItemFinished(IabResult result,
                                    ArrayList<SkuDetails> itemDetailList,
                                    ArrayList<Purchase> itemPurchaseList) {
        Debug.logD("On query item detail finished.");

        // EscApplication app = (EscApplication) getApplication();

        if (itemDetailList == null || itemDetailList.size() == 0) {
            // complain("価格情報の取得に失敗しました。");
            // Debug.logD("価格情報の取得に失敗しました。");
            purchaseItem.getSavedPrices(this);
            // loadingDialog.dismissAllowingStateLoss();
            // loadingDialog = null;
            // app.setPurchaseItem(purchaseItem);
        } else {

            // 価格リストを作成
            for (SkuDetails sku : itemDetailList) {
                Debug.logD("sku : id = " + sku.getSku() + " price = "
                        + sku.getPrice());
                String price = sku.getPrice().replaceAll("[^0-9]", "");
                purchaseItem.putPrice(sku.getSku(), price);
            }
            purchaseItem.savePrices(this);
            // app.setPurchaseItem(purchaseItem);
        }
        boolean isConsumeStage = false; // FIXME デバッグ用：リリース時はfalseにすること
        if (itemPurchaseList != null && itemPurchaseList.size() > 0) {
            // 購入済み:stageのみ
            for (Purchase purchase : itemPurchaseList) {
                if (purchase.getSku().equals(purchaseItem.getStageItemId())) {
                    // stage購入済み
                    PurchaseItem.setStagePurchased(this, true);
                    itemPurchaseList.remove(purchase);
                    if (isConsumeStage) {
                        // DEBUG用
                        consumeItem(purchase);
                        PurchaseItem.setStagePurchased(this, false);
                        isConsumeStage = false;
                    }
                }
            }
        }

        // point付与
        if (itemPurchaseList != null && itemPurchaseList.size() > 0) {
            if (isCallPointApi)
                callPointTradeApiMulti(itemPurchaseList);
        }

        if (mOnQueryListener != null) {
            mOnQueryListener.onQueryFinished(result, itemDetailList,
                    itemPurchaseList);
        }

        // loadingDialog.dismissAllowingStateLoss();
        // loadingDialog = null;
    }

    // ⑥アイテム課金処理完了後にアプリ側の処理を行う
    @Override
    public void onPurchaseItemFinished(IabResult result, Purchase purchase) {
        Debug.logD("Purchase finished: " + result + ", purchase: " + purchase);

        // Is it a failure?
        if (result.isFailure()) {
            if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                complain(getString(R.string.err_purchase_owned));
                return;
            }
            mOnPurchaseListener.onPurchaseFinished(false);
            return;
        }

        Debug.logD("Purchase successful.");

        showProgress();

        // coinならポイント付与
        String point_id = null;
        if (purchase.getSku().equals(purchaseItem.get3CoinsItemId())) {
            point_id = WebapiPoint.POINTID_3COINS;
        } else if (purchase.getSku().equals(purchaseItem.get10CoinsItemId())) {
            point_id = WebapiPoint.POINTID_10COINS;
        } else {
            // stage
            // 購入済みを保存
            PurchaseItem.setStagePurchased(this, true);
            // verify
            callVerifyApi(purchase);
        }
        if (point_id != null) {
            // ポイント付与APIを呼び出す
            callPointTradeApi(point_id, purchase);
            return;
        }

        if (mOnPurchaseListener != null) {
            mOnPurchaseListener.onPurchaseFinished(true);
        }

    }

    @Override
    public void onConsumeItmFinished(Purchase purchase, IabResult result) {
        super.onConsumeItmFinished(purchase, result);

        if (needPointCallPurchases != null) {
            if (call_api_num < needPointCallPurchases.size() - 1)
                return;
        }
        onFinishConsumed(result.isSuccess());
    }

    void onFinishConsumed(boolean result) {

        dismissProgress();
        if (mOnPurchaseListener != null) {
            mOnPurchaseListener.onPurchaseFinished(result);
        }
    }

    // //

    void showProgress() {
        // progress表示
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(getString(R.string.point_trading));
        progress.setCancelable(false);
        progress.show();
    }

    void dismissProgress() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    /**
     * レシートverify:ポイント以外を購入時に呼び出す
     */
    void callVerifyApi(Purchase purchase) {
        webapi = new WebapiVerify(this);
        webapi.setPurchaseData(purchase);
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                dismissProgress();
            }
        });
    }

    /**
     * ポイント付与API
     */
    void callPointTradeApi(String point_id, Purchase purchase) {
        webapi = new WebapiPoint(this);
        webapi.setPointId(point_id);
        webapi.setPurchaseData(purchase);
        // POINT付与API呼び出し
        webapi.execute(new WebapiFinishListener() {

            @Override
            public void onFinish(boolean result) {
                if (!result) {
                    dismissProgress();
                    complain(webapi.getErrorMessage());
                    if (mOnPurchaseListener != null)
                        mOnPurchaseListener.onPurchaseFinished(false);
                } else {
                    //
                    purchaseItem.setCoinNum(webapi.getCoinNum());
                    // 消費する
                    consumeItem(webapi.getPurchase());
                }
                // ポイントの場合は、ここで購入完了とする
                if (mOnPurchaseListener != null) {
                    mOnPurchaseListener.onPurchaseFinished(true);
                }
            }
        });
    }

    int call_api_num = 0;
    ArrayList<Purchase> needPointCallPurchases;
    WebapiPoint webapi;
    WebapiFinishListener onFinishedPointTradeMulti = new WebapiFinishListener() {

        @Override
        public void onFinish(boolean result) {
            if (!result) {
                dismissProgress();
                complain(webapi.getErrorMessage());
                if (mOnPurchaseListener != null)
                    mOnPurchaseListener.onPurchaseFinished(false);
            } else {
                // 消費する
                consumeItem(webapi.getPurchase());
                //
                call_api_num++;
                if (call_api_num >= needPointCallPurchases.size()) {
                    // 終わり
                    purchaseItem.setCoinNum(webapi.getCoinNum());
                    needPointCallPurchases = null;
                    dismissProgress();
                } else {
                    // 次の購入アイテムのポイント処理
                    callApi(call_api_num);
                }
            }
        }
    };

    void callPointTradeApiMulti(ArrayList<Purchase> purchasedList) {
        // progress表示
        showProgress();

        call_api_num = 0;
        needPointCallPurchases = purchasedList;
        callApi(call_api_num);

        isCallPointApi = false;
    }

    void callApi(int index) {
        webapi = new WebapiPoint(this);
        String point_id = null;
        Purchase purchase = needPointCallPurchases.get(call_api_num);
        if (purchase.getSku().equals(purchaseItem.get3CoinsItemId())) {
            point_id = WebapiPoint.POINTID_3COINS;
        } else if (purchase.getSku().equals(purchaseItem.get10CoinsItemId())) {
            point_id = WebapiPoint.POINTID_10COINS;
        }
        webapi.setPointId(point_id);
        webapi.setPurchaseData(purchase);
        // POINT付与API呼び出し
        webapi.execute(onFinishedPointTradeMulti);
    }

    /**
     * ネットワーク接続確認
     */
    protected boolean checkConnect() {
        boolean r = NetworkUtil.isConnected(this);
        if (!r) {
            setDoingOther(true);
            setStopBGM(false);
            NetworkErrorDialog d = new NetworkErrorDialog();
            d.show(getFragmentManager(), "nerr");
        }
        return r;
    }

    /**
     * ポイント取得API呼び出し
     */
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
                onFinishedCoinNumApi();
            }
        });
    }

    /**
     * コイン枚数取得API完了時処理
     */
    protected void onFinishedCoinNumApi() {
        Debug.logD("onFinishedCoinNumApi");
    }

    // //

    /**
     * ステージ購入済みフラグ取得
     */
    public boolean isStagePurchased() {
        return PurchaseItem.isStagePurchased(this);
    }

    /**
     * ステージ価格取得
     */
    public String getStagePrice() {
        return purchaseItem.getStagePrice();
    }

    /**
     * コインx3価格取得
     */
    public String get3CoinsPrice() {
        return purchaseItem.get3CoinsPrice();
    }

    /**
     * コインx10価格取得
     */
    public String get10CoinsPrice() {
        return purchaseItem.get10CoinsPrice();
    }

    public int getCoinNum() {
        return purchaseItem.getCoinNum();
    }

    private void minorsOnDenied() {
        setDoingOther(true);
        stopBGM = false;
        setResult(Common.RESULT_AGREEMENT_DENY);
        finish();
    }

    private void minorsOnAgreed(String itemId) {
        try {
            purchaseItem(itemId);
        } catch (Exception e) {
            complain(getString(R.string.err_purchase));
        }
    }

    private void purchase(final String itemId) {
        OnDeclineListener onDecline = new OnDeclineListener() {
            @Override
            public void onDecline() {
                minorsOnDenied();
            }
        };
        MinorsDialogListener.OnCancelListener onCancel = new MinorsDialogListener.OnCancelListener() {
            @Override
            public void onCancel() {
                minorsOnDenied();
            }
        };
        OnAgreeListener onAgree = new OnAgreeListener() {
            @Override
            public void onAgree() {
                minorsOnAgreed(itemId);
            }
        };
        MinorsDialogManager dialog = AgreementUtil.newMinorsManager(this,getFragmentManager());
        dialog.setOnAgreeListener(onAgree);
        dialog.setOnDeclineListener(onDecline);
        dialog.setOnCancelListener(onCancel);
        if (dialog.isAgreement()) {
            try {
                purchaseItem(itemId);
            } catch (Exception e) {
                complain(getString(R.string.err_purchase));
            }
        } else {
            dialog.show();
        }
    }

    /**
     * stageを購入
     */
    public void purchaseStage() {
        purchase(purchaseItem.getStageItemId());
    }

    /**
     * hint coin3を購入
     */
    public void purchaseCoin3() {
        purchase(purchaseItem.get3CoinsItemId());
    }

    /**
     * hint coin10を購入
     */
    public void purchaseCoin10() {
        purchase(purchaseItem.get10CoinsItemId());
    }

    /**
     * 購入終了リスナ
     */
    public void setOnPusrhcaseFinishedListener(OnPurchaseFinishedListener l) {
        mOnPurchaseListener = l;
    }

    /**
     * query終了リスナ
     */
    public void setOnQueryFinishedListener(OnQueryFinishedListener l) {
        mOnQueryListener = l;
    }

    /**
     * preferenceに保存された設定を取得
     */
    void getSettings() {
        SharedPreferences pref = getSharedPreferences(Common.TAG,
                Context.MODE_PRIVATE);
        isPlayBGM = pref.getBoolean(Common.PREF_KEY_SOUND, true);
        isPlaySE = pref.getBoolean(Common.PREF_KEY_SE, true);
    }

    /**
     * SE再生フラグを取得
     */
    public boolean isPlaySE() {
        return isPlaySE;
    }

    /**
     * BGM再生フラグを取得
     */
    public boolean isPlayBGM() {
        return isPlayBGM;
    }

    /**
     * ボタンSE再生
     */
    public void playButtonSE() {
        if (isPlaySE) {
            SoundManager.getInstance().playButtonSE();
        }
    }

    /**
     * closeSE再生
     */
    public void playCloseSE() {
        if (isPlaySE) {
            SoundManager.getInstance().playCloseSE();
        }
    }

    synchronized public void setDoingOther(boolean flag) {
        doingOther = flag;
    }

    /**
     * 他の画面を実行中フラグ
     */
    synchronized public boolean getDoingOther() {
        return doingOther;
    }

    public void setStopBGM(boolean flag) {
        stopBGM = flag;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Debug.logD(getClass().getSimpleName()
                + ":onWindowFocusChanged:hasFocus=" + hasFocus + " stopBGM="
                + stopBGM + " doingOther=" + doingOther);
        // onResumeだとスクリーン点灯からのロック画面表示時にきてしまう機種がある
        // ↑4.1以降の修正らしい
        // のでここでやる
        if (hasFocus) {
            if (!getDoingOther()) {
                // 再生再開
                if (isPlayBGM && stopBGM) {
                    SoundManager.getInstance().startBGM();
                }
                stopBGM = true;
            }
        } else {
            if (!getDoingOther()) {
                // BGM再生停止
                if (isPlayBGM && stopBGM) {
                    SoundManager.getInstance().pauseBGM();
                }
            }
        }
    }
}
