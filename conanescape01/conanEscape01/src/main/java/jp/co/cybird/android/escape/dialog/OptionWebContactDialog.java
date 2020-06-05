package jp.co.cybird.android.escape.dialog;

import android.content.Context;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import jp.co.cybird.android.compliance.AgreementUtil;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.escape.util.NetworkUtil;
import jp.co.cybird.android.minors.MinorsDialogManager;
import jp.co.cybird.android.util.Uid;
import jp.co.cybird.app.android.lib.commons.security.popgate.Codec;

/**
 * オプション画面でのWebView画面用全画面ダイアログ
 *
 * @author S.Kamba
 */
public class OptionWebContactDialog extends OptionWebDialog implements
        OnClickListener {

    @Override
    protected void loadUrl(WebView webView) {
        if (!NetworkUtil.isConnected(getActivity())) {
            Toast.makeText(this.getActivity(),
                    getResources().getString(R.string.err_network),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Context context = getActivity().getApplicationContext();

        // UUID
        String x_cy_identify = Codec.encode(Uid.getCyUserId(context));
        //
        //
        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("X-CY-IDENTIFY", x_cy_identify);
        //
//		String url = String.format("%s?id=%s", request_url, x_cy_identify);
        MinorsDialogManager manager = AgreementUtil.newMinorsManager(context, getFragmentManager());
        String url = String.format("%s?id=%s&%s", request_url, x_cy_identify,
                AgreementUtil.getMinorsParamEncrypt(context, manager));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.loadUrl(url, extraHeaders);
    }
}
