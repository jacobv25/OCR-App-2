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

        Amplify.Storage.uploadFile(
                fileName,
                csvFile,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }

    public void uploadImage(File imageFile, String fileName) {
        Log.i("UPLOADING FILE", "uploading file");

        TextractCommunicator textractCommunicator = new TextractCommunicator();
        Amplify.Storage.uploadFile(
                fileName,
                imageFile,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    textractCommunicator.escapeHatch();
                },
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
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
