package com.example.android.infotainment.backend.models;

import android.util.Log;

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
    public SlidingWindow(int size){
        window = new int[size];
        for (int i = 0; i< size; i++){
            window[i]=0;
        }
    }

    public void add(int element){
        sum = sum - window[counter%window.length] + element;
        window[counter%window.length] = element;
        average = (sum/window.length);
        counter++;
    }
    public double getAverage(){
        return average;
    }
    public double getStdDev(){
        double rollingSum=0.0;
        for (int i = 0; i < window.length; i++){
            rollingSum+=((window[i]-average)*window[i]-average);
        }
        Log.i("rollingSum", rollingSum+"");
        Log.i("window length", window.length+"");
        return Math.sqrt(rollingSum/window.length);
    }
}
