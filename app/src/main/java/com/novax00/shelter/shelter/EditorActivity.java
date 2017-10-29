package com.novax00.shelter.shelter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novax00.shelter.shelter.data.ShelterContract;
import com.novax00.shelter.shelter.data.ShelterDbHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>  {

    private boolean mHasChanged;
    private Uri uri;
    private TextView nameView;
    private TextView descriptionView;
    private TextView quantityView;
    private TextView priceView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                 Toast.makeText(this, "ex: "+ex, Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.novax00.shelter.shelter.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            final Serializable serializableExtra = data.getSerializableExtra(MediaStore.EXTRA_OUTPUT);
            if(data == null){
                File file = new File(mCurrentPhotoPath);
                Uri uri = Uri.fromFile(file);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    bitmap = crupAndScale(bitmap, 300); // if you mind scaling
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
            }
        }
    }

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
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
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
                if(save()) {
                    finish();

                }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                delete();
                finish();
//                dialog.dismiss();
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

    private void delete() {
        // TODO: Implement this method
        if(this.getContentResolver().delete(this.uri, null, null)==1){
            Toast.makeText(this, R.string.item_is_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }
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

    private boolean save() {
        String name = nameView.getText().toString();
        String description = descriptionView.getText().toString();
        String sQuantity = quantityView.getText().toString();
        String sPrice = priceView.getText().toString();
        TextInputLayout nameLayout = (TextInputLayout)findViewById(R.id.nameLayout);
        TextInputLayout descriptionLayout = (TextInputLayout)findViewById(R.id.descriptionLayout);
        TextInputLayout quantityLayout = (TextInputLayout)findViewById(R.id.quantityLayout);
        TextInputLayout priceLayout = (TextInputLayout)findViewById(R.id.priceLayout);

        nameLayout.setErrorEnabled(false);
        descriptionLayout.setErrorEnabled(false);
        quantityLayout.setErrorEnabled(false);
        priceLayout.setErrorEnabled(false);

        boolean err = false;
        if(TextUtils.isEmpty(name)){
            nameLayout.setErrorEnabled(true);
            nameLayout.setError(getString(R.string.must_not_be_empty));
            err = true;
        }
        if(TextUtils.isEmpty(description)){
            descriptionLayout.setErrorEnabled(true);
            descriptionLayout.setError(getString(R.string.must_not_be_empty));
            err = true;
        }
        String quantityPattern = "\\d+";
        if(!Pattern.compile(quantityPattern).matcher(sQuantity).matches()){
            quantityLayout.setErrorEnabled(true);
            quantityLayout.setError(getString(R.string.must_match_pattern)+ quantityPattern);
            err = true;
        }
        String pricePattern ="\\d+(\\.\\d{0,2})?";
        if(!Pattern.compile(pricePattern).matcher(sPrice).matches()){
            priceLayout.setErrorEnabled(true);
            priceLayout.setError(getString(R.string.must_match_pattern)+ pricePattern);
            err = true;
        }
        if(!err) {
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
        return !err;
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
