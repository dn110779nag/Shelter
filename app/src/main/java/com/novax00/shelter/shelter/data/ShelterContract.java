package com.novax00.shelter.shelter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Samsung on 9/24/2017.
 */

public class ShelterContract {

    private ShelterContract() {}

    public static final String LOG_TAG = "shelter";

    public static final String CONTENT_AUTHORITY = "com.novax00.shelter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

    public static final String CREATE_TABLE =
            "CREATE TABLE "+ ItemEntry.TABLE_NAME
                    +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ItemEntry.COLUMN_ITEM_NAME+" TEXT, "
                    + ItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, "
                    + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER, "
                    + ItemEntry.COLUMN_ITEM_PRICE+ " NUMBER,"
                    + ItemEntry.COLUMN_ITEM_IMAGE_PATH+ " TEXT);";

    public static final String DELETE_TABLE = "DELETE FROM "+ ItemEntry.TABLE_NAME+";";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ ItemEntry.TABLE_NAME+";";


    public static final class ItemEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;


        public static final String TABLE_NAME = "ITEMS";
        public static final String COLUMN_ITEM_NAME = "NAME";
        public static final String COLUMN_ITEM_DESCRIPTION = "DESCRIPTION";
        public static final String COLUMN_ITEM_QUANTITY = "QUANTITY";
        public static final String COLUMN_ITEM_PRICE = "PRICE";
        public static final String COLUMN_ITEM_IMAGE_PATH = "IMAGE_PATH";


    }
}
