package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.alexsykes.mapmonster.R;

public class HelpActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        String url = "https://android.alexsykes.com/mapHelp.html";

        webView = findViewById(R.id.webView);
        String userAgent = webView.getSettings().getUserAgentString();
        webView.loadUrl(url);
    }
}