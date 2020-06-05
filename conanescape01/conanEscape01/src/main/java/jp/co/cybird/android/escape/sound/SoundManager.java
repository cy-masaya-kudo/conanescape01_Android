package jp.co.cybird.android.escape.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.util.Debug;

/**
 * 音関係[シングルトン]
 *
 * @author S.Kamba
 *
 */
public class SoundManager {

	final static int SE_NO_ITEM_GET = 0;
	final static int SE_NO_SAVE = 1;
	final static int SE_NO_ITEM_SELECT = 2;
	final static int SE_NO_ITEM_ZOOM = 3;
	final static int SE_NO_ZOOM_END = 4;
	final static int SE_NO_ITEM_COMBINE = 5;
	final static int SE_NO_SERIF = 6;
	final static int SE_NO_BUTTON = 7;
	final static int SE_NO_CLOSE = 8;
	final static int SE_NO_STAGE_SELECTED = 9;
	final static int DEFAULT_STREAMS = SE_NO_STAGE_SELECTED + 1;

	// BGM再生関連
	String bgmFilepath = null;
	int mBgmResId = 0;
	MediaPlayer mBGMPlayer = null;
	float mBgmVolume = 0.8f; // seよりやや小さくしておく
	boolean isPausing = false;

	// SE再生関連
	// String[] seFileapaths = null;
	HashMap<String, Integer> seFiles = null;
	SoundPool mSePlayer = null;
	int[] mSeSound = null;
	float mSeVolume = 1.f;

	// シングルトンインスタンス
	static SoundManager mInstance = null;

	private SoundManager() {
	}

	/** シングルトンインスタンス取得 */
	public static SoundManager getInstance() {
		if (mInstance == null)
			mInstance = new SoundManager();
		return mInstance;
	}

	/** インスタンス解放 */
	public static void clearInstance() {
		mInstance = null;
	}

	/** SE初期化 */
	public void initSE(Context context, String se_filepaths[]) {
		if (mSePlayer != null) {
			mSePlayer.release();
			mSePlayer = null;
		}
		int senum = DEFAULT_STREAMS;
		if (se_filepaths != null) {
			senum += se_filepaths.length;
		}
		// seFileapaths = se_filepaths;

		mSeSound = new int[DEFAULT_STREAMS];

		mSePlayer = new SoundPool(senum, AudioManager.STREAM_MUSIC, 0);
		mSeSound[SE_NO_ITEM_GET] = mSePlayer.load(context, R.raw.se2_item_get,
				1);
		mSeSound[SE_NO_SAVE] = mSePlayer.load(context, R.raw.save, 1);
		mSeSound[SE_NO_ITEM_SELECT] = mSePlayer.load(context,
				R.raw.se3_item_select, 1);
		mSeSound[SE_NO_ITEM_ZOOM] = mSePlayer.load(context,
				R.raw.se4_item_zoom, 1);
		mSeSound[SE_NO_ZOOM_END] = mSePlayer.load(context, R.raw.se5_zoom_end,
				1);
		mSeSound[SE_NO_ITEM_COMBINE] = mSePlayer.load(context,
				R.raw.se6_item_combine, 1);
		mSeSound[SE_NO_SERIF] = mSePlayer.load(context,
				R.raw.se8_serif_process, 1);
		mSeSound[SE_NO_BUTTON] = mSePlayer.load(context, R.raw.se9_button, 1);
		mSeSound[SE_NO_CLOSE] = mSePlayer.load(context, R.raw.se10_close, 1);
		mSeSound[SE_NO_STAGE_SELECTED] = mSePlayer.load(context,
				R.raw.openingdoor, 1);

		if (se_filepaths != null && se_filepaths.length > 0) {
			seFiles = new HashMap<String, Integer>(se_filepaths.length);
			for (String sename : se_filepaths) {
				int seId = mSePlayer.load(sename, 1);
				seFiles.put(sename, seId);
			}
		}
	}

	/** リソースを解放 */
	public void release() {
		if (mSePlayer != null) {
			for (int se : mSeSound) {
				mSePlayer.unload(se);
			}
			mSePlayer.release();
			mSePlayer = null;
		}
		mBgmResId = 0;
		bgmFilepath = null;
		seFiles = null;
		if (mBGMPlayer != null) {
			mBGMPlayer.reset();
			mBGMPlayer.release();
			mBGMPlayer = null;
		}
	}

	/** SE音量セット */
	public void setSEVolume(float v) {
		mSeVolume = v;
	}

	/** BGM関連初期化 */
	public void initBGM(String bgmFilepath) {
		this.bgmFilepath = bgmFilepath;
		if (mBGMPlayer != null) {
			if (mBGMPlayer.isPlaying()) {
				mBGMPlayer.stop();
			}
			mBGMPlayer.release();
		}
		mBGMPlayer = new MediaPlayer();
	}

	/** BGM関連初期化 */
	public void initBGM(Context context, int resId) {
		this.mBgmResId = resId;
		if (mBGMPlayer != null) {
			if (mBGMPlayer.isPlaying()) {
				mBGMPlayer.stop();
			}
			mBGMPlayer.release();
		}
		mBGMPlayer = MediaPlayer.create(context, resId);
		mBGMPlayer.setLooping(true);
	}

	/** BGM再生開始 */
	public void prepareBGM() {
		Debug.logD("=== prepareBGM ===");

		if (mBGMPlayer == null)
			return;
		if (mBgmResId > 0) {
			return;
		}
		if (bgmFilepath == null || bgmFilepath.length() == 0)
			return;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(bgmFilepath);
			mBGMPlayer.setDataSource(fis.getFD());
			mBGMPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mBGMPlayer.setLooping(true);
			mBGMPlayer.setVolume(mBgmVolume, mBgmVolume);
			mBGMPlayer.prepare();
			isPausing = false;
		} catch (IllegalArgumentException e) {
			if (Debug.isDebug) {
				e.printStackTrace();
			}
		} catch (IllegalStateException e) {
			if (Debug.isDebug) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if (Debug.isDebug) {
				e.printStackTrace();
			}
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/** リソースファイルから再生 */
	void prepareBGMFromRes() {

	}

	/** 一時停止 */
	public void pauseBGM() {
		Debug.logD("=== pauseBGM ===");

		if (mBGMPlayer != null) {
			try {
				if (mBGMPlayer.isPlaying())
					mBGMPlayer.pause();
				isPausing = true;
			} catch (IllegalStateException e) {
				if (Debug.isDebug) {
					e.printStackTrace();
				}
			}
		}
	}

	/** BGM再開 */
	public void startBGM() {
		Debug.logD("=== startBGM ===");
		if (mBGMPlayer != null) {
			try {
				if (!mBGMPlayer.isPlaying())
					mBGMPlayer.start();
				isPausing = false;
			} catch (IllegalStateException e) {
				if (Debug.isDebug) {
					e.printStackTrace();
				}
			}
		}
	}

	/** BGM停止 */
	public void stopBGM() {
		Debug.logD("=== stopBGM ===");
		if (mBGMPlayer != null) {
			try {
				if (mBGMPlayer.isPlaying() || isPausing)
					mBGMPlayer.stop();
				// mBGMPlayer.prepare();
			} catch (IllegalStateException e) {
				if (Debug.isDebug) {
					e.printStackTrace();
				}
			}
		}
	}

	/** BGM音量 */
	public void setBGMVolume(float v) {
		mBgmVolume = v;
	}

	/** 効果音再生：ファイル名を指定 */
	public void playSE(String se_name) {
		if (mSePlayer == null || seFiles == null)
			return;
		Integer seId = seFiles.get(se_name);
		if (seId == null)
			return;
		mSePlayer.play(seId, mSeVolume, mSeVolume, 0, 0, 1.0f);
	}

	// play se local files

	/** アイテム獲得音再生 */
	public void playItemSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_ITEM_GET], mSeVolume, mSeVolume, 0, 0,
				1.0f);
	}

	/** アイテム選択音再生 */
	public void playItemSelectSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_ITEM_SELECT], mSeVolume, mSeVolume, 0, 0,
				1.0f);
	}

	/** アイテム拡大音再生 */
	public void playItemZoomSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_ITEM_ZOOM], mSeVolume, mSeVolume, 0, 0,
				1.0f);
	}

	/** 拡大終了音再生 */
	public void playZoomEndSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_ZOOM_END], mSeVolume, mSeVolume, 0, 0,
				1.0f);
	}

	/** アイテム合成音再生 */
	public void playItemCombineSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_ITEM_COMBINE], mSeVolume, mSeVolume, 0,
				0, 1.0f);
	}

	/** セリフ進行音再生 */
	public void playSerifSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_SERIF], mSeVolume, mSeVolume, 0, 0, 1.0f);
	}

	/** AppUI:ボタンタップ音再生 */
	public void playButtonSE() {
		if (mSePlayer == null)
			return;
		mSePlayer
				.play(mSeSound[SE_NO_BUTTON], mSeVolume, mSeVolume, 0, 0, 1.0f);
	}

	/** AppUI:クローズ音再生 */
	public void playCloseSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_CLOSE], mSeVolume, mSeVolume, 0, 0, 1.0f);
	}

	/** AppUI:ステージ選択音再生 */
	public void playStageSelectSE() {
		if (mSePlayer == null)
			return;
		mSePlayer.play(mSeSound[SE_NO_STAGE_SELECTED], mSeVolume, mSeVolume, 0,
				0, 1.0f);
	}

	/** BGM初期化済みか取得 */
	public boolean isInitializedBGM() {
		if (mBGMPlayer != null)
			return true;
		return false;
	}

	/** SE初期化済みか取得 */
	public boolean isInitializedSE() {
		if (mSePlayer != null)
			return true;
		return false;
	}

}
