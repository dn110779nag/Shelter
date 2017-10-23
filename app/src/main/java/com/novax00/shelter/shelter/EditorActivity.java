package com.novax00.shelter.shelter;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.novax00.shelter.shelter.data.ShelterContract;
import com.novax00.shelter.shelter.data.ShelterDbHelper;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>  {

    private boolean mHasChanged;
    private Uri uri;
    private TextView nameView;
    private TextView descriptionView;
    private TextView quantityView;
    private TextView priceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        this.uri = getIntent().getData();
        if (this.uri != null) {

            getSupportLoaderManager().initLoader(1, null, this);
        } else {
            this.setTitle("Add new Pet !!!");
        }
        invalidateOptionsMenu();


        this.nameView = (TextView)findViewById(R.id.name);
        this.descriptionView = (TextView)findViewById(R.id.description);
        this.quantityView = (TextView)findViewById(R.id.quantity);
        this.priceView = (TextView)findViewById(R.id.price);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (this.uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                save();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    private void save() {
        String name = nameView.getText().toString();
        String description = descriptionView.getText().toString();
        String sQuantity = quantityView.getText().toString();
        String sPrice = priceView.getText().toString();
        StringBuilder err = new StringBuilder();
        if(TextUtils.isEmpty(name)){

        }
        if(TextUtils.isEmpty(description)){

        }
        if(!Pattern.compile("\\d+").matcher(sQuantity).matches()){

        }
        if(!Pattern.compile("\\d+(\\.\\d{0,2})?").matcher(sQuantity).matches()){

        }
        long quantity = Long.parseLong(sQuantity);
        BigDecimal price = new BigDecimal(sPrice);
        if (this.uri != null) {
            int cnt = getContentResolver().update(
                    this.uri,
                    ShelterDbHelper.createValues(name, description, quantity, price),
                    null, null);
        } else {
            Uri newUri = getContentResolver().insert(
                    ShelterContract.CONTENT_URI,
                    ShelterDbHelper.createValues(name, description, quantity, price));
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            this.setTitle("Edit " + data.getInt(data.getColumnIndex(ShelterContract.ItemEntry._ID)));
            nameView.setText(data.getString(data.getColumnIndex(ShelterContract.ItemEntry.COLUMN_ITEM_NAME)));
            descriptionView.setText(data.getString(data.getColumnIndex(ShelterContract.ItemEntry.COLUMN_ITEM_DESCRIPTION)));
            quantityView.setText(String.valueOf(data.getInt(data.getColumnIndex(ShelterContract.ItemEntry.COLUMN_ITEM_QUANTITY))));
            priceView.setText(data.getString(data.getColumnIndex(ShelterContract.ItemEntry.COLUMN_ITEM_PRICE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameView.setText("");
        descriptionView.setText("");
        quantityView.setText("0");
        priceView.setText("0.00");
    }
}
