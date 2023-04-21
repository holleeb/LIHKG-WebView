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

package com.culefa.android.LIHKGWebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.culefa.android.LIHKGWebView.ui.main.MainFragment;
import com.culefa.android.LIHKGWebView.ui.main.MainViewModel;
import com.culefa.android.LIHKGWebView.ui.webview.MyWebView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    public WeakReference<Bundle> webViewBundleRef = new WeakReference<>( new Bundle());

    public String requestedUrl = null;
    public MainViewModel mViewModel = null;
    public Menu systemSelectionMenu = null;
    public int copyActionId = -1;

    public static void replaceToNewFragment(FragmentManager fm){
        fm.beginTransaction()
        .replace(R.id.container, MainFragment.newInstance())
        .commitNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity","onCreate");


        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        if(savedInstanceState == null){


            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data != null) {
                String text = data.toString();
                if(!text.isEmpty()){
                    if (URLUtil.isValidUrl(text)) {
                        requestedUrl = text;
                        mViewModel.webViewURL = requestedUrl;
                    }
                }
                // Do something with the text data...
            }

        }


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            isFragmentKilled = false;
            replaceToNewFragment(getSupportFragmentManager());
        }



//        refreshColors();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        MyWebView mWebView = (MyWebView) findViewById(R.id.webview);
        if(mWebView!=null){
            mWebView.onActivityResult(requestCode, resultCode, intent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int PERMISSION_REQUEST_CODE = 1;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyWebView mWebView = (MyWebView) findViewById(R.id.webview);
                mWebView.reload();
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        MyWebView mWebView = (MyWebView) findViewById(R.id.webview);
        if(mWebView!=null && mWebView.canGoBack()){
            mWebView.goBack();
        }else{



            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap back twice to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

//            super.onBackPressed();
        }
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState )
    {
        Log.i("MainActivity","onSaveInstanceState");
        MyWebView mWebView = (MyWebView) findViewById(R.id.webview);

        Bundle webViewBundle = webViewBundleRef.get();
        if(webViewBundle == null) {
            webViewBundleRef = new WeakReference<>(new Bundle());
            webViewBundle = webViewBundleRef.get();
        } else if(mWebView != null){
            webViewBundle.clear();
        }
        if(webViewBundle != null) {
            if(mWebView != null) {
                mWebView.saveState(webViewBundle);
            }
            outState.putBundle("webViewState", webViewBundle);
        }else{
            outState.putBundle("webViewState", null);
        }

        super.onSaveInstanceState(outState);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {

        Log.i("MainActivity","onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        MyWebView mWebView = (MyWebView) findViewById(R.id.webview);
        if(savedInstanceState != null && mWebView != null){
            Bundle webViewBundle =  savedInstanceState.getBundle("webViewState");
            if(webViewBundle != null){
                mWebView.restoreState(webViewBundle);
            }
        }

    }

    boolean isFragmentKilled = false;

    public void fragmentKilled(){

        isFragmentKilled = true;

        Log.i("MainActivity","fragmentKilled");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity","onStart");
    }

    @Override
    protected void onResumeFragments() {
        // 　onResumeFragments()は、
        // そのActivityの持つ全てのFragmentが開始された後に呼び出されるライフサイクルイベントです。
        // 複数のFragmentを横断して処理する必要がある場合は、このコールバックイベントで行います。
        super.onResumeFragments();
        FragmentManager fm = getSupportFragmentManager();
        int nosOfFragments = fm.getFragments().size();
        Log.i("MainActivity","onResumeFragments - "+ nosOfFragments);
        if(isFragmentKilled && nosOfFragments == 0 ){
            isFragmentKilled = false;

            replaceToNewFragment(fm);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","onResume");
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("MainActivity","onDetachedFromWindow");

        FragmentManager fm = getSupportFragmentManager();
        int nosOfFragments = fm.getFragments().size();
        if(!isFragmentKilled && nosOfFragments == 1 ){
            try {
                MainFragment mainFragment = (MainFragment) fm.getFragments().get(0);
                mainFragment.killWebView();
            }catch (Throwable ignored){

            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // MyWebView mWebView = (MyWebView) findViewById(R.id.webview);

        Log.i("MainActivity","onLowMemory");
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        Log.i("MainActivity", "onActionModeStarted");
        super.onActionModeStarted(mode);
        systemSelectionMenu = mode.getMenu(); // keep a reference to the menu
//        MenuItem copyItem = systemSelectionMenu.getItem(0); // fetch any menu items you want
//        copyActionId = copyItem.getItemId(); // store reference to each item you want to manually trigger
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                systemSelectionMenu.clear();
//                systemSelectionMenu.close();
//            }
//        }, 1000);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        Log.i("MainActivity", "onActionModeFinished");
        systemSelectionMenu = null;
        super.onActionModeFinished(mode);
    }

}