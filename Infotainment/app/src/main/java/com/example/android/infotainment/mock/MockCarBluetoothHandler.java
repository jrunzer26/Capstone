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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100520993 on 11/12/2016.
 */

public class MockCarBluetoothHandler {
    private DataParser dataParser;
    private ArrayList<SimData> simDatas;
    private int index = 0;
    public MockCarBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        this.dataParser = dataParser;
        simDataCreator();
    }

    /**
     * Sends data to the data parser.
     */
    public void run() {
        SimData simData = simDatas.get(index);
        dataParser.sendSimData(simData);
        index++;
        if (index == simDatas.size())
            index = 0;
    }

    /**
     * Creates data to simulate the car simulator if the programmer does not have all devices
     */
    public void simDataCreator() {
        simDatas = new ArrayList<>();
        /**
        SimData speed70 = new SimData();
        speed70.setSpeed(70);
        speed70.setAcceleration(0);
        speed70.setPause(false);
        simDatas.add(speed70);
        simDatas.add(speed70);
        SimData speedOver = new SimData();
        speedOver.setSpeed(150);
        simDatas.add(speedOver);
         */

        //fromNearStopAccelTest();
        //fromSpeedAccelTest();
        fromSpeedAccelRandomLengths();

        //steeringTest();
        //slowAccelerationTest();

    }

    private void steeringTest() {
        for (int i = 0; i < 30; i++) {
            SimData simData = new SimData();
            simData.setSteering(-40 - i * 3);
            simData.setSpeed(200);
            simDatas.add(simData);
        }
    }

    private void fromSpeedAccelRandomLengths() {
        for (int j = 0; j < 5; j++) {
            constantSpeed(30, 10);
            Random rand = new Random();
            // random int between 5 - 20
            int random = rand.nextInt(20 - 5) + 5;
            for(int i = 0; i < random; i++) {
                SimData simData = new SimData();
                simData.setSpeed(30 + i * 10);
                simDatas.add(simData);
            }
        }

    }
    private void fromSpeedAccelTest() {
        constantSpeed(30, 10);
        for(int i = 0; i < 10; i++) {
            SimData simData = new SimData();
            simData.setSpeed(30 + i * 10);
            simDatas.add(simData);
        }

    }

    private void noSpeed(int length) {
        for(int i = 0; i < length; i++) {
            SimData simData = new SimData();
            simData.setSpeed(0);
            simDatas.add(simData);
        }
    }

    private void constantSpeed(int speed, int length) {
        for(int i = 0; i < length; i++) {
            SimData simData = new SimData();
            simData.setSpeed(speed);
            simDatas.add(simData);
        }
    }

    public void fromNearStopAccelTest() {
        noSpeed(10);
        for(int i = 0; i < 30; i++) {
            SimData simData = new SimData();
            simData.setSpeed(i * 5);
            simDatas.add(simData);
        }
    }

    public void slowAccelerationTest() {
        for (int i = 0; i < 15; i++) {
            SimData simData = new SimData();
            simData.setSteering(i + 25);
            simData.setSpeed(30 + i);
            simDatas.add(simData);
        }
        for (int i = 15; i > 0; i--) {
            SimData simData = new SimData();
            simData.setSteering(i + 25);
            simData.setSpeed(30 + i);
            simDatas.add(simData);
        }

        for (int i = 0; i < 15; i++) {
            SimData simData = new SimData();
            simData.setSteering(i + 40);
            simData.setSpeed(30 + i);
            simDatas.add(simData);
        }
        for (int i = 15; i > 0; i--) {
            SimData simData = new SimData();
            simData.setSteering(i + 40);
            simData.setSpeed(30 + i);
            simDatas.add(simData);
        }
    }
}
