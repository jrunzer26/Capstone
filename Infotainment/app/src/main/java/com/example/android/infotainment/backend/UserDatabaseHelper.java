package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                "climateVisibility single, " +
                "timeHour int, " +
                "timeMinute int, " +
                "timeSecond int, " +
                "timeAM int, " +
                "roadCondition int, " +
                "heartRate int " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Inserts a Sim Data row into the database.
     * @param simData
     */
    public void insertSimData(SimData simData) {
        ContentValues values = new ContentValues();
        values.put("tripID", simData.getTripID());
        values.put("speed", simData.getSpeed());
        values.put("gear", simData.getGear());
        values.put("cruseControl", simData.isCruseControl());
        values.put("signal", simData.getSignal());
        values.put("steering", simData.getSteering());
        values.put("acceleration", simData.getAcceleration());
        values.put("climate", simData.getClimate());
        values.put("climateVisibility", simData.getClimateVisibility());
        values.put("timeHour", simData.getTimeHour());
        values.put("timeMinute", simData.getTimeMinute());
        values.put("timeSecond", simData.getTimeSecond());
        values.put("roadCondition", simData.getRoadCondition());
        values.put("heartRate", simData.getHeartRate());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("Data", null, values);
        db.close();
    }

    /**
     * Returns an ArrayList of SimData.
     * @return the sim data.
     */
    public ArrayList<SimData> getData() {
        ArrayList<SimData> simDatas = new ArrayList<>();
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from Data", where);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SimData simData = new SimData();
            // add the data to the object
            simData.setTripID(cursor.getInt(cursor.getColumnIndex("tripID")));
            simData.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            simData.setGear(cursor.getString(cursor.getColumnIndex("gear")));
            simData.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            simData.setSteering(cursor.getDouble(cursor.getColumnIndex("steering")));
            simData.setAcceleration(cursor.getDouble(cursor.getColumnIndex("acceleration")));
            simData.setClimate(cursor.getInt(cursor.getColumnIndex("climate")));
            simData.setClimateVisibility(cursor.getDouble(cursor.getColumnIndex("climateVisibility")));
            simData.setTimeHour(cursor.getInt(cursor.getColumnIndex("timeHour")));
            simData.setTimeMinute(cursor.getInt(cursor.getColumnIndex("timeMinute")));
            simData.setTimeSecond(cursor.getInt(cursor.getColumnIndex("timeSecond")));
            simData.setRoadCondition(cursor.getInt(cursor.getColumnIndex("roadCondition")));
            simData.setHeartRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            simDatas.add(simData);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return simDatas;
    }

    /**
     * Displays all data in the database.
     * @param context the current context
     */
    public void showData(Context context) {
        ArrayList<SimData> simDatas = new UserDatabaseHelper(context).getData();
        for(SimData simData : simDatas) {
            System.out.println(simData.toString());
            System.out.println("=========================================");
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
