package com.example.android.infotainment.backend.models;

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
    private boolean pause = false;
    private int signal = 0;
    private double steering = 0;
    private double acceleration = 0;
    private int climate = 0;
    private int climateVisibility = 0;
    private int climateDensity = 0;
    private int roadSeverity = 0;
    private int timeHour = 0;
    private int timeMinute = 0;
    private int timeSecond = 0;
    private int roadCondition = 0;
    private int roadType =0;


    /**
     * Outputs the state of the SimData.
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("speed: ").append(speed).append("\n");
        stringBuilder.append("gear: ").append(gear).append("\n");
        stringBuilder.append("cruseControl: ").append(cruseControl).append("\n");
        stringBuilder.append("pause: ").append(pause).append("\n");
        stringBuilder.append("signal: ").append(signal).append("\n");
        stringBuilder.append("steering: ").append(steering).append("\n");
        stringBuilder.append("acceleration: ").append(acceleration).append("\n");
        stringBuilder.append("climate: ").append(climate).append("\n");
        stringBuilder.append("climateVisibility: ").append(climateVisibility).append("\n");
        stringBuilder.append("climateDensity: ").append(climateDensity).append("\n");
        stringBuilder.append("roadSeverity: ").append(roadSeverity).append("\n");
        stringBuilder.append("timeHour: ").append(timeHour).append("\n");
        stringBuilder.append("timeMinute: ").append(timeMinute).append("\n");
        stringBuilder.append("timeSecond: ").append(timeSecond).append("\n");
        stringBuilder.append("roadCondition: ").append(roadCondition).append("\n");
        stringBuilder.append("roadType: ").append(roadType).append("\n");
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

    public void setPause(boolean pause) {this.pause = pause; }

    public boolean isPaused() {return pause;}

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

    public int getClimateVisibility() {
        return climateVisibility;
    }

    public void setClimateVisibility(int climateVisibility) {
        this.climateVisibility = climateVisibility;
    }

    public void setClimateDensity(int density){
        this.climateDensity = density;
    }

    public int getClimateDensity() {return climateDensity;}

    public void setRoadSeverity(int severity){
        this.roadSeverity = severity;
    }

    public int getRoadSeverity() {return roadSeverity;}

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

    public void setRoadType(int roadType) {this.roadType = roadType; }

    public int getRoadType() {return roadType;}




}
