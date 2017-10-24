package com.novax00.shelter.shelter;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.novax00.shelter.shelter.data.ShelterContract;
import com.novax00.shelter.shelter.data.ShelterDbHelper;
import com.novax00.shelter.shelter.data.ShelterItemAdapter;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShelterItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent instant = new Intent(MainActivity.this, EditorActivity.class);
//
//                startActivity(instant);
//            }
//        });

        ListView lvItems = (ListView) findViewById(R.id.list_view_main);
        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);
        adapter = new ShelterItemAdapter(this, null);
        lvItems.setAdapter(adapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Log.i("MY_DEBUG", "id ==> " + id);
                intent.setData(ContentUris.withAppendedId(ShelterContract.CONTENT_URI, id));
                startActivity(intent);

            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void addItem(View v) {

        Intent instant = new Intent(MainActivity.this, EditorActivity.class);

        startActivity(instant);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_dummy) {
            getContentResolver().insert(ShelterContract.CONTENT_URI,
                    ShelterDbHelper.createValues("test", "Тестовый товар", 5, new BigDecimal("1.23"))
            );
            return true;
        } else if(id == R.id.action_delete_all_entries){

            showDeleteConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ShelterContract.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                delete();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void delete() {
        if(this.getContentResolver().delete(ShelterContract.CONTENT_URI, null, null)>0){
            Toast.makeText(this, R.string.item_is_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }
    }
}
