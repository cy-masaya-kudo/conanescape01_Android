package jp.co.cybird.android.escape.dialog;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.GameActivity;
import jp.co.cybird.android.escape.sound.SoundManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

/**
 * クリア時ポップアップダイアログ
 * 
 * @author S.Kamba
 * 
 */
public class EndDialog extends TranslucentFullscreenDialog implements
		View.OnClickListener {

	@Override
	public int getLayoutId() {
		return R.layout.popup_ending;
	}

	@Override
	public void initView() {
		ImageButton b = (ImageButton) content.findViewById(R.id.btn_top);
		b.setOnClickListener(this);
		setCancelable(false);
	}

	@Override
	public void onStart() {
		View fin = content.findViewById(R.id.img_fin);
		AlphaAnimation a = new AlphaAnimation(0, 1);
		a.setDuration(1000);
		a.setInterpolator(new LinearInterpolator());
		fin.startAnimation(a);
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		if (isPlaySE)
			SoundManager.getInstance().playButtonSE();
		GameActivity a = (GameActivity) getActivity();
		// ダイアログ閉じる
		// dismiss();
		// Activity終了してトップへ戻る
		a.setResult(Common.RESULT_BACKTOTOP);
		a.finishGameActivity();
	}

}
