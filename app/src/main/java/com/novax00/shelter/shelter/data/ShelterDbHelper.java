package com.novax00.shelter.shelter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by dn110 on 28.09.2017.
 */

public class ShelterDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Shelter.db";

    public ShelterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ShelterContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ShelterContract.DROP_TABLE);
        Log.v(ShelterContract.LOG_TAG, ShelterContract.DROP_TABLE + "==> OK");
        sqLiteDatabase.execSQL(ShelterContract.CREATE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public static ContentValues createValues(
            String name,
            String description,
            long quantity,
            BigDecimal price,
            String imagePath){
        ContentValues v = new ContentValues();
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_NAME, name);
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_DESCRIPTION, description);
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_PRICE, price.toString());
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH, imagePath);
        return v;
    }
}
