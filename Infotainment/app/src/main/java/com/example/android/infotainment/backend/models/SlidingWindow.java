package com.example.android.infotainment.backend.models;

import android.util.Log;

import com.example.android.infotainment.utils.Util;

/**
 * Created by 100514374 on 2/9/2017.
 */

public class SlidingWindow {
    private int[] window;
    private double average = 0;
    private double sum = 0;
    private int counter = 0;

    public SlidingWindow(){
        this(5);
    }

    /**
     * Creates a sliding window.
     * @param size
     */
    public SlidingWindow(int size){
        window = new int[size];
        for (int i = 0; i< size; i++){
            window[i]=0;
        }
    }

    /**
     * Adds to the sliding window.
     * @param element the element to increase by.
     */
    public void add(int element){
        sum = sum - window[counter%window.length] + element;
        window[counter%window.length] = element;
        average = (sum/window.length);
        counter++;
        String test="";

    }
    public double getAverage(){
        return average;
    }

    /**
     * Gets the standard deviation.
     * @return
     */
    public double getStdDev(){
        double rollingSum=0.0;
        for (int i = 0; i < window.length; i++){
            rollingSum+=((window[i]-average)*(window[i]-average));
        }
        return Math.sqrt(rollingSum/window.length);
    }
}
