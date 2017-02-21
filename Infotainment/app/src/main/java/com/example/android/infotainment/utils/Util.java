package com.example.android.infotainment.utils;

import android.util.Log;

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
                for (int j = 0; j < array[0].length; j++) {
                    stringBuilder.append(array[i][j]).append("\t");
                }
                Log.i(tag, stringBuilder.toString());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i(tag, "length 0!");
        }
    }
}
