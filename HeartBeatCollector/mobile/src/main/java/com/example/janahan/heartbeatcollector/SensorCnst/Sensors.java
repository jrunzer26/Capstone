package com.example.janahan.heartbeatcollector.SensorCnst;

/**
 * Created by janahan on 08/11/16.
 */
import com.example.janahan.heartbeatcollector.SensorCnst.SensorList;

public class Sensors {
    private int sensorID, accuracy;
    private String name;
    private float [] values;
    private long timeStamp;
    SensorList sensors;

    /**
     * Object to house the sensor
     * @param id - the sensor id
     */
    public Sensors(int id){
        sensors = new SensorList();
        sensorID = id;
        name = sensors.getName(id);
    }

    /**
     * Updates this sensors value
     * @param accuracy - the accuracy of the sensor
     * @param timeStamp - the time the reading was taken by the sensor
     * @param values - the values taken in by the sensor
     */
    public void updateSensor(int accuracy, long timeStamp, float [] values){
        setAccuracy(accuracy);
        setTimeStamp(timeStamp);
        setValues(values);
    }

    /**
     * Sets accuracy
     * @param accuracy - accuracy of sensor reading
     */
    public void setAccuracy(int accuracy){
        this.accuracy = accuracy;
    }

    /**
     * Sets time stamp
     * @param timeStamp - time of the sensor reading
     */
    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    /**
     * Sets the values
     * @param values - array of values
     */
    public void setValues(float [] values) {
        this.values = values;
    }

    /**
     * Gets the sensor id
     * @return the current sensor id
     */
    public int getSensorID(){
        return sensorID;
    }

    /**
     * Gets the accuracy
     * @return the accuracy for the sensor
     */
    public int getAccuracy(){
        return accuracy;
    }

    /**
     * Gets the name of sensor
     * @return the name of the sensor
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the values
     * @return the values of the sensor
     */
    public float[] getValues(){
        return values;
    }

    /**
     * Gets the time sensor was read
     * @return the time the sensor gets data
     */
    public long getTimeStamp(){
        return timeStamp;
    }

}
