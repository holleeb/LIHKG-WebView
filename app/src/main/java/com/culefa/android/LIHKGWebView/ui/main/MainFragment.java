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

package com.culefa.android.LIHKGWebView.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.culefa.android.LIHKGWebView.MainActivity;
import com.culefa.android.LIHKGWebView.R;
import com.culefa.android.LIHKGWebView.ui.webview.MyWebView;
import com.culefa.android.LIHKGWebView.ui.webview.MyWebViewRef;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;


public class MainFragment extends Fragment implements MyWebView.Listener {

    public MyWebViewRef mWebViewRef = null;
//    public Boolean overrideDarkMode = null;
//    public Boolean currentDarkMode = null;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    ImageHandler imgHandler = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LIHKGWebView", "onCreate");
        // TODO: Use the ViewModel

        Log.i("LIHKGWebView - url", getViewModel().webViewURL + "");


    }

    public MyWebView getMyWebView(){
        MyWebView mWebView = null;
        if(mWebViewRef != null) mWebView = mWebViewRef.get();
        return mWebView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState )
    {
        MyWebView mWebView = getMyWebView();
        Log.i("LIHKGWebView", "onSaveInstanceState");
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null){

            Bundle webViewBundle = mainActivity.webViewBundleRef.get();
            if(webViewBundle == null) {
                mainActivity.webViewBundleRef = new WeakReference<>(new Bundle());
                webViewBundle = mainActivity.webViewBundleRef.get();
            }else if(mWebView != null){
                webViewBundle.clear();
            }

            if(webViewBundle != null) {
                if(mWebView != null) mWebView.saveState(webViewBundle);
                outState.putBundle("webViewState", webViewBundle);
            }else{
                outState.putBundle("webViewState", null);
            }
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        Log.i("LIHKGWebView", "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
        refreshColors();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.i("LIHKGWebView", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("LIHKGWebView", "onDetach");
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.i("LIHKGWebView", "onInflate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i("LIHKGWebView", "onInflate");
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        Log.i("LIHKGWebView", "onMultiWindowModeChanged - "+ isInMultiWindowMode);
    }

    @Override
    public void onStart() {
        MyWebView mWebView = getMyWebView();
        super.onStart();
        Log.i("LIHKGWebView", "onStart");

        if(mWebView != null && mWebView.getParent() == null && mWebViewParent != null){
            mWebViewParent.addView(mWebViewParent);
        }

        mWebViewParent = null;
//        if(mWebView == null){
//            MainActivity mainActivity = (MainActivity) requireActivity();
//            mainActivity.recreateFragment();
//        }

//        if(mWebViewParent != null && mWebView != null) {
//            mWebViewParent.addView(mWebView);
//            mWebViewParent = null;
//            mWebView.setEnabled(true);
//            mWebView.postInvalidate();
//        }
    }

    public ViewGroup mWebViewParent = null;

    @Override
    public void onStop() {
        MyWebView mWebView = getMyWebView();
        Log.i("LIHKGWebView", "onStop");
        mWebViewParent = null;
        if(mWebView != null && mWebView.getParent() != null){
            mWebViewParent = (ViewGroup) mWebView.getParent();
        }

//        mWebView = null;

//        this.requireView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                killWebView();
//            }
//        },300);




//        if(mWebViewParent == null && mWebView != null) {
//            mWebView.setEnabled(false);
//            mWebViewParent = ((ViewGroup) mWebView.getParent());
//
//
//            if (mWebViewParent != null) {
//                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
//            }
//        }
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MyWebView mWebView = getMyWebView();
        Log.i("LIHKGWebView", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null && mWebView != null){
            Bundle webViewBundle =  savedInstanceState.getBundle("webViewState");
            if(webViewBundle != null){
                mWebView.restoreState(webViewBundle);
            }
        }
    }



    private ProgressBar progressBar = null;

    @SuppressLint("SetJavaScriptEnabled")
    public void setupWebViewSettings(){
        MyWebView mWebView = getMyWebView();

        if(getActivity() == null || mWebView == null) return;



//        mWebView.setMixedContentAllowed(true); // cdn.lihkg.com
//        mWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = mWebView.getSettings();

//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        mWebView.setMixedContentMode();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mWebView.setTouchscreenBlocksFocus(false); //?
//        }
//        mWebView.setFilterTouchesWhenObscured(false); //?
//        mWebView.setFocusableInTouchMode(false); // ?

        mWebView.disableFlash(this.requireActivity().getWindow());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setGeolocationEnabled(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        webSettings.setNeedInitialFocus(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webSettings.setOffscreenPreRaster(false);
        }
//        webSettings.setSupportMultipleWindows(false);
//        webSettings.setSupportZoom(false);

        webSettings.setDomStorageEnabled(true); // DOMストレージAPIを有効にする
//        webSettings.setSavePassword(false); // フォームに入力した内容を記憶しないようにする
//        webSettings.setSaveFormData(false); // フォームに入力した内容を記憶しないようにする
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
//        mWebView.setVerticalScrollbarOverlay(true);
//        mWebView.setHorizontalScrollbarOverlay(true);
//        webSettings.setBuiltInZoomControls(false); // ズームを無効にする

        // Samsung S23 Ultra
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36");

        webSettings.setJavaScriptEnabled(true);
//        mWebView.clearView();
        mWebView.clearFormData();
        mWebView.clearMatches();
        mWebView.clearSslPreferences();
//        mWebView.clearCache(true); // キャッシュ・履歴の削除
        mWebView.clearHistory(); // キャッシュ・履歴の削除



        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            }catch  (Throwable ignored1){
//
//                try{
//                    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//                }catch (Throwable ignored2) {
//
//                }
//
//            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        MyWebView mWebView = getMyWebView();
        if(getActivity() == null) return null;

        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if(imgHandler == null){
            imgHandler = new ImageHandler(){
                {
                    this.mContext= getActivity().getApplicationContext();
                    this.mActivity = getActivity();
                    this.mFragment = MainFragment.this;
                }
            };
        }

        View view = inflater.inflate(R.layout.fragment_main, container, false);

//        mWebView = (MyWebView) view.findViewById(R.id.webview);

        mWebViewRef = MyWebViewRef.createWebViewRef((MyWebView) view.findViewById(R.id.webview));

        MyWebView mWebView = getMyWebView();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

//        mWebView.restoreState( ((MainActivity) requireActivity()).webViewBundle);
//
//        try{
//
//            Log.i("SSX", mWebView.getUrl()+"");
//        }catch (Throwable e){
//
//        }

//        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().getOnBackInvokedDispatcher().registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, new OnBackInvokedCallback() {
                @Override
                public void onBackInvoked() {
                    MyWebView mWebView = getMyWebView();

                    if(mWebView!=null && mWebView.canGoBack()) mWebView.goBack();
                    else if(getActivity() != null) requireActivity().onBackPressed();
                }
            });
        }

//        ViewCompat.setOnReceiveContentListener(mWebView, mWebView.MIME_TYPES, MyReceiver())


        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);


        swipeRefreshLayout.setEnabled(true);

        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {

                MyWebView mWebView = getMyWebView();
                if(getActivity() == null || mWebView == null) return false;
//                if(mWebView == null) return false;
////                Log.i("canChildScrollUp", mWebView.capturePullDown+"");
////                if(!mWebView.capturePullDown) return false;
                return mWebView.canScrollVertically(-1);
            }
        });
        registerForContextMenu(mWebView);

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        }

        if(savedInstanceState == null) {
            mWebView.setActivity(((AppCompatActivity) getActivity()));
            mWebView.setFragment(this);
            mWebView.setDefaultWebViewClient(mWebView.new BWebViewClient());
            mWebView.setDefaultWebChromeClient(mWebView.new BWebChromeClient());
            mWebView.setDownloadListener(mWebView.new ADownloadListener());

            mWebView.wxStartSafeBrowsing();



            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    MyWebView mWebView = getMyWebView();

                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    mWebView.clearCache(true);
                    mWebView.reload();

                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                }
            });

            mWebView.blockAds(new MyWebView.AdBlocker() {
                {
                    this.initAdsList(getResources().openRawResource(R.raw.pgl_yoyo_org));
                }
            });

            mWebView.setListener((Activity) requireActivity(), this);

            setupWebViewSettings();

        }

        if(mWebView.getUrl() == null || mWebView.getUrl().isEmpty()){

            String requestedUrl = getViewModel().webViewURL;
            if(requestedUrl != null) {
                mWebView.loadUrl(requestedUrl);
            }else{
                mWebView.loadUrl("https://www.lihkg.com/");
            }

        }

        mMessage= (TextView) view.findViewById(R.id.message);
//        mMessage.setText("1223");
        startMemoryMonitor(1024 * 1024);
        view.post(new Runnable() {
            @Override
            public void run() {
                MyWebView mWebView = getMyWebView();

                if(getActivity() == null || mWebView == null) return;
                refreshColors();
            }
        });
        return view;
    }

    public TextView mMessage;

    @Override
    public void onResume() {
        MyWebView mWebView = getMyWebView();
        super.onResume();
        Log.i("LIHKGWebView","onResume");
        if(mWebView!=null){
            mWebView.onResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mWebView.releasePointerCapture();
            }
            mWebView.invalidate();
        }
        refreshColors();
    }

    @Override
    public void onPause() {
        MyWebView mWebView = getMyWebView();
        Log.i("LIHKGWebView","onPause");
        if(mWebView!=null)mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        MyWebView mWebView = getMyWebView();
        Log.i("LIHKGWebView","onDestroyView");

        if(mWebView!=null) {
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIHKGWebView","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

        if(getActivity() == null) return;

        if(progressBar != null)
        progressBar.setVisibility(View.VISIBLE);

    }


    //
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        mWebView.onActivityResult(requestCode, resultCode, intent);
//        // ...
//    }
//
//    @Override
//    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) {

        if(getActivity() == null) return;

//        Log.i("onPageFinished",url);
        getViewModel().webViewURL = url;

        if(progressBar != null) progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if(getActivity() == null) return;

        if(progressBar != null) {
            progressBar.setProgress(newProgress);
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

        if(getActivity() == null) return;

    }

    @Override
    public void onDownloadRequested(String url, String mimetype, long contentLength, String contentDisposition, String userAgent) {


        if(getActivity() == null) return;

        int PERMISSION_REQUEST_CODE = 1;

        // Check for storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

        if(url.startsWith("blob:")){
            downloadBlob(url, userAgent, contentDisposition, mimetype);
//            Log.i("WWS", mimetype);

        }else {

            downloadFile(url, userAgent, contentDisposition, mimetype);
        }


//
//        String fileName = suggestedFilename;
//
//        // String fileName = "image_" + System.currentTimeMillis() + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype);
//
//
//        // Create a new file in the Downloads directory
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//
//        // Download the file
//        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setTitle(fileName);
//        request.setDestinationUri(Uri.fromFile(file));
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        downloadManager.enqueue(request);
//
//        // Show a toast message indicating the file was saved
//        Toast.makeText(requireContext(), "Image saved to Downloads folder", Toast.LENGTH_SHORT).show();



    }

    private void downloadFile(String url, String userAgent, String contentDisposition, String mimetype) {

        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
        }catch (Throwable err){
            Log.e("LIHKGWebView","Download Failed", err);
        }
        if(request == null) return;
        request.setMimeType(mimetype);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        request.addRequestHeader("User-Agent", userAgent);
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    int downloadId = 0;
    public HashMap<Integer, String> downloadList = new HashMap<Integer, String>();



    public String getBase64StringFromBlobUrl(String blobUrl,String mimeType) {
        if(blobUrl.startsWith("blob")){
//            fileMimeType = mimeType;
            return "javascript: (()=>{var xhr = new XMLHttpRequest();" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobFile = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            xec2D.getBase64FromBlobData(base64data);" +
                    "        };" +
                    "        reader.readAsDataURL(blobFile);console.log('zz',55);" +
                    "    } " +
                    "        console.log('ss', this.status, this.response);" +
                    "};" +
                    "xhr.responseType = 'arraybuffer';" +
                    "xhr.open('GET', '"+ blobUrl +"');" +
//                    "xhr.setRequestHeader('Content-type','" + mimeType +";charset=UTF-8');" +
                    "xhr.send();console.log('tt', 33); setTimeout(()=>{console.log(xhr.status)},500) })();";
        }
        return "javascript: console.log('It is not a Blob URL');";
    }
    private void downloadBlob(String url, String userAgent, String contentDisposition, String mimetype) {

//        String fileName = "file_" + System.currentTimeMillis() + ".pdf"; // Customize this based on your file type and desired name
//        String mimeType = "application/pdf"; // Set MIME type based on the file type you want to download

        if(getActivity() == null) return;

        downloadId++;
        int currentId = downloadId;
        downloadList.put(currentId, url+"\n"+userAgent+"\n"+contentDisposition+"\n"+mimetype);
        Log.i("downloadBlob", url);
        Log.e("LIHKGWebView", "blobUrl cannot be downloaded");

//        if(mWebView != null  ) {
////            String newUrl = getBase64StringFromBlobUrl(url, mimetype);
////            Log.i("downloadBlob-", newUrl);
//
////            mWebView.loadUrl(newUrl);
////            mWebView.evaluateJavascript(newUrl, null);
//
//            mWebView.loadUrl("javascript: ((url)=>{fetch(url).then(r => r.blob()).then(r=>console.log(r));})('"+ url +"')");
//
//        }
        /*
        String js = "javascript: fetch('" + url + "')" +
                ".then(resp => resp.blob())" +
                ".then(blob => {" +
                "  let reader = new FileReader();" +
                "  reader.onloadend = function() {" +
                "    let base64data = reader.result;" +
                "    xec2D.onBlobDataReceived("+ currentId +", base64data);" +
                "  };" +
                "  reader.readAsDataURL(blob);" +
                "});";

         */
//
//        String mimeType = "application/octet-stream"; // Set MIME type based on the file type you want to download
//
//
//        String js = "javascript: (()=>{let xhr = new XMLHttpRequest();" +
//                "xhr.open('GET', '"+ url +"', true);" +
//                "xhr.setRequestHeader('Content-type','" + mimeType +";charset=UTF-8');" +
//                "xhr.responseType = 'blob';" +
//                "xhr.onload = function(e) {" +
//                "    if (this.status == 200) {" +
//                "        let blobFile = this.response;console.log(1);" +
//                "        let reader = new FileReader();console.log(2);" +
//                "        reader.onloadend = function() {" +
//                "            base64data = reader.result;" +
//                "            Android.getBase64FromBlobData(base64data);" +
//                "        };" +
//                "        reader.readAsDataURL(blobFile);console.log(4);" +
//                "    }" +
//                "};" +
//                "xhr.send();})();";


//        mWebView.evaluateJavascript(js, null);
    }

    @Override
    public void onExternalPageRequest(String url) {


        if(getActivity() == null) return;

    }

    /* ----- */



    public void startMemoryMonitor(final long memoryThreshold) {

        final Runtime runtime = Runtime.getRuntime();
        final long lastUsedMemoryArr[] = new long[]{0};

        // Define a Runnable that updates the message text view
        final Runnable updateMessage = new Runnable() {
            @Override
            public void run() {
                MyWebView mWebView = getMyWebView();
                if(getActivity() == null || mMessage == null || mWebView == null) return;
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                if (Math.abs(usedMemory - lastUsedMemoryArr[0]) >= memoryThreshold) {
                    lastUsedMemoryArr[0] = usedMemory;
                    String messageText = String.format(Locale.ROOT, "Memory usage: %d KB", usedMemory / 1024);
                    mMessage.setText(messageText);
                }

                // Schedule the next update
                mMessage.postDelayed(this, 300);
            }
        };

        updateMessage.run();

        // Schedule the first update
        mMessage.postDelayed(updateMessage, 300);
    }




    public void refreshColors(){

//        boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        boolean isDarkMode = getViewModel().isDarkMode;
        AppCompatActivity mActivity =  ((AppCompatActivity)getActivity());
        if(mActivity == null) return;

        TextView mMessage= (TextView) mActivity.findViewById(R.id.message);
        if(mMessage==null) return;


        Context context = mActivity.getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

            Resources res = context.getResources();
            Configuration configuration = new Configuration(res.getConfiguration());
            int filter = res.getConfiguration().uiMode & ~Configuration.UI_MODE_NIGHT_MASK;

            if (!getViewModel().isDarkMode) {
                configuration.uiMode = Configuration.UI_MODE_NIGHT_NO | filter;
            } else {
                configuration.uiMode = Configuration.UI_MODE_NIGHT_YES | filter;
            }

            context = context.createConfigurationContext(configuration);
        }


        mMessage.setTextColor(ContextCompat.getColor(context, R.color.lihkg_text_color));

        mMessage.getRootView().setBackgroundColor(ContextCompat.getColor(context, R.color.navbar_background_color));


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Window window = mActivity.getWindow();
            if(window != null){

                View view = window.getDecorView();
                if(view != null){

                    WindowInsetsControllerCompat wic =  WindowCompat.getInsetsController(window,view);
                    if (isDarkMode) {
                        wic.setAppearanceLightStatusBars(false);
                    } else {
                        wic.setAppearanceLightStatusBars(true);
                    }
                }
            }

        }

        setStatusBarColor(ContextCompat.getColor(context, R.color.navbar_background_color));
        setNavigationBarColor(ContextCompat.getColor(context, R.color.navbar_background_color));

    }

    public MainViewModel getViewModel (){
        MainActivity mainActivity = (MainActivity) requireActivity();
        return mainActivity.mViewModel;
    }

    public void setDarkMode(boolean isDarkMode, boolean forced){

        AppCompatActivity mActivity =  ((AppCompatActivity)getActivity());
        if(mActivity == null) return;
        androidx.appcompat.app.AppCompatDelegate delegate = mActivity.getDelegate();

        isDarkMode = getViewModel().isDarkMode;

        Log.i("isDarkMode",isDarkMode+"");

        if (isDarkMode) {
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES || forced) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                delegate.applyDayNight();
            }
//                                Log.i("MODE_NIGHT","MODE_NIGHT_YES");
        } else  {

            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO || forced) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                delegate.applyDayNight();
            }
//                                Log.i("MODE_NIGHT","MODE_NIGHT_NO");
        }
        refreshColors();
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Activity mActivity = getActivity();
            if(mActivity == null) return;
            Window window = mActivity.getWindow();
            if (window != null) {
                window.setStatusBarColor(color);
            }
        }
    }

    private void setNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Activity mActivity = getActivity();
            if(mActivity == null) return;
            Window window = mActivity.getWindow();
            if (window != null) {
                window.setNavigationBarColor(color);
            }
        }
    }

    public Configuration previousConfig = null;


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        MyWebView mWebView = getMyWebView();

        super.onConfigurationChanged(newConfig);
        boolean isDarkMode = getViewModel().isDarkMode;

        if(previousConfig !=null &&  previousConfig.orientation != newConfig.orientation){

        }else{

            int nightModeFlags = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {

                isDarkMode = false;

            } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {

                isDarkMode = true;
            }
        }
        previousConfig = newConfig;

        getViewModel().setDarkMode(isDarkMode);
        mWebView.setDarkMode(isDarkMode);
        setDarkMode(isDarkMode, false);



    }

    public ImageHandler.ContextDomData webViewContextDomData = null;



    // Add this to your activity or fragment to handle the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        WebView webView = (WebView) v;
        WebView.HitTestResult result = webView.getHitTestResult();

        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {


            if(webViewContextDomData!=null){


                View hv =  LayoutInflater.from(getContext()).inflate(R.layout.context_menu_header, null);
                if(webViewContextDomData.img != null)
                    ((TextView) hv.findViewById(R.id.headerLine1)).setText("Image: "  + webViewContextDomData.img );
                else
                    ((TextView) hv.findViewById(R.id.headerLine1)).setText("Image: null"  );
                if(webViewContextDomData.link != null)
                    ((TextView) hv.findViewById(R.id.headerLine2)).setText("Link: "  + webViewContextDomData.link );
                else
                    ((TextView) hv.findViewById(R.id.headerLine2)).setText("Link: null"  );
                if(webViewContextDomData.img != null){
                    ((Button) hv.findViewById(R.id.headerLeft)).setText("I");
                }else if(webViewContextDomData.link != null){
                    ((Button) hv.findViewById(R.id.headerLeft)).setText("L");
                }else {

                    ((Button) hv.findViewById(R.id.headerLeft)).setText("?");
                }
                menu.setHeaderView(hv);


                if(webViewContextDomData.img!=null){

                    menu.add(0, 0x20, 1, "Save image link");
                    menu.add(1, 0x40, 1, "Share image link");
                }
                if(webViewContextDomData.link!=null){

                    menu.add(0, 0x60, 2, "Save hyperlink");
                    menu.add(2, 0x80, 2, "Share hyperlink");
                }

            }


//            menu.add(0, 0x200, 0, "Save image - Not Yet Supported");
//            menu.add(0, 0x400, 0, "Share image - Not Yet Supported");
        } else {
//            menu.setHeaderTitle("Options");
//            menu.add(0, 0x800, 0,"Not Yet Supported");

        }
        menu.setGroupEnabled(0, false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        MyWebView mWebView = getMyWebView();
        if(mWebView == null || webViewContextDomData ==null) return super.onContextItemSelected(item);


        if(!item.isEnabled()) return super.onContextItemSelected(item);


        if (item.getItemId()==0x20) {
            // Code to save the image
            if(webViewContextDomData.img!=null) imgHandler.onSaveImageSelected(webViewContextDomData.img);

            return true;
        } else if (item.getItemId()==0x40) {
            // Code to share the image
            if(webViewContextDomData.img!=null) imgHandler.onShareImageSelected(webViewContextDomData.img);

            return true;
        } else if (item.getItemId()==0x60) {
            // Code to save the link
            if(webViewContextDomData.link!=null) imgHandler.onSaveImageSelected(webViewContextDomData.link);

            return true;
        } else if (item.getItemId()==0x80) {
            // Code to share the link
            if(webViewContextDomData.link!=null) imgHandler.onShareImageSelected(webViewContextDomData.link);

            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    public boolean fragmentDead = false;

    public void killWebView() {
        MyWebView mWebView = getMyWebView();
        if(mWebView != null) {
            mWebView.destroy();
            mWebView = null;
            mWebViewRef = null;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try{

                    if(getActivity() == null) return;

                    MainActivity mActivity = (MainActivity) requireActivity();
                    mActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(MainFragment .this)
                            .commitAllowingStateLoss();

                    fragmentDead = true;

                    mActivity.fragmentKilled();
                }catch (Throwable ignored){
                    // unexpected error, e.g.  not attached to an activity.
                }


            }
        },1);

    }



}