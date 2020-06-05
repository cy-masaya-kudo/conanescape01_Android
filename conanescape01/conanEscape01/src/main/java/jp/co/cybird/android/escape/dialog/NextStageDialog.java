package jp.co.cybird.android.escape.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.GameActivity;
import jp.co.cybird.android.conanescape01.gui.LoadingActivity;
import jp.co.cybird.android.conanescape01.gui.PurchaseActivity;
import jp.co.cybird.android.conanescape01.model.PurchaseItem;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.util.Debug;

public class NextStageDialog extends TranslucentFullscreenDialog implements
        OnClickListener {

    boolean isPlaySE = true;

    @Override
    public int getLayoutId() {
        return R.layout.popup_nextstage;
    }

    @Override
    public void initView() {
        ImageButton btn = (ImageButton) content.findViewById(R.id.btn_top);
        btn.setOnClickListener(this);

        Button b = (Button) content.findViewById(R.id.btn_nextstage);
        b.setOnClickListener(this);

        SharedPreferences pref = getActivity().getSharedPreferences(Common.TAG,
                Context.MODE_PRIVATE);
        isPlaySE = pref.getBoolean(Common.PREF_KEY_SE, true);

        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (isPlaySE)
            SoundManager.getInstance().playButtonSE();

        switch (v.getId()) {
            case R.id.btn_top:
                onBackToTop();
                break;
            default:
                onNextStage();
                break;
        }

    }

    /**
     * 次のステージへ進む
     */
    void onNextStage() {
        Activity a = getActivity();
        EscApplication app = (EscApplication) getActivity().getApplication();
        GameManager gm = app.getGameManager();

        boolean nextEnabled = false;
        if (gm.getStageNo() == Common.STAGE_PROROGUE) {
            nextEnabled = true;
        } else {
            nextEnabled = isPurchased();
        }
        // 次のステージへ進む
        if (nextEnabled) {
            // ローディング画面表示
            Intent intent = new Intent(a.getApplicationContext(),
                    LoadingActivity.class);
            int nextStage = ((EscApplication) a.getApplication()).getStage(gm
                    .getStageNo()).stageNo + 1;
            intent.putExtra(Common.KEY_STAGE_NO, nextStage);
            intent.putExtra(Common.KEY_NEXTGAME, true);
            a.startActivityForResult(intent,
                    Common.ACTIVITY_REQUESTCODE_LOADING);

        } else {
            // 購入画面表示
            Intent intent = new Intent(a.getApplicationContext(),
                    PurchaseActivity.class);
            intent.putExtra(Common.KEY_FROM_GAGME, true);
            a.startActivityForResult(intent,
                    Common.ACTIVITY_REQUESTCODE_PURCHASE);
        }
        // dismissAllowingStateLoss();
    }

    /**
     * TOPへ戻る
     */
    void onBackToTop() {
        GameActivity a = (GameActivity) getActivity();
        a.setResult(Common.RESULT_BACKTOTOP);
        a.setStopBGM(false);
        a.finishGameActivity();
        dismissAllowingStateLoss();
    }

    /**
     * ステージ購入済みかチェックする<br>
     * 起動時などにAPIで取得？
     *
     * @return 購入済みならtrue
     */
    private boolean isPurchased() {
        if (Debug.isDebugStagePurchase) {
            return true;
        } else {
            return PurchaseItem.isStagePurchased(getActivity());
        }
    }

}
