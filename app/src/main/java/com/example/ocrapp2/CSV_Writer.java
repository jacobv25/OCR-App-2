package com.example.ocrapp2;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSV_Writer {

    public String[] createHeader(){
        String[] header = {"Name", "Phone", "Year", "Make", "Model"};
        return header;
    }

    public HashMap<String,String[]> createDataMap(HashMap<String, String> originalMap){
        HashMap<String,String[]> dataMap = new HashMap<>();
        String[] data = new String[5];
        String[] keys = {"Name", "Phone", "Year", "Make", "Model"};
        for(int i=0; i<keys.length; i++){
            data[i] = originalMap.get(keys[i]);
        }
        dataMap.put("Data", data);
        return dataMap;
    }

    public File writeCSVFileToPhone( String[] _header, HashMap<String,String[]> dataMap)
    {
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyCsvFile.csv"); // Here csv file name is MyCsvFile.csv
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));

            // adding header to csv
            String[] header = _header;
            writer.writeNext(header);

            // add data to csv
            // String[] data1 = { "Aman", "10", "620" };
            // writer.writeNext(data1);
            // String[] data2 = { "Suraj", "10", "630" };
            // writer.writeNext(data2);
            for(String[] value: dataMap.values()){
                writer.writeNext(value);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(csv);
    }
}
