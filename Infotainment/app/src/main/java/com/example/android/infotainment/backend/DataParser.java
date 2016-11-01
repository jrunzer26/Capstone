package com.example.android.infotainment.backend;

/**
 * Created by 100520993 on 10/31/2016.
 */

import java.util.ArrayList;

/** Parses the data from the watch heart rate and the car data **/
public class DataParser implements DataReceiver {
    private DataReceiver dataReceiver;
    // buffers
    ArrayList<String> carData;
    ArrayList<String> heartRateData;

    /**
     * Constructs the Parser
     * @param dataReceiver the analyst to receive the two parts
     */
    public DataParser(DataReceiver dataReceiver) {
        this.dataReceiver = dataReceiver;
        // TODO: 10/31/2016
    }

    /**
     * Save data from one of the bluetooth handlers into buffer,
     * send if both peaces are present.
     * @param data
     */
    @Override
    public void onReceive(String data) {
        // TODO: 10/31/2016
    }
}
