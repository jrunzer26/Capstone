package com.example.android.infotainment.backend.models;

import java.util.LinkedList;


/**
 * Created by 100514374 on 2/17/2017.
 */

public class VehicleSpeedHistory {

    private LinkedList<Integer> speedList;

    public VehicleSpeedHistory(){
        speedList = new LinkedList<Integer>();
    }

    public LinkedList<Integer> getHistory(){
        return speedList;
    }
}
