package com.example.android.infotainment.utils;

import android.util.Log;

import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100520993 on 2/19/2017.
 */

public class Util {

    /**
     * Prints a 2d double array.
     * @param array the array
     * @param tag the tag info
     */
    public static void print2dArray(double[][] array, String tag) {
        try {
            Log.i(tag, "length: " + array.length + " length[0]: " + array[0].length);
            for (int i = 0; i < array.length; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\t");
                for (int j = 0; j < array[i].length; j++) {
                    stringBuilder.append(array[i][j]).append("\t");
                }
                Log.i(tag, stringBuilder.toString());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(tag, "length 0!");
        }
    }

    /**
     * Prints a single double array.
     * @param array the single array.
     * @param tag the string tag.
     */
    public static void printArray(double[] array, String tag) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < array.length; i++) {
                stringBuilder.append(array[i]).append("\t");
            }
            Log.i(tag, stringBuilder.toString());
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(tag, "length 0!");
        }
    }

    /**
     * Prints the 2d user data list
     * @param arrayList2D the data
     * @param tag the tag
     */
    public static void print2dUserDataListSpeed(ArrayList<ArrayList<UserData>> arrayList2D, String tag) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList2D.size(); i++) {
            ArrayList<UserData> data = arrayList2D.get(i);
            for (int j = 0; j < data.size(); j++) {
                stringBuilder.append(data.get(j).getSimData().getSpeed()).append("\t");
            }
            stringBuilder.append("\n");
        }
        Log.i(tag, stringBuilder.toString());
    }

    /**
     * Prints a 2d user data array list
     * @param arrayList2D the data
     * @param tag the string tag
     */
    public static void print2dUserDataListSteering(ArrayList<ArrayList<UserData>> arrayList2D, String tag) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList2D.size(); i++) {
            ArrayList<UserData> data = arrayList2D.get(i);
            for (int j = 0; j < data.size(); j++) {
                stringBuilder.append(data.get(j).getSimData().getSteering()).append("\t");
            }
            stringBuilder.append("\n");
        }
        Log.i(tag, stringBuilder.toString());
    }

    /**
     * Prints a list
     * @param vData the list
     * @param s the string tag
     */
    public static void printList(List vData, String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Object i : vData) {
            stringBuilder.append("\t").append(i);
        }
        Log.i(s, stringBuilder.toString());
    }
}
