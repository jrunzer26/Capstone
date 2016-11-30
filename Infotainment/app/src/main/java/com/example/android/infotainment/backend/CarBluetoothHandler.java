package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.models.SimData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class CarBluetoothHandler extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final DataInputStream mmDataIS;
    private final Context act;
    private DataParser dataParser;

    public CarBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        act = mainContext;
        this.dataParser = dataParser;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {}
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mmDataIS = new DataInputStream(mmInStream);
    }

    public void run() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String keepAlive = "~";
                    mmOutStream.write(keepAlive.getBytes());
                } catch (IOException e) {}
            }
        }, 0, 1000);

        byte[] buffer = new byte[2048];
        int bytes;
        while (true) {
            try {
                System.out.println("<<<<<<<<<CAR>>>>>>>>>>>>>>>>");
                bytes = mmInStream.read(buffer);
                if(bytes> 0){
                    byte[] bfcopy = new byte[bytes];
                    for (int i = 0; i < 4; i++) { // problem with syncing threads
                        System.out.println(bfcopy.length);
                        System.arraycopy(buffer, 0, bfcopy, 0, bytes);
                        ByteArrayInputStream bais = new ByteArrayInputStream(bfcopy);
                        DataInputStream dis = new DataInputStream(bais);
                        SimData temp = new SimData();
                        temp.setGear(dis.readUTF());
                        temp.setCruseControl(dis.readBoolean());
                        temp.setPause(dis.readBoolean());
                        temp.setSpeed(dis.readDouble());
                        temp.setAcceleration(dis.readDouble());
                        temp.setSteering(dis.readDouble());
                        temp.setSignal(dis.readInt());
                        temp.setClimate(dis.readInt());
                        temp.setClimateVisibility(dis.readInt());
                        temp.setClimateDensity(dis.readInt());
                        temp.setRoadSeverity(dis.readInt());
                        temp.setTimeHour(dis.readInt());
                        temp.setTimeMinute(dis.readInt());
                        temp.setTimeSecond(dis.readInt());
                        temp.setRoadCondition(dis.readInt());
                        temp.setRoadType(dis.readInt());
                        dataParser.sendSimData(temp);
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e){}
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {}
    }
}