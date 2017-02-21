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

    public Turn(int id, int turnType, int flag) {
        this.id = id;
        this.turnType = turnType;
        data = new ArrayList<>();
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

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


