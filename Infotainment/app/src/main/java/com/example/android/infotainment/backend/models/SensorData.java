package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 11/20/2016.
 */

/**
 * Data from the wearable sensor.
 */
public class SensorData {
    private int heartRate;

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + "\n";
    }
}
