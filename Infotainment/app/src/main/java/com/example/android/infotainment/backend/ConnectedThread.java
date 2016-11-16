package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final DataInputStream mmDataIS;

    public ConnectedThread(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


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
                    System.out.println("The value is: "+ dis.readUTF());
                    System.out.println("The boolean value for cruis is: "+ dis.readBoolean());
                    System.out.println("The boolean vlaue for pause is: "+ dis.readBoolean());
                    System.out.println("The value of the speed is: "+ dis.readDouble());
                    System.out.println("The value for acceleration is: "+ dis.readDouble());
                    System.out.println("The value for the steering is: "+ dis.readDouble());
                    System.out.println("The value of signal is: "+ dis.readInt());
                    System.out.println("THe value of climate is: "+ dis.readInt());
                    System.out.println("The value of visibility is: "+ dis.readInt());
                    System.out.println("The value of feel is: "+ dis.readInt());
                    System.out.println("THe value of severity is: "+ dis.readInt());
                    System.out.println("The value of time is: "+ dis.readInt()+":"+dis.readInt()+"."+dis.readInt());
                    System.out.println("The value of roadCondition: "+dis.readInt());
                    System.out.println("The value of road type is: "+ dis.readInt());
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
