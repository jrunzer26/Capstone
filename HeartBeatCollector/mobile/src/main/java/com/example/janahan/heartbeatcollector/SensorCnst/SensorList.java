package com.example.janahan.heartbeatcollector.SensorCnst;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by janahan on 08/11/16.
 */

public class SensorList {
    public HashMap<Integer,String> sensors;
    public SensorList(){
        sensors = new HashMap<Integer,String>();
        sensors.put(Sensor.TYPE_HEART_BEAT,"HeartRate");

    }
    public Map<Integer,String> getList(){
        return sensors;
    }
    public String getName(int id){
        return sensors.get(id);
    }
}
