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

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class DropDataProvider extends ContentProvider {


    private static final String CLASS_NAME = "DropDataProvider";

    public static final String AUTHORITY = "com.culefa.android.LIHKGWebView";

    private Context mContext;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        // Initialize the provider here
        mContext = getContext();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, "img/*", 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        MatrixCursor c = null;


        String fileLocation = getContext().getFilesDir() + File.separator + uri.getLastPathSegment();

        File file = new File(fileLocation);

        long time = System.currentTimeMillis();

        c = new MatrixCursor(new String[] { "_id", "_data", "orientation", "mime_type", "datetaken", "_display_name" });

        c.addRow(new Object[] { 0,  file, 0, "image/jpeg", time, uri.getLastPathSegment() });

        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    // Override other methods of ContentProvider here


    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        // Get the file path from the URI
        String filePath = uri.getLastPathSegment();

        switch (uriMatcher.match(uri)) {

            case 1:

                // Create a new file object from the file path
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath);

                ParcelFileDescriptor image = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                return image;

            default:
                Log.i("TT", "Unsupported uri: '" + uri + "'.");
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }
    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return null;
    }
}
