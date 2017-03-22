package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 11/20/2016.
 */

public class UserData {

    public static final int FLAG_NONE = 0;
    public static final int FLAG_LEFT_TURN = 1;
    public static final int FLAG_RIGHT_TURN = 2;
    public static final int FLAG_SPEEDING = 3;
    public static final int FLAG_LEFT_TURN_SPEEDING = 4;
    public static final int FLAG_RIGHT_TURN_SPEEDING = 5;
    public static final int FLAG_STRAIGHT = 6;
    public static final int FLAG_STRAIGHT_SPEEDING = 7;
    public static final int FLAG_EXIT_TURN = 8;
    public static final int FLAG_ACCELERATION = 9;
    public static final int FLAG_BRAKING = 10;
    public static final int FLAG_ACCELERATION_NEAR_STOP = 11;
    public static final int FLAG_ACCELERATION_FROM_SPEED = 12;

    private int tripID = 0;
    private SimData simData;
    private SensorData sensorData;
    private int turnFlag = FLAG_NONE;


    private void initFlags() {
        if (simData.getSteering() > 10) {
            turnFlag = FLAG_RIGHT_TURN;
        } else if(simData.getSteering() < -10) {
            turnFlag = FLAG_LEFT_TURN;
        } else {
            turnFlag = FLAG_NONE;
        }
    }


    public int getTurnFlag() {
        return turnFlag;
    }

    public void setTurnFlag(int turnFlag) {
        this.turnFlag = turnFlag;
    }

    public void addToFlag(int flag) {
        if (flag == FLAG_SPEEDING) {
            if (this.turnFlag == FLAG_LEFT_TURN) {
                this.turnFlag = FLAG_LEFT_TURN_SPEEDING;
            } else if (this.turnFlag == FLAG_RIGHT_TURN) {
                this.turnFlag = FLAG_RIGHT_TURN_SPEEDING;
            } else {
                this.turnFlag = FLAG_SPEEDING;
            }
        }
    }

    /**
     * Gets the Trip ID.
     * @return the trip id.
     */
    public int getTripID() {
        return tripID;
    }

    /**
     * Sets the trip ID.
     * @param tripID the trip ID.
     */
    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    /**
     * Gets the SimData object.
     * @return the simdata.
     */
    public SimData getSimData() {
        return simData;
    }

    /**
     * Sets the SimData.
     * @param simData - the car simulator data.
     */
    public void setSimData(SimData simData) {
        this.simData = simData;
        initFlags();
    }

    /**
     * Gets the sensor data
     * @return the heart rate sensor object.
     */
    public SensorData getSensorData() {
        return sensorData;
    }

    /**
     * Sets the sensor data.
     * @param sensorData - the heart rate sensory object.
     */
    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }


    /**
     * Prints out the object.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Trip ID: " + tripID + "\n");
        stringBuilder.append("Flag: " + turnFlag + "\n");
        stringBuilder.append("-----------------------------------------\n");
        stringBuilder.append(sensorData.toString());
        stringBuilder.append("-----------------------------------------\n");
        stringBuilder.append(simData.toString());
        stringBuilder.append("=========================================");
        return stringBuilder.toString();
    }
}
