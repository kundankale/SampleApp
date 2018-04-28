package com.assignment_kundan_kale.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class DbContract {


    public static final String AUTHORITY = "com.capstoneproject";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_FILESINFO = "fileinfotable";
    public static final String PATH_SETTING = "settingtable";


    public static final class DataEntry implements BaseColumns {

        public static final Uri CONTENT_URI_FILES =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILESINFO).build();



        public static final String TABLE_FILEINFO_TABLE = "fileinfotable";
        public static final String TABLE_SETTING_TABLE = "settingtable";


        public static final String COLUMN_ID = "id";

        public static final String COLUMN_ITEM_NAME = "itemname";
        public static final String COLUMN_ITEM_DISCRIPTION = "itemdiscription";
        public static final String COLUMN_ITEM_LOCATION = "itemlocation";
        public static final String COLUMN_ITEM_COST = "itemcost";
        public static final String COLUMN_IMAGEFOLDER_PATH = "imagedirpath";




        /*---------------------------*/



    }

}