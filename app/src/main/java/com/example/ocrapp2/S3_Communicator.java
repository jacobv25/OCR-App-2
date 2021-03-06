package com.example.ocrapp2;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amplifyframework.core.Amplify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class S3_Communicator {


    public void uploadCSV(File csvFile, String fileName) {
        Log.i("UPLOADING FILE", "uploading file");
        Toaster toaster = new Toaster();
        Amplify.Storage.uploadFile(
                fileName,
                csvFile,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    toaster.displayToast(2);
                },
                storageFailure -> {
                    Log.e("MyAmplifyApp", "Upload failed", storageFailure);
                    toaster.displayToast(3);
                }
        );
    }

    public void uploadImage(File imageFile, String fileName) {
        Log.i("UPLOADING FILE", "uploading file");

        TextractCommunicator textractCommunicator = new TextractCommunicator();
        Toaster toaster = new Toaster();
        Amplify.Storage.uploadFile(
                fileName,
                imageFile,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    toaster.displayToast(0);
                    textractCommunicator.escapeHatch();
                },
                storageFailure -> {
                    Log.e("MyAmplifyApp", "Upload failed", storageFailure);
                    toaster.displayToast(1);
                }
        );
    }

    public void downloadFile(){
        Amplify.Storage.downloadFile(
                "ExampleKey", //Object to find in S3 Bucket
                new File(OCR_App_2.getContext().getFilesDir() + "/download.txt"), //Name of file downloaded to Android
                result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName()),
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );
    }
}
