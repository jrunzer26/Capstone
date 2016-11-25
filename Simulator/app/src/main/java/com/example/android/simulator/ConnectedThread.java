package com.example.android.simulator;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

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
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                System.out.println(readMessage);
            } catch (Exception e) {}
        }
    }

    public void write(byte[] bytes) {
        try {
            Log.e("Writing", "Message");
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {}
    }
}

