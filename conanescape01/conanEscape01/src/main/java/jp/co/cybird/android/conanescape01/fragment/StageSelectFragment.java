package jp.co.cybird.android.conanescape01.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.BillingBaseActivity;
import jp.co.cybird.android.conanescape01.gui.LoadingActivity;
import jp.co.cybird.android.conanescape01.gui.MainActivity;
import jp.co.cybird.android.conanescape01.gui.PurchaseActivity;
import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.SwipeView;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.util.Debug;

/**
 * ステージ選択用
 *
 * @author S.Kamba
 */
public class StageSelectFragment extends ConanFragmentBase implements
        OnClickListener {

    View root_view = null;

    static final String KEY_INDEX = "index";

    /**
     * Stageスクロール
     */
    SwipeView swipe = null;

    int activeIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //
        root_view = inflater.inflate(R.layout.fragment_stageselect, container,
                false);
        swipe = (SwipeView) root_view.findViewById(R.id.scroll_stages);

        if (savedInstanceState != null) {
            activeIndex = savedInstanceState.getInt(KEY_INDEX);
        }

        // 最初は位置がずれているので非表示
        swipe.setVisibility(View.INVISIBLE);

        ImageButton b;
        b = (ImageButton) root_view.findViewById(R.id.btn_back);
        b.setOnClickListener(this);
        b.setClickable(true);
        b = (ImageButton) root_view.findViewById(R.id.btn_buy);
        b.setOnClickListener(this);
        b.setClickable(true);

        for (int i = 0; i < Common.STAGE_NUM; i++) {
            int resid = getResources().getIdentifier("stage_" + i, "id",
                    container.getContext().getPackageName());
            View l = swipe.findViewById(resid);
            ImageView iv = (ImageView) l.findViewById(R.id.img_stage);
            iv.setOnClickListener(this);
            iv.setClickable(true);
        }

        return root_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStageThumbs();

    }

    void setupStageThumbs() {
        // stageサムネイルに貼り替え
        ImageView img;
        View v;

        Activity a = getActivity();
        boolean stagePurchased = isPurchased();

        ArrayList<Stage> stageList = ((EscApplication) a.getApplication())
                .getStageList();
        for (int i = 0; i < Common.STAGE_NUM; i++) {
            Stage s = stageList.get(i);
            int id = getResources().getIdentifier("stage_" + i, "id",
                    getActivity().getPackageName());
            v = root_view.findViewById(id);
            img = (ImageView) v.findViewById(R.id.img_stage);
            switch (i) {
                case Common.STAGE_PROROGUE:
                    // プロローグ
                    img.setBackgroundResource(R.drawable.pro);
                    break;
                case Common.STAGE_EPILOGUE:
                    // エピローグ
                {
                    if (stagePurchased) {
                        img.setBackgroundResource(R.drawable.ep);
                    } else {
                        img.setBackgroundResource(R.drawable.ep_2);
                    }
                }
                break;
                case Common.STAGE_1: {
                    if (s.clear) {
                        // クリア済み
                        id = R.drawable.stage_1_2;
                    } else {
                        id = R.drawable.stage_1_1;
                    }
                    img.setBackgroundResource(id);
                }
                break;
                default: {
                    // Stage2〜4
                    if (s.clear) {
                        // クリア済み
                        id = getResources().getIdentifier("stage_" + i + "_2",
                                "drawable", a.getPackageName());
                    } else if (stagePurchased) {
                        // 購入済み
                        id = getResources().getIdentifier("stage_" + i + "_1",
                                "drawable", a.getPackageName());
                    } else {
                        // 未購入
                        id = getResources().getIdentifier("stage_" + i + "_3",
                                "drawable", a.getPackageName());
                    }
                    img.setBackgroundResource(id);
                }
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // GoogleAnalytics
        Tracking.sendView("StageSelect");
    }

    @Override
    public void onResume() {
        setClickable(true);
        swipe.setCurrentIndex(activeIndex);
        swipe.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //
        if (outState == null) {
            outState = new Bundle();
        }
        activeIndex = swipe.getCurrentIndex();
        outState.putInt(KEY_INDEX, activeIndex);
        super.onSaveInstanceState(outState);
    }

    /**
     * ステージ課金情報取得
     */
    boolean isPurchased() {
        if (Debug.isDebugStagePurchase) {
            return true;
        } else {
            BillingBaseActivity a = (BillingBaseActivity) getActivity();
            return a.isStagePurchased();
        }
    }

    @Override
    public void onClick(View v) {
        //
        switch (v.getId()) {
            case R.id.btn_back:
                playButtonSE();
                onBack();
                break;
            case R.id.btn_buy:
                playButtonSE();
                onBuy();
                break;
            default:
                onStageSelect();
                break;
        }

    }

    /**
     * Stage開始
     */
    void gameStart(Stage stage) {
        if (isPlayBGM) {
            // BGM停止
            SoundManager.getInstance().stopBGM();
        }

        //////TODO
        setClickable(false);
        
        Intent intent = new Intent(getActivity(), LoadingActivity.class);
        intent.putExtra(Common.KEY_NEWGAME, true);
        intent.putExtra(Common.KEY_STAGE_NO, stage.stageNo);
        startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_LOADING);
    }

    void setClickable(boolean clickable) {
        if (root_view == null)
            return;
        
        root_view.findViewById(R.id.btn_back).setClickable(clickable);
        root_view.findViewById(R.id.btn_buy).setClickable(clickable);

        for (int i = 0; i < Common.STAGE_NUM; i++) {
            int id = getResources().getIdentifier("stage_" + i, "id",
                    getActivity().getPackageName());
            View v = root_view.findViewById(id);
            ImageView img = (ImageView) v.findViewById(R.id.img_stage);
            img.setClickable(clickable);
        }
    }

    /**
     * TOPに戻るボタン
     */
    void onBack() {
        getActivity().onBackPressed();
    }

    /**
     * 購入/復元ボタン
     */
    void onBuy() {
        activeIndex = swipe.getCurrentIndex();
        MainActivity a = (MainActivity) getActivity();
        a.setStopBGM(false);
        a.setDoingOther(true);
        // 購入画面呼び出し
        Intent intent = new Intent(a, PurchaseActivity.class);
        startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_PURCHASE);
    }

    /**
     * stage選択ボタン
     */
    void onStageSelect() {
        activeIndex = swipe.getCurrentIndex();
        Debug.logD("onClick:id=" + activeIndex);

        if (activeIndex > Common.STAGE_1) {
            // 購入済み判断
            if (!isPurchased()) {
                if (isPlaySE) {
                    SoundManager.getInstance().playButtonSE();
                }
                onBuy();
                return;
            }
        }

        if (isPlaySE) {
            SoundManager.getInstance().playStageSelectSE();
        }
        ArrayList<Stage> stageList = ((EscApplication) getActivity()
                .getApplication()).getStageList();
        Stage stage = stageList.get(activeIndex);
        gameStart(stage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Common.ACTIVITY_REQUESTCODE_LOADING:
                if (resultCode == Common.RESULT_LOAD_ERROR) {
                    SoundManager.getInstance().release();
                    MainActivity a = (MainActivity) getActivity();
                    a.setStopBGM(true);
                    a.initSounds();
                    SoundManager.getInstance().startBGM();
                    a.showBlackFade(false);
                } else {
                    MainActivity a = (MainActivity) getActivity();
                    a.setStopBGM(true);
                    a.onBackPressed();
                    a.showBlackFade(true);
                    a.postBlackFadeShow();
                }
                break;
            case Common.ACTIVITY_REQUESTCODE_PURCHASE: {
                // 購入画面から戻り
                swipe.setCurrentIndex(activeIndex);
                if (resultCode == Common.RESULT_STAGE_PURCHASED) {
                    // stage購入済みなので貼り替え
                    setupStageThumbs();
                } else if (resultCode == Common.RESULT_AGREEMENT_DENY) {
                    MainActivity a = (MainActivity) getActivity();
                    a.setStopBGM(true);
                    a.setDoingOther(false);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
