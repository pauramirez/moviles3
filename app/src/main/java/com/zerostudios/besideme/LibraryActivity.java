package com.zerostudios.besideme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class LibraryActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private Button mPhoto;
    private ImageButton mMaps;
    private ImageView mImageView;
    private static final int CAMERA_REQUES_CODE =1;
    private  String mCurrentPhotoPath;
    private Uri photoURI;

    private StorageReference mStorage;

    private ProgressDialog mProgress;

    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                 photoURI = FileProvider.getUriForFile(this,
                        "com.zerostudios.besideme.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUES_CODE);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mStorage = FirebaseStorage.getInstance().getReference();
        mPhoto = (Button) findViewById(R.id.picture);
        mImageView = (ImageView) findViewById(R.id.imageview);

        mProgress = new ProgressDialog(this);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkCameraPermission();
        }

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });
    }

    private boolean checkCameraPermission()
    {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))
                {
                    ActivityCompat.requestPermissions(this,new String[]{

                            Manifest.permission.CAMERA
                    },CAMERA_REQUES_CODE);
                }
                else
                    ActivityCompat.requestPermissions(this,new String[]{

                            Manifest.permission.CAMERA
                    },CAMERA_REQUES_CODE);

                return false;
            }
            else
                return true;

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUES_CODE && resultCode == RESULT_OK){
            mProgress.setMessage("Uploading...");
            mProgress.show();

            StorageReference filepath = mStorage.child("Photos").child(photoURI.getLastPathSegment());
            filepath.putFile(photoURI).addOnSuccessListener(new    OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(LibraryActivity.this).load(downloadUri).fit().centerCrop().into(mImageView);
                    Toast.makeText(LibraryActivity.this, "Upload Successful!",    Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LibraryActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}



   
