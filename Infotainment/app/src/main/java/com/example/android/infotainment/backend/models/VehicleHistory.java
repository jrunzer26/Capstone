package com.example.android.infotainment.backend.models;

import android.util.Log;

import java.util.LinkedList;


/**
 * Created by 100514374 on 2/17/2017.
 */

public class VehicleHistory {

    private LinkedList<Double> speedList;
    private LinkedList<Double> steeringList;
    private LinkedList<Double> speedingDevList;
    private int currentSize;
    private int maxSize;

    /**
     * Creates a Vehicle History object.
     * @param maxSize the max size of the data.
     */
    public VehicleHistory(int maxSize){
        this.maxSize = maxSize;
        currentSize = 0;
        speedingDevList = new LinkedList<>();
        speedList = new LinkedList<>();
        steeringList = new LinkedList<>();
    }

    /**
     * Inserts data into the vehicle history.
     * @param userData
     */
    public void insertData(UserData userData) {
        SimData simData = userData.getSimData();
        currentSize++;
        if (currentSize > maxSize) {
            speedList.remove();
            steeringList.remove();
            speedingDevList.remove();
            currentSize--;
        }
        speedList.add(simData.getSpeed());
        steeringList.add(simData.getSteering());
        speedingDevList.add(simData.getSpeedingDevPercent());
    }

    /**
     * Returns true if the vehicle data has enough to use in DTW.
     * @return true if enough
     */
    public boolean hasEnoughData() {
        return currentSize >= maxSize;
    }

    /**
     * Gets the speed history
     * @return the speed history
     */
    public LinkedList<Double> getSpeedHistory(){
        return speedList;
    }

    /**
     * Gets the turning history
     * @return turning history
     */
    public LinkedList<Double> getTurningHistory() { return steeringList; }

    /**
     * Gets the speeding deviation history
     * @return the speeding deviation history.
     */
    public LinkedList<Double> getSpeedingDevHistory() { return speedingDevList; }
}
