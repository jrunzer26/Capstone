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
    public Sensors(int id){
        sensors = new SensorList();
        sensorID = id;
        name = sensors.getName(id);
    }
    public void updateSensor(int accuracy, long timeStamp, float [] values){
        setAccuracy(accuracy);
        setTimeStamp(timeStamp);
        setValues(values);

    }
    public void setAccuracy(int accuracy){
        this.accuracy = accuracy;
    }
    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }
    public void setValues(float [] values) {
        this.values = values;
    }
    public int getSensorID(){
        return sensorID;
    }
    public int getAccuracy(){
        return accuracy;
    }
    public String getName(){
        return name;
    }
    public float[] getValues(){
        return values;
    }
    public long getTimeStamp(){
        return timeStamp;
    }

}
