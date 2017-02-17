package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 1/30/2017.
 */

public class TurnDataPoint {
    private double speed;
    private double steering;

    public TurnDataPoint(double speed, double steering) {
        this.speed = speed;
        this.steering = steering;
    }

    public double getSteering() {
        return steering;
    }

    public void setSteering(double steering) {
        this.steering = steering;
    }

    /**
     * Gets the speed.
     * @return the speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed.
     * @param speed the speed.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }


    /**
     * A string representation of the data point.
     * @return the data point string.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Speed: ").append(speed).append("\n")
                .append("Steering: ").append(steering).append("\n");
        return stringBuilder.toString();
    }
}
