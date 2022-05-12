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

    private static String imageFileName = "ocrapp2-capture";
    private static String s3imageFileName = "ocrapp2-capture.png";
    private ImageView imageView;
    private Button gallery;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageView = findViewById(R.id.imageView);
            imageView.setImageURI(selectedImage);
            try {
                bitmap = getBitmapFromSelectedImage(selectedImage);
                File imageFile = convertToPNGAndSave(bitmap);
                sendImageToS3(imageFile);
;            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
                    imageFileName,  /* prefix */
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