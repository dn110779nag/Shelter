package com.novax00.shelter.shelter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dn110 on 28.09.2017.
 */

public class ShelterDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Shelter.db";
    private Context context;

    public ShelterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ShelterContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ShelterContract.CREATE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public static ContentValues createValues(
            String name,
            String breed,
            int gender,
            int weight){
        ContentValues v = new ContentValues();
        v.put(ShelterContract.ItemEntry.COLUMN_ITEM_NAME, name);
        v.put(PetEntry.COLUMN_PET_BREED, breed);
        v.put(PetEntry.COLUMN_PET_GENDER, gender);
        v.put(PetEntry.COLUMN_PET_WEIGHT, weight);
        return v;
    }
}
