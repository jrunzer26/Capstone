package com.example.android.infotainment.backend.models;

import java.util.ArrayList;

/**
 * Created by 100520993 on 1/30/2017.
 */

public class Turn {
    public static final int TURN_LEFT = 0;
    public static final int TURN_RIGHT = 1;

    private int turnType;
    private ArrayList<TurnDataPoint> data;
    private int id;
    private int flag;
    private int multi;

    /**
     * Creates a new turn.
     * @param turnType TURN_LEFT or TURN_RIGHT
     * @param id the turn id for the database.
     */
    public Turn(int turnType, int id) {
        this.turnType = turnType;
        data = new ArrayList<>();
        this.id = id;
        flag = UserData.FLAG_NONE;
    }

    /**
     * Creates a turn
     * @param id the id of the turn
     * @param turnType TURN_LEFT or TURN_RIGHT
     * @param flag the flag
     */
    public Turn(int id, int turnType, int flag) {
        this.id = id;
        this.turnType = turnType;
        data = new ArrayList<>();
        this.flag = flag;
    }

    /**
     * Returns the multiplicity of the baseline.
     * @return the number of times to dba
     */
    public int getMulti() {
        return multi;
    }

    /**
     * Sets the multi parameter
     * @param multi the int value
     */
    public void setMulti(int multi) {
        this.multi = multi;
    }

    /**
     * Gets the flag of the turn.
     * @return the flag
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Sets the flag of the turn.
     * @param flag the flag.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Add a point to the turn
     * @param dataPoint data point for a turn.
     */
    public void addTurnPoint(TurnDataPoint dataPoint) {
        data.add(dataPoint);
    }

    /**
     * Gets the turn type.
     * @return the turn type.
     */
    public int getTurnType() {
        return turnType;
    }

    /**
     * Gets the data points
     * @return the data points.
     */
    public ArrayList<TurnDataPoint> getTurnDataPoints() {
        return data;
    }

    /**
     * Gets the id of the turn.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * String representation of all turn data points.
     * @return the built string
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TurnID: ").append(id)
            .append("\n-------------------------------\n");
        for (TurnDataPoint point : data) {
            stringBuilder.append(point).append("-------------------------------\n");
        }
        stringBuilder.append("===============================\n");
        return stringBuilder.toString();
    }

    public int size() {
        return data.size();
    }
}


