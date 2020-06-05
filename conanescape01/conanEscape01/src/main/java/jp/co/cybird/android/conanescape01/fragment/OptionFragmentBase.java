package jp.co.cybird.android.conanescape01.fragment;

import jp.co.cybird.android.conanescape01.gui.OptionActivity;
import jp.co.cybird.android.escape.util.Tracking;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Option画面のフラグメント基本クラス
 * 
 * @author S.Kamba
 * 
 */
public abstract class OptionFragmentBase extends ConanFragmentBase {

	OptionActivity parent = null;

	/** ViewName */
	abstract public String getViewName();

	@Override
	public void onAttach(Activity activity) {
		parent = (OptionActivity) activity;
		super.onAttach(activity);
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// if (parent != null) {
		// parent.setOptionTitle(getTitleResId());
		// }
		super.onViewCreated(view, savedInstanceState);
		parent.showBackButton(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		// GoogleAnalytics
		Tracking.sendView(getViewName());
	}
}
