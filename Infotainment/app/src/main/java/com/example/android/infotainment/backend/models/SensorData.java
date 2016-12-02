package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 11/20/2016.
 */

/**
 * Data from the wearable sensor.
 */
public class SensorData {

    private int heartRate;

    /**
     * Sets the heart rate.
     * @param heartRate
     */
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    /**
     * Gets the heart rate.
     * @return heart rate.
     */
    public int getHeartRate() {
        return heartRate;
    }

    /**
     * Prints the object.
     */
    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + "\n";
    }
}
