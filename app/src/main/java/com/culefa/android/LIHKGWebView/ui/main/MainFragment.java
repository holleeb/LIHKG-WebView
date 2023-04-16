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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

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
import android.net.http.SslError;
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
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.culefa.android.LIHKGWebView.MainActivity;
import com.culefa.android.LIHKGWebView.R;
import com.culefa.android.LIHKGWebView.ui.webview.MyWebView;

import java.io.File;
import java.util.Locale;


public class MainFragment extends Fragment implements MyWebView.Listener {

    MyWebView mWebView = null;
    public Boolean overrideDarkMode = null;
    public Boolean currentDarkMode = null;

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
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState )
//    {
//        super.onSaveInstanceState(outState);
//        if(mWebView != null) {
//            mWebView.saveState(outState);
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState)
//    {
//        super.onRestoreInstanceState(savedInstanceState);
//        if(mWebView != null) {
//            mWebView.restoreState(savedInstanceState);
//        }
//    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("LIHKGWebView", "onViewStateRestored");
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
        super.onStart();
        Log.i("LIHKGWebView", "onStart");
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
        Log.i("LIHKGWebView", "onStop");

//        mWebView = null;

        this.requireView().postDelayed(new Runnable() {
            @Override
            public void run() {
                killWebView();
            }
        },300);

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
        super.onViewCreated(view, savedInstanceState);
        Log.i("LIHKGWebView", "onViewCreated");
    }



    private ProgressBar progressBar = null;

    @SuppressLint("SetJavaScriptEnabled")
    public void setupWebViewSettings(){



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
        webSettings.setSupportMultipleWindows(false);
        webSettings.setSupportZoom(false);

        webSettings.setDomStorageEnabled(true); // DOMストレージAPIを有効にする
//        webSettings.setSavePassword(false); // フォームに入力した内容を記憶しないようにする
//        webSettings.setSaveFormData(false); // フォームに入力した内容を記憶しないようにする
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
//        mWebView.setVerticalScrollbarOverlay(true);
//        mWebView.setHorizontalScrollbarOverlay(true);
        webSettings.setBuiltInZoomControls(false); // ズームを無効にする

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

        mWebView = (MyWebView) view.findViewById(R.id.webview);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);


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
                    if(mWebView.canGoBack()) mWebView.goBack();
                    else requireActivity().onBackPressed();
                }
            });
        }

//        ViewCompat.setOnReceiveContentListener(mWebView, mWebView.MIME_TYPES, MyReceiver())


        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);


        swipeRefreshLayout.setEnabled(true);

        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
//                if(mWebView == null) return false;
////                Log.i("canChildScrollUp", mWebView.capturePullDown+"");
////                if(!mWebView.capturePullDown) return false;
                return mWebView.canScrollVertically(-1);
            }
        });
        registerForContextMenu(mWebView);
        if(savedInstanceState == null) {
            mWebView.setActivity(((AppCompatActivity) getActivity()));
            mWebView.setFragment(this);
            mWebView.setDefaultWebViewClient(mWebView.new BWebViewClient());
            mWebView.setDefaultWebChromeClient(mWebView.new BWebChromeClient());
            mWebView.setDownloadListener(mWebView.new ADownloadListener());



            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

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
                refreshColors();
            }
        });
        return view;
    }

    public TextView mMessage;

    @Override
    public void onResume() {
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
        Log.i("LIHKGWebView","onPause");
        if(mWebView!=null)mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIHKGWebView","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIHKGWebView","onDestroy");
        if(mWebView!=null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

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

//        Log.i("onPageFinished",url);
        getViewModel().webViewURL = url;

        if(progressBar != null) progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if(progressBar != null) {
            progressBar.setProgress(newProgress);
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String mimetype, long contentLength, String contentDisposition, String userAgent) {

        int PERMISSION_REQUEST_CODE = 1;

        // Check for storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
        downloadFile(url, userAgent, contentDisposition, mimetype);


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

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

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

    @Override
    public void onExternalPageRequest(String url) { }

    /* ----- */



    public void startMemoryMonitor(final long memoryThreshold) {

        final Runtime runtime = Runtime.getRuntime();
        final long lastUsedMemoryArr[] = new long[]{0};

        // Define a Runnable that updates the message text view
        final Runnable updateMessage = new Runnable() {
            @Override
            public void run() {
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

    public void killWebView() {
        mWebView = null;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try{

                    MainActivity mActivity = (MainActivity) requireActivity();
                    mActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(MainFragment .this)
                            .commitAllowingStateLoss();

                    mActivity.fragmentKilled();
                }catch (Throwable ignored){
                    // unexpected error, e.g.  not attached to an activity.
                }


            }
        },1);

    }
}