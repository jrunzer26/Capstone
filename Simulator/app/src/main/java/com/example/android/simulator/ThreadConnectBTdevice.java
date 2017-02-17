package com.example.android.simulator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ThreadConnectBTdevice extends Thread {

    //Variables for connecting to the bluetooth Server
    private BluetoothSocket bluetoothSocket =null;
    private final BluetoothDevice bluetoothDevice;
    public ConnectedThread connectedThread;
    public boolean isConnected;

    /**
     * Sets up the socket that it will try to connect to
     * @param device - The server that it is trying to connect to
     * @param myUUID - The unique key that is tied to the socket
     */
    public ThreadConnectBTdevice(BluetoothDevice device, UUID myUUID){
        //Sets the bluetooth device you are trying to connect to
        isConnected = false;
        bluetoothDevice = device;
        try {
            //Trys to create a socket based off of the UUID and device object you passed in
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            System.out.println(bluetoothSocket.toString());
        } catch (Exception e) { }
    }

    /**
     * Trys to connect to the open socket on the server side
     */
    public void run() {
        try {
            //Attempts to connect to the device server socket
            bluetoothSocket.connect();
            //Creates a connectedThread object that passes in the bluetoothSocket
            connectedThread = new ConnectedThread(bluetoothSocket, this);
            //Starts the thread in the connectedThread object
            isConnected = true;
            connectedThread.start();
        } catch (Exception e) {
            Log.e("Error", "Could not connect to the server");
            isConnected = false;
        }
    }

    public void reconnect(BluetoothDevice device, UUID myUUID) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();
            //Creates a connectedThread object that passes in the bluetoothSocket
            connectedThread = new ConnectedThread(bluetoothSocket, this);
            //Starts the thread in the connectedThread object
            isConnected = true;
            connectedThread.start();
        } catch (IOException e) {
            Log.e("Error", "Could not reconnect to the server");
        }
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    /**
     * Sends a message to the server that it is disconnecting from it
     * and then closes it socket
     */
    public void cancel() {
        try {
            //Closes connection between the client and server, by sending a close message to the server
            connectedThread.cancel();
            System.out.println("The bluetooth socket is connected? "+ bluetoothSocket.isConnected());
            isConnected = false;
        } catch (Exception e) { }
    }
}
