package com.example.android.infotainment.backend;

/**
 * Created by 100520993 on 10/31/2016.
 */

import android.content.Context;

import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;

/** Parses the data from the watch heart rate and the car data **/
public class DataParser {
    private DataReceiver dataReceiver;
    private UserDatabaseHelper userDatabaseHelper;
    private int tripID;
    // buffers
    private ArrayList<SimData> carData;
    private ArrayList<SensorData> heartRateData;

    /**
     * Constructs the Parser
     * @param dataReceiver the analyst to receive the two parts
     */
    public DataParser(DataReceiver dataReceiver, Context context, int tripID) {
        this.dataReceiver = dataReceiver;
        userDatabaseHelper = new UserDatabaseHelper(context);
        carData = new ArrayList<>(10);
        heartRateData = new ArrayList<>(10);
        this.tripID = tripID;
    }

    /**
     * Handler for sending car sim data to this.
     * @param simData the sim data
     */
    public void sendSimData(SimData simData) {
        carData.add(simData);
        trySend();
    }

    /**
     * Tries to send data to the analyst based on if data is available
     * from both the car and the wearable.
     */
    private void trySend() {
        if (carData.size() > 0  && heartRateData.size() > 0) {
            UserData userData = createUser();
            dataReceiver.onReceive(userData);
            saveData(userData);
        }
    }

    /**
     * Handler for sending sensor data from the watch
     * @param sensorData the data
     */
    public void sendHRData(SensorData sensorData) {
        heartRateData.add(sensorData);
        trySend();
    }

    /**
     * Creates a new user with the first items in the arraylist.
     * @return the user with the sim data and hr data set.
     */
    private UserData createUser() {
        UserData userData = new UserData();
        userData.setSimData(carData.get(0));
        userData.setSensorData(heartRateData.get(0));
        carData.remove(0);
        heartRateData.remove(0);
        return userData;
    }

    /**
     * Saves a user in the database.
     * @param userData
     */
    private void saveData(UserData userData) {
        userDatabaseHelper.insertSimData(userData);
    }

    /**
     * Closes the database
     */
    public void close() {
        userDatabaseHelper.close();
    }
}
