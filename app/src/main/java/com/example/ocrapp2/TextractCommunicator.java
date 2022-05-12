package com.example.ocrapp2;

import static com.amazonaws.regions.Regions.US_WEST_1;

import android.os.Environment;

import com.amazonaws.regions.Region;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.Relationship;
import com.amazonaws.services.textract.model.S3Object;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.aws.AWSPredictionsEscapeHatch;
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextractCommunicator {


    private AnalyzeDocumentResult result;
    private String bucket = "ocrapplication2fc15f311960a489aa9941e0426f2916d201736-dev";
    private String document = "public/ocrapp2-capture.png";
    private HashMap<String, String> dataMap;

    public void escapeHatch() {
        // Obtain reference to the plugin
        AWSPredictionsPlugin predictionsPlugin = (AWSPredictionsPlugin)
                Amplify.Predictions.getPlugin("awsPredictionsPlugin");
        AWSPredictionsEscapeHatch escapeHatch = predictionsPlugin.getEscapeHatch();

        // Send a new request to the Textract endpoint directly using the client
        AmazonTextract _client = escapeHatch.getTextractClient();
        _client.setRegion(Region.getRegion(US_WEST_1));

        AnalyzeDocumentRequest _request = new AnalyzeDocumentRequest()
                .withFeatureTypes("TABLES", "FORMS")
                .withDocument(new Document().
                        withS3Object(new S3Object().withName(document).withBucket(bucket)));

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    AnalyzeDocumentResult result = _client.analyzeDocument(_request);

                    List<Block> blocks = result.getBlocks();
                    dataMap = convertDataToFinalHashMap(blocks);
                    System.out.println("-----PRINTING HASHMAP-----");
                    System.out.println(dataMap);
                    System.out.println("-----writing file external storage-----");
                    CSV_Writer csv_writer = new CSV_Writer();
                    HashMap<String, String[]> finalDataMap = csv_writer.createDataMap(dataMap);
                    String[] header = csv_writer.createHeader();
                    File csvFile = csv_writer.writeCSVFileToPhone(header, finalDataMap);
                    S3_Communicator s3Communicator = new S3_Communicator();
                    s3Communicator.uploadCSV(csvFile, "MyCsvFile.csv");
                    /**
                     * good for debugging
                     *
                    System.out.println("----PRINTING BLOCKS-----");
                    for (Block block : blocks) {
                        displayBlockInfo(block);
                    }
                     */

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void writeFileExternalStorage() {
        //fileName
        String filenameExternal = "DownloadedTextDocumentYAYA.txt";

        //Text of the Document
        String textToWrite = "bla bla bla";

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filenameExternal);

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(textToWrite.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HashMap<String, String> convertDataToFinalHashMap(List<Block> list) throws Exception {
        HashMap<String, String> hashMap;
        List<String> data=new ArrayList<>(), keys=new ArrayList<>(), values=new ArrayList<>();
        String key, value;
        for (Block block : list) {
            if (block.getBlockType().equals("LINE")) {
                if(block.getText() != null){
                    data.add(block.getText());
                }
            }
        }
        listToKeyValueListPairs(data, keys, values);
        hashMap = keyValueListPairsToHashMap(keys, values);
        return hashMap;
    }

    private HashMap<String, String> keyValueListPairsToHashMap(List<String> keys, List<String> values) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        String key,value;
        if(keys.size() == values.size()){
            for(int i=0; i < keys.size(); i++){
                key = keys.get(i);
                value = values.get(i);
                hashMap.put(key, value );
            }
        }
        else {
            throw new Exception("Key and Value sizes do not match!");
        }
        return hashMap;
    }

    private void listToKeyValueListPairs(List<String> data, List<String> keys, List<String> values) {
        String key,value;
        for(int i = 0; i < 5; i++){
            key = data.get(i);
            keys.add(key);
        }
        for(int i = 5; i < 10; i++){
            value = data.get(i);
            values.add(value);
        }
    }


    private HashMap<String, String> convertLinesToHashMap(List<Block> list) {
        HashMap<String, String> hashMap = new HashMap<>();
        String key, value;
        for (Block block : list) {
            if (block.getBlockType().equals("LINE")) {
                if(block.getText() != null){
                    key = block.getId();
                    value = block.getText();
                    hashMap.put(key,value);
                }
            }
        }
        return hashMap;
    }

    /**
     * Helpful in Debugging
     */
    public void displayBlockInfo(Block block) {
        System.out.println("Block Id : " + block.getId());
        if (block.getText()!=null)
            System.out.println("    Detected text: " + block.getText());
        System.out.println("    Type: " + block.getBlockType());

        if (block.getBlockType().equals("PAGE") !=true) {
            System.out.println("    Confidence: " + block.getConfidence().toString());
        }
        if(block.getBlockType().equals("CELL"))
        {
            System.out.println("    Cell information:");
            System.out.println("        Column: " + block.getColumnIndex());
            System.out.println("        Row: " + block.getRowIndex());
            System.out.println("        Column span: " + block.getColumnSpan());
            System.out.println("        Row span: " + block.getRowSpan());

        }

        System.out.println("    Relationships");
        List<Relationship> relationships=block.getRelationships();
        if(relationships!=null) {
            for (Relationship relationship : relationships) {
                System.out.println("        Type: " + relationship.getType());
                System.out.println("        IDs: " + relationship.getIds().toString());
            }
        } else {
            System.out.println("        No related Blocks");
        }

        System.out.println("    Geometry");
        System.out.println("        Bounding Box: " + block.getGeometry().getBoundingBox().toString());
        System.out.println("        Polygon: " + block.getGeometry().getPolygon().toString());

        List<String> entityTypes = block.getEntityTypes();

        System.out.println("    Entity Types");
        if(entityTypes!=null) {
            for (String entityType : entityTypes) {
                System.out.println("        Entity Type: " + entityType);
            }
        } else {
            System.out.println("        No entity type");
        }

        if(block.getBlockType().equals("SELECTION_ELEMENT")) {
            System.out.print("    Selection element detected: ");
            if (block.getSelectionStatus().equals("SELECTED")){
                System.out.println("Selected");
            }else {
                System.out.println(" Not selected");
            }
        }

        if(block.getPage()!=null)
            System.out.println("    Page: " + block.getPage());
        System.out.println();
    }
}
