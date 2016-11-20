package com.example.janahan.heartbeatcollector;

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
            System.out.println(bluetoothSocket.toString());
        } catch (Exception e) { }
    }

    public void run() {
        try {
            System.out.println(bluetoothSocket.toString());
            bluetoothSocket.connect();
            System.out.println("The bluetooth socket is: "+bluetoothSocket.toString());
            connectedThread = new ConnectedThread(bluetoothSocket);
            System.out.println("The connected that is: "+ connectedThread.toString());
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
