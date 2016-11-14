package com.example.android.simulator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.UUID;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ThreadConnectBTdevice extends Thread {
    private BluetoothSocket bluetoothSocket =null;
    private final BluetoothDevice bluetoothDevice;
    public ConnectedThread connectedThread;

    public ThreadConnectBTdevice(BluetoothDevice device, UUID myUUID){
        bluetoothDevice = device;
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
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
            bluetoothSocket.close();
        } catch (Exception e) { }
    }
}
