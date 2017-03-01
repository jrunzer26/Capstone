package com.example.android.infotainment.backend.models;

/**
 * Created by 100514374 on 2/17/2017.
 */

public class Baselines {

    private double[][] leftTurnBaseline;
    private double[][] rightTurnBaseline;
    private int[] accelBaseline;
    private int[] brakeBaseline;
    private int[] cruiseBaseline;
    private int[] speedingBaseline;

    //For the following two 2-dimensional arrays, the 0th index represents the steering data, and the 1st index represents the speed data.
    public double[][] getLeft(){
        return leftTurnBaseline;
    }

    public double[][] getRight(){
        return rightTurnBaseline;
    }

    public int[] getAccel(){
        return accelBaseline;
    }
    public int[] getBrake(){
        return brakeBaseline;
    }
    public int[] getCruise(){
        return cruiseBaseline;
    }
    public int[] getSpeeding(){
        return speedingBaseline;
    }
}
