/*
 * Copyright (C) 2023 culefa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.culefa.android.LIHKGWebView.ui.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;


import android.app.AlertDialog;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.culefa.android.LIHKGWebView.MainActivity;
import com.culefa.android.LIHKGWebView.R;
import com.culefa.android.LIHKGWebView.ui.main.ImageHandler;
import com.culefa.android.LIHKGWebView.ui.main.MainFragment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import im.delight.android.webview.AdvancedWebView;

public class MyWebView extends AdvancedWebView {

    static final String CONTROLLER_JS_INTERFACE = "xec2D";

    private boolean isKilled = false;

    public MyWebView(Context context) {
        super(context); initWebView(context);
    }


    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);initWebView(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);initWebView(context);
    }


    private int mTouchSlop;
    private float mLastX;
    private float mLastY;
    private boolean mIsScrolling;

    private void initWebView(Context context) {

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public static class TextInputConnection extends BaseInputConnection
    {
        /// <summary>
        /// SpannableStringBuilder
        /// </summary>
        private SpannableStringBuilder buildr;

        public TextInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);

            buildr = new SpannableStringBuilder("");
        }

        @Override
        public android.text.Editable getEditable() {
            return buildr;
        }

    }
    public final String[] MIME_TYPES = new String[]{"image/*", "video/*"};


    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
//        outAttrs.actionLabel = null;
//        outAttrs.inputType = EditorInfo.TYPE_NULL;
//        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
////        return new BaseInputConnection(this, false) {
////            @Override
////            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
////                if (beforeLength == 1 && afterLength == 0) {
////                    // backspace
////                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
////                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
////                }
////                return super.deleteSurroundingText(beforeLength, afterLength);
////            }
////        };
        try{
            return super.onCreateInputConnection(editorInfo); // gboard for double tap text selection
        }catch (Throwable ignored){}



        return null;


    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        try {
            super.onScrollChanged(l, t, oldl, oldt);
        } catch (Exception ignored) {
            //
        }
    }



    // onInterceptTouchEvent might not be required.
    // https://developer.android.com/develop/ui/views/touch-and-input/gestures/viewgroup

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mIsScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(event.getX() - mLastX);
                float dy = Math.abs(event.getY() - mLastY);

                if (dx > mTouchSlop || dy > mTouchSlop || (dx*dx+dy*dy)>mTouchSlop*mTouchSlop ) {
                    mIsScrolling = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsScrolling = false;
                break;
        }

        // In general, don't intercept touch events. The child view handles them.
        return mIsScrolling || super.onInterceptTouchEvent(event);
    }

    private long lastScrollTime = 0;
    private long stopScrollTime = 0;
    public boolean disableScroll = false;
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//        Log.i("SSC","WW");
        lastScrollTime = System.currentTimeMillis();
        int d = 220;
        if(deltaX*deltaX + deltaY * deltaY < d*d) {
            if (disableScroll) return false;
            if (stopScrollTime > 0 && lastScrollTime - stopScrollTime < 140) {
                stopScrollTime = 0;
                disableScroll = true;
                return false;
            }
//            Log.i("WSSC", "WAAQ");
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public void scrollTo(int x, int y) {
        // Do nothing

//        if(disableScroll) return ;
//        if(stopScrollTime > 0 && lastScrollTime - stopScrollTime < 140) return;
        super.scrollTo(x, y);
    }

    @Override
    public void computeScroll() {
        // Do nothing
//        if(disableScroll) return ;
//        if(stopScrollTime > 0 && lastScrollTime - stopScrollTime < 140) return;
        super.computeScroll();
    }

    public class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }
//
//        @JavascriptInterface
//        public void closeClickHandler(){
//
//        }

        @JavascriptInterface
        public void stopScroll(){
            if(mFragment==null || mFragment.fragmentDead) return;
            stopScrollTime = System.currentTimeMillis();



        }

        long notifyDarkModeRId = 0;

        // This method can be called from JavaScript
        @JavascriptInterface
        public void notifyDarkMode(String strDarkMode) {
            if(mFragment==null || mFragment.fragmentDead) return;
            final boolean darkMode =  strDarkMode.equals("true");
//            Log.i("WSS", darkMode+"");



            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {



                    if(mFragment == null || mFragment.getActivity() == null) return;

                    if (darkMode) {
//                        mFragment.overrideDarkMode = true;
                        mFragment.getViewModel().setDarkMode(true);
                        mFragment.setDarkMode(true, true);
                    }else {

//                        mFragment.overrideDarkMode = false;
                        mFragment.getViewModel().setDarkMode(false);
                        mFragment.setDarkMode(false, true);
                    }
                }
            },1);
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        final HashMap<Integer,Boolean> touchRecord = new HashMap<Integer, Boolean>();

        @JavascriptInterface
        public void notifyContextMenu(String elementHit){
            if(mFragment==null || mFragment.fragmentDead) return;

            HashMap<String, String> kMap = createHashMap(elementHit);
            int uid = -1;
            try {
                String s = kMap.get("uid");
                if(s!=null) uid = Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
            }


            boolean kk = uid>=1 && "touchstart".equals( kMap.get("action"));
            if(kk){

                final int pUid = uid;
                mFragment.webViewContextDomData = new ImageHandler.ContextDomData(){
                    {
                        this.img = kMap.get("img");
                        this.link = kMap.get("link");
                        this.uid = pUid;
                    }
                };
            }


        }

        public HashMap<String, String> createHashMap(String elementHit) {
            HashMap<String, String> map = new HashMap<>();
            String[] elements = elementHit.split("\n");
            for (String element : elements) {
                String[] keyValue = element.split("\t");
                if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
            return map;
        }

        @JavascriptInterface
        public void stateChanged(String currentUrl){
            if(mFragment==null || mFragment.fragmentDead || mFragment.getActivity() == null) return;

            Log.i("jsIt.stateChanged", currentUrl);

            mFragment.getViewModel().webViewURL = currentUrl;
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    MyWebView.this.saveState(((MainActivity)  mFragment.requireActivity()).webViewBundle);
//                }
//            },1);

        }

    }


    private float startY = -1;
    private float startX = -1;
    private boolean allowPull = true;
//    public boolean capturePullDown = false;
    private final float refreshThreshold = 200;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(Context context) {
        super.init(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // https://developer.android.com/develop/ui/views/layout/webapps/managing-webview?hl=en
            MyWebView.this.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true);
        }


        // Set an OnScrollChangeListener to enable/disable SwipeRefreshLayout based on the WebView scroll position
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MyWebView.this.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //
//                    Log.i("WSSX", scrollY+"");

                }
            });


            WebView webView = MyWebView.this;

            webView.setNestedScrollingEnabled(true); // important

            webView.setOnTouchListener((view, event) -> {
                disableScroll = false;
                SwipeRefreshLayout srl = (SwipeRefreshLayout) mFragment.requireActivity().findViewById(R.id.swipe_refresh_layout);
                if (srl == null) return webView.onTouchEvent(event);

                int action = event.getAction();

                if (!allowPull && action == MotionEvent.ACTION_MOVE && srl.isEnabled()) {
                    srl.setEnabled(false);
                }

                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                    startY = event.getY();
                    startX = event.getX();
                    allowPull = false;
                    srl.setEnabled(true);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {


                    startY = -1;
                    startX = -1;

                    allowPull = true;
                    srl.setEnabled(true);

                } else if (startY >=0 && startX >=0 ){
                    final float endY = event.getY();
                    final float endX = event.getX();

                    if (!allowPull && webView.getScrollY() <= 0 && endY - startY > refreshThreshold && Math.abs(endX - startX) < refreshThreshold) {
                        srl.setEnabled(true);
                        allowPull = true;
                    }
                }

                return webView.onTouchEvent(event);
            });

        }

        MyJavaScriptInterface jsInterface = new MyJavaScriptInterface(context);
//        Log.i("SS","SS11");
//        if(Build.VERSION.SDK_INT>17) {
            this.addJavascriptInterface(jsInterface, CONTROLLER_JS_INTERFACE);
//        }

//        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//            WebSettingsCompat.setForceDark(this.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
//        }

//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
//            WebSettingsCompat.setForceDarkStrategy();
//        }

        WebSettings webSettings = this.getSettings();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                WebSettingsCompat.setAlgorithmicDarkeningAllowed(webSettings, false);
//                Log.i("WWS","WWS");
            }
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            WebView myWebView = this;
//            myWebView.setForceDarkAllowed(false);
//        }
//        this.getSettings().setForceDarkAllowed(false);;



        webSettings.setBlockNetworkLoads(false);
        webSettings.setBlockNetworkImage(false);
        webSettings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webSettings.setDisabledActionModeMenuItems(0);
        }

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(false);
        }



    }

    AppCompatActivity mActivity;
    MainFragment mFragment;
    public void setActivity(AppCompatActivity activity){

        mActivity = activity;
    }
    public void setFragment(MainFragment fragment){

        mFragment = fragment;
    }

    public void setMixedContentMode(){

//        WebSettings webSettings = this.getSettings();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE); // do not use advancedview's method
//        }

    }

    public void disableFlash(@Nullable android.view.Window window){
//
//        WebSettings webSettings = this.getSettings();
//
//        // NO FLASH
//
////        webSettings.setPluginState(WebSettings.PluginState.ON); // Flashを有効にする
////        this.requireActivity().getWindow().setFlags(0x01000000, 0x01000000); // PluginState.ON for SDK > 10
//        webSettings.setPluginState(WebSettings.PluginState.OFF);
//
//        // 0x01000000と数値で記述しているのは、WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATEDが定義されていないAPIレベルでも使えるようにするため。
//        // マニフェストで<application>か対象の<activity>にandroid:hardwareAccelerated="true"を書く方法でもOK。
//
//
//        if(window != null) {
//            window.clearFlags(0x01000000);
//        }
    }


    @Override
    public void destroy() {
        if(!this.isKilled) {
            this.isKilled = true;

//            try {
//                ViewGroup webViewContainer = (ViewGroup) this.getParent();
//                if (webViewContainer != null) webViewContainer.removeView(this);
//            }catch (Throwable ignored){
//
//            }

            try {
                this.stopLoading();
            }catch (Throwable ignored){

            }


            try {
                this.setTag(null);
                this.clearCache(true);
                this.clearHistory();
                this.removeAllViews();

            }catch (Throwable ignored){

            }



            try {
                this.removeJavascriptInterface(CONTROLLER_JS_INTERFACE);
            }catch (Throwable ignored){

            }


            try {
                this.setCustomWebViewClient(null);
                this.setCustomWebChromeClient(null);
                this.setDefaultWebViewClient(null);
                this.setDefaultWebChromeClient(null);
            }catch (Throwable ignored){

            }

            try {
                ViewGroup container = (ViewGroup) this.getParent();
                if(container != null) container.removeView(this);

            }catch (Throwable ignored){

            }
        }
        super.destroy();
    }


    public AdBlocker mAdBlocker = null;

    public void blockAds(AdBlocker adblocker){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAdBlocker = adblocker;
        }
    }


    public String readTextFile(Context context, int resourceId, boolean doTrim) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            String result = outputStream.toString();
            if(doTrim) result = result.trim();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // some codes from https://www.hidroh.com/2016/05/19/hacking-up-ad-blocker-android/
    public static class AdBlocker {
        private final Set<String> AD_HOSTS = new HashSet<>();
        private final Map<String,Boolean> AD_HOSTS_BLOCKED_STATUS = new HashMap<String,Boolean>();

        public boolean isAd(String url) {
            String hostName = getHostname(url);
            Boolean result = AD_HOSTS_BLOCKED_STATUS.get(hostName);
            if(result != null) return result;
            result = isAdHost(hostName != null ? hostName : "");
            AD_HOSTS_BLOCKED_STATUS.put(hostName, result);
            return result;
        }

        public String getHostname(String urlString) {
            String hostname = null;
            try {
                URL url = new URL(urlString);
                hostname = url.getHost();
            } catch (MalformedURLException e) {
                // handle invalid URL
            }
            return hostname;
        }

        public void initAdsList(InputStream inputStream) {
            AD_HOSTS.clear();
//            HashSet<String> adsSet = new HashSet<>();
//            InputStream inputStream = getResources().openRawResource(R.raw.pgl_yoyo_org);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    AD_HOSTS.add(line);
                }
            } catch (IOException e) {
                // handle error
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    // handle error
                }
            }
//            AD_HOSTS = adsSet;
//            return adsSet;
        }

        private boolean isAdHost(String host) {
            if (TextUtils.isEmpty(host)) {
                return false;
            }
            int index = host.indexOf(".");
            return index >= 0 && (AD_HOSTS.contains(host) ||
                    index + 1 < host.length() && isAdHost(host.substring(index + 1)));
        }

        public WebResourceResponse createEmptyResource() {
            return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
        }
    }

    // https://pgl.yoyo.org/as/serverlist.php?hostformat=nohtml&showintro=0&mimetype=plaintext
    // some codes from https://www.hidroh.com/2016/05/19/hacking-up-ad-blocker-android/


    String strJsSetDarkMode = null;

    public void setDarkMode(boolean darkMode ){

        if(strJsSetDarkMode == null) strJsSetDarkMode = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_set_dark_mode,true);;


        this.evaluateJavascript("javascript: !location.hostname.endsWith('lihkg.com') ? 0 : ("+ strJsSetDarkMode +")("+darkMode+")", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
//
            }
        });

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (clampedX || clampedY) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }



    protected static void evaluateJavascriptX(WebView webview, String script, String uid, ValueCallback<String>
            resultCallback) {

        webview.evaluateJavascript("javascript: (()=>!location.hostname.endsWith('lihkg.com')?0:'"+uid+"' in window?2:((window."+ uid +"=(function(){\n" + script + "\n})()),1))();", resultCallback);

//        webview.evaluateJavascript("javascript: !location.hostname.endsWith('lihkg.com')||('"+uid+"' in window)||(window."+ uid +"=(function(){\n" + script + "\n})());", resultCallback);


    }

    public class BWebViewClient extends AWebViewClient{

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            // https://developer.android.com/develop/ui/views/layout/webapps/managing-webview?hl=en
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!detail.didCrash()) {
                    // Renderer was killed because the system ran out of memory.
                    // The app can recover gracefully by creating a new WebView instance
                    // in the foreground.
                    Log.e("LIHKGWebView", "System killed the WebView rendering process " +
                            "to reclaim memory. Recreating...");

                    MyWebView mWebView = MyWebView.this;

                    if (mWebView != null && !isKilled) {
                        if(mFragment != null && !mFragment.fragmentDead && mFragment.mWebView != null){
                            mFragment.killWebView();
                        }else {
                            mWebView.destroy();
                        }
                    }

                    // By this point, the instance variable "mWebView" is guaranteed
                    // to be null, so it's safe to reinitialize it.

                    return true; // The app continues executing.
                }
            }

            // Renderer crashed because of an internal error, such as a memory
            // access violation.
            Log.e("LIHKGWebView", "The WebView rendering process crashed!");

            // In this example, the app itself crashes after detecting that the
            // renderer crashed. If you choose to handle the crash more gracefully
            // and allow your app to continue executing, you should 1) destroy the
            // current WebView instance, 2) specify logic for how the app can
            // continue executing, and 3) return "true" instead.
            return false;
        }

//        public SwipeRefreshLayout swipeRefreshLayout;

//        public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
//            this.swipeRefreshLayout = swipeRefreshLayout;
//        }





        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view,url,favicon);
            // Show a progress dialog while the page is loading
//            progressDialog.show();
        }


        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if(mActivity!=null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Is it OK to open the page with the wrong ssl certificate?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @SuppressLint("WebViewClientOnReceivedSslError")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            handler.cancel();
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                super.onReceivedSslError( view,  handler,  error);
            }
        }


        final private Map<String, Boolean> loadedUrls = new HashMap<>();

        public boolean checkIsAd(String url){
            if(mAdBlocker == null) return false;
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = mAdBlocker.isAd(url);
                if(ad){
                }
                loadedUrls.put(url, ad);
            } else {
                ad = Boolean.TRUE.equals(loadedUrls.get(url));
            }
            return ad;
        }

        // some codes from https://blog.rosuh.me/web-view-ad-blocker
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            // This method was deprecated in API level 21.
            // Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
            if (checkIsAd(url)) {
                return createEmptyResource();
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }



        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && checkIsAd(request.getUrl().toString())) {
                return createEmptyResource();
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        public WebResourceResponse createEmptyResource() {
            return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
        }

//        public boolean isAdDomain(String url) {
//            return (!url.contains("googleads.g.doubleclick.net")) || url.contains("baidu");
//        }

        String strUserCSS = null;

        String strUserScript01 = null;
        String strUserScript02 = null;
        String strUserScript03 = null;
        String strUserScript04 = null;
        String strUserScript05 = null;
        String strUserScript06 = null;
        String strUserScript07 = null;


        String strJsTidyUpSettings = null;
        String strJsGetDarkMode = null;

        String strJsNotifyDarkModeChanged= null;



        // reference : https://blog.rosuh.me/web-view-ad-blocker
        @Override
        public void onPageFinished(WebView webview, String url) {
            super.onPageFinished(webview, url);

            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)mFragment.getView().findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setRefreshing(false);


            if(strUserScript06 == null) strUserScript06 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_fix_events,true);;

            if(strUserScript06 != null && strUserScript06.length() > 12) {
                String s = strUserScript06;
                evaluateJavascriptX(webview, s, "tpPw5", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        if(value.contains("1")) {
                            Log.i("sText", "userscript 6");
                        }
                        //
                    }
                });
            }






            if(strUserScript05 == null) strUserScript05 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_remove_ads,true);;
//                Log.i("sText",s.length()+"");

            if(strUserScript05 != null && strUserScript05.length() > 12) {
                String s = strUserScript05;

                evaluateJavascriptX(webview, s, "xfg83S", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 5");
                        }
                        //
                    }
                });
            }




            webview.evaluateJavascript("javascript: window.lwelv?window.lwelv():0", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.i("MyWebView", "AdsRemoval" + " - " + s);
                }
            });



            if(strJsTidyUpSettings == null) strJsTidyUpSettings = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_tidy_up_settings,true);;



            webview.evaluateJavascript("javascript: !location.hostname.endsWith('lihkg.com') ? 0 : (" + strJsTidyUpSettings + ")() ", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.i("MyWebView", "Tidy Up localStorage.modesettings");
                }
            });


            if(strJsGetDarkMode == null) strJsGetDarkMode = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_get_dark_mode,true);;



            webview.evaluateJavascript("javascript: !location.hostname.endsWith('lihkg.com') ? 0 : (" + strJsGetDarkMode + ")()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                    value = value.replace("\"",""); // "true" => true
                    if(mFragment != null) {
//                            Log.i("WW", value);
                        if (value.equals("true")) {
//                            mFragment.overrideDarkMode = true;
                            mFragment.getViewModel().setDarkMode(true);
                            mFragment.setDarkMode(true, true);
                        } else if (value.equals("false")) {
//                            mFragment.overrideDarkMode = false;
                            mFragment.getViewModel().setDarkMode(false);
                            mFragment.setDarkMode(false, true);
                        }
                    }
                    Log.i("darkMode", value);
                }
            });

            if(strUserCSS == null) strUserCSS = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_styles,true);;
//                Log.i("sText",s.length()+"");

            if(strUserCSS != null && strUserCSS.length() > 12) {
                String s = strUserCSS;
                webview.evaluateJavascript("javascript: document.querySelector('style#wfg41T') ? 1 : !location.hostname.endsWith('lihkg.com') ? 0 : (function(k){\n" +
                        "\n" +
                        "    let style = document.createElement('style');\n" +
                        "    style.id = 'wfg41T';\n" +
//                            "    style.setAttribute('type','text/css');\n" +
                        "    style.textContent = k;\n" +
                        "    document.head.appendChild(style);\n" +
                        "})(`"+ s +"`)", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("sText", "usercss");
                        //
                    }
                });
            }



            if(strUserScript01 == null) strUserScript01 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_label_users_user,true);;
//                Log.i("sText",s.length()+"");

            if(strUserScript01 != null && strUserScript01.length() > 12) {
                String s = strUserScript01;

                evaluateJavascriptX(webview, s, "xfg41S", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 1");
                        }
                        //
                    }
                });
            }

            if(strUserScript02 == null) strUserScript02 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_latex_user,true);;

            if(strUserScript02 != null && strUserScript02.length() > 12) {
                String s = strUserScript02;

                evaluateJavascriptX(webview, s, "vAr4I", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 2");
                        }
                        //
                    }
                });
            }


            if(strUserScript03 == null) strUserScript03 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_json_parse,true);;

            if(strUserScript03 != null && strUserScript03.length() > 12) {
                String s = strUserScript03;

                evaluateJavascriptX(webview, s, "tpPi2", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 3");
                        }
                        //
                    }
                });
            }


            if(strUserScript04 == null) strUserScript04 = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_fix_pageurl,true);;

            if(strUserScript04 != null && strUserScript04.length() > 12) {
                String s = strUserScript04;

                evaluateJavascriptX(webview, s, "tpPi8", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 4");
                        }
                        //
                    }
                });
            }




            if(strJsNotifyDarkModeChanged == null) strJsNotifyDarkModeChanged = readTextFile(mActivity.getApplicationContext(),R.raw.lihkg_notify_dark_mode_changed,true);;


            webview.evaluateJavascript("javascript:  !location.hostname.endsWith('lihkg.com') ? 0 : (" + strJsNotifyDarkModeChanged + ")() ", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
//                        Log.i("MyWebView", "Tidy Up localStorage.modesettings");
                }
            });







            if(strUserScript07 == null) strUserScript07 = readTextFile(mActivity.getApplicationContext(),R.raw.webview_contextmenu,true);;
//                Log.i("sText",s.length()+"");

            if(strUserScript07 != null && strUserScript07.length() > 12) {
                String s = strUserScript07;

                evaluateJavascriptX(webview, s, "mek32", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(value.contains("1")) {
                            Log.i("sText", "userscript 7");
                        }
                        //
                    }
                });
            }



        }



    }


    public class BWebChromeClient extends AWebChromeClient{




        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            // This method is called when the WebView requests permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            } else {
                super.onPermissionRequest(request);
            }
        }
    }

    public void loadDownloadedFile(String fileName) {
        // Create a URI for the file in the Downloads directory
        Uri uri = Uri.parse("content://com.example.myapp.provider/" + fileName);

        // Load the file into the WebView
        loadDataWithBaseURL(null, "<img src=\"" + uri.toString() + "\">", "text/html", "utf-8", null);
    }




}
