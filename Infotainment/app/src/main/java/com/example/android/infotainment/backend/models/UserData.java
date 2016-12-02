package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 11/20/2016.
 */

public class UserData {

    private int tripID = 0;
    private SimData simData;
    private SensorData sensorData;

    /**
     * Gets the Trip ID.
     * @return the trip id.
     */
    public int getTripID() {
        return tripID;
    }

    /**
     * Sets the trip ID.
     * @param tripID the trip ID.
     */
    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    /**
     * Gets the SimData object.
     * @return the simdata.
     */
    public SimData getSimData() {
        return simData;
    }

    /**
     * Sets the SimData.
     * @param simData - the car simulator data.
     */
    public void setSimData(SimData simData) {
        this.simData = simData;
    }

    /**
     * Gets the sensor data
     * @return the heart rate sensor object.
     */
    public SensorData getSensorData() {
        return sensorData;
    }

    /**
     * Sets the sensor data.
     * @param sensorData - the heart rate sensory object.
     */
    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }


    /**
     * Prints out the object.
     * @return
     */
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
