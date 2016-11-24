package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SensorData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class MockWatchBluetoothHandler {

    private DataParser dataParser;
    private ArrayList<SensorData> sensorDatas;
    private int index;

    public MockWatchBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        this.dataParser = dataParser;
        sensorDatas = new ArrayList<>();
        simDataCreator();
        index = 0;
    }

    public void run() {
        SensorData sensorData = sensorDatas.get(index);
        dataParser.sendHRData(sensorData);
        index++;
        if (index == sensorDatas.size())
            index = 0;
    }
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
