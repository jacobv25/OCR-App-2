package com.example.ocrapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public static final String DEFAULT_BUCKET = "ocrapplication2fc15f311960a489aa9941e0426f2916d201736-dev";
    public static final String DEFAULT_DOCUMENT = "public/ocrapp2-capture.png";

    private static String phoneImageFileName = "ocrapp2-capture";
    private static String s3imageFileName = "ocrapp2-capture.png";
    private ImageView imageView;
    private Button galleryButton;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        galleryButton = findViewById(R.id.gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

    }

    /**
     * Code that is run when image is picked.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(imageIsPickedSuccessfully(resultCode, data)){
            Uri selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
            try {
                imageBitmap = getBitmapFromSelectedImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File imageFile = convertToPNGAndSave(imageBitmap);
            sendImageToS3(imageFile);
;

        }
    }

    private boolean imageIsPickedSuccessfully(int resultCode, Intent data) {
        return resultCode == RESULT_OK && data != null;
    }

    private Bitmap getBitmapFromSelectedImage(Uri selectedImage) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
    }

    private void extractTextWithTextract() {
        TextractCommunicator textractCommunicator = new TextractCommunicator();
        textractCommunicator.escapeHatch();
    }

    private void sendImageToS3(File imageFile) {
        Log.i("SENDING FILE", "sending file");
        S3_Communicator s3Communicator = new S3_Communicator();
        s3Communicator.uploadImage(imageFile, s3imageFileName);
    }

    public static File convertToPNGAndSave(Bitmap image) {

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    phoneImageFileName,  /* prefix */
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        return imageFile;
    }
}