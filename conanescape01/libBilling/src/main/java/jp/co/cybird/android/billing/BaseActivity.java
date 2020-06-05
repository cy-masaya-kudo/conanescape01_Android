package jp.co.cybird.android.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import jp.co.cybird.android.billing.util.DebugLog;
import jp.co.cybird.android.billing.util.IabHelper;
import jp.co.cybird.android.billing.util.IabResult;
import jp.co.cybird.android.billing.util.Inventory;
import jp.co.cybird.android.billing.util.Purchase;
import jp.co.cybird.android.billing.util.SkuDetails;

public class BaseActivity extends Activity {
    // Debug tag, for logging

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        DebugLog.d("Destroying helper.");
        if (mHelper != null) {
            try {
                mHelper.dispose();
            }catch (IabHelper.IabAsyncInProgressException e) {

            }

            mHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.d("onActivityResult(" + requestCode + "," + resultCode + ","
                + data);
        if (mHelper == null)
            return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            DebugLog.d("onActivityResult handled by IABUtil.");
        }
    }

    // 課金初期処理（成功後にアイテムリストを取得する）
    public void startSetup(String appPublicKey) {
        // Create the helper, passing it our context and the public key to
        // verify signatures with
        DebugLog.d("Creating IAB helper.");
        mHelper = new IabHelper(this, appPublicKey);

        // enable debug logging (for a production application, you should set
        // this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        DebugLog.d("Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                DebugLog.d("Setup finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null)
                    return;

                onSetupFinished(result);
            }
        });
    }

    // 課金初期処理終了後にアプリ側の処理を行う
    public void onSetupFinished(IabResult result) {
        DebugLog.d("Setup finished.");
    }

    // 課金アイテム詳細を取得する
    public void queryItemDetail(ArrayList<String> skuIdList) {
        try {
            mHelper.queryInventoryAsync(true, skuIdList, null, mGotInventoryListener);
        }catch (IabHelper.IabAsyncInProgressException e) {

        }
    }

    // 購入済アイテム詳細を取得する
    public void queryItemPurchase() {
        try {
            mHelper.queryInventoryAsync(true, null, null, mGotInventoryListener);
        }catch (IabHelper.IabAsyncInProgressException e) {

        }
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            DebugLog.d("Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;

			/*
            * Check for items we own. Notice that for each purchase, we check
			* the developer payload to see if it's correct! See
			* verifyDeveloperPayload().
			*/
            onQueryItemFinished(result,
                    (inventory == null) ? null : inventory.getAllSkuDetails(),
                    (inventory == null) ? null : inventory.getAllPurchases());
        }
    };

    // 課金アイテムリスト取得後にアプリ側の処理を行う
    public void onQueryItemFinished(IabResult result,
                                    ArrayList<SkuDetails> ItemDetailList,
                                    ArrayList<Purchase> ownedItemPurchaseList) {
        DebugLog.d("On query inventory finished.");
    }

    // アイテム課金処理
    public void purchaseItem(final String itemId) {
        try {
            mHelper.launchPurchaseFlow(this, itemId, RC_REQUEST,
                    mPurchaseFinishedListener, "");
        }catch (IabHelper.IabAsyncInProgressException e) {

        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            DebugLog.d("Purchase finished: " + result + ", purchase: "
                    + purchase);

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;

            onPurchaseItemFinished(result, purchase);
        }
    };

    // アイテム課金処理完了後にアプリ側の処理を行う
    public void onPurchaseItemFinished(IabResult result, Purchase purchase) {
        DebugLog.d("On purchase finished: " + result + ", purchase: "
                + purchase);
    }

    // アイテムを消費する
    public void consumeItem(Purchase purchase) {
        DebugLog.d("We have item:" + purchase.getOriginalJson()
                + " Consuming it.");

        try {
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }catch (IabHelper.IabAsyncInProgressException e) {

        }

    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            DebugLog.d("Consumption finished. Purchase: " + purchase
                    + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            // We know this is the "gas" sku because it's the only one we
            // consume,
            // so we don't check which sku was consumed. If you have more than
            // one
            // sku, you probably should check...

            onConsumeItmFinished(purchase, result);
        }
    };

    // アイテム消費後にアプリ側の処理を行う
    public void onConsumeItmFinished(Purchase purchase, IabResult result) {
        DebugLog.d("On consumption finished. Purchase: " + purchase
                + ", result: " + result);

    }

    // 複数アイテムを消費する
    public void consumeMultiItems(List<Purchase> purchases) {
        DebugLog.d("We have item:" + purchases + " Consuming it.");

        try {
            mHelper.consumeAsync(purchases, mConsumeMultiFinishedListener);
        }catch (IabHelper.IabAsyncInProgressException e) {

        }

    }

    // Called when consumption is complete
    IabHelper.OnConsumeMultiFinishedListener mConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {
        public void onConsumeMultiFinished(List<Purchase> purchases,
                                           List<IabResult> results) {
            DebugLog.d("On multi consumption finished. Purchases: " + purchases
                    + ", results: " + results);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            // We know this is the "gas" sku because it's the only one we
            // consume,
            // so we don't check which sku was consumed. If you have more than
            // one
            // sku, you probably should check...

            onConsumeMultiItemFinished(purchases, results);
        }
    };

    // アイテム消費後にアプリ側の処理を行う
    public void onConsumeMultiItemFinished(List<Purchase> purchases,
                                           List<IabResult> results) {
        DebugLog.d("On multi consumption finished. Purchases: " + purchases
                + ", results: " + results);

    }

    public void complain(String message) {
        DebugLog.e("**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    public void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        DebugLog.d("Showing alert dialog: " + message);
        bld.create().show();
    }

}
