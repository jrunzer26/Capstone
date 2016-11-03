package com.example.android.simulator.backend;

/**
 * Created by 100520993 on 11/3/2016.
 */

public interface Car {
    void park();
    void reverse();
    void drive();
    void cruise();
    void pause();
    double getSteering();
    void signalLeft();
    void signalRight();
    void changeLeft();
    void changeRight();
}
