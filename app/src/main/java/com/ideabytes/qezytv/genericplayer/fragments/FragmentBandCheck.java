package com.ideabytes.qezytv.genericplayer.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.ideabytes.qezytv.genericplayer.R;

/**
 * Created by suman on 23/12/15.
 */
public class FragmentBandCheck extends Fragment {
    private static final String LOGTAG = "FragmentBandCheck";
    private EditText IPAddress;
    private WebView mWebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bandcheck, container, false);

        mWebView = (WebView) view.findViewById(R.id.webView);
        if(mWebView != null)
        mWebView.loadUrl("http://www.bandwidthplace.com/");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());


        Log.d(LOGTAG, "FragmentBandCheck ");

        return view;
    }
}
