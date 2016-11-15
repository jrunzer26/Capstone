package com.example.android.infotainment.backend;

import android.content.Context;

import com.example.android.infotainment.alert.AlertSystem;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst implements DataReceiver, Runnable {
    private Context applicationContext;
    private AlertSystem alertSystem;
    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
        alertSystem = new AlertSystem();
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
        // alert custom message
        //alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_FATAL, "testing");
    }
}
