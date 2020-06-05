package jp.co.cybird.android.escape.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.escape.sound.SoundManager;
import jp.co.cybird.android.escape.util.NetworkUtil;
import jp.co.cybird.android.escape.util.Tracking;

/**
 * オプション画面でのWebView画面用全画面ダイアログ
 *
 * @author S.Kamba
 */
public class OptionWebDialog extends BaseDialogFragment implements
        OnClickListener {

    /**
     * contentビュー
     */
    View content = null;

    String request_url;
    String ga_scree_name;

    WebViewClient mWebClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Log.d("OptionWebDialog", "shouldOverrideUrlLoading:url=" + url);
            // 外部ブラウザの起動を抑制
            return false;
        }

        ;

    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.AppTheme);
        // タイトルバー無し
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーンでダイアログを表示。
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        Bundle args = getArguments();
        if (args != null) {
            request_url = args.getString(Common.KEY_URL);
            ga_scree_name = args.getString(Common.KEY_GA_SCREENNAME);
        }
        return dialog;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        content = inflater.inflate(R.layout.fragment_web, null);

        ImageButton b = (ImageButton) content.findViewById(R.id.btn_close);
        b.setOnClickListener(this);

        WebView webView = (WebView) content.findViewById(R.id.webView);
        webView.setWebViewClient(mWebClient); // 外部ブラウザの起動を抑制するために必要
        // Log.d("OptionWebDialog", "loadUrl::" + request_url);

        if (NetworkUtil.isConnected(getActivity())) {
            loadUrl(webView);
        } else {
            Toast.makeText(this.getActivity(),
                    getResources().getString(R.string.err_network),
                    Toast.LENGTH_LONG).show();
        }

        // for GA
        Tracking.sendView(ga_scree_name);

        return content;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            if (isPlaySE) {
                SoundManager.getInstance().playCloseSE();
            }
            dismiss();
        }
    }

    protected void loadUrl(WebView webView) {
        webView.loadUrl(request_url);
    }
}
