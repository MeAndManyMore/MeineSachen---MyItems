package com.example.christian.meinesachen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText medtSache, medtPreis, medtDatum;
    Button mbtnSpeichern, mbtnListe;
    ImageView mimgFoto,mimgRechnung;
    TextView mtxtFoto, mtxtRechnung;

    final int REQUEST_CODE_GALLERY = 999;

    public int wtf; //to figure out which picture was clicked to assign the correct one

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Neuer Eintrag");

        medtSache = findViewById(R.id.edtSache);
        medtPreis = findViewById(R.id.edtPreis);
        medtDatum = findViewById(R.id.edtDatum);
        mbtnSpeichern = findViewById(R.id.btnSpeichern);
        mbtnListe = findViewById(R.id.btnListe);
        mimgFoto = findViewById(R.id.imgFoto);
        mimgRechnung = findViewById(R.id.imgRechnung);
        mtxtFoto = findViewById(R.id.txtFoto);
        mtxtRechnung = findViewById(R.id.txtRechnung);

        //creating database
        mSQLiteHelper = new SQLiteHelper(this, "RECORDDB.sqlite", null, 1);

        //creating table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, sache VARCHAR, preis VARCHAR, datum VARCHAR, foto1 BLOB, foto2 BLOB)");

        //select image Sache by on imageview click
        mimgFoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //read external storage permission to selsct image from gallery
                //runtime permission for devices android 6.0 and above
                wtf = 1;
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
        //select image Rechnung by on imageview click
        mimgRechnung.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //read external storage permission to selsct image from gallery
                //runtime permission for devices android 6.0 and above
                wtf = 2;
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        //add record to sqlite DB
        mbtnSpeichern.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try{
                    mSQLiteHelper.insertData(
                            medtSache.getText().toString().trim(),
                            medtPreis.getText().toString().trim(),
                            medtDatum.getText().toString().trim(),
                            imageViewToByte(mimgFoto),
                            imageViewToByte(mimgRechnung)
                    );
                    Toast.makeText(MainActivity.this, "gespeichert", Toast.LENGTH_SHORT).show();
                    //reset views
                    medtSache.setText("");
                    medtPreis.setText("");
                    medtDatum.setText("");
                    mimgFoto.setImageResource(R.drawable.addphoto);
                    mimgRechnung.setImageResource(R.drawable.addphoto);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //show record list
        mbtnListe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //start recordlist activity
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }
            else{
                Toast.makeText(this, "Keine Erlaubnis Foto zu laden.", Toast.LENGTH_SHORT).show();
            }
            return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
            .setAspectRatio(1,1) // image will be square
            .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //get image choosen from gallery to image view
                switch (wtf) {
                    case 1:
                        mimgFoto.setImageURI(resultUri);
                        break;
                    case 2:
                        mimgRechnung.setImageURI(resultUri);
                        break;
                    default:
                        break;
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}



















