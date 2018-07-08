package com.example.christian.meinesachen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;
    Button dRechnButton;
    ImageView iupdFoto, iupdRechnung, dRechnBild;
    private Uri mImageUri;

    public int uwtf; //to figure out which picture was clicked to assign the correct one

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Liste meiner Sachen");

        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null){
            mImageUri = callingActivityIntent.getData();
            //vermutlich muss da was her...bilddaten oder so
        }

        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String sache = cursor.getString(1);
            String preis = cursor.getString(2);
            String datum = cursor.getString(3);
            byte[] image1 = cursor.getBlob(4);
            byte[] image2 = cursor.getBlob(5);

            //add to list
            mList.add(new Model(id, sache, preis, datum, image1, image2));
        }
        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            //if there is no record in table database which mean list view is empty
            Toast.makeText(this, "Noch keine Sachen da :-(", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Rechnung ansehen", "Sache löschen"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Option wählen");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            //update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showInvoice(RecordListActivity.this, arrID.get(position));}
                        if (i == 1) {
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });


    }

    private void showInvoice(Activity activity, final int position){

        //Create Dialog window
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.invoice_dialog);
        dialog.setTitle("Rechnung");
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.75);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        dRechnBild = dialog.findViewById(R.id.RechnBild);
        dRechnButton = dialog.findViewById(R.id.RechnAktion);

        //picture data from invoice for Dialog window and set picture
        try {
            Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                byte[] Rechnung = cursor.getBlob(5);
                dRechnBild.setImageBitmap(BitmapFactory.decodeByteArray(Rechnung, 0,Rechnung.length));
            }
        } catch (Exception e) {
            Log.e("Fehler", e.getMessage());
        }

        //set Click Listener on picture
        dRechnBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "yepp, you clicked picture", Toast.LENGTH_SHORT).show();
                // ActionProviderImplementeda
                //MenuItem menuItem = menu.findItem(R.id.image_share_menu)
                ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(dRechnBild);
                shareActionProvider.setShareIntent();
                return true;


            }
        });
        //set Click Listener on button
        dRechnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "yepp, you clicked button", Toast.LENGTH_SHORT).show();


            }
        });

        //show what date comes on to that dialog window
        String numberAsString = Integer.toString(position);
        Toast.makeText(getApplicationContext(), numberAsString, Toast.LENGTH_SHORT).show();

    }

    private void showDialogUpdate(Activity activity, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Ändern");

        iupdFoto = dialog.findViewById(R.id.updFoto);
        iupdRechnung = dialog.findViewById(R.id.updRechnung);
        final EditText edtSache = dialog.findViewById(R.id.updSache);
        final EditText edtPreis = dialog.findViewById(R.id.updPreis);
        final EditText edtDatum = dialog.findViewById(R.id.edtDatum);
        Button btnUpdate = dialog.findViewById(R.id.updUpdate);

        //get data of row clicked from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String sache = cursor.getString(1);
            edtSache.setText(sache);
            String preis = cursor.getString(2);
            edtPreis.setText(preis);
            String datum = cursor.getString(3);
            edtDatum.setText(datum);
            byte[] image1 = cursor.getBlob(4);
            iupdFoto.setImageBitmap(BitmapFactory.decodeByteArray(image1, 0,image1.length));
            byte[] image2 = cursor.getBlob(5);
            iupdFoto.setImageBitmap(BitmapFactory.decodeByteArray(image2, 0,image2.length));

            //add to list
            mList.add(new Model(id, sache, preis, datum, image1, image2));
        }

        //set width of dialog
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        //set height of dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        //in update dialog click image view to update
        //select image Sache by on imageview click
        iupdFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read external storage permission to selsct image from gallery
                //runtime permission for devices android 6.0 and above
                uwtf = 1;
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });
        //select image Rechnung by on imageview click
        iupdRechnung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read external storage permission to selsct image from gallery
                //runtime permission for devices android 6.0 and above
                uwtf = 2;
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.mSQLiteHelper.updateData(
                            edtSache.getText().toString().trim(),
                            edtPreis.getText().toString().trim(),
                            edtDatum.getText().toString().trim(),
                            MainActivity.imageViewToByte(iupdFoto),
                            MainActivity.imageViewToByte(iupdRechnung),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Änderung erfolgreich", Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Log.e("Änderung Fehler", error.getMessage());
                }
                updateRecordList();
            }
        });
    }

    private void updateRecordList() {
        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dsache = cursor.getString(1);
            String dpreis = cursor.getString(2);
            String ddatum = cursor.getString(3);
            byte[] dbild1 = cursor.getBlob(4);
            byte[] dbild2 = cursor.getBlob(5);

            mList.add(new Model(id, dsache, dpreis, ddatum, dbild1, dbild2));
        }
        mAdapter.notifyDataSetChanged();
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
            } else {
                Toast.makeText(this, "Keine Erlaubnis Foto zu laden.", Toast.LENGTH_SHORT).show();
            }
            return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                    .setAspectRatio(1, 1) // image will be square
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //get image choosen from gallery to image view
                switch (uwtf) {
                    case 1:
                        iupdFoto.setImageURI(resultUri);
                        break;
                    case 2:
                        iupdRechnung.setImageURI(resultUri);
                        break;
                    default:
                        break;
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warnung!");
        dialogDelete.setMessage("Wirklich löschen?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Fehler", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
        return shareIntent;
    }
}


























