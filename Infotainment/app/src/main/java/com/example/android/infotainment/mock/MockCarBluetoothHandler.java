package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import android.content.res.AssetManager;


/**
 * Created by 100520993 on 11/12/2016.
 */

public class MockCarBluetoothHandler {
    private DataParser dataParser;
    private ArrayList<SimData> simDatas;
    private int index = 0;
    private BufferedReader br;
    private AssetManager AM;
    public MockCarBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        AM = mainContext.getAssets();

        this.dataParser = dataParser;
        try {
            InputStream is = AM.open("test1.csv");
            br= new BufferedReader(new InputStreamReader(is));
        } catch(FileNotFoundException e){
            System.out.println("File not found!\n" + e);
        } catch (IOException e){
            System.out.println("Inputstream failed\n" + e);
        }

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

        try{
            String line;

            br.readLine();
            int lineCount = 0;
            while((line = br.readLine())!= null){
                simDatas.add(lineCount, new SimData());
                String[] RowData = line.split(",");
                simDatas.get(lineCount).setSpeed((int)Math.round(Double.parseDouble(RowData[0])));
                simDatas.get(lineCount).setSteering(Double.parseDouble(RowData[1]));
                lineCount++;
            }
            lineCount=0;
        } catch (IOException e){

        }

        /*
        SimData speed70 = new SimData();
        speed70.setSpeed(70);
        speed70.setAcceleration(0);
        speed70.setPause(false);
        simDatas.add(speed70);
        simDatas.add(speed70);
        SimData speedOver = new SimData();
        speedOver.setSpeed(150);
        simDatas.add(speedOver);

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


        for (int i = 0; i < 30; i++) {
            SimData simData = new SimData();
            simData.setSteering(-40 - i * 3);
            simData.setSpeed(200);
            simDatas.add(simData);
        }*/
    }
}
