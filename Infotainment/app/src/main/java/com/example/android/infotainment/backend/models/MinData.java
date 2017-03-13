package com.example.android.infotainment.backend.models;

import java.util.ArrayList;

/**
 * Created by 100514374 on 3/12/2017.
 */

public class MinData {
    private ArrayList vData;
    private double[] baseline;
    private String event;

    public void setBaseline(double[] baseline){
        this.baseline = baseline;
    }

    public void setVData(ArrayList data){
        vData = data;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public double[] getBaseline(){
        return baseline;
    }

    public ArrayList getVData(){
        return vData;
    }

    public String getEvent(){
        return event;
    }
}
