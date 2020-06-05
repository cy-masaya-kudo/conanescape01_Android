package jp.co.cybird.android.conanescape01.gui;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.GameManager.GameManagerSoundEventListsner;
import jp.co.cybird.android.conanescape01.GameManager.GameMangerFinishEventListener;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.EngineCallback.EngineEventListener;
import jp.co.cybird.android.conanescape01.minigame.NineBoxPuzzle;
import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.escape.dialog.EndDialog;
import jp.co.cybird.android.escape.dialog.NextStageDialog;
import jp.co.cybird.android.escape.dialog.SaveDialog;
import jp.co.cybird.android.escape.dialog.SaveDialog.OnSaveFinishListener;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.util.Debug;
import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.object.Status;
import jp.co.cybird.escape.engine.lib.object.TextSet;
import jp.co.cybird.escape.engine.lib.object.item.Item;
import jp.co.cybird.escape.engine.lib.util.Consts;

/**
 * ゲームメイン画面
 *
 * @author S.Kamba
 */
public class GameActivity extends ConanActivityBase implements
        EngineEventListener, OnLongClickListener,
        GameMangerFinishEventListener, GameManagerSoundEventListsner {
    GameManager gm = null;

    /**
     * ジェスチャー用
     */
    GestureDetector gestureDetector = null;

    /**
     * node表示ビュー
     */
    ImageView nodeView = null;
    /**
     * アイテム拡大表示用ビュー
     */
    View itemZoomView = null;

    /**
     * nodeImageViewのオフセット:当たり判定の座標調整に使う
     */
    int nodeImageViewOffset[] = null;

    /**
     * itemZoomViewのオフセット：当たり判定の座標調整に使う
     */
    int itemZoomViewOffset[] = null;

    /**
     * 9マスパズル用
     */
    ViewGroup puzzle_layout = null;
    NineBoxPuzzle puzzle = null;

    /**
     * node遷移アニメーション
     */
    ImageView animView = null;
    boolean isAnimating = false;

    /**
     * エンジンからのコールバックを受け取る
     */
    EngineCallback engineCallback = null;

    /**
     * 操作可能フラグ
     */
    boolean enableTouch = true;

    static GameActivity obj = null;

    public GameActivity() {
        super();
        //
        obj = this;
    }

    public static GameActivity getInstance() {
        return obj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        nodeView = (ImageView) findViewById(R.id.nodeImage);
        itemZoomView = findViewById(R.id.itemview_root);

        // デバッグ用アイテム獲得ボタン
        if (!Debug.isDebug) {
            View b = findViewById(R.id.btn_allitem);
            b.setVisibility(View.GONE);
        }

        // デバッグ用アイテム獲得(長押し)
        if (Debug.isDebug) {
            View b = null;
            for (int i = 0; i < Consts.ITEM_NUM_MAX; i++) {
                String name = String.format("item%02d", (i + 1));
                int resid = getResources().getIdentifier(name, "id",
                        getPackageName());
                b = findViewById(resid);
                b.setOnLongClickListener(this);
            }
        }

        // ジェスチャーの設定
        gestureDetector = new GestureDetector(this, gestureListener);

        // 設定を取得
        getSettings();

        // 初期化
        initGame(savedInstanceState == null ? false : true);
    }

    private void initGame(boolean restart) {
        Debug.logD("GameActivity:intiGame");

        EscApplication app = (EscApplication) getApplication();
        gm = app.getGameManager();

        // アイテム欄いったん全非表示
        for (int i = 0; i < gm.getItemAreaNum(); i++) {
            String name = String.format("lay_item%02d", (i + 1));
            int resid = getResources().getIdentifier(name, "id",
                    getPackageName());
            View frame = findViewById(resid);
            if (frame != null) {
                frame.setVisibility(View.INVISIBLE);
            }
        }

        // 英語版はヒント無し
        // if (!Locale.JAPAN.equals(Locale.getDefault())) {
        // // 日本語以外
        // View b = findViewById(R.id.btn_hint);
        // b.setVisibility(View.INVISIBLE);
        // }
        // 言語の設定
        // if (Locale.JAPAN.equals(Locale.getDefault())) {
        gm.setLanguage(TextSet.LANGUAGE_JAPANESE);
        // } else {
        // gm.setLanguage(TextSet.LANGUAGE_ENGLISH);
        // }

        // コールバック群作成
        engineCallback = new EngineCallback(this, this,
                findViewById(R.id.root), gm);

        // 最初の描画
        gm.setFinishEventListener(this);
        setEngineCallbacks();
        if (app.isNewGame()) {
            gm.startOpening();
        } else {
            gm.gameStart();
        }
        // 音楽初期化
        SoundManager.getInstance().release();
        gm.initSounds();
        gm.setSoundEventListener(this);
        // 再生準備開始
        SoundManager.getInstance().prepareBGM();

        // ミニゲーム
        puzzle = new NineBoxPuzzle(gm, getDraggingImageView());
        gm.setMiniGameRunner(puzzle);

        fadeoutBlackFade();
        // showBlackFade(false);

    }

    @Override
    public void onBackPressed() {
        if (Debug.isDebug) {
            finishGameActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // GoogleAnalytics
        Tracking.sendView("Game");
    }

    @Override
    public void onDestroy() {
        cleanupView(findViewById(R.id.root));
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 画面回転時、画面点灯オフ時に破棄→作成されないために
        // これをオーバーライドしておくといいらしい
        if (Debug.isDebug) {
            Debug.logD("onConfigurationChanged");
        }
        //
        super.onConfigurationChanged(newConfig);
    }

    public void releaseSounds() {
        Debug.logD("GameActivity:releaseSounds");
        // 音関連解放
        // if (isPlayBGM) {
        SoundManager.getInstance().stopBGM();
        SoundManager.getInstance().release();
        // }
    }

    public void clearEngineCallbacks() {
        gm.setOnDrawCallback(null);
        gm.setOnSECallback(null);
        gm.setOnActiveChangeCallback(null);
        gm.setOnEventCallback(null);
    }

    public void setEngineCallbacks() {
        gm.setOnDrawCallback(engineCallback);
        gm.setOnSECallback(engineCallback);
        gm.setOnActiveChangeCallback(engineCallback);
        gm.setOnEventCallback(engineCallback);
    }

    /**
     * Hintボタン押下処理
     *
     * @param v not used
     */
    public void onHint(View v) {
        if (!engineCallback.enableClick)
            return;

        if (gm.isEventRunning())
            return;

        playButtonSE();

        // HintDialog
        stopBGM = false; // bgm止めない
        doingOther = true;
        // HintDialog dlg = new HintDialog();
        // dlg.show(getFragmentManager(), "hint");
        Intent intent = new Intent(this, HintActivity.class);
        startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_HINT);
    }

    /**
     * Saveボタン押下処理
     *
     * @param v not used
     */
    public void onSave(View v) {
        if (!engineCallback.enableClick)
            return;

        if (gm.isEventRunning())
            return;

        playButtonSE();

        // セーブ実施
        EscApplication app = (EscApplication) getApplication();
        boolean result = gm.save(
                new File(app.getDir("save", Context.MODE_PRIVATE),
                        "data_node.csv").getAbsolutePath(),
                new File(app.getDir("save", Context.MODE_PRIVATE),
                        "data_item.csv").getAbsolutePath());
        Debug.logD("save " + (result ? "success" : "fail"));

        if (result) {
            stopBGM = false; // 停止しない
            doingOther = true;
            app.setSavedStageNo(gm.getStageNo());
            SaveDialog dlg = new SaveDialog();
            dlg.setOnSaveFinishListener(new OnSaveFinishListener() {

                @Override
                public void onSaveFinish(boolean backToTop) {
                    if (backToTop) {
                        // topへ戻る
                        finishGameActivity();
                    }
                }
            });
            dlg.show(getFragmentManager(), "save");
        } else {
            Toast.makeText(this, "保存に失敗しました。", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Optionボタン押下処理
     *
     * @param v not used
     */

    public void onOptions(View v) {
        if (!engineCallback.enableClick)
            return;

        playButtonSE();

        stopBGM = false; // 停止しない
        doingOther = true;

        // Option ボタン押下処理
        Intent intent = new Intent(getApplicationContext(),
                OptionActivity.class);
        intent.putExtra(Common.KEY_FROM_GAGME, true);
        startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_OPTIONS);
    }

    /**
     * アイテム欄クリック処理
     *
     * @param v 押下したビュー
     */
    public void onClickItem(View v) {

        // セリフ表示中は無視
        if (gm.isSerifShowing() || gm.isHintShowing()) {
            return;
        }

        // 同じのを二回目以上タップならアイテムズーム
        Item item = getItemFromCellView(v);

        Item ac_item = gm.getActiveItem();
        if (ac_item != null && item == ac_item) {
            showItemZoom(v);
            return;
        }

        // 古い方のアクティブを解除
        if (ac_item != null) {
            int old_index = ac_item.getDisplayIndex();
            View acv = getItemActiveCellView(old_index);
            acv.setVisibility(View.VISIBLE);
        }
        try {
            int index = item.getDisplayIndex();
            gm.onClickItem(index);

            if (gm.getActiveItem() != null) {
                // アクティブアイテム枠を表示

                if (index < 0)
                    return;

                View acv = getItemActiveCellView(index);
                acv.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ItemCellViewからアイテム欄のインデクスを取得
     */
    int getItemIndexFromCellView(View v) {
        try {
            String tag = (String) v.getTag();
            int index = Integer.valueOf(tag) - 1;
            return index;
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * ItemCellViewにセットされているItemオブジェクトを取得
     */
    Item getItemFromCellView(View v) {
        Object tag = v.getTag(R.id.TAG_ITEM);
        if (tag != null && tag instanceof Item) {
            return (Item) tag;
        }
        return null;
    }

    /**
     * アイテムを拡大表示
     *
     * @param v
     */
    void showItemZoom(View v) {
        //
        Item item = getItemFromCellView(v);
        if (item == null)
            return;
        if (gm.isItemGet(item)) {
            int index = item.getDisplayIndex();
            // アイテム拡大表示処理
            gm.setActiveItemFromDisplayIndex(index);
            gm.setItemDetailShowing(true, index);
            gm.draw(); // 再描画
        }
    }

    // ------------------
    // MARK CALLBAKCKS
    // ------------------

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_close:
                if (gm.isHintShowing() || gm.isEventRunning())
                    return;
                closeItemZoom();
                return;
        }

    }

    void closeItemZoom() {
        // アイテム画面を閉じる
        itemZoomView.setVisibility(View.INVISIBLE);
        // アクティブアイテムはいったんクリア
        clearActiveCell();

        // 終了を通知
        gm.setItemDetailShowing(false, 0);
    }

    // デバッグ用の全アイテム表示ボタン
    public void onAllItemGet(View v) {
        if (!Debug.isDebug)
            return; // 念のため

        // 獲得ではなく、表示だけONする(獲得は長押しで個別にやる)
        GameManagerBase.drawAllItems = true;
        gm.drawItemList();
    }

    @Override
    public boolean onLongClick(View v) {
        if (!Debug.isDebug)
            return false;

        Item item = getItemFromCellView(v);
        if (item == null)
            return false;

        item.flagON(Status.FLAG_GOT);
        item.flagOFF(Status.FLAG_USED);
        Toast.makeText(this, "item id= " + item.getId() + " get flag on",
                Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.logD("GameActivity:onActivityResult");

        switch (requestCode) {
            case Common.ACTIVITY_REQUESTCODE_OPTIONS: {
                boolean old_bgm = isPlayBGM;
                // 設定値を再取得
                getSettings();
                if (old_bgm != isPlayBGM) {
                    if (isPlayBGM) {
                        SoundManager.getInstance().prepareBGM();
                    }
                }
                if (resultCode == Common.RESULT_BACKTOTOP) {
                    // TOPに戻る
                    finishGameActivity();
                }
            }
            break;
            case Common.ACTIVITY_REQUESTCODE_PURCHASE:
                // 購入画面からの戻り
                if (resultCode == RESULT_OK) {
                    // stage購入完了
                    Intent intent = new Intent(this, LoadingActivity.class);
                    intent.putExtra(Common.KEY_NEWGAME, false);
                    int stage = gm.getStageNo();
                    intent.putExtra(Common.KEY_STAGE_NO, stage + 1);
                    startActivityForResult(intent,
                            Common.ACTIVITY_REQUESTCODE_LOADING);

                }
                break;
            case Common.ACTIVITY_REQUESTCODE_LOADING:
                if (resultCode == Common.RESULT_LOAD_ERROR) {
                    // finish();
                    showBlackFade(false);
                } else {
                    // restart Game
                    restartGame();
                }
                break;
            case Common.ACTIVITY_REQUESTCODE_HINT:
                if (resultCode == Common.RESULT_DOHINT) {
                    // ヒント実行
                    String pointTransaction = data
                            .getStringExtra(Common.KEY_POINT_TRANSACTION);
                    showHint(pointTransaction);
                } else if (resultCode == Common.RESULT_AGREEMENT_DENY) {
                    setStopBGM(true);
                    setDoingOther(false);
                }
                break;
        }
    }

    /**
     * ヒント表示を実行
     */
    void showHint(String pointTransaction) {
        //
        EscApplication app = (EscApplication) getApplication();
        GameManager gm = app.getGameManager();
        gm.onHint(pointTransaction);
    }

    // ------------------
    // MARK ENGINE CALLBACKS
    // ------------------

    /**
     * アクティブアイテムをクリア
     */
    @Override
    public void clearActiveCell() {
        for (int i = 0; i < gm.getItemAreaNum(); i++) {
            View acv = getItemActiveCellView(i);
            acv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAnimationFlag(boolean b) {
        isAnimating = b;
    }

    /**
     * アイテム用のアクティブ枠Viewを取得
     *
     * @param index 　アイテムインデクス
     * @return View
     */
    @Override
    public View getItemActiveCellView(int index) {
        String ac_name = String.format(Locale.ENGLISH, "ac_item%02d",
                (index + 1));
        int resid = getResources().getIdentifier(ac_name, "id",
                getPackageName());
        View acv = findViewById(resid);
        return acv;
    }

    @Override
    public boolean isPlaySE() {
        return isPlaySE;
    }

    // ------------------
    // GESTURES
    // ------------------
    boolean isTouching = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (gm.isItemDetailShowing())
            return gestureDetector.onTouchEvent(event);

        if (nodeImageViewOffset == null) {
            nodeImageViewOffset = new int[2];
            nodeView.getLocationInWindow(nodeImageViewOffset);
            int x = nodeImageViewOffset[0];
            int y = nodeImageViewOffset[1];
            Debug.logD("nodeImage view x=" + x + ", y=" + y);
            int w = nodeView.getWidth();
            int h = nodeView.getHeight();
            Debug.logD("nodeImage view w=" + w + ", h=" + h);
        }

        if (gm.isEventRunning())
            return gestureDetector.onTouchEvent(event);

        if (gm.isRunningMiniGame()) {
            puzzle.onTouchEvent(event, nodeImageViewOffset);
        } else {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                // タッチダウン処理
                gm.onTouchDown(
                        (int) (event.getX() - nodeImageViewOffset[0] + 0.5f),
                        (int) (event.getY() - nodeImageViewOffset[1] + 0.5f));
                isTouching = true;
            } else if (action == MotionEvent.ACTION_UP) {
                if (isTouching) {
                    // タッチアップ処理
                    gm.onTouchUp(
                            (int) (event.getX() - nodeImageViewOffset[0] + 0.5f),
                            (int) (event.getY() - nodeImageViewOffset[1] + 0.5f));
                }
                isTouching = false;
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (!enableTouch) {
                return true;
            }
            if (isAnimating) {
                // 移動アニメーション中はフリック無し
                return super.onFling(event1, event2, velocityX, velocityY);
            }
            // フリック処理
            if ((event2.getY() - event1.getY()) > 120
                    && Math.abs(velocityY) > 200) {
                if (gm.isItemDetailShowing()) {
                    if (!gm.isEventRunning() && !gm.isSerifShowing())
                        closeItemZoom();
                } else {
                    // 下へフリック
                    gm.onFlickDown();
                }
                return true;
            }
            if (gm.isItemDetailShowing()) {
                // アイテム表示中やイベント中は左右フリック無し
                return super.onFling(event1, event2, velocityX, velocityY);
            }

            if (event1.getX() - event2.getX() > 120
                    && Math.abs(velocityX) > 200) {
                // 右へフリック
                gm.onFlickLeft();
                return true;

            } else if (event2.getX() - event1.getX() > 120
                    && Math.abs(velocityX) > 200) {
                // 左へフリック
                gm.onFlickRight();
                return true;
            }

            return super.onFling(event1, event2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {

            if (!enableTouch)
                return true;

            // クリック処理
            if (gm.isRunningMiniGame() && !gm.isHintShowing()) {
                return true;
            }

            if (isAnimating)
                return true;

            if (gm.isItemDetailShowing()) {
                if (itemZoomViewOffset == null) {
                    View root = findViewById(R.id.img_itemzoom);
                    itemZoomViewOffset = new int[2];
                    root.getLocationInWindow(itemZoomViewOffset);
                    int x = itemZoomViewOffset[0];
                    int y = itemZoomViewOffset[1];
                    Debug.logD("img_itemzoom view x=" + x + ", y=" + y);
                }

                gm.onClick((int) (event.getX() - itemZoomViewOffset[0] + 0.5f),
                        (int) (event.getY() - itemZoomViewOffset[1] + 0.5f));
            } else {
                gm.onClick(
                        (int) (event.getX() - nodeImageViewOffset[0] + 0.5f),
                        (int) (event.getY() - nodeImageViewOffset[1] + 0.5f)); // 四捨五入で当たり判定
            }
            return true;
            // return super.onSingleTapUp(event);
        }

    };

    @Override
    public void onGameFinish() {
        if (gm.getStageNo() == Common.STAGE_LAST) {
            // 最終ステージなら終了ダイアログを表示
            stopBGM = false; // 停止しない
            doingOther = true;
            EndDialog d = new EndDialog();
            d.show(getFragmentManager(), "end");
        } else {

            // ステージクリアをセット
            Stage s = gm.getStage();
            s.clear = true;
            Stage.saveStageClearFlags(this,
                    ((EscApplication) getApplication()).getStageList());
            stopBGM = false;
            doingOther = true;
            // ステージクリア画面を表示
            NextStageDialog d = new NextStageDialog();
            d.show(getFragmentManager(), "next");
            enableTouch = false;
        }
    }

    public void restartInit() {
        Debug.logD("GameActivity:restartInit");
        releaseSounds();
        DialogFragment f = (DialogFragment) getFragmentManager()
                .findFragmentByTag("next");
        if (f != null)
            f.dismissAllowingStateLoss();
        showBlackFade(true);
    }

    /**
     * ゲーム画面再開
     */
    private void restartGame() {
        Debug.logD("GameActivity:restartGame");
        initGame(true);
        enableTouch = true;
        // bgmここで再生開始する?
        if (isPlayBGM) {
            SoundManager.getInstance().startBGM();
        }
    }

    public void showBlackFade(boolean flag) {
        View effectView = findViewById(R.id.effectview);
        if (flag) {
            effectView.setBackgroundColor(0xFF000000);
            effectView.setVisibility(View.VISIBLE);
        } else {
            effectView.setVisibility(View.INVISIBLE);
        }
    }

    public void fadeoutBlackFade() {
        View effectView = findViewById(R.id.effectview);
        showBlackFade(true);
        Animation a = new AlphaAnimation(1, 0);
        a.setDuration(500);
        a.setInterpolator(new LinearInterpolator());
        a.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showBlackFade(false);
            }
        });
        effectView.startAnimation(a);
    }

    /**
     * ゲーム画面を終了
     */
    public void finishGameActivity() {
        setResult(Common.RESULT_BACKTOTOP);
        releaseSounds();
        stopBGM = false;

        finish();
    }

    /**
     * 指定したビュー階層内のドローワブルをクリアする。 （ドローワブルをのコールバックメソッドによるアクティビティのリークを防ぐため）
     *
     * @param view
     */
    void cleanupView(View view) {
        if (view instanceof ImageButton) {
            ImageButton ib = (ImageButton) view;
            ib.setImageDrawable(null);
        } else if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            iv.setImageDrawable(null);
        }
        view.setBackgroundDrawable(null);
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int size = vg.getChildCount();
            for (int i = 0; i < size; i++) {
                cleanupView(vg.getChildAt(i));
            }
        }
    }

    /**
     * 9マスパズルドラッグ用View取得
     */
    public ImageView getDraggingImageView() {
        return (ImageView) findViewById(R.id.image_drag);
    }

    @Override
    public void onPlaySE(String se_name) {
        if (!isPlaySE)
            return;

        SoundManager m = SoundManager.getInstance();
        m.playSE(se_name);
    }

    @Override
    public void onPlayBGM(String bgm_name) {
        SoundManager m = SoundManager.getInstance();
        m.initBGM(bgm_name);
        if (!isPlayBGM)
            return;
        m.prepareBGM();
        m.startBGM();

    }
}
