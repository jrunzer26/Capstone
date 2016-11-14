package com.example.android.infotainment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ThreadBeConnected extends Thread{
    private BluetoothServerSocket bluetoothServerSocket = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket bluetoothSocket = null;
    private UUID myUUID;
    public ConnectedThread connectedThread;

    public ThreadBeConnected(String myName, UUID myUUID){
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);
            Log.i("Attempting", "Waiting "+bluetoothServerSocket);
        } catch (Exception e){
            Log.e("Failed", "Conecction did not successed");
        }
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket = null;
        if(bluetoothServerSocket !=null) {
            try {
                bluetoothSocket = bluetoothServerSocket.accept();
                BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();
                Log.i("Connected", "Device name: "+ remoteDevice.getName());
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
            } catch (Exception e){
                Log.e("Error", "Not Correct, in the run of ThreadConnect class, please give up");
            }
        }
    }

    public void cancel() {
        try{
            bluetoothSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}