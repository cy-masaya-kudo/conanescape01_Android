package jp.co.cybird.android.conanescape01.fragment;

import jp.co.cybird.android.conanescape01.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 遊び方画面用フラグメント
 * 
 * @author S.Kamba
 * 
 */
public class ManualFragment extends OptionFragmentBase {

	@Override
	public String getViewName() {
		return "Manual";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_manual, container,
				false);
		return rootView;
	}

}
