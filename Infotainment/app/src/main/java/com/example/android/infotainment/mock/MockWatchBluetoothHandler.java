package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SensorData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
/**
 * Created by 100520993 on 11/12/2016.
 */

/**
 * Mock Watch Handler to send data determined in the simDataCreator method.
 */
public class MockWatchBluetoothHandler {

    private DataParser dataParser;
    private ArrayList<SensorData> sensorDatas;
    private int index;
    private BufferedReader br;
    private AssetManager AM;
    /**
     * Creates a mock handler for the watch data.
     * @param socket null
     * @param mainContext null
     * @param dataParser null
     */
    public MockWatchBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        this.dataParser = dataParser;
        sensorDatas = new ArrayList<>();
        AM = mainContext.getAssets();
        try {
            InputStream is = AM.open("test1.csv");
            br= new BufferedReader(new InputStreamReader(is));
        } catch(FileNotFoundException e){
            System.out.println("File not found!\n" + e);
        } catch (IOException e){
            System.out.println("Inputstream failed\n" + e);
        }
        simDataCreator();
        index = 0;
    }

    /**
     * Sends data to the data parser.
     */
    public void run() {
        SensorData sensorData = sensorDatas.get(index);
        dataParser.sendHRData(sensorData);
        index++;
        //System.out.println(index);
        if (index == sensorDatas.size())
            index = 0;
    }

    /**
     * Appends test data to send to the parser.
     */
    public void simDataCreator() {
        try{
            String line;
            br.readLine();
            int lineCount = 0;
            while((line = br.readLine())!= null){
                sensorDatas.add(lineCount, new SensorData());
                String[] RowData = line.split(",");
                sensorDatas.get(lineCount).setHeartRate(Integer.parseInt(RowData[2]));
                lineCount++;
            }
        } catch (IOException e){

        }
        /*
        SensorData sensorData70 = new SensorData();
        sensorData70.setHeartRate(70);
        SensorData sensorData90 = new SensorData();
        sensorData90.setHeartRate(90);
        sensorDatas.add(sensorData70);
        sensorDatas.add(sensorData70);
        sensorDatas.add(sensorData90);
        */
    }
}
