package com.example.ocrapp2;

import static com.example.ocrapp2.MainActivity.s3imageFileName;

import android.util.Log;

import com.amplifyframework.core.Amplify;

import java.io.File;

public class S3 implements S3_IF {
    @Override
    public void uploadFile(File file, String fileName) {
        Log.i("UPLOADING FILE", "uploading file");
        Toaster toaster = new Toaster();
        Amplify.Storage.uploadFile(
                fileName,
                file,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    processResult(fileName, toaster, 0, 2);
                },
                storageFailure -> {
                    Log.e("MyAmplifyApp", "Upload failed", storageFailure);
                    processResult(fileName, toaster, 1, 3);
                }
        );
    }

    private void processResult(String fileName, Toaster toaster, int discriminator1, int discriminator2) {
        if (fileName == s3imageFileName) {
            toaster.displayToast(discriminator1);
            Textract textract = new Textract();
            textract.extractText();
        }
        else{
            toaster.displayToast(discriminator2);
        }
    }

    @Override
    public void downloadFile(File imageFile, String fileName) { }
}
