package com.example.android.infotainment.mock;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.models.SimData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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


        /*
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

*/
        //constantSpeed(50, 10);
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

         */

        /* ACCELERATION TESTS */
        fromNearStopAccelTest();
        fromSpeedAccelTest();
        //fromSpeedAccelRandomLengths();
        //slowAccelerationTest();


        /* Steering Tests */
        //steeringLeftTest();
        //steeringRightTest();

        /* Braking Tests */
        //brakingTest();
        //brakingTest2();

        /* Cruising Tests */
        //crusingTest();
        //constantSpeed(100, 10);
    }


    private void crusingTest() {
        for(int i = 0; i < 10; i++) {
            SimData simData = new SimData();
            simData.setSpeed(60);
            simData.setSpeedLimit(40);
            simDatas.add(simData);
        }
        for (int i = 0; i < 10; i++) {
            SimData simData = new SimData();
            simData.setSpeed(60);
            simData.setSteering(i);
            simData.setSpeedLimit(50);
            simDatas.add(simData);
        }
        for (int i = 9; i >= 0; i--) {
            SimData simData = new SimData();
            simData.setSpeed(60);
            simData.setSteering(i);
            simDatas.add(simData);
        }
        constantSpeed(60, 10);
    }
    private void brakingTest() {
        constantSpeed(60, 10);
        for (int i = 0; i < 10; i++) {
            SimData simData = new SimData();
            simData.setSpeed(60 - i * 5);
            simDatas.add(simData);
        }
    }

    private void brakingTest2() {
        constantSpeed(100, 10);
        for (int i = 0; i < 20; i++) {
            SimData simData = new SimData();
            simData.setSpeed(100 - i * 8);
            simDatas.add(simData);
        }
    }

    private void steeringLeftTest() {
        SimData simData3 = new SimData();
        simData3.setSteering(0);
        simData3.setSpeed(100);
        simDatas.add(simData3);
        for (int i = 0; i < 30; i++) {
            SimData simData2 = new SimData();
            simData2.setSteering(-40 - i * 3);
            simData2.setSpeed(100);
            simDatas.add(simData2);
        }
        SimData simData = new SimData();
        simData.setSteering(0);
        simData.setSpeed(100);
        simDatas.add(simData);
    }

    private void steeringRightTest() {
        SimData simData = new SimData();
        simData.setSteering(0);
        simData.setSpeed(100);
        simDatas.add(simData);
        for (int i = 0; i < 30; i++) {
            SimData simData2 = new SimData();
            simData2.setSteering(40 + i * 3);
            simData2.setSpeed(100);
            simDatas.add(simData2);
        }
        SimData simData3 = new SimData();
        simData3.setSteering(0);
        simData3.setSpeed(100);
        simDatas.add(simData3);
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


        for (int i = 0; i < 30; i++) {
            SimData simData = new SimData();
            simData.setSteering(-40 - i * 3);
            simData.setSpeed(200);
            simDatas.add(simData);
        }

    }
}
