package com.example.android.infotainment.backend.models;

import java.util.LinkedList;


/**
 * Created by 100514374 on 2/17/2017.
 */

public class VehicleHistory {

    private LinkedList<Integer> speedList;
    private LinkedList<Double> steeringList;


    public VehicleHistory(){

        speedList = new LinkedList<Integer>();
        steeringList = new LinkedList<Double>();

    }

    public LinkedList<Integer> getSpeedHistory(){
        return speedList;
    }
    public LinkedList<Double> getTurningHistory() { return steeringList; }
}
