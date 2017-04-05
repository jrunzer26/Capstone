package com.example.android.infotainment.backend.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100514374 on 3/12/2017.
 */

public class MinData {
    private List vData;
    private double[] baseline;
    private String event;

    /**
     * The baseline data.
     * @param baseline the double time series
     */
    public void setBaseline(double[] baseline){
        this.baseline = baseline;
    }

    /**
     * Sets the vehicle history data.
     * @param data the data.
     */
    public void setVData(List data){
        vData = data;
    }

    /**
     * Sets the event with the data.
     * @param event the event.
     */
    public void setEvent(String event){
        this.event = event;
    }

    /**
     * Gets the baseline.
     * @return the baseline.
     */
    public double[] getBaseline(){
        return baseline;
    }

    /**
     * Gets the vehicle history data.
     * @return the history list data.
     */
    public List getVData(){
        return vData;
    }

    /**
     * Gets the event corresponding to the data.
     * @return the event
     */
    public String getEvent(){
        return event;
    }

    /**
     * String version of the data
     * @return the string version of the data.
     */
    public String toString() {
        return event + " baseline size: " + baseline.length + " vData size: " + vData.size();
    }
}
