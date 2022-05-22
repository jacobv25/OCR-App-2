package com.example.ocrapp2;

import java.io.File;

public interface S3_IF {
    void uploadFile(File imageFile, String fileName);
    void downloadFile(File imageFile, String fileName);
}
