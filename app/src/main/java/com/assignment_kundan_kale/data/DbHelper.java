
package com.assignment_kundan_kale.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "data.db";


    private static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + DbContract.DataEntry.TABLE_FILEINFO_TABLE + " (" +


                        DbContract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DbContract.DataEntry.COLUMN_ID + " INTEGER, " +
                        DbContract.DataEntry.COLUMN_ITEM_NAME + " TEXT, " +

                        DbContract.DataEntry.COLUMN_ITEM_DISCRIPTION + " TEXT, " +
                        DbContract.DataEntry.COLUMN_ITEM_COST + " TEXT, " +

                        DbContract.DataEntry.COLUMN_ITEM_LOCATION + " TEXT, " +
                        DbContract.DataEntry.COLUMN_IMAGEFOLDER_PATH + " TEXT" + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);





    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.DataEntry.TABLE_FILEINFO_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.DataEntry.TABLE_SETTING_TABLE);
        onCreate(sqLiteDatabase);
    }
}