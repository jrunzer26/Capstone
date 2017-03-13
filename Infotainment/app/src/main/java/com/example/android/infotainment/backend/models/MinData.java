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

    public void setBaseline(double[] baseline){
        this.baseline = baseline;
    }

    public void setVData(List data){
        vData = data;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public double[] getBaseline(){
        return baseline;
    }

    public List getVData(){
        return vData;
    }

    public String getEvent(){
        return event;
    }

    public String toString() {
        return event + " baseline size: " + baseline.length + " vData size: " + vData.size();
    }
}
