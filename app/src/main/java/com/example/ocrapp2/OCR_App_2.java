package com.example.ocrapp2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

import org.w3c.dom.Text;

public class OCR_App_2 extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();


        context = getApplicationContext();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSPredictionsPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }

        //S3 download file
        S3_Communicator s3Communicator = new S3_Communicator();
//        s3Communicator.downloadFile();

        //Textract
        //Run in a separate thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                TextractCommunicator textractCommunicator = new TextractCommunicator();
//                textractCommunicator.escapeHatch();
//                s3Communicator.uploadFile();
            }
        });

        thread.start();
    }

    public static Context getContext() {
        return context;
    }
}
