package com.example.android.infotainment;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class WatchBluetoothHandler implements Runnable {
    private DataReceiver dataReceiver;
    
    public WatchBluetoothHandler(DataReceiver dataReceiver) {
        this.dataReceiver = dataReceiver;
    }
    /**
     * Listens to the Watch Bluetooth connection.
     */
    @Override
    public void run() {
        // TODO: 10/31/2016  
    }
}
