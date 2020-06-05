package jp.co.cybird.android.conanescape01;

import android.content.Context;
import android.os.AsyncTask;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.HashMap;

import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.AssetUtil;
import jp.co.cybird.android.util.Debug;
import jp.co.cybird.android.util.Uid;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;
import jp.co.cybird.escape.engine.lib.data.CsvDataManager;
import jp.co.cybird.escape.engine.lib.data.OnDecodeZipListener;
import jp.co.cybird.escape.engine.lib.manager.GameManagerBase;
import jp.co.cybird.escape.engine.lib.manager.OnTimerDoneOnUIThread;

/**
 * GameManagerの実態クラス<br>
 * GameManagerBaseの実装
 *
 * @author S.Kamba
 */
public class GameManager extends GameManagerBase implements OnDecodeZipListener {

	Stage mStage = null;
	Context mContext = null;
	AsyncTask<String, Void, Boolean> mLoadingTask = null;

	public interface GameManagerInitEventListener {
		public void onInitEnd(boolean result);

		public void onGameStart();

	}

	public interface GameMangerFinishEventListener {
		public void onGameFinish();
	}

	public interface GameManagerSoundEventListsner {
		public void onPlaySE(String se_name);

		public void onPlayBGM(String bgm_name);
	}

	GameManagerInitEventListener mInitListener = null;
	GameMangerFinishEventListener mFinishListener = null;
	GameManagerSoundEventListsner mSoundListener = null;

	public GameManager(Context context) {
		super();
		this.mContext = context;

	}

	/** ステージ情報をセット */
	public void setStage(Stage stage) {
		setStageNo(stage.stageNo);
		mStage = stage;
	}

	/** イベントリスナをセット */
	public void setInitEventListener(GameManagerInitEventListener listener) {
		mInitListener = listener;
	}

	/** イベントリスナをセット */
	public void setFinishEventListener(GameMangerFinishEventListener listener) {
		mFinishListener = listener;
	}

	/** イベントリスナをセット */
	public void setSoundEventListener(GameManagerSoundEventListsner listener) {
		mSoundListener = listener;
	}

	@Override
	public void initDataManagerDirs() {
		String dir = mContext.getCacheDir().getAbsolutePath();
		CsvDataManager.setCashDir(dir);
		dir = mContext.getDir("", Context.MODE_PRIVATE).getParent();
		CsvDataManager.setDataDir(dir);

		// ヘッダーを登録
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("X-Cy-Identify", Codec.encode(Uid.getCyUserId(mContext)));
		CsvDataManager.setRequestHeaders(map);
		// Zip解凍コールバックを登録
		CsvDataManager.setOnDecodeZipListener(this);
	}

	/**
	 * New Game 初期化
	 */
	public void startInit() {
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {

				boolean download = mStage.need_update;
				if (params[0].startsWith(Common.ASSETS_PREFIX)) {
					download = false;
					// assetから読込の場合
					String zip_name = params[0].replace(Common.ASSETS_PREFIX, "");
					String zip_path = mContext.getCacheDir().getAbsolutePath() + File.separator + zip_name;
					// 存在チェック
					File f = new File(zip_path);
					if (f.exists() && (mStage.need_update)) {
						// キャッシュコピーを削除
						f.delete();
					}
					if (mStage.need_update) {
						// assetsからコピー
						boolean r = AssetUtil.copyFromAssets(mContext,
								zip_path, zip_name);
						if (!r) {
							Debug.logD("アセットファイルをコピーできませんでした。");
							return false;
						}
					}
					// Zip解凍コールバックを登録
					if (Debug.isDebug && !Debug.isAssetEncrypt) {
						CsvDataManager.setOnDecodeZipListener(null);
					}
				}
				boolean result = initialize(params[0], download, true);
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				initEnd(result);
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mStage.data_url);
		mLoadingTask = task;
	}

	/**
	 * 初期化終了時コールバック
	 *
	 * @param result
	 */
	protected void initEnd(Boolean result) {
		if (mLoadingTask != null && mLoadingTask.isCancelled())
			return;
		if (mInitListener != null) {
			mInitListener.onInitEnd(result);
		}
	}

	class TimerTask extends AsyncTask<Integer, Void, Void> {

		OnTimerDoneOnUIThread callback = null;

		public TimerTask(OnTimerDoneOnUIThread callback) {
			this.callback = callback;
		}

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				Thread.sleep(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (callback != null) {
				callback.onTimerDone(GameManager.this);
			}
		}
	}

	@Override
	public void startTimerDoneOnUIThread(int delay,
			OnTimerDoneOnUIThread callback) {
		TimerTask task = new TimerTask(callback);
		task.execute(delay);
	}

	/**
	 *
	 * @param nodesData
	 * @param itemsData
	 */
	public void startLoadSaveData(String nodesData, String itemsData) {
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// まずデータを読み込み
				boolean result = initialize(params[0], false, false);
				if (!result)
					return result;

				// 復元
				result = restore(params[1], params[2]);

				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				initEnd(result);
				mLoadingTask = null;
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mStage.data_url,
				nodesData, itemsData);
		mLoadingTask = task;
	}

	@Override
	public void gameStart() {
		super.gameStart();
		if (mInitListener != null) {
			mInitListener.onGameStart();
		}
	}

	@Override
	public void gameFinish() {
		if (mFinishListener != null) {
			mFinishListener.onGameFinish();
		}
		// mContext = null;
	}

	/**
	 * 読込タスクが実行中かチェック
	 */
	public boolean isLoading() {
		if (mLoadingTask == null)
			return false;
		if (mLoadingTask.getStatus() == AsyncTask.Status.FINISHED
				|| mLoadingTask.isCancelled())
			return false;
		return true;
	}

	/**
	 * 読込タスクをキャンセル
	 */
	public void cancelLoading() {
		if (mLoadingTask == null)
			return;
		if (mLoadingTask.getStatus() == AsyncTask.Status.RUNNING) {
			mLoadingTask.cancel(true);
		}
	}

	/** 音関係の初期化 */
	public void initSounds() {
		SoundManager m = SoundManager.getInstance();
		// SE初期化
		m.initSE(mContext, seFilepaths);
		// BGM関係初期化
		m.initBGM(bgmFilepath);
	}

	@Override
	public void playSE(String se_name) {
		if (mSoundListener != null) {
			mSoundListener.onPlaySE(se_name);
		}
	}

	@Override
	public void playBGM(String bgm_name) {
		if (mSoundListener != null) {
			mSoundListener.onPlayBGM(bgm_name);
		}
	}

	@Override
	public boolean onDecodeZip(String zipfile, String dstDir) {
		// パスワード付きzipの解凍
		String key = "CRJYCzBUCDdXNjYOCxk4CAAQU";
		String decoded = Codec.decode(key);
		try {
			ZipFile zipFile = new ZipFile(zipfile);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(decoded);
			}
			zipFile.extractAll(dstDir);
			Debug.logD("onDecodeZip:Success");
			return true;
		} catch (ZipException e) {
			Debug.logD("onDecodeZip:Exception");
			e.printStackTrace();
		}
		Debug.logD("onDecodeZip:Error");
		return false;

	}

	/** ステージ情報の取得 */
	public Stage getStage() {
		return mStage;
	}

}
