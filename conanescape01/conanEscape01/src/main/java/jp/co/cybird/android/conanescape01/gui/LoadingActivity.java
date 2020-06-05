package jp.co.cybird.android.conanescape01.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.GameManager;
import jp.co.cybird.android.conanescape01.GameManager.GameManagerInitEventListener;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.util.Debug;

/**
 * Fadeout/Fadein するローディング画面用
 * 
 * @author S.Kamba
 *
 */
public class LoadingActivity extends Activity implements
		GameManagerInitEventListener {

	/** stage開始音の長さ */
	static final int SE_WAIT_TIME = 3 * 1000;

	/** ローディングフラグ */
	boolean isLoading = false;

	boolean enableStartGame = true;

	GameManager gm;

	View view = null;
	View progressLayouts = null;
	ImageView imgGif;
	AnimationDrawable animGif = null;

	DotAnimation animDot;

	int stageNo;
	boolean isNewGame = true;
	boolean isNextGame = false;

	boolean needFinish = true;

	long startedTime = 0;

	AnimationListener onFadeInListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			showProgress(true);
			// データローディング開始
			EscApplication app = (EscApplication) getApplication();
			Stage stage = app.getStage(stageNo);
			gm = initGameManagerEngine(stage);
			if (isNewGame) {
				app.setNewGameFlag(true);
				gm.startInit();
			} else {
				Intent intent = getIntent();
				app.setNewGameFlag(false);
				String savedNode = intent
						.getStringExtra(Common.KEY_SAVED_NODEFILE);
				String savedItem = intent
						.getStringExtra(Common.KEY_SAVED_ITEMFILE);
				gm.startLoadSaveData(savedNode, savedItem);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_loading);
		view = findViewById(R.id.root);
		view.setBackgroundColor(0xFF000000);
		imgGif = (ImageView) findViewById(R.id.img_loading);
		// animationを取得
		animGif = (AnimationDrawable) imgGif.getBackground();

		// dot部分
		ImageView imgDot[] = new ImageView[3];
		imgDot[0] = (ImageView) findViewById(R.id.img_dot1);
		imgDot[1] = (ImageView) findViewById(R.id.img_dot2);
		imgDot[2] = (ImageView) findViewById(R.id.img_dot3);
		animDot = new DotAnimation(imgDot);

		// progress部分全体
		progressLayouts = findViewById(R.id.lay_progress);
		showProgress(false);

		Intent intent = getIntent();
		isNewGame = intent.getBooleanExtra(Common.KEY_NEWGAME, true);
		stageNo = intent.getIntExtra(Common.KEY_STAGE_NO, 0);
		isNextGame = intent.getBooleanExtra(Common.KEY_NEXTGAME, false);

		startedTime = System.currentTimeMillis();
		// アニメーションを開始
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(500);
		anim.setAnimationListener(onFadeInListener);
		view.startAnimation(anim);

	}

	@Override
	protected void onStop() {
		if (isLoading) {
			if (gm != null)
				gm.cancelLoading();
			showProgress(false);
			isLoading = false;
			enableStartGame = false;
			setResult(Common.RESULT_LOAD_ERROR);
		}
		if (needFinish)
			finish();
		super.onStop();
	}

	@Override
	protected void onRestart() {
		//
		showProgress(false);
		super.onRestart();
	}

	@Override
	public void onBackPressed() {
		//
		setResult(Common.RESULT_LOAD_ERROR);
		super.onBackPressed();
	}

	/**
	 * ゲームエンジン共通初期化
	 *
	 * @return
	 */
	protected GameManager initGameManagerEngine(Stage stage) {

		float density = getResources().getDisplayMetrics().density;
		int w = (int) getResources().getDimension(R.dimen.game_main_width);
		int h = (int) getResources().getDimension(R.dimen.game_main_height);
		// int w = (int) (wdp * density + 0.5f);
		// int h = (int) (hdp * density + 0.5f);
		float ff = getResources().getDimension(R.dimen.item_zoom_width)
				/ (300.f * 2);
		float font_cond = getResources().getDimension(R.dimen.font_cond) / 10.f
				/ density;

		GameManager gm = new GameManager(getApplicationContext());
		gm.setStage(stage);
		gm.setInitEventListener(this);
		gm.setItemAreaNum(8);
		gm.setRatioToOriginalSize(w / 640.f, h / 860.f);
		gm.setWindowSize(w, h);
		gm.setCollisionBoxNum(6, 8);
		gm.setItemZoomScreenRatio(ff, ff);
		gm.setItemZoomScreenPositionOffset(0, 0);
		gm.setFontSizeRatio(density * font_cond);
		GameManager.drawAllItems = false;
		return gm;
	}

	void startGame() {
		if (!enableStartGame)
			return;

		EscApplication app = (EscApplication) getApplication();
		app.setGameManager(gm);

		// 初回起動かチェック
		boolean isFirstRun = app.checkIsFirstRun();
		if (isFirstRun && stageNo == 1) {
			startManualActivity();
		} else {
			startGameActivity();
		}

		MainActivity a = MainActivity.getInstance();
		if (a != null) {
			a.showBlackFade(true);
			a.setStopBGM(false);
		}
		GameActivity g = GameActivity.getInstance();
		if (g != null) {
			g.showBlackFade(true);
		}
	}

	void showError() {
		ErrorDialog d = ErrorDialog.newInstance("ファイル読み込みに失敗しました");
		d.show(getFragmentManager(), "err");
	}

	void waitStart(long duration) {
		Debug.logD("LoadingActivity: waitStart for millseconds:" + duration);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				startGame();
				showProgress(false);
			}
		}, duration);
	}

	void waitError(long duration) {
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				showError();
				showProgress(false);
			}
		}, duration);
	}

	@Override
	public void onInitEnd(boolean result) {
		if (!enableStartGame) {
			return;
		}
		long now = System.currentTimeMillis();
		long duration = now - startedTime;
		duration = SE_WAIT_TIME - duration;
		if (result) {
			if (isNewGame) {
				if (duration <= 0) {
					startGame();
				} else {
					waitStart(duration);
					return;
				}
			} else {
				startGame();
			}

		} else {
			if (duration <= 0) {
				showError();
			} else {
				waitError(duration);
				return;
			}
		}
		showProgress(false);
	}

	/** 遊び方説明画面スタート */
	void startManualActivity() {
		needFinish = false;
		GameActivity a = GameActivity.getInstance();
		if (a != null) {
			a.restartInit();
		}
		Intent intent = new Intent(this, ManualActivity.class);
		startActivityForResult(intent,
				Common.ACTIVITY_REQUESTCODE_FIRST_HOWTOPLAY);
	}

	/** Gameスタート */
	void startGameActivity() {
		if (isNextGame) {
			GameActivity a = GameActivity.getInstance();
			a.restartInit();
			setResult(RESULT_OK);
			finish();
		} else {
			Intent intent = new Intent(this, GameActivity.class);
			startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_GAME);
		}
	}

	/**
	 * プログレス表示切り替え
	 *
	 * @param flag
	 */
	public void showProgress(boolean flag) {
		if (progressLayouts == null)
			return;

		if (flag) {
			progressLayouts.setVisibility(View.VISIBLE);
			isLoading = flag;
			animGif.start();
			animDot.start();
		} else {
			progressLayouts.setVisibility(View.INVISIBLE);
			animGif.stop();
			animDot.stop();
			isLoading = false;
		}
	}

	@Override
	public void onGameStart() {
		EscApplication app = (EscApplication) getApplication();
		app.setNewGameFlag(false);
	}

	// // DialogFragments

	/** 初期化エラー表示 */
	public static class ErrorDialog extends DialogFragment implements
			DialogInterface.OnClickListener {

		public static final String KEY_MESSAGE = "message";

		public static ErrorDialog newInstance(String message) {
			ErrorDialog fragment = new ErrorDialog();
			Bundle args = new Bundle();
			args.putString(ErrorDialog.KEY_MESSAGE, message);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String message = getString(R.string.err_network);
			AlertDialog.Builder b = new AlertDialog.Builder(
					getActivity());
			b.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.err_title)
					.setNegativeButton(android.R.string.ok, this);
			Bundle args = getArguments();
			if (args != null ) {
				if (args.containsKey(KEY_MESSAGE)) {
					message = args.getString(KEY_MESSAGE);
			}
			}
			b.setMessage(message);
			return b.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			LoadingActivity activity = (LoadingActivity) getActivity();
			if(activity != null) {
				activity.setResult(Common.RESULT_LOAD_ERROR);
				activity.finish();
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			LoadingActivity activity = (LoadingActivity) getActivity();
			if(activity != null) {
				activity.setResult(Common.RESULT_LOAD_ERROR);
				activity.finish();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Common.ACTIVITY_REQUESTCODE_FIRST_HOWTOPLAY:
			needFinish = true;
			startGameActivity();
			break;
		case Common.ACTIVITY_REQUESTCODE_GAME:
			setResult(resultCode);
			finish();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class DotAnimation {
		ImageView imgDot[];
		int dotIndex = 0;
		static final int DOT_NUM = 3;

		boolean isContinue = true;

		public DotAnimation(ImageView imgDot[]) {
			this.imgDot = imgDot;
		}

		public void reset() {
			dotIndex = 0;
			for (int i = 0; i < DOT_NUM; i++) {
				imgDot[i].setVisibility(View.INVISIBLE);
			}
			isContinue = true;
		}

		public void start() {
			reset();
			runAnim();
		}

		public void stop() {
			isContinue = false;
		}

		void runAnim() {
			view.postDelayed(dotRunnable, 500);
		}

		Runnable dotRunnable = new Runnable() {

			@Override
			public void run() {
				if (++dotIndex > DOT_NUM)
					dotIndex = 0;

				int i = 0;
				for (; i < dotIndex; i++) {
					imgDot[i].setVisibility(View.VISIBLE);
				}
				for (; i < DOT_NUM; i++) {
					imgDot[i].setVisibility(View.INVISIBLE);
				}

				if (isContinue) {
					runAnim();
				}
			}
		};
	}
}
