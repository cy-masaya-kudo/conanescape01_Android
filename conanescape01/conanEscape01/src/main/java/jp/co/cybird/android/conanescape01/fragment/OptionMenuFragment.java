package jp.co.cybird.android.conanescape01.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import jp.co.cybird.android.compliance.DialogContactSelect;
import jp.co.cybird.android.compliance.DialogContactSelect.OnContactSelectListener;
import jp.co.cybird.android.conanescape01.Common;
import jp.co.cybird.android.conanescape01.R;
import jp.co.cybird.android.conanescape01.gui.ConanActivityBase;
import jp.co.cybird.android.escape.dialog.OptionWebContactDialog;
import jp.co.cybird.android.escape.dialog.OptionWebDialog;
import jp.co.cybird.android.escape.dialog.OptionWebParentContactDialog;

/**
 * Optionメニュー画面
 *
 * @author S.Kamba
 */
public class OptionMenuFragment extends OptionFragmentBase implements
        OnClickListener, OnContactSelectListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option, container,
                false);
        ImageButton b;
        b = (ImageButton) rootView.findViewById(R.id.btn_manual);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_sound);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_terms);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_policy);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_tokusho);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_fund);
        b.setOnClickListener(this);
        b = (ImageButton) rootView.findViewById(R.id.btn_contact);
        b.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent.showBackButton(false);
    }

    @Override
    public void onClick(View v) {
        playButtonSE();
        DialogFragment df = null;

        // 画面遷移
        int id = v.getId();
        switch (id) {
            case R.id.btn_manual: {
                Fragment f = new ManualFragment();
                parent.move(f);
            }
            break;
            case R.id.btn_sound: {
                Fragment f = new SoundFragment();
                parent.move(f);
            }
            break;
            case R.id.btn_terms: {
                df = new OptionWebDialog();
                Bundle args = new Bundle();
                args.putString(Common.KEY_URL, getString(R.string.url_termsofuse));
                args.putString(Common.KEY_GA_SCREENNAME, "OptionTerms");
                df.setArguments(args);
                df.show(getFragmentManager(), "terms");
            }
            break;
            case R.id.btn_policy: {
                df = new OptionWebDialog();
                Bundle args = new Bundle();
                args.putString(Common.KEY_URL,
                        getString(R.string.url_privacypolicy));
                args.putString(Common.KEY_GA_SCREENNAME, "OptionPrivacyPolicy");
                df.setArguments(args);
                df.show(getFragmentManager(), "policy");
            }
            break;
            case R.id.btn_tokusho: {
                df = new OptionWebDialog();
                Bundle args = new Bundle();
                args.putString(Common.KEY_URL, getString(R.string.url_tokusho));
                args.putString(Common.KEY_GA_SCREENNAME,
                        "OptionSpecialCommercialCode");
                df.setArguments(args);
                df.show(getFragmentManager(), "tokusho");
            }
            break;
            case R.id.btn_fund: {
                df = new OptionWebDialog();
                Bundle args = new Bundle();
                args.putString(Common.KEY_URL, getString(R.string.url_fund));
                args.putString(Common.KEY_GA_SCREENNAME,
                        "OptionFundsSettlementMethod");
                df.setArguments(args);
                df.show(getFragmentManager(), "fund");
            }
            break;
            case R.id.btn_contact: {
                df = DialogContactSelect.newInstance(this);
                df.show(getFragmentManager(), DialogContactSelect.TAG);
            }
            break;
        }

        if (df != null) {
            // sound制御
            ConanActivityBase a = (ConanActivityBase) getActivity();
            a.setStopBGM(false);
            a.setDoingOther(true);
        }
    }

    @Override
    public String getViewName() {
        return "OptionTop";
    }


    @Override
    public void onContactSelected(final boolean isNormalContct) {
        playButtonSE();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogFragment df = null;
                if (isNormalContct) {
                    // 通常問い合わせ
                    df = new OptionWebContactDialog();
                    Bundle args = new Bundle();
                    args.putString(Common.KEY_URL, getString(R.string.url_contact));
                    args.putString(Common.KEY_GA_SCREENNAME, "OptionContact");
                    df.setArguments(args);
                    df.show(getFragmentManager(), "contact");
                } else {
                    df = new OptionWebParentContactDialog();
                    Bundle args = new Bundle();
                    args.putString(Common.KEY_GA_SCREENNAME, "OptionParentContact");
                    df.setArguments(args);
                    df.show(getFragmentManager(), "parent_contact");
                }
                if (df != null) {
                    // sound制御
                    ConanActivityBase a = (ConanActivityBase) getActivity();
                    a.setStopBGM(false);
                    a.setDoingOther(true);
                }
            }
        }, 100);
    }
}
