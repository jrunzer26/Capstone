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

    /**
     * Gets the speed.
     * @return the car speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Set the speed.
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Gets the gear.
     * @return the gear.
     */
    public String getGear() {
        return gear;
    }

    /**
     * Sets the gear
     * @param gear - the gear.
     */
    public void setGear(String gear) {
        this.gear = gear;
    }

    /**
     * Returns if the cruse control is enabled.
     * @return true if cruse control is on.
     */
    public boolean isCruseControl() {
        return cruseControl;
    }

    /**
     * Sets the cruise control.
     * @param cruseControl - true if cruise control is enabled.
     */
    public void setCruseControl(boolean cruseControl) {
        this.cruseControl = cruseControl;
    }

    /**
     * Sets pause.
     * @param pause - true to pause the simulation
     */
    public void setPause(boolean pause) {this.pause = pause; }

    /**
     * Returns true if paused.
     * @return true if paused.
     */
    public boolean isPaused() {return pause;}

    /**
     * Gets the turn signal.
     * @return the turn signal
     */
    public int getSignal() {
        return signal;
    }

    /**
     * Sets the turn signal.
     * @param signal - the turn signal.
     */
    public void setSignal(int signal) {
        this.signal = signal;
    }

    /**
     * Gets the steering degree.
     * @return the steering degree.
     */
    public double getSteering() {
        return steering;
    }

    /**
     * Sets the steering degree.
     * @param steering - the angle of the wheel.
     */
    public void setSteering(double steering) {
        this.steering = steering;
    }

    /**
     * Gets the acceleration.
     * @return the acceleration of the vehicle.
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * Sets the acceleration of the vehicle.
     * @param acceleration - the acceleration.
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Gets the surrounding climate.
     * @return the climate.
     */
    public int getClimate() {
        return climate;
    }

    /**
     * Sets the surrounding climate.
     * @param climate - the climate.
     */
    public void setClimate(int climate) {
        this.climate = climate;
    }

    /**
     * Gets the visibility.
     * @return the visibility of the climate.
     */
    public int getClimateVisibility() {
        return climateVisibility;
    }

    /**
     * Sets the climate visibility.
     * @param climateVisibility - the climate visibility
     */
    public void setClimateVisibility(int climateVisibility) {
        this.climateVisibility = climateVisibility;
    }

    /**
     * Sets the climate density.
     * @param density - the density.
     */
    public void setClimateDensity(int density){
        this.climateDensity = density;
    }

    /**
     * Gets the climate density.
     * @return the climate density.
     */
    public int getClimateDensity() {return climateDensity;}

    /**
     * Sets how slippery the road is.
     * @param severity - the integer value of the severity.
     */
    public void setRoadSeverity(int severity){
        this.roadSeverity = severity;
    }

    /**
     * Get the road severity.
     * @return the road severity.
     */
    public int getRoadSeverity() {return roadSeverity;}

    /**
     * Gets the hour.
     * @return the hour.
     */
    public int getTimeHour() {
        return timeHour;
    }

    /**
     * Set time hour.
     * @param timeHour - the current hour.
     */
    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    /**
     * Get the time minutes.
     * @return the minutes.
     */
    public int getTimeMinute() {
        return timeMinute;
    }

    /**
     * Sets the minute.
     * @param timeMinute - the current minute.
     */
    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    /**
     * Gets the seconds of the event.
     * @return the seconds.
     */
    public int getTimeSecond() {
        return timeSecond;
    }

    /**
     * Sets the seconds of the event.
     * @param timeSecond - the seconds.
     */
    public void setTimeSecond(int timeSecond) {
        this.timeSecond = timeSecond;
    }

    /**
     * Gets the road condition.
     * @return the road condition.
     */
    public int getRoadCondition() {
        return roadCondition;
    }

    /**
     * Sets the road condition
     * @param roadCondition - the road condition
     */
    public void setRoadCondition(int roadCondition) {
        this.roadCondition = roadCondition;
    }

    /**
     * Sets the road type.
     * @param roadType - the road type
     */
    public void setRoadType(int roadType) {this.roadType = roadType; }

    /**
     * Gets the road type.
     * @return the road type.
     */
    public int getRoadType() {return roadType;}




}
