package com.example.android.infotainment;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataLogger {
    private StringBuilder stringBuilder;

    public DataLogger() {
        stringBuilder = new StringBuilder();
    }

    /**
     * Appends data to the StringBuilder.
     * @param data
     */
    public void addData(String data) {
        stringBuilder.append(data);
    }

    /**
     * Writes the trip data to a file
     * @param tripID the id of the trip (date)
     */
    public void writeData(int tripID) {
        // TODO: 10/31/2016  
    }
}
