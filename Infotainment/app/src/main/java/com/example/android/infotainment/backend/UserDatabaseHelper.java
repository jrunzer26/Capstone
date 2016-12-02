package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;

/**
 * Created by 100520993 on 11/15/2016.
 */

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "USER_DATABASE";
    private static final int DATABASE_VERSION = 1;

    public UserDatabaseHelper(Context context) {
        super(context, NAME, null, DATABASE_VERSION);
    }

    /**
     * Creat the database on the first run of the app.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Data(                 " +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "tripID int, " +
                "speed single, " +
                "gear varchar(64), " +
                "cruseControl boolean, " +
                "signal int, " +
                "steering single, " +
                "acceleration single, " +
                "climate int, " +
                "climateVisibility int, " +
                "climateDensity int, " +
                "roadSeverity int, " +
                "timeHour int, " +
                "timeMinute int, " +
                "timeSecond int, " +
                "timeAM int, " +
                "roadCondition int, " +
                "roadType int, " +
                "heartRate int " +
                ")"
        );
    }

    /**
     * Upgrades the database.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Inserts a Sim Data row into the database.
     * @param userData - a row of data for the database
     */
    public void insertSimData(UserData userData) {
        ContentValues values = new ContentValues();
        SimData simData = userData.getSimData();
        SensorData sensorData = userData.getSensorData();
        // user specific data
        values.put("tripID", userData.getTripID());
        // sim data
        values.put("speed", simData.getSpeed());
        values.put("gear", simData.getGear());
        values.put("cruseControl", simData.isCruseControl());
        values.put("signal", simData.getSignal());
        values.put("steering", simData.getSteering());
        values.put("acceleration", simData.getAcceleration());
        values.put("climate", simData.getClimate());
        values.put("climateVisibility", simData.getClimateVisibility());
        values.put("climateDensity", simData.getClimateDensity());
        values.put("roadSeverity", simData.getRoadSeverity());
        values.put("timeHour", simData.getTimeHour());
        values.put("timeMinute", simData.getTimeMinute());
        values.put("timeSecond", simData.getTimeSecond());
        values.put("roadCondition", simData.getRoadCondition());
        values.put("roadType", simData.getRoadType());
        // sensor data
        values.put("heartRate", sensorData.getHeartRate());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("Data", null, values);
        db.close();
    }

    /**
     * Returns an ArrayList of SimData.
     * @return the sim data.
     */
    public ArrayList<UserData> getData() {
        ArrayList<UserData> userDatas = new ArrayList<>();
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from Data", where);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserData userData = new UserData();
            SensorData sensorData = new SensorData();
            SimData simData = new SimData();
            // add the data to the object
            // user data
            userData.setTripID(cursor.getInt(cursor.getColumnIndex("tripID")));
            // sim data
            simData.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            simData.setGear(cursor.getString(cursor.getColumnIndex("gear")));
            simData.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            simData.setSteering(cursor.getDouble(cursor.getColumnIndex("steering")));
            simData.setAcceleration(cursor.getDouble(cursor.getColumnIndex("acceleration")));
            simData.setClimate(cursor.getInt(cursor.getColumnIndex("climate")));
            simData.setClimateVisibility(cursor.getInt(cursor.getColumnIndex("climateVisibility")));
            simData.setTimeHour(cursor.getInt(cursor.getColumnIndex("timeHour")));
            simData.setTimeMinute(cursor.getInt(cursor.getColumnIndex("timeMinute")));
            simData.setTimeSecond(cursor.getInt(cursor.getColumnIndex("timeSecond")));
            simData.setRoadCondition(cursor.getInt(cursor.getColumnIndex("roadCondition")));
            // sensor data
            sensorData.setHeartRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            // add data to user
            userData.setSimData(simData);
            userData.setSensorData(sensorData);
            userDatas.add(userData);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return userDatas;
    }

    /**
     * Displays all data in the database.
     * @param context - the current context
     */
    public void showData(Context context) {
        ArrayList<UserData> userDatas = new UserDatabaseHelper(context).getData();
        for(UserData userData : userDatas) {
            System.out.println(userData.toString());
        }
    }

    /**
     * Gets the next valid trip ID to distinguish trips.
     * @return the next tripID
     */
    public int getNextTripID() {
        String[] where = new String[0];
        SQLiteDatabase db = getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT tripID from Data", where);
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            id = cursor.getInt(0) + 1;
        }
        db.close();
        cursor.close();
        return id;
    }
}
