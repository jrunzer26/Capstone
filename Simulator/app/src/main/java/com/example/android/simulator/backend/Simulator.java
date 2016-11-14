package com.example.android.simulator.backend;

import android.content.Context;
import android.view.View;

/**
 * Created by 100520993 on 11/3/2016.
 */

public class Simulator implements Runnable, Car {

    private final int CLIMATE_SUNNY = 10;
    private final int CLIMATE_HAIL = 11;
    private final int CLIMATE_SNOWY = 12;
    private final int CLIMATE_RAIN = 13;

    private final int SIGNAL_RIGHT = 20;
    private final int SIGNAL_LEFT = 21;

    private final int CHANGE_RIGHT = 30;
    private final int CHANGE_LEFT = 31;

    private final int ROAD_CON_ICE = 40;
    private final int ROAD_CON_WARM_ICE = 41;
    private final int ROAD_CON_WET = 42;

    private final int ROAD_TYPE_GRAVEL = 50;
    private final int ROAD_TYPE_PAVED = 51;

    private final int GEAR_PARK = 60;
    private final int GEAR_DRIVE = 61;
    private final int GEAR_REVERSE = 62;


    private double speed;
    private String gear;
    private boolean crusieControl;
    private boolean pause;
    private int signal;
    private double steering;
    private double acceleration;
    private int climate;
    private double climateVisibility;
    private int timeHour;
    private int timeMinute;
    private int timeSecond;
    private int timeAM;
    private int roadCondition;
    private byte[] dataBuffer;
    private BluetoothHandler bluetoothHandler;


    /**
     * Stores data in the buffer;
     */
    private void storeData(byte [] bytes) {
        // TODO: 11/3/2016 Simulator - storeData
    }

    public Simulator(Context context, View view) {
        
    }
    /**
     * Polls for data every 5 seconds.
     */
    @Override
    public void run() {
        // TODO: 11/3/2016 Simulator - run
    }

    @Override
    public void park() {

    }

    @Override
    public void reverse() {

    }

    @Override
    public void drive() {

    }

    @Override
    public void cruise() {

    }

    @Override
    public void pause() {

    }

    @Override
    public double getSteering() {
        return 0;
    }

    @Override
    public void signalLeft() {

    }

    @Override
    public void signalRight() {

    }

    @Override
    public void changeLeft() {

    }

    @Override
    public void changeRight() {

    }
}
