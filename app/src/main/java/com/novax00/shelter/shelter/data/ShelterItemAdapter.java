package com.novax00.shelter.shelter.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.novax00.shelter.shelter.R;

/**
 * Created by dn110 on 28.09.2017.
 */

public class ShelterItemAdapter extends CursorAdapter {

    public ShelterItemAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);

        nameView.setText(cursor.getString(cursor.
                getColumnIndexOrThrow(ShelterContract.ItemEntry.COLUMN_ITEM_NAME)));

        summaryView.setText(cursor.getString(cursor.
                getColumnIndexOrThrow(ShelterContract.ItemEntry.COLUMN_ITEM_DESCRIPTION)));
    }
}
