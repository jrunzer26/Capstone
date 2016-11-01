package com.example.android.infotainment;

import android.os.AsyncTask;

/**
 * Created by 100520993 on 10/31/2016.
 */

/**
 *  Thread to listen to bluetooth information from the car simulator.
 */
public class CarBluetoothHandler implements Runnable{
    private DataReceiver dataReceiver;

    public CarBluetoothHandler(DataReceiver dataReceiver) {
        this.dataReceiver = dataReceiver;
    }

    /**
     * Listens to the incoming data and informs the dataReceiver.
     */
    @Override
    public void run() {
        // TODO: 10/31/2016 call onReceive
    }
}
