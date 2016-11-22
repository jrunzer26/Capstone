package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
    private String myName;
    private Context holder;
    private DataParser dataParser;
    private boolean car;

    public ThreadBeConnected(String myName, UUID myUUID, Context temp, DataParser dataParser, boolean car){
        this.dataParser = dataParser;
        this.car = car;
        this.myName = myName;
        this.myUUID = myUUID;
        try {
            holder = temp;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);
            Log.i("Attempting", "Waiting "+bluetoothServerSocket);
        } catch (Exception e){
            Log.e("Failed", "Conecction did not successed");
        }
    }

    public String getMyName() {
        return myName;
    }

    public UUID getMyUUID() {
        return myUUID;
    }

    public Context getContext() {
        return holder;
    }

    public DataParser getDataParser() {
        return dataParser;
    }

    public boolean getCar() {
        return car;
    }

    @Override
    public void run() {
        Log.e("Run", "In the run statment");
        System.out.println("Name: "+ myName+ " UUID: "+ myUUID+ " Boolean: "+car+ " BluetoothServerSockete: "+ bluetoothServerSocket + " bluetoothAdapter: "+ mBluetoothAdapter);
        if(bluetoothServerSocket !=null) {
            Log.e("If", "In the if statment");
            try {
                System.out.println("bluetoothSocket: "+ bluetoothSocket+ " bluetoothServerSocket: "+ bluetoothServerSocket);
                Log.e("Try", "In the try");
                bluetoothSocket = bluetoothServerSocket.accept();
                Log.e("After", "the bluetoothSocket accept");
                BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();
                Log.i("Connected", "Device name: "+ remoteDevice.getName());
                if (car) {
                    Log.e("Car", "In the car bluetooth");
                    new CarBluetoothHandler(bluetoothSocket, holder, dataParser, this).start();
                } else {
                    Log.e("Watch", "In the watch bluetooth");
                    new WatchBluetoothHandler(bluetoothSocket, holder, dataParser).start();
                }
            } catch (Exception e){
                Log.e("Error", "Not Correct, in the run of ThreadConnect class, please give up");
            }
        } else {
            Log.e("Error", "There was an error in the run");
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
