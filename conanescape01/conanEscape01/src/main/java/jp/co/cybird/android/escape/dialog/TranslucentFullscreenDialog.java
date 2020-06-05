package jp.co.cybird.android.escape.dialog;

import jp.co.cybird.android.conanescape01.R;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 全画面フルスクリーンダイアログ
 * 
 * @author S.Kamba
 *
 */
public abstract class TranslucentFullscreenDialog extends BaseDialogFragment {

	/**
	 * レイアウトidを取得
	 * 
	 * @return レイアウトid
	 */
	public abstract int getLayoutId();

	/**
	 * 独自初期化
	 * 
	 * @param builder
	 */
	public abstract void initView();

	/** contentビュー */
	View content = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(),
				R.style.TransparentDialogTheme);
		// タイトルバー無し
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// ダイアログの背景を完全に透過。
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		// フルスクリーンでダイアログを表示。
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		dialog.setCanceledOnTouchOutside(false);
		// setCancelable(false);
		return dialog;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		content = inflater.inflate(getLayoutId(), null);
		// 独自初期化
		initView();

		return content;
	}

}
