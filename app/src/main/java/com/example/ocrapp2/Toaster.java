package com.example.ocrapp2;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {
    Context context;
    public Toaster(){
        context = MainActivity.getAppContext();
    }
    public void displayToast(int discriminator){
        switch (discriminator){
            case 0:
                setToastText("Image Successfully Uploaded to S3 Bucket!");
                break;
            case 1:
                setToastText("Image Upload Failed!");
                break;
            case 2:
                setToastText("CSV Upload Successful!");
                break;
            case 3:
                setToastText("CSV Upload Failed!");
                break;
        }

    }
    public void displayToast(String message){
        setToastText(message);

    }

    private void setToastText(String message){
        // Displaying positioned Toast message
        Toast t = Toast.makeText(context,
                message,
                Toast.LENGTH_LONG);
        t.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
        t.show();
    }
}
