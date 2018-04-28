/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.assignment_kundan_kale.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ID;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_NAME;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.TABLE_FILEINFO_TABLE;


public class DataContentProvider extends ContentProvider {


    private DbHelper mTaskDbHelper;

    public static final int FILES_ALL = 100;
    public static final int FILES_ID = 101;
    public static final int FILES_VALUE = 102;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {


        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_FILESINFO, FILES_ALL);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_FILESINFO + "/#", FILES_ID);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_FILESINFO + "/*", FILES_VALUE);


        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_SETTING, FILES_ALL);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_SETTING + "/#", FILES_ID);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH_SETTING + "/*", FILES_VALUE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mTaskDbHelper = new DbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();


        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        String value = uri.getPathSegments().get(0);

        switch (match) {
            case FILES_ALL:

                if (value.equals(TABLE_FILEINFO_TABLE)) {
                    long id = db.insert(TABLE_FILEINFO_TABLE, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(DbContract.DataEntry.CONTENT_URI_FILES, id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();


        int match = sUriMatcher.match(uri);
        Cursor retCursor = null;
        String pointvalue = uri.getPathSegments().get(0);

        switch (match) {
            // Query for the tasks directory
            case FILES_ALL:

                if (pointvalue.equals(TABLE_FILEINFO_TABLE)) {

                    retCursor = db.query(TABLE_FILEINFO_TABLE,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null);
                }
                break;
            case FILES_ID:

                if (pointvalue.equals(TABLE_FILEINFO_TABLE)) {
                    String value = uri.getPathSegments().get(1);

                    String mSelection = "id=?";
                    String[] mSelectionargs = new String[]{value};


                    retCursor = db.query(TABLE_FILEINFO_TABLE,
                            projection,
                            mSelection,
                            mSelectionargs,
                            null,
                            null,
                            null);

                }

                break;
            case FILES_VALUE:

                String value1 = uri.getPathSegments().get(1);


                String mSelection1 = COLUMN_ITEM_NAME + "=?";
                String[] mSelectionargs1 = new String[]{value1};


                retCursor = db.query(TABLE_FILEINFO_TABLE,
                        projection,
                        mSelection1,
                        mSelectionargs1,
                        null,
                        null,
                        null);


                break;
            default:
                if (pointvalue.equals(TABLE_FILEINFO_TABLE)) {

                    retCursor = db.query(TABLE_FILEINFO_TABLE,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null);
                }

        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);


        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {


        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        String pointvalue = uri.getPathSegments().get(0);

        if (pointvalue.equals(TABLE_FILEINFO_TABLE)) {

            return db.delete(TABLE_FILEINFO_TABLE, COLUMN_ID + " = ?", selectionArgs);
        }  else {
            return 0;
        }


        // throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        String pointvalue = uri.getPathSegments().get(0);

        if (pointvalue.equals(TABLE_FILEINFO_TABLE)) {

            return db.update(TABLE_FILEINFO_TABLE, values, COLUMN_ID + " = ?", selectionArgs);
        }  else {
            return 0;
        }


    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}