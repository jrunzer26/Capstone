package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 1/30/2017.
 */

public class TurnDataPoint {
    private double speed;
    private int timeTaken;
    private int heartRate;
    private double steering;

    public TurnDataPoint(double speed, int timeTaken, int heartRate, double steering) {
        this.speed = speed;
        this.timeTaken = timeTaken;
        this.heartRate = heartRate;
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
     * Gets the time taken.
     * @return the time taken.
     */
    public int getTimeTaken() {
        return timeTaken;
    }

    /**
     * Sets the time taken.
     * @param timeTaken the time taken.
     */
    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * Gets the heart rate.
     * @return the HR.
     */
    public int getHeartRate() {
        return heartRate;
    }

    /**
     * Sets the heart rate.
     * @param heartRate the HR.
     */
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    /**
     * A string representation of the data point.
     * @return the data point string.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Speed: ").append(speed).append("\n")
                .append("Time Taken: ").append(timeTaken).append("\n")
                .append("Heart Rate: ").append(heartRate).append("\n")
                .append("Steering: ").append(steering).append("\n");
        return stringBuilder.toString();
    }
}
