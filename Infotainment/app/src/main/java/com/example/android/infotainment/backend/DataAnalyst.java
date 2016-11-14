package com.example.android.infotainment.backend;

import android.content.Context;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst implements DataReceiver, Runnable {
    private Context applicationContext;
    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
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
        // TODO: 11/1/2016  
    }
}
