package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SensorData;
import java.util.ArrayList;

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

    /**
     * Creates a mock handler for the watch data.
     * @param socket null
     * @param mainContext null
     * @param dataParser null
     */
    public MockWatchBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        this.dataParser = dataParser;
        sensorDatas = new ArrayList<>();
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
        if (index == sensorDatas.size())
            index = 0;
    }

    /**
     * Appends test data to send to the parser.
     */
    public void simDataCreator() {
        SensorData sensorData70 = new SensorData();
        sensorData70.setHeartRate(70);
        SensorData sensorData90 = new SensorData();
        sensorData90.setHeartRate(90);
        sensorDatas.add(sensorData70);
        sensorDatas.add(sensorData70);
        sensorDatas.add(sensorData90);
    }
}
