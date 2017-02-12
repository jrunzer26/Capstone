package com.example.janahan.heartbeatcollector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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


    public boolean getIsConnected() {
        return isConnected;
    }

    /**
     * Closes the bluetooth socket
     */
    public void cancel() {
        try {
            //Closes connection between the client and server, by sending a close message to the server
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(boas);
            dos.writeUTF("close");
            byte[] bytes = boas.toByteArray();
            connectedThread.write(bytes);
            connectedThread.cancel();
            bluetoothSocket.close();
            isConnected = false;
        } catch (Exception e) { }
    }
}
