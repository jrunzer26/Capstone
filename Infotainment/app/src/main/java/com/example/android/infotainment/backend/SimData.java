package com.example.android.infotainment.backend;

/**
 * Created by 100520993 on 11/15/2016.
 */

/**
 * Holds the data received from the car and wearable for the database.
 */
public class SimData {

    private double speed = 0;
    private String gear = null;
    private boolean cruseControl = false;
    private int signal = 0;
    private double steering = 0;
    private double acceleration = 0;
    private int climate = 0;
    private double climateVisibility = 0;
    private int timeHour = 0;
    private int timeMinute = 0;
    private int timeSecond = 0;
    private int roadCondition = 0;

    private int heartRate = 0;

    private int tripID = 0;

    /**
     * Outputs the state of the SimData.
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("trip id: ").append(tripID).append("\n");
        stringBuilder.append("speed: ").append(speed).append("\n");
        stringBuilder.append("gear: ").append(gear).append("\n");
        stringBuilder.append("cruseControl: ").append(cruseControl).append("\n");
        stringBuilder.append("signal: ").append(signal).append("\n");
        stringBuilder.append("steering: ").append(steering).append("\n");
        stringBuilder.append("acceleration: ").append(acceleration).append("\n");
        stringBuilder.append("climate: ").append(climate).append("\n");
        stringBuilder.append("climateVisibility: ").append(climateVisibility).append("\n");
        stringBuilder.append("timeHour: ").append(timeHour).append("\n");
        stringBuilder.append("timeMinute: ").append(timeMinute).append("\n");
        stringBuilder.append("timeSecond: ").append(timeSecond).append("\n");
        stringBuilder.append("roadCondition: ").append(roadCondition).append("\n");
        stringBuilder.append("heartRate: ").append(heartRate).append("\n");
        return stringBuilder.toString();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public boolean isCruseControl() {
        return cruseControl;
    }

    public void setCruseControl(boolean cruseControl) {
        this.cruseControl = cruseControl;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public double getSteering() {
        return steering;
    }

    public void setSteering(double steering) {
        this.steering = steering;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate(int climate) {
        this.climate = climate;
    }

    public double getClimateVisibility() {
        return climateVisibility;
    }

    public void setClimateVisibility(double climateVisibility) {
        this.climateVisibility = climateVisibility;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public int getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(int timeSecond) {
        this.timeSecond = timeSecond;
    }

    public int getRoadCondition() {
        return roadCondition;
    }

    public void setRoadCondition(int roadCondition) {
        this.roadCondition = roadCondition;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }
}
