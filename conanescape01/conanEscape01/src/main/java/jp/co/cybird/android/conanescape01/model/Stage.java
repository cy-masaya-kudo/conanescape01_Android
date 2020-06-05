package jp.co.cybird.android.conanescape01.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.webapi.WebapiStageData;
import jp.co.cybird.android.util.Debug;

/**
 * APIから取得するステージデータ格納用
 *
 * @author S.Kamba
 *
 */
public class Stage {

	public boolean need_update = true;

	public int id = 0;
	public int stageNo = 0;
	public String stageName = null;
	public String last_update = null;
	public String data_url = null;
	public boolean clear = false;

	/** プロローグ用データ */
	static Stage createProrogue() {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_PROROGUE;
		s.stageName = "プロローグ";
		s.data_url = Common.ASSETS_PREFIX + "data_0.zip";
		return s;
	}

	/** Stage1用データ */
	static Stage createStage1(Context c) {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_1;
		s.stageName = "Stage1";
		if (Debug.isDebug) {
			// s.data_url = getUrl(c, "0001");
			s.data_url = Common.ASSETS_PREFIX + "data_1.zip";
		} else {
			s.data_url = Common.ASSETS_PREFIX + "data_1.zip";
		}
		return s;
	}

	/** Stage2用データ */
	static Stage createStage2(Context c) {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_2;
		s.stageName = "Stage2";
		s.data_url = getUrl(c, "0002");
		// s.data_url = Common.ASSETS_PREFIX + "data_2.zip";
		return s;
	}

	/** Stage3用データ */
	static Stage createStage3(Context c) {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_3;
		s.stageName = "Stage3";
		s.data_url = getUrl(c, "0003");
		// s.data_url = Common.ASSETS_PREFIX + "data_3.zip";
		return s;
	}

	/** Stage4用データ */
	static Stage createStage4(Context c) {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_4;
		s.stageName = "Stage4";
		s.data_url = getUrl(c, "0004");
		// s.data_url = Common.ASSETS_PREFIX + "data_4.zip";
		return s;
	}

	/** エピローグ用データ */
	static Stage createEpilogue(Context c) {
		Stage s = new Stage();
		s.stageNo = Common.STAGE_EPILOGUE;
		s.stageName = "エピローグ";
		s.data_url = getUrl(c, "0005");
		// s.data_url = Common.ASSETS_PREFIX + "data_5.zip";
		return s;
	}

	static String getUrl(Context c, String stage_no) {
		String api_domain = c.getString(R.string.api_domain);
		String api_name = c.getString(R.string.stage_data);

		WebapiStageData api = new WebapiStageData(c, stage_no);
		String encrypted_params = api.getEncryptedParams();

		Uri.Builder builder = new Uri.Builder();
		builder.scheme(Common.WEBAPI_SCHEME);
		builder.encodedAuthority(api_domain);
		builder.path(api_name);
		try {
			builder.appendQueryParameter("param", encrypted_params);
			return builder.build().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/** 初期化リストを作成:static */
	static public ArrayList<Stage> initStageList(Context c) {
		ArrayList<Stage> list = new ArrayList<Stage>();
		list.add(createProrogue());
		list.add(createStage1(c));
		list.add(createStage2(c));
		list.add(createStage3(c));
		list.add(createStage4(c));
		list.add(createEpilogue(c));

		// クリアフラグを設定ファイルから読み出してセット
		/*		if (Debug.isDebug) {
					 for (int i = 0; i < list.size(); i++) {
					 Stage s = list.get(i);
					 s.clear = false; // FIXME
					 }
				} else*/{
			SharedPreferences pref = c.getSharedPreferences(Common.TAG,
					Context.MODE_PRIVATE);
			int clear_flags = pref.getInt(Common.PREF_KEY_CLEARFLAGS, 0);
			for (int i = 0; i < list.size(); i++) {
				Stage s = list.get(i);
				if ((clear_flags & (1 << i)) != 0) {
					s.clear = true;
				}
			}
		}

		return list;
	}

	/**
	 * ステージクリアフラグの保存
	 * 
	 */
	public static void saveStageClearFlags(Context c, ArrayList<Stage> list) {
		SharedPreferences.Editor e = c.getSharedPreferences(Common.TAG,
				Context.MODE_PRIVATE).edit();
		int clear_flags = 0;
		for (int i = 0; i < list.size(); i++) {
			Stage s = list.get(i);
			if (s.clear) {
				clear_flags |= (1 << i);
			}
		}
		e.putInt(Common.PREF_KEY_CLEARFLAGS, clear_flags);
		e.commit();
	}
}
