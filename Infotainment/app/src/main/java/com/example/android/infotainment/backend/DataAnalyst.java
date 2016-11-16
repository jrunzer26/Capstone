package com.example.android.infotainment.backend;

import android.content.Context;

import com.example.android.infotainment.alert.AlertSystem;


/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst implements DataReceiver, Runnable {
    private Context applicationContext;
    private AlertSystem alertSystem;
    private UserDatabaseHelper userDatabaseHelper;

    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
        alertSystem = new AlertSystem();
        userDatabaseHelper = new UserDatabaseHelper(applicationContext);
    }

    @Override
    public void onReceive(String data) {
        // TODO: 10/31/2016 implement processing of watch and car data 
    }

    /**
     * Analyze the heart rate and car data.
     */
    @Override
    public void run() {
        // alert default message
        alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_WARNING);
        // alert custom message sample
        //alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_FATAL, "testing");

        // sample on how to use the database
        userDatabaseHelper = new UserDatabaseHelper(applicationContext);
        SimData simData = new SimData();
        simData.setSpeed(100);
        simData.setHeartRate(65);
        simData.setTripID(userDatabaseHelper.getNextTripID());
        userDatabaseHelper.insertSimData(simData);
        SimData nextTripSample = new SimData();
        nextTripSample.setTripID(userDatabaseHelper.getNextTripID());
        userDatabaseHelper.insertSimData(nextTripSample);
        userDatabaseHelper.showData(applicationContext);
    }
}
