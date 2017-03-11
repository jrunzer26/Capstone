package com.example.android.infotainment.utils;

import android.util.Log;

import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;

/**
 * Created by 100520993 on 2/19/2017.
 */

public class Util {

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
}
