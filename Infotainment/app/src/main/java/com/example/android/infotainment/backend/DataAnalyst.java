package com.example.android.infotainment.backend;

import android.content.Context;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;

import java.util.LinkedList;


/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst extends Thread implements DataReceiver {
    private Context applicationContext;
    private AlertSystem alertSystem;
    private UserDatabaseHelper userDatabaseHelper;
    private LinkedList<UserData> userDataLinkedList;

    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
        alertSystem = new AlertSystem();
        userDatabaseHelper = new UserDatabaseHelper(applicationContext);
        userDataLinkedList = new LinkedList<>();
    }

    @Override
    public void onReceive(UserData userData) {
        // TODO: 10/31/2016 implement processing of watch and car data
        userDataLinkedList.add(userData);
    }

    /**
     * Analyze the heart rate and car data.
     */
    @Override
    public void run() {
        while (true) {
            // check to see if data is available
            if (userDataLinkedList.size() > 0) {
                UserData userData = userDataLinkedList.get(0);
                System.out.println(userData.toString());
                userDataLinkedList.remove(0);
                SensorData sensorData = userData.getSensorData();
                SimData simData = userData.getSimData();
                if (simData.getSpeed() > 120) {
                    alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_FATAL, "SLOW DOWN!");
                }
            }
        }
    }
}
