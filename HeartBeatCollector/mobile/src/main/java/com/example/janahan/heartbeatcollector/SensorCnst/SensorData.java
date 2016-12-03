package com.example.janahan.heartbeatcollector.SensorCnst;

import com.example.janahan.heartbeatcollector.ThreadConnectBTdevice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 100522058 on 11/20/2016.
 */

public class SensorData implements  Runnable{
    private ArrayList<Integer> heartRate = new ArrayList<>();
    private ThreadConnectBTdevice bluetooth;

    public SensorData(ThreadConnectBTdevice bluetooth) {
        this.bluetooth = bluetooth;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate.add(heartRate);
    }
    public int getHeartRate() {
        return heartRate.get(0);
    }

    /**
     * Gets the heart rate of the slider
     * @return the heart rate of the slider
     */
    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + "\n";
    }

    /**
     * Packages the array of heartRate into a byte array
     * and sends it off to the server side if there is a connection
     */
    @Override
    public void run() {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        for(int i = 0; i < 5; i++) {
            try {
                dos.writeInt(heartRate.get(i));


            } catch(IOException e) {e.printStackTrace();}
        }
        byte[] bytes = boas.toByteArray();
        if(bluetooth.connectedThread != null){
            System.out.print("The heart rate is: "+ heartRate);
            bluetooth.connectedThread.write(bytes);
        } else {
            System.out.println("There is no connection available. Please try again later!");
        }
        heartRate.clear();
    }
}
