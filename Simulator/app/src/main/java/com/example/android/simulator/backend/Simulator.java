package com.example.android.simulator.backend;

import android.content.Context;
import android.view.View;

import com.example.android.simulator.ThreadConnectBTdevice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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


    private boolean gearPark = false;
    private boolean gearReverse = false;
    private boolean gearDrive = false;

    private boolean climateSunny = false;
    private boolean climateHail = false;
    private boolean climateSnowy = false;
    private boolean climateRain = false;

    private boolean roadConIce = false;
    private boolean roadConWarmIce = false;
    private boolean roadConWet = false;

    private boolean roadTypeGravel = false;
    private boolean roadTypePaved = false;
    private boolean roadTypeDirt = false;


    private ArrayList<Double> speed = new ArrayList<>();
    private ArrayList<String> gear = new ArrayList<>();
    private boolean crusieControl = false;
    private boolean pause = false;
    private int signal = 0;
    private ArrayList<Double> steering = new ArrayList<>();
    private ArrayList<Double> acceleration =new ArrayList<>();
    private ArrayList<Integer> climate = new ArrayList<>();
    private ArrayList<Integer> climateVisibility = new ArrayList<>();
    private ArrayList<Integer> roadSeverity = new ArrayList<>();
    private ArrayList<Integer> climateFeel = new ArrayList<>();
    private ArrayList<Integer> timeHour = new ArrayList<>();
    private ArrayList<Integer> timeMinute = new ArrayList<>();
    private ArrayList<Integer> timeSecond =new ArrayList<>();
    private ArrayList<Integer> roadCondition = new ArrayList<>();
    private ArrayList<Integer> roadType =new ArrayList<>();
    private ThreadConnectBTdevice blueTooth;




    /**
     * Stores data in the buffer;
     */
    private void storeData(byte [] bytes) {
        // TODO: 11/3/2016 Simulator - storeData
    }

    public Simulator(Context context, View view, ThreadConnectBTdevice device) {
        blueTooth = device;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("In the run statement");
                if(gearPark) {
                    gear.add("Park");
                } else if(gearReverse) {
                    gear.add("Reverse");
                } else if(gearDrive) {
                    gear.add("Drive");
                } else {
                    gear.add("Park");
                }

                if(climateHail) {
                    climate.add(CLIMATE_HAIL);
                } else if(climateSnowy) {
                    climate.add(CLIMATE_SNOWY);
                } else if(climateSunny) {
                    climate.add(CLIMATE_SUNNY);
                } else if(climateRain) {
                    climate.add(CLIMATE_RAIN);
                } else {
                    climate.add(CLIMATE_SUNNY);
                }

                if(roadConWet) {
                    roadCondition.add(ROAD_CON_WET);
                } else if(roadConWarmIce) {
                    roadCondition.add(ROAD_CON_WARM_ICE);
                } else if(roadConIce) {
                    roadCondition.add(ROAD_CON_ICE);
                } else {
                    roadCondition.add(0);
                }

                if(roadTypePaved) {
                    roadType.add(ROAD_TYPE_PAVED);
                } else if(roadTypeGravel) {
                    roadType.add(ROAD_TYPE_GRAVEL);
                } else if(roadTypeDirt) {
                    roadType.add(ROAD_TYPE_DIRT);
                } else {
                    roadType.add(ROAD_TYPE_PAVED);
                }
            }
        }, 0, 1000);
    }
    /**
     * Polls for data every 5 seconds.
     */
    @Override
    public void run() {
        // TODO: 11/3/2016 Simulator - run
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        for(int i =0; i<5; i++) {
            try {

                if(blueTooth.connectedThread != null) {
                    System.out.println(gear.get(i));
                    dos.writeUTF(gear.get(i));
                    dos.writeBoolean(crusieControl);
                    dos.writeBoolean(pause);
                    dos.writeDouble(speed.get(i));
                    dos.writeDouble(acceleration.get(i));
                    dos.writeDouble(steering.get(i));
                    dos.writeInt(signal);
                    dos.writeInt(climate.get(i));
                    dos.writeInt(climateVisibility.get(i));
                    dos.writeInt(climateFeel.get(i));
                    dos.writeInt(roadSeverity.get(i));
                    dos.writeInt(timeHour.get(i));
                    dos.writeInt(timeMinute.get(i));
                    dos.writeInt(timeSecond.get(i));
                    dos.writeInt(roadCondition.get(i));
                    dos.writeInt(roadType.get(i));

                    byte[] bytes = boas.toByteArray();
                    blueTooth.connectedThread.write(bytes);
                } else {
                    System.out.println("There is no connection avaible. Please try again later!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        gear.clear();
        speed.clear();
        acceleration.clear();
        steering.clear();
        climate.clear();
        climateVisibility.clear();
        climateFeel.clear();
        roadSeverity.clear();
        timeHour.clear();
        timeMinute.clear();
        timeSecond.clear();
        roadCondition.clear();
        roadType.clear();
    }

    @Override
    public void park() {
        gearPark = true;
        gearDrive = false;
        gearReverse = false;
    }

    @Override
    public void reverse() {
        gearReverse = true;
        gearPark = false;
        gearDrive = false;
    }

    @Override
    public void drive() {
        this.gearDrive = true;
        this.gearPark = false;
        this.gearReverse = false;
        System.out.println("The vales in drive method: "+ gearDrive+ " "+ gearPark+ " "+gearReverse);
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
        steering.add(degree);
    }

    public void setAcceleration(double acceleration){
        this.acceleration.add(acceleration);
    }

    @Override
    public double getSteering() {
        return steering.get(0);
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
        climateSunny = true;
        climateSnowy = false;
        climateRain = false;
        climateHail = false;
    }

    public void climateHail() {
        climateSunny = false;
        climateSnowy = false;
        climateRain = false;
        climateHail = true;
    }

    public void climateSnowy() {
        climateSunny = false;
        climateSnowy = true;
        climateRain = false;
        climateHail = false;
    }

    public void climateRain() {
        climateSunny = false;
        climateSnowy = false;
        climateRain = true;
        climateHail = false;
    }

    public void setSpeed(double speed) {
        System.out.println("The speed is: "+ speed);
        this.speed.add(speed);
    }

    public void roadConditionIce() {
        roadConIce = true;
        roadConWarmIce = false;
        roadConWet = false;
    }

    public void roadConditionWarmIce() {
        roadConIce = false;
        roadConWarmIce = true;
        roadConWet = false;
    }

    public void roadConditionWet() {
        roadConIce = false;
        roadConWarmIce = false;
        roadConWet = true;
    }

    public void roadTypeDirt() {
        roadTypeDirt = true;
        roadTypeGravel = false;
        roadTypePaved = false;
    }

    public void roadTypePaved() {
        roadTypeDirt = false;
        roadTypeGravel = false;
        roadTypePaved = true;
    }

    public void roadTypeGravel() {
        roadTypeDirt = false;
        roadTypeGravel = true;
        roadTypePaved = false;
    }

    public void setHour(int hour) {
        timeHour.add(hour);
    }

    public void setMin(int min) {
        timeMinute.add(min);
    }

    public void setSecond(int second) {
        timeSecond.add(second);
    }

    public void setVisibility(int visibility){
        this.climateVisibility.add(visibility);
    }

    public void setClimateFeel(int feeling){
        this.climateFeel.add(feeling);
    }

    public void setSeverity(int severity) {
        this.roadSeverity.add(severity);
    }

}
