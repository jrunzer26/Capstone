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

    public VehicleHistory(int maxSize){
        this.maxSize = maxSize;
        currentSize = 0;
        speedingDevList = new LinkedList<>();
        speedList = new LinkedList<>();
        steeringList = new LinkedList<>();
    }

    public void insertData(UserData userData) {
        SimData simData = userData.getSimData();
        speedList.add(simData.getSpeed());
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

    public boolean hasEnoughData() {
        return currentSize >= maxSize;
    }



    public LinkedList<Double> getSpeedHistory(){
        return speedList;
    }
    public LinkedList<Double> getTurningHistory() { return steeringList; }
    public LinkedList<Double> getSpeedingDevHistory() { return speedingDevList; }
}
