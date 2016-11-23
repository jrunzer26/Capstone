package com.example.android.infotainment.backend;

import android.content.Context;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;

import java.util.LinkedList;
import java.lang.Math;


/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst extends Thread implements DataReceiver {
    private Context applicationContext;
    private AlertSystem alertSystem;
    private UserDatabaseHelper userDatabaseHelper;
    private LinkedList<UserData> userDataLinkedList;
    private int userAverage = 70;


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
                int deviation = determineHRDeviation(sensorData);
                System.out.println(deviation);
                if(deviation >= 20) { //HR Deviation values come from: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2653595/
                    System.out.println("High deviation occurred");
                } else if (deviation>=10 && deviation <20) {
                    System.out.println("Moderate deviation occurred");
                } else {
                    System.out.println("No deviation occurred");
                }
                if (simData.getSpeed() > 120) { //TODO: Change this statement to work off of the results from determineHRDeviation
                    alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_FATAL, "SLOW DOWN!");
                }
            }
        }
    }

    /**
     * Determines deviations in the driver's behaviours
     * TODO: This function should use patterndata matching or alternative learning algorithms in the next semester
     * @param sensorData: The sensor data
     * @return Deviations in the heart rate
     */
    private int determineHRDeviation(SensorData sensorData) {
        int stdDev = 0;
        //Determine the estimated weighted average
        userAverage = (int)Math.round((0.9*userAverage)+(0.1*sensorData.getHeartRate()));
        stdDev = Math.abs(sensorData.getHeartRate() - userAverage);
        return stdDev;
    }
}
