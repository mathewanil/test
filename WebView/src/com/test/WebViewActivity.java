package com.test;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        WebView.enablePlatformNotifications();
        
        setContentView(R.layout.webview_1);
        
        final String mimeType = "text/html";
        //final String encoding = "utf-8";
        
        WebView wv;
        
        wv = (WebView) findViewById(R.id.wv1);
        //wv.loadData("<a href='x'>Hello World! - 1</a>", mimeType, encoding);
        //wv.loadUrl("http://vexflow.com/");
        wv.loadUrl("http://www.google.com");
        
        
    }
}