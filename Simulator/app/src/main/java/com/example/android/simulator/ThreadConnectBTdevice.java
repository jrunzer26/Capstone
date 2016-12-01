package com.example.android.simulator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ThreadConnectBTdevice extends Thread {
    private BluetoothSocket bluetoothSocket =null;
    private final BluetoothDevice bluetoothDevice;
    public ConnectedThread connectedThread;
    public Button reCon_Button;

    public ThreadConnectBTdevice(BluetoothDevice device, UUID myUUID){
        bluetoothDevice = device;
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            System.out.println(bluetoothSocket.toString());
        } catch (Exception e) { }
    }

    public void run() {
        try {
            bluetoothSocket.connect();
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
        } catch (Exception e) {
            Log.e("Error", "Could not connect to the server");
        }
    }

    public void cancel() {
        try {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(boas);
            dos.writeUTF("close");
            byte[] bytes = boas.toByteArray();
            connectedThread.write(bytes);
            connectedThread.cancel();
            bluetoothSocket.close();
        } catch (Exception e) { }
    }
}
