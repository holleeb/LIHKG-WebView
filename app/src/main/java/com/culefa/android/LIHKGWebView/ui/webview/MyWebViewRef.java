package com.culefa.android.LIHKGWebView.ui.webview;

import java.lang.ref.WeakReference;

public class MyWebViewRef {
    WeakReference<MyWebView> myRef;
    public MyWebViewRef(MyWebView webView){
        myRef = new WeakReference<MyWebView>(webView);
    }
    public MyWebView get(){
        return myRef.get();
    }

    public static MyWebViewRef createWebViewRef(MyWebView webView){
        if(webView == null) return null;
        return new MyWebViewRef(webView);
    }

}
