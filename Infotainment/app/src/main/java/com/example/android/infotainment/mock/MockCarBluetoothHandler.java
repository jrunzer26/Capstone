package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class MockCarBluetoothHandler {
    private DataParser dataParser;
    private ArrayList<SimData> simDatas;
    private int index = 0;
    public MockCarBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        this.dataParser = dataParser;
        simDataCreator();
    }


    public void run() {
        SimData simData = simDatas.get(index);
        dataParser.sendSimData(simData);
        index++;
        if (index == simDatas.size())
            index = 0;
    }


    public void simDataCreator() {
        simDatas = new ArrayList<>();
        SimData speed70 = new SimData();
        speed70.setSpeed(70);
        speed70.setAcceleration(0);
        speed70.setPause(false);
        simDatas.add(speed70);
        simDatas.add(speed70);
        simDatas.add(new SimData());
    }
}
