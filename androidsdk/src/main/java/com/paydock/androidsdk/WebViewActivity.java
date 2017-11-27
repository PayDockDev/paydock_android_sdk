package com.paydock.androidsdk;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class WebViewActivity extends Activity {
    private String checkoutType;
    private String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        try {
            url = intent.getStringExtra("url");
            checkoutType = intent.getStringExtra("checkoutType");
        } catch (Exception e)
        {
            url = "https://paydock.com/result=cancelled";
        }

        WebView webView = findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new HelloWebViewClient());
        webView.loadUrl(url);
    }

    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("https://paydock.com")){
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("checkoutType", checkoutType);
                if (Objects.equals(checkoutType, "zipmoney")) {
                    if (url.contains("result=approved")) {
                        editor.putBoolean("success", true);
                    } else {
                        editor.putBoolean("success", false);
                    }
                } else if (Objects.equals(checkoutType, "afterpay")) {
                    if (url.contains("result=approved")) {
                        editor.putBoolean("success", true);
                    } else {
                        editor.putBoolean("success", false);
                    }
                } else if (Objects.equals(checkoutType, "paypal")) {
                    if (url.contains("result=approved")) {
                        editor.putBoolean("success", true);
                    } else {
                        editor.putBoolean("success", false);
                    }
                } else {
                    editor.putBoolean("success", false);

                }
                editor.apply();
                finish();
                return false;
            } else {
                view.loadUrl(url);
                return true;
            }
        }
    }


}