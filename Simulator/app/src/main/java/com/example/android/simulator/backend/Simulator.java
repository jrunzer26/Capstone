package com.example.android.simulator.backend;

import android.content.Context;
import android.view.View;

import com.example.android.simulator.ThreadConnectBTdevice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
    private final int ROAD_TYPE_DIRT = 52;


    private double speed = 0;
    private String gear = "Park";
    private boolean crusieControl = false;
    private boolean pause = false;
    private int signal = 0;
    private double steering = 0;
    private double acceleration =0;
    private int climate = 0;
    private int climateVisibility = 0;
    private int roadSeverity =0;
    private int climateFeel = 0;
    private int timeHour = 0;
    private int timeMinute = 0;
    private int timeSecond =0;
    private int roadCondition =0;
    private int roadType =0;
    private ThreadConnectBTdevice blueTooth;


    /**
     * Stores data in the buffer;
     */
    private void storeData(byte [] bytes) {
        // TODO: 11/3/2016 Simulator - storeData
    }

    public Simulator(Context context, View view, ThreadConnectBTdevice device) {
        blueTooth = device;
    }
    /**
     * Polls for data every 5 seconds.
     */
    @Override
    public void run() {
        // TODO: 11/3/2016 Simulator - run
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);

        try {
            dos.writeUTF(gear);
            dos.writeBoolean(crusieControl);
            dos.writeBoolean(pause);
            dos.writeDouble(speed);
            dos.writeDouble(acceleration);
            dos.writeDouble(steering);
            dos.writeInt(signal);
            dos.writeInt(climate);
            dos.writeInt(climateVisibility);
            dos.writeInt(climateFeel);
            dos.writeInt(roadSeverity);
            dos.writeInt(timeHour);
            dos.writeInt(timeMinute);
            dos.writeInt(timeSecond);
            dos.writeInt(roadCondition);
            dos.writeInt(roadType);

            byte[] bytes = boas.toByteArray();
            if(blueTooth.connectedThread != null){
                blueTooth.connectedThread.write(bytes);
            } else {
                System.out.println("There is no connection avaible. Please try again later!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void park() {
        gear = "Park";
    }

    @Override
    public void reverse() {
        gear = "Reverse";
    }

    @Override
    public void drive() {
        gear = "Drive";
    }

    @Override
    public void cruise() {
        crusieControl = true;
    }

    @Override
    public void pause() {
        pause = true;
    }

    public void setSteering(double degree){
        steering = degree;
    }

    public void setAcceleration(double acceleration){
        this.acceleration = acceleration;
    }

    @Override
    public double getSteering() {
        return steering;
    }

    @Override
    public void signalLeft() {
        signal = SIGNAL_LEFT;
    }

    @Override
    public void signalRight() {
        signal = SIGNAL_RIGHT;
    }

    @Override
    public void changeLeft() {

    }

    @Override
    public void changeRight() {

    }

    public void climateSuuny() {
        climate = CLIMATE_SUNNY;
    }

    public void climateHail() {
        climate = CLIMATE_HAIL;
    }

    public void climateSnowy() {
        climate = CLIMATE_SNOWY;
    }

    public void climateRain() {
        climate = CLIMATE_RAIN;
    }

    public void setSpeed(double speed) {
        this.speed =speed;
    }

    public void roadConditionIce() {
        roadCondition = ROAD_CON_ICE;
    }

    public void roadConditionWarmIce() {
        roadCondition = ROAD_CON_WARM_ICE;
    }

    public void roadConditionWet() {
        roadCondition = ROAD_CON_WET;
    }

    public void roadTypeDirt() {
        roadType = ROAD_TYPE_DIRT;
    }

    public void roadTypePaved() {
        roadType = ROAD_TYPE_PAVED;
    }

    public void roadTypeGravel() {
        roadType = ROAD_TYPE_GRAVEL;
    }

    public void setHour(int hour) {
        timeHour = hour;
    }

    public void setMin(int min) {
        timeMinute = min;
    }

    public void setSecond(int second) {
        timeSecond = second;
    }

    public void setVisibility(int visibility){
        this.climateVisibility = visibility;
    }

    public void setClimateFeel(int feeling){
        this.climateFeel = feeling;
    }

    public void setSeverity(int severity) {
        this.roadSeverity = severity;
    }

}
