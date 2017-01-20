package com.example.android.infotainment.backend.models;

/**
 * Created by 100520993 on 1/18/2017.
 */

public class Time {
    private int hour = 0;
    private int minute = 0;
    private int second = 0;

    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public Time copyTime() {
        return new Time(hour, minute, second);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timeHour: ").append(hour).append("\n")
        .append("timeMinute: ").append(minute).append("\n")
        .append("timeSecond: ").append(second).append("\n");
        return stringBuilder.toString();
    }
}
