package jp.co.cybird.android.conanescape01;

/**
 * Android 汎用処理クラス
 *
 * @author S.Kamba
 */
public class Common {

    /**
     * ログ用のタグ
     */
    public static final String TAG = "ConanEscape2";
    /**
     * prefernceキー：sound
     */
    public static final String PREF_KEY_SOUND = "sound";
    /**
     * prefernceキー：se
     */
    public static final String PREF_KEY_SE = "se";
    /**
     * preferenceキー：利用規約同意
     */
    public static final String PREF_KEY_FIRST = "first";
    /**
     * preferenceキー：前回起動バージョン
     */
    public static final String PREF_KEY_VERSION = "version";
    /**
     * preferenceキー：saved stage no
     */
    public static final String PREF_KEY_STAGENO = "stageno";
    /**
     * preferenceキー：クリアフラグ
     */
    public static final String PREF_KEY_CLEARFLAGS = "clear_flags";
    /**
     * preferenceキー：stage購入フラグ
     */
    public static final String PREF_KEY_STAGE_PURCHASED = "stage_purchased";

    /**
     * API web scheme
     */
    public static final String WEBAPI_SCHEME = "https";

    /**
     * ASSETS prefix
     */
    public static final String ASSETS_PREFIX = "file:///android_asset/";

    /**
     * オプション画面呼び出しリクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_OPTIONS = 100;
    /**
     * ステージ選択画面呼び出しリクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_SELECT = 101;
    /**
     * 初回用遊び方画面呼び出しリクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_FIRST_HOWTOPLAY = 102;
    /**
     * ゲーム画面呼び出しリクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_GAME = 103;
    /**
     * 購入画面リクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_PURCHASE = 104;
    /**
     * ステージデータローディング画面リクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_LOADING = 105;
    /**
     * ヒント画面リクエストコード
     */
    public static final int ACTIVITY_REQUESTCODE_HINT = 106;

    /**
     * フラグメントargs・インテントextra用キー
     */
    public static final String KEY_FROM_GAGME = "fromgame";
    public static final String KEY_STAGE_NO = "stageno";
    public static final String KEY_NEWGAME = "newgame";
    public static final String KEY_NEXTGAME = "next";
    public static final String KEY_SAVED_NODEFILE = "saved_node";
    public static final String KEY_SAVED_ITEMFILE = "saved_item";
    public static final String KEY_POINT_TRANSACTION = "point_transaction";
    public static final String KEY_URL = "url";
    public static final String KEY_GA_SCREENNAME = "ga_screen_name";

    /**
     * GameActivityリザルトコード:トップへ戻る
     */
    public static final int RESULT_BACKTOTOP = 100;
    /**
     * LoadActivityリザルトコード:ロードエラー
     */
    public static final int RESULT_LOAD_ERROR = 101;
    /**
     * PurchaseActivityリザルトコード：ステージ購入完了
     */
    public static final int RESULT_STAGE_PURCHASED = 102;
    /**
     * PurchaseActivityリザルトコード：コイン購入完了
     */
    public static final int RESULT_COIN_PURCHASED = 103;
    /**
     * HintActivityリザルトコード：コイン消費
     */
    public static final int RESULT_DOHINT = 104;

    /**
     * BillingBaseActivityリザルトコード：同意画面で非許諾
     */
    public static final int RESULT_AGREEMENT_DENY = 105;

    /**
     * Node遷移アニメーション時間(ms)
     */
    public static final int NODE_ANIM_DURATION = 200;

    public static final int STAGE_PROROGUE = 0;
    public static final int STAGE_1 = 1;
    public static final int STAGE_2 = 2;
    public static final int STAGE_3 = 3;
    public static final int STAGE_4 = 4;
    public static final int STAGE_EPILOGUE = 5;
    public static final int STAGE_LAST = STAGE_EPILOGUE;
    public static final int STAGE_NUM = STAGE_LAST + 1;

    /**
     * 広告用アプリパッケージ名
     */
    public static final String PACKAGE_ESCAPE1 = "jp.co.cybird.app.android.conanroom01";
    public static final String PACKAGE_MYSTERY = "jp.co.cybird.conannara";

    public static String getEncryptAPIKey() {
        return "LCgoIygLIC8jBgoQCQoIJlgWUSMgMCQnICAuIiAwWSAsKCgjIgYqIiAwJCBSFU4RVlcEO1QiIgUmWSMiBQY0DQI2FTtOMzkOOSwKBwQTFiI5JgUSCQMpEwgvUSsAWRQbDAo3IxYIC1cCFBYFJgAJGCIgVDlSABg2MzJVBztXNRg5CAxZEwMjKgcNWRsLA0otVAAbTlEUNy4kDCg5FAoCGyM5GUpKMCYIFVVXVRc5GCxQEAQgUDQSDyoIOQ5RCAUGMQcbNiQrMiArBCo5USwQIgg0ThYJVzQFNCI4JBMCDApQKAwmDRsuDgwwTismDBYoWDsUBgU2IAYYVhUgJVAvDyw0GQpYElMnDBhWNioTJk47NCQuNDMRNyUCLQsXClUzDlQuLRJUADdOEiwpVVdZLwUGVA8EJ1BTA1JSBCMsVgcvWVMIADs0DjcmNyMVJlY4CwxTURVWMSlYMFYwJTQOCFMvAhM5D1AZKBQOGxIEKyINFy0lBwI4UgsLEzJXDVISMBYoJSAwICMGUr";
    }

}
