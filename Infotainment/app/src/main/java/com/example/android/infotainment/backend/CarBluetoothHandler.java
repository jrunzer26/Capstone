package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.models.SimData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                if(bytes> 0){
                    byte[] bfcopy = new byte[bytes];
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

               // String mes = new String(buffer,0,7);
                //System.out.println("The gear is: "+ mes);
                //String mes2 = new String(buffer,7,bytes);
                //System.out.println("The speed is: "+ mes2);
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
