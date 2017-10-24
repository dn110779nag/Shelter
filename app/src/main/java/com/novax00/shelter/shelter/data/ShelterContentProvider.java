package com.novax00.shelter.shelter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by novax00 on 29.09.17.
 */

public class ShelterContentProvider extends ContentProvider {

    private ShelterDbHelper shelterDbHelper;
    public static final int ITEMS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    public static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ShelterContract.CONTENT_AUTHORITY, ShelterContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ShelterContract.CONTENT_AUTHORITY, ShelterContract.PATH_ITEMS + "/#", ITEM_ID);
    }


    @Override
    public boolean onCreate() {
        this.shelterDbHelper = new ShelterDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = shelterDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                break;
            case ITEM_ID:
                selection = ShelterContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        Cursor cursor = database.query(ShelterContract.ItemEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);

         cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(Constants.LOG_TAG, "URI ==> "+uri);
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return insertItem(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertItem(Uri uri, ContentValues values) {
        validate(values);

        long id = this.shelterDbHelper.getWritableDatabase().insert(
                ShelterContract.ItemEntry.TABLE_NAME, null, values);
        this.getContext().getContentResolver().notifyChange(uri,null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = shelterDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int res = -1;
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args

                res = database.delete(ShelterContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ShelterContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                res = database.delete(ShelterContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        this.getContext().getContentResolver().notifyChange(uri,null);
        return res;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int res = -1;
        switch (match) {
            case ITEMS:
                res = updateItem(contentValues, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ShelterContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                res = updateItem(contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        this.getContext().getContentResolver().notifyChange(uri,null);
        return res;
    }

    private int updateItem(ContentValues values, String selection, String[] selectionArgs) {
        validate(values);
        return this.shelterDbHelper.getWritableDatabase().update(ShelterContract.ItemEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    private void validate(ContentValues values) {
    }
}
