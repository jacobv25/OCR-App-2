package com.example.ocrapp2;

import static com.example.ocrapp2.MainActivity.DEFAULT_BUCKET;
import static com.example.ocrapp2.MainActivity.DEFAULT_DOCUMENT;
import androidx.annotation.NonNull;
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
        Thread thread = new Thread(() -> result = client.analyzeDocument(request));
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
        AWSPredictionsPlugin predictionsPlugin = getAwsPredictionsPlugin();
        AWSPredictionsEscapeHatch escapeHatch = predictionsPlugin.getEscapeHatch();
        AmazonTextract _client = escapeHatch.getTextractClient();
//        _client.setRegion(Region.getRegion(US_WEST_1));
        return _client;
    }

    @NonNull
    private AWSPredictionsPlugin getAwsPredictionsPlugin() {
        return (AWSPredictionsPlugin) Amplify.Predictions.getPlugin("awsPredictionsPlugin");
    }

    public AnalyzeDocumentResult getResult() {
        return result;
    }
}

