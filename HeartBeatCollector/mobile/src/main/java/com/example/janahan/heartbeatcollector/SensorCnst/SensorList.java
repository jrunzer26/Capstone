package com.example.janahan.heartbeatcollector.SensorCnst;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by janahan on 08/11/16.
 */

public class SensorList {
    public HashMap<Integer,String> sensors;

    /**
     * Class for SensorList
     */
    public SensorList(){
        sensors = new HashMap<Integer,String>();
        sensors.put(Sensor.TYPE_HEART_BEAT,"HeartRate");

    }

    /**
     * Gets a list of all the sensors avalible
     * @return the sensor map
     */
    public Map<Integer,String> getList(){
        return sensors;
    }

    /**
     * Gives the name of the id
     * @param id - sensor id
     * @return the name of the sensor
     */
    public String getName(int id){
        return sensors.get(id);
    }
}
