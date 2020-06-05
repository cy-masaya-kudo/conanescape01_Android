package jp.co.cybird.android.conanescape01.gui;

import jp.co.cybird.android.conanescape01.EscApplication;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.fragment.ManualFragment;
import android.os.Bundle;

/**
 * 遊び方初回表示用画面
 * 
 * @author S.Kamba
 * 
 */
public class ManualActivity extends OptionActivity {

	@Override
	protected void setFirstFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new ManualFragment()).commit();
		}
	}

	@Override
	protected String getScreenName() {
		return "Manual";
	}

	@Override
	protected void onStart() {
		EscApplication app = (EscApplication) getApplication();
		app.setFirstRunPreference(false);
		showBackButton(false);
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		// 戻れない
		// super.onBackPressed();
	}
}
