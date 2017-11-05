package com.novax00.shelter.shelter.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novax00.shelter.shelter.PhotoViewActivity;
import com.novax00.shelter.shelter.R;

import java.io.File;

/**
 * Created by dn110 on 28.09.2017.
 */

public class ShelterItemAdapter extends CursorAdapter implements View.OnClickListener {

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
        View btn = view.findViewById(R.id.btn);

        nameView.setText(cursor.getString(cursor.
                getColumnIndexOrThrow(ShelterContract.ItemEntry.COLUMN_ITEM_NAME)));

        summaryView.setText(cursor.getString(cursor.
                getColumnIndexOrThrow(ShelterContract.ItemEntry.COLUMN_ITEM_DESCRIPTION)));


        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        LinearLayout vwParentRow = (LinearLayout) view.getParent();
        int position = vwParentRow.indexOfChild(view);
        Cursor cursor =getCursor();
        cursor.move(position);
        String path = cursor.getString(cursor.
                getColumnIndexOrThrow(ShelterContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH));
        Log.v(ShelterContract.LOG_TAG, "path ==> "+path);
        if(!TextUtils.isEmpty(path)) {

            Intent i = new Intent(view.getContext(),  PhotoViewActivity.class);
            i.setData(Uri.fromFile(new File(path)));
            view.getContext().startActivity(i);
        } else {
            Toast.makeText(view.getContext(), "No image", Toast.LENGTH_SHORT).show();
        }
    }
}
