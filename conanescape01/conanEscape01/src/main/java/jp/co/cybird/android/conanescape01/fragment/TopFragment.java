package jp.co.cybird.android.conanescape01.fragment;

import java.io.File;
import java.util.ArrayList;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.LoadingActivity;
import jp.co.cybird.android.conanescape01.gui.MainActivity;
import jp.co.cybird.android.conanescape01.gui.OptionActivity;
import jp.co.cybird.android.conanescape01.model.Stage;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.Tracking;
import jp.co.cybird.android.escape.util.TransparentWebView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Top画面用
 * 
 * @author S.Kamba
 *
 */
public class TopFragment extends ConanFragmentBase implements OnClickListener {

	View root_view = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		root_view = inflater.inflate(R.layout.fragment_top, container, false);

		ImageButton b;
		b = (ImageButton) root_view.findViewById(R.id.btn_newgame);
		b.setOnClickListener(this);
		b = (ImageButton) root_view.findViewById(R.id.btn_load);
		b.setOnClickListener(this);
		b = (ImageButton) root_view.findViewById(R.id.btn_option);
		b.setOnClickListener(this);

		// copyright
		TransparentWebView web = (TransparentWebView) root_view
				.findViewById(R.id.web_copyright);
		web.init();
		web.loadUrl(getString(R.string.url_copyright));

		return root_view;
	}

	@Override
	public void onStart() {
		super.onStart();
		// GoogleAnalytics
		Tracking.sendView("Top");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_newgame:
			playButtonSE();
			onNewGame();
			break;
		case R.id.btn_load:
			playButtonSE();
			onLoad();
			break;
		case R.id.btn_option:
			playButtonSE();
			onOption();
			break;
		}
	}

	/**
	 * NEW GAME
	 */
	void onNewGame() {
		Fragment f = new StageSelectFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, f, null);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
	}

	/**
	 * LOAD
	 */
	void onLoad() {
		File savedNodeData = new File(getActivity().getDir("save",
				Context.MODE_PRIVATE), "data_node.csv");
		File savedItemData = new File(getActivity().getDir("save",
				Context.MODE_PRIVATE), "data_item.csv");
		if (!savedNodeData.exists() || !savedItemData.exists()) {
			Toast.makeText(getActivity(), "セーブデータがありません。", Toast.LENGTH_SHORT)
					.show();
			return; // なければ実行しない
		}

		EscApplication app = (EscApplication) getActivity().getApplication();

		// 保存したステージ番号にする
		int savedNo = app.getSavedStageNo();
		int index = findIndexFromStageNo(savedNo);
		if (index < 0) {
			Toast.makeText(getActivity(), "セーブデータのステージに一致するステージデータがありません。",
					Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<Stage> stageList = app.getStageList();
		Stage activeStage = stageList.get(index);

		Intent intent = new Intent(getActivity(), LoadingActivity.class);
		intent.putExtra(Common.KEY_NEWGAME, false);
		intent.putExtra(Common.KEY_STAGE_NO, activeStage.stageNo);
		intent.putExtra(Common.KEY_SAVED_NODEFILE,
				savedNodeData.getAbsolutePath());
		intent.putExtra(Common.KEY_SAVED_ITEMFILE,
				savedItemData.getAbsolutePath());
		startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_LOADING);
	}

	/**
	 * OPTION
	 * 
	 * @param v
	 */
	void onOption() {
		Intent intent = new Intent(getActivity(), OptionActivity.class);
		MainActivity a = (MainActivity) getActivity();
		a.setStopBGM(false);
		a.setDoingOther(true);
		startActivityForResult(intent, Common.ACTIVITY_REQUESTCODE_OPTIONS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Common.ACTIVITY_REQUESTCODE_OPTIONS: {
			getSettings();
			MainActivity a = (MainActivity) getActivity();
			a.onActivityResult(requestCode, resultCode, data);
		}
			break;
		case Common.ACTIVITY_REQUESTCODE_LOADING:
			if (resultCode == Common.RESULT_LOAD_ERROR) {
				SoundManager.getInstance().release();
				MainActivity a = (MainActivity) getActivity();
				a.setStopBGM(true);
				a.initSounds();
			} else {
				MainActivity a = (MainActivity) getActivity();
				a.setStopBGM(true);
				a.postBlackFadeShow();
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// // locals

	/** stageListから指定のstageNoのindexを探す */
	private int findIndexFromStageNo(int savedNo) {
		ArrayList<Stage> stageList = ((EscApplication) getActivity()
				.getApplication()).getStageList();
		for (int i = 0; i < stageList.size(); i++) {
			Stage s = stageList.get(i);
			if (s.stageNo == savedNo)
				return i;
		}
		return -1;
	}

}
