package com.example.ocrapp2;

import static com.amazonaws.regions.Regions.US_WEST_1;
import static com.example.ocrapp2.MainActivity.DEFAULT_BUCKET;
import static com.example.ocrapp2.MainActivity.DEFAULT_DOCUMENT;

import androidx.annotation.NonNull;

import com.amazonaws.regions.Region;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.S3Object;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.aws.AWSPredictionsEscapeHatch;
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin;

public class Textract implements Textract_IF{
    private AnalyzeDocumentResult result;

    @Override
    public AnalyzeDocumentResult extractText() {
        AmazonTextract client = setUpAmazonTextract();
        AnalyzeDocumentRequest request = buildAnalyzeDocumentRequest();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = client.analyzeDocument(request);
            }
        });
        thread.start();
        return result;
    }

    private AnalyzeDocumentRequest buildAnalyzeDocumentRequest() {
        AnalyzeDocumentRequest request = new AnalyzeDocumentRequest()
                .withFeatureTypes("TABLES", "FORMS")
                .withDocument(new Document().
                        withS3Object(new S3Object().withName(DEFAULT_DOCUMENT).withBucket(DEFAULT_BUCKET)));
        return request;
    }

    @NonNull
    private AmazonTextract setUpAmazonTextract() {
        // Obtain reference to the plugin
        AWSPredictionsPlugin predictionsPlugin = (AWSPredictionsPlugin)
                Amplify.Predictions.getPlugin("awsPredictionsPlugin");
        AWSPredictionsEscapeHatch escapeHatch = predictionsPlugin.getEscapeHatch();

        // Send a new request to the Textract endpoint directly using the client
        AmazonTextract _client = escapeHatch.getTextractClient();
        _client.setRegion(Region.getRegion(US_WEST_1));
        return _client;
    }

    public AnalyzeDocumentResult getResult() {
        return result;
    }
}
