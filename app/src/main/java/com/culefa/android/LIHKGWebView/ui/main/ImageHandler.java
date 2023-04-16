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

// Add these imports to your class
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ImageHandler {

    public Context mContext;
    public Activity mActivity;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public MainFragment mFragment;


    private String getImageUrlFromHtmlAnchor(WebView webView, String imageUrl) {
        // Get the HTML code of the page currently loaded in the WebView
        String htmlCode = webView.getOriginalUrl();


        // Check if the tapped element is an <img> tag or an <a> tag
        if (htmlCode.contains("<img") && htmlCode.contains("<a")) {
            // Find the <img> tag that has the same URL as the tapped image
            int index = htmlCode.indexOf(imageUrl);
            if (index >= 0) {
                int startIndex = htmlCode.lastIndexOf("<img", index);
                if (startIndex >= 0) {
                    int endIndex = htmlCode.indexOf(">", startIndex);
                    if (endIndex >= 0) {
                        String imgTag = htmlCode.substring(startIndex, endIndex + 1);
                        int srcIndex = imgTag.indexOf("src=");
                        if (srcIndex >= 0) {
                            int startQuoteIndex = imgTag.indexOf("\"", srcIndex);
                            int endQuoteIndex = imgTag.indexOf("\"", startQuoteIndex + 1);
                            if (startQuoteIndex >= 0 && endQuoteIndex >= 0) {
                                String imgUrl = imgTag.substring(startQuoteIndex + 1, endQuoteIndex);
                                return imgUrl;
                            }
                        }
                    }
                }
            }

            // If the tapped element is inside an <a> tag, get the URL of the <a> tag instead
            int anchorIndex = htmlCode.indexOf("<a", index);
            if (anchorIndex >= 0) {
                int hrefIndex = htmlCode.indexOf("href=", anchorIndex);
                if (hrefIndex >= 0) {
                    int startQuoteIndex = htmlCode.indexOf("\"", hrefIndex);
                    int endQuoteIndex = htmlCode.indexOf("\"", startQuoteIndex + 1);
                    if (startQuoteIndex >= 0 && endQuoteIndex >= 0) {
                        String hrefUrl = htmlCode.substring(startQuoteIndex + 1, endQuoteIndex);
                        return hrefUrl;
                    }
                }
            }
        }

        // If we couldn't find the URL of the image or the <a> tag, return null
        return null;
    }
    private String getImageUrlFromHtml(WebView webView, String imageUrl) {
        // Get the HTML code of the page currently loaded in the WebView
        String htmlCode = webView.getOriginalUrl();


        // Find the <img> tag that has the same URL as the tapped image
        int index = htmlCode.indexOf(imageUrl);
        if (index >= 0) {
            int startIndex = htmlCode.lastIndexOf("<img", index);
            if (startIndex >= 0) {
                int endIndex = htmlCode.indexOf(">", startIndex);
                if (endIndex >= 0) {
                    String imgTag = htmlCode.substring(startIndex, endIndex + 1);
                    int srcIndex = imgTag.indexOf("src=");
                    if (srcIndex >= 0) {
                        int startQuoteIndex = imgTag.indexOf("\"", srcIndex);
                        int endQuoteIndex = imgTag.indexOf("\"", startQuoteIndex + 1);
                        if (startQuoteIndex >= 0 && endQuoteIndex >= 0) {
                            String imgUrl = imgTag.substring(startQuoteIndex + 1, endQuoteIndex);
                            return imgUrl;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void onSaveImageSelected(String imageUrl){

//
//        // Get the URL of the image
//        WebView.HitTestResult result = webView.getHitTestResult();
//        String imageUrl = null;
//        if (result != null && result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
//            // Get the URL of the image from the <img> tag in the HTML code
//            Log.i("WM2A",result.getExtra());
////            imageUrl = getImageUrlFromHtml(webView, result.getExtra());
//        }else if (result != null && result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
//            // Get the URL of the image from the <img> tag in the HTML code
////            imageUrl = getImageUrlFromHtmlAnchor(webView, result.getExtra());
//            Log.i("WM2B",result.getExtra());
//        }


        mActivity = mFragment.requireActivity();
        mContext = mFragment.requireActivity().getApplicationContext();

        // Show the context menu for the image
        if (imageUrl != null) {


            // Check for the WRITE_EXTERNAL_STORAGE permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(mFragment.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                // If not, request the permission
                ActivityCompat.requestPermissions(mFragment.requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
            } else {
                // Save the image to the device's gallery
                saveImage(imageUrl);
            }


        }



    }
    public void onShareImageSelected(String imageUrl){


        // Get the URL of the image
//        WebView.HitTestResult result = webView.getHitTestResult();
//        String imageUrl = null;
//        if (result != null && result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
//            // Get the URL of the image from the <img> tag in the HTML code
////            imageUrl = getImageUrlFromHtml(webView, result.getExtra());
//
//            Log.i("WM2A",result.getExtra());
//        }else if (result != null && result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
//            // Get the URL of the image from the <img> tag in the HTML code
////            imageUrl = getImageUrlFromHtmlAnchor(webView, result.getExtra());
//
//            Log.i("WM2B",result.getExtra());
//        }
        // Show the context menu for the image
        if (imageUrl != null) {

            // Share the image with other apps
            shareImage(imageUrl);


        }

    }


    // This method saves the image to the device's gallery
    private void saveImage(String imageUrl) {
        // Check that the URL is valid
        if (!URLUtil.isValidUrl(imageUrl)) {
            return;
        }

        // Create a new thread to download the image from the URL
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Download the image as a bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
                            Uri.parse(imageUrl));

                    // Save the image to the device's gallery
                    String fileName = URLUtil.guessFileName(imageUrl, null, null);
                    String galleryPath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    String filePath = galleryPath + "/" + fileName;

                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, fileName,
                            "Image downloaded from " + imageUrl);

                    // Show a toast message to indicate that the image was saved
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext.getApplicationContext(),
                                    "Image saved to gallery", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void copyToClipboard(String text) {

        final ClipboardManager clipboardManager = (ClipboardManager) mContext
                .getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = ClipData.newPlainText("", text);
        clipboardManager.setPrimaryClip(clipData);

    }


    public void shareImageP(final String url) {



        final Context applicationContext = mFragment.requireActivity();
        Uri webpage = Uri.parse(url);
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setDataAndType(webpage, "text/plain");
        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final  PackageManager packageManager = mFragment.requireActivity().getPackageManager();
        List<ResolveInfo> activities = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activities = packageManager.queryIntentActivities(webIntent, PackageManager.MATCH_ALL);
        }else{
            activities = packageManager.queryIntentActivities(webIntent, PackageManager.MATCH_DEFAULT_ONLY);
        }

        // Create a list of package names and app names for each activity that can handle the intent
        List<String> packageNames = new ArrayList<>();
        List<String> appNames = new ArrayList<>();
        appNames.add("<< Just Copy >>");
        packageNames.add("--just-copy--");
        for (ResolveInfo info : activities) {
            String packageName = info.activityInfo.packageName;

            String appName = info.loadLabel(packageManager).toString();
            packageNames.add(packageName);
            appNames.add(appName);
        }
        packageNames.add("--just-copy--");
        appNames.add("<< Just Copy >>");

// Display the list of app names to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.requireActivity());
        builder.setTitle("Select an app");
        builder.setItems(appNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create an explicit intent to launch the selected app
                String packageName = packageNames.get(which);
//                Intent explicitIntent = packageManager.getLaunchIntentForPackage(packageName);
//                explicitIntent.setDataAndType(webpage, "text/plain");
//                explicitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                applicationContext.startActivity(explicitIntent);

                if(packageName.equals("--just-copy--")){

                    copyToClipboard(url);
                    Toast.makeText(mContext, "URL Copied.", Toast.LENGTH_SHORT).show();

                }else {

                    webIntent.setPackage(packageName);
                    mActivity.startActivity(webIntent);
                }


            }
        });// Create the dialog and set its layout parameters
        AlertDialog dialog = builder.create();

// Show the dialog
        dialog.show();

//                Intent chooser = Intent.createChooser(webIntent, "Choose Your Browser");
//                applicationContext.startActivity(chooser);

//                Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                Intent chooser = Intent.createChooser(sendIntent, "Choose Your Browser");
//                if (sendIntent.resolveActivity(mActivity.getPackageManager()) != null) {
//                    mActivity.startActivity(chooser);
//                }


    }

    public void shareImage(String imageUrl){

        if (!URLUtil.isValidUrl(imageUrl)) {
            return;
        }


        final String url =  imageUrl; // Replace with your desired URL


        mActivity = mFragment.requireActivity();
        mContext = mActivity.getApplicationContext();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                shareImageP(url);
            }
        }, 0);





//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage(null); // Replace with the package name of the app you want to use
//
//// Check if there are any apps that can handle this intent
//        List<ResolveInfo> activities =  mActivity.getPackageManager().queryIntentActivities(intent, 0);
//
//
//        Log.i("WWY", activities.size()+"");
//
//        boolean isIntentSafe = activities.size() > 0;
//
//// If there's an app that can handle this intent, start the activity
//        if (isIntentSafe) {
//            mActivity.startActivity(intent);
//        }
    }

    // This method shares the image with other apps
    private void shareImage2(String imageUrl) {
        // Check that the URL is valid
        if (!URLUtil.isValidUrl(imageUrl)) {
            return;
        }

        // Create a new thread to download the image from the URL
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Download the image as a bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
                            Uri.parse(imageUrl));

                    // Share the image with other apps
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpeg");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));

                    // Start the share activity
                    mActivity.startActivity(Intent.createChooser(shareIntent, "Share image with"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    // This method converts a Bitmap to a local Uri
    private Uri getLocalBitmapUri(Bitmap bitmap) {
        // Save the bitmap to a local file
        File file = new File( mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png");
        try (OutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the Uri of the saved file
        return Uri.fromFile(file);
    }

    public static class ContextDomData{
        public int uid;
        public String link;
        public String img;
    }

}
