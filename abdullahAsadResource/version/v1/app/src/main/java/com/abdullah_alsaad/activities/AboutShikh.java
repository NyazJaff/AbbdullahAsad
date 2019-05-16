package com.abdullah_alsaad.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.abdullah_alsaad.R;

public class AboutShikh extends AppCompatActivity  {
    private ImageButton homeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_shikh);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        WebView mWebView = null;
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/aboutShikh.html"); //new.html is html file name.
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AboutShikh.this, MainActivity.class);
                AboutShikh.this.startActivity(myIntent);
            }
        });

    }
}
