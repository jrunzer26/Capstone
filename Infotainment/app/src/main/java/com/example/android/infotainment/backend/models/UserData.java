package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 11/20/2016.
 */

public class UserData {

    private int tripID = 0;
    private SimData simData;
    private SensorData sensorData;

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }
    public SimData getSimData() {
        return simData;
    }

    public void setSimData(SimData simData) {
        this.simData = simData;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Trip ID: " + tripID + "\n");
        stringBuilder.append("-----------------------------------------\n");
        stringBuilder.append(sensorData.toString());
        stringBuilder.append("-----------------------------------------\n");
        stringBuilder.append(simData.toString());
        stringBuilder.append("=========================================");
        return stringBuilder.toString();
    }
}
