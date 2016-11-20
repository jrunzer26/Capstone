package com.example.janahan.heartbeatcollector.SensorCnst;

import com.example.janahan.heartbeatcollector.ThreadConnectBTdevice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 100522058 on 11/20/2016.
 */

public class SensorData implements  Runnable{
    private int heartRate = 70;
    private ThreadConnectBTdevice bluetooth;

    public SensorData(ThreadConnectBTdevice bluetooth) {
        this.bluetooth = bluetooth;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
    public int getHeartRate() {
        return heartRate;
    }

    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + "\n";
    }

    @Override
    public void run() {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);

        try {
            dos.writeInt(heartRate);

            byte[] bytes = boas.toByteArray();
            if(bluetooth.connectedThread != null){
                System.out.print("The heart rate is: "+ heartRate);
                bluetooth.connectedThread.write(bytes);
            } else {
                System.out.println("There is no connection avaible. Please try again later!");
            }
        } catch(IOException e) {e.printStackTrace();}
    }
}
