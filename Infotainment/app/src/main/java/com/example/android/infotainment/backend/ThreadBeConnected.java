package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.backend.ConnectedThread;

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
    private Context holder;
    public ConnectedThread connectedThread;

    public ThreadBeConnected(String myName, UUID myUUID, Context temp){
        try {
            holder = temp;
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
                connectedThread = new ConnectedThread(bluetoothSocket, holder);
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
