package jp.co.cybird.android.conanescape01.gui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import jp.co.cybird.android.agreement.AgreementDialog;
import jp.co.cybird.android.compliance.AgreementUtil;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.fragment.TopFragment;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.TransparentWebView;
import jp.co.cybird.android.escape.util.TransparentWebView.OnWebViewClickListener;

import com.gency.gcm.GencyGCMUtilities;

import jp.co.cybird.android.util.Debug;

/**
 * トップ画面
 *
 * @author S.Kamba
 */
public class MainActivity extends BillingBaseActivity implements
        OnWebViewClickListener {

    static MainActivity obj = null;

    public MainActivity() {
        super();
        //
        obj = this;
    }

    public static MainActivity getInstance() {
        return obj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new TopFragment()).commit();
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int bannerHeight = (int) (280.f * dm.widthPixels / 640.f + 0.5f);
        Debug.logD("bannerHeight = " + bannerHeight);

        TransparentWebView web = (TransparentWebView) findViewById(R.id.web_topbanner);
        android.view.ViewGroup.LayoutParams lp = web.getLayoutParams();
        lp.height = bannerHeight;

        web.init();
        web.setOnViewClickListener(this);
        String bannerUrl = getBannerUrl();
        Debug.logD("bannerUrl = " + bannerUrl);
        web.loadUrl(bannerUrl);

        // PUSH関連
        startGCM();

        showBlackFade(false);

        // 同意確認
        AgreementDialog dialog = AgreementUtil.newAgreementDialog(this);
        dialog.show();
    }

    private void startGCM () {
        // コールバック生成時にUserIdとDeviceIdを登録
        // GCMレシーバを起動
        try {
            GencyGCMUtilities.runGCM(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GencyGCMUtilities.setWillPlaySound(true);
        GencyGCMUtilities.setWillVibrate(true);
    }

    private String getBannerUrl() {
        int resId = R.string.url_banner1;
        PackageManager pm = getPackageManager();
        try {
            // 推理ゲームを持っている
            pm.getApplicationInfo(Common.PACKAGE_MYSTERY, 0);
            resId = R.string.url_banner2;

            // 脱出1を持っている
            try {
                pm.getApplicationInfo(Common.PACKAGE_ESCAPE1, 0);
                resId = R.string.url_banner3;
            } catch (NameNotFoundException e) {
                ;
            }

        } catch (NameNotFoundException e) {
        }

        return getString(resId);
    }

    @Override
    protected void onStart() {
        //
        if (stopBGM) {
            initSounds();
        }
        // GoogleAnalyticsはFragmentで送る

        super.onStart();
    }

    @Override
    protected void onStop() {
        Debug.logD("MainActivity:onStop");
        // 音関連解放
        if (isPlayBGM && stopBGM) {
            SoundManager.getInstance().stopBGM();
            SoundManager.getInstance().release();
        }
        super.onStop();
    }

    /**
     * 音関係初期化
     */
    public void initSounds() {
        SoundManager m = SoundManager.getInstance();
        // すでに初期化済みならしない
        if (!m.isInitializedBGM()) {
            // BGM
            m.initBGM(this, R.raw.main_bgm);
        }
        if (!m.isInitializedSE()) {
            // SE初期化
            m.initSE(this, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        startGCM();
        if (requestCode == Common.ACTIVITY_REQUESTCODE_OPTIONS) {
            stopBGM = false;
            boolean old_play_bgm = isPlayBGM;
            getSettings();
            if (old_play_bgm != isPlayBGM) {
                if (isPlayBGM) {
                    stopBGM = true;
                } else {
                    SoundManager.getInstance().pauseBGM();
                }
            }
//        } else if (requestCode == Common.ACTIVITY_REQUESTCODE_PURCHASE) {
//            if (resultCode == Common.RESULT_AGREEMENT_DENY) {
//                // 音を再度鳴らす？
//                setDoingOther(false);
//                stopBGM = true;
//            }
        }
    }

    public void showBlackFade(boolean flag) {
        View v = findViewById(R.id.black_fade);
        if (flag) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public void postBlackFadeShow() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                showBlackFade(false);
            }
        }, 500);
    }

    @Override
    public void onClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
