package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.Time;
import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;

/**
 * Created by 100520993 on 11/15/2016.
 */

// TODO: Modify entire object to fit project constraints. 
public class UserDatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "UserDataHelper";
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
                "speedLimit single, " +
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
                "heartRate int, " +
                "flag int " +
                ")"
        );
        System.out.println("Create user db");
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
        values.put("flag", userData.getTurnFlag());
        // sim data
        values.put("speed", simData.getSpeed());
        values.put("speedLimit", simData.getSpeedLimit());
        values.put("gear", simData.getGear());
        values.put("cruseControl", simData.isCruseControl());
        values.put("signal", simData.getSignal());
        values.put("steering", simData.getSteering());
        values.put("acceleration", simData.getAcceleration());
        values.put("climate", simData.getClimate());
        values.put("climateVisibility", simData.getClimateVisibility());
        values.put("climateDensity", simData.getClimateDensity());
        values.put("roadSeverity", simData.getRoadSeverity());
        Time time = simData.getTime();
        values.put("timeHour", time.getHour());
        values.put("timeMinute", time.getMinute());
        values.put("timeSecond", time.getSecond());
        values.put("roadCondition", simData.getRoadCondition());
        values.put("roadType", simData.getRoadType());
        // sensor data
        values.put("heartRate", sensorData.getHeartRate());
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.insert("Data", null, values);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            db.insert("Data", null, values);
        }

        db.close();
    }

    /**
     * Returns an ArrayList of SimData.
     * @return the sim data.
     */
    public ArrayList<UserData> getData() {
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * from Data", where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from Data", where);
        }
        ArrayList<UserData> userDatas = getAllDataFromCursor(cursor);
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
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT tripID from Data", where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT tripID from Data", where);
        }
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            id = cursor.getInt(0) + 1;
        }
        db.close();
        cursor.close();
        return id;
    }

    public int getCurrentTripID() {
        int id = getNextTripID() - 1;
        return id;
    }

    /**
     * Prints out the data captured in a table format to copy and paste into excel
     */
    public void printRelevantDataSet() {
        String[] where = new String[0];
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tripID, speed, speedLimit, steering, acceleration, " +
                "timeHour, timeMinute, timeSecond, heartRate, flag from Data", where);
        cursor.moveToFirst();
        System.out.printf("\t%6s\t%-6s\t%-6s\t%8s\t%12s\t%2s\t%4s\t%6s\t%6s\t%6s\n", "TripID", "Speed", "SpeedLimit", "Steering",
                "Acceleration", "HR", "Hour", "Minute", "Second", "Flag");
        while(!cursor.isAfterLast()) {
            int tripID = cursor.getInt(cursor.getColumnIndex("tripID"));
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double speedLimit = cursor.getDouble(cursor.getColumnIndex("speedLimit"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            double acceleration = cursor.getDouble(cursor.getColumnIndex("tripID"));
            int hr = cursor.getInt(cursor.getColumnIndex("heartRate"));
            int timeHour = cursor.getInt(cursor.getColumnIndex("timeHour"));
            int timeMinute = cursor.getInt(cursor.getColumnIndex("timeMinute"));
            int timeSecond = cursor.getInt(cursor.getColumnIndex("timeSecond"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            System.out.printf("\t%6d\t%6.2f\t%6.2f\t%8.2f\t%12.2f\t%2d\t%4d\t%6d\t%6d\t%6d\n", tripID, speed, speedLimit, steering,
                    acceleration, hr, timeHour, timeMinute, timeSecond, flag);
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
    }

    /**
     * Gets the last trip data from the database.
     * @return the last trip data.
     */
    public ArrayList<UserData> getLastTripData() {
        String[] where = new String[1];
        where[0] = getCurrentTripID() +"";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT *                  " +
                                    "from Data WHERE tripID = ?", where);
        return getAllDataFromCursor(cursor);
    }

    /**
     * Gets all data from a database cursor
     * @param cursor the user database cursor
     * @return the user data.
     */
    private ArrayList<UserData> getAllDataFromCursor(Cursor cursor) {
        cursor.moveToFirst();
        ArrayList<UserData> userDatas = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            UserData userData = new UserData();
            SensorData sensorData = new SensorData();
            SimData simData = new SimData();
            // add the data to the object
            // user data
            userData.setTripID(cursor.getInt(cursor.getColumnIndex("tripID")));
            userData.setTurnFlag(cursor.getInt(cursor.getColumnIndex("flag")));
            // sim data
            simData.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            simData.setSpeedLimit(cursor.getInt(cursor.getColumnIndex("speedLimit")));
            simData.setGear(cursor.getString(cursor.getColumnIndex("gear")));
            simData.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            simData.setSteering(cursor.getDouble(cursor.getColumnIndex("steering")));
            simData.setAcceleration(cursor.getDouble(cursor.getColumnIndex("acceleration")));
            simData.setClimate(cursor.getInt(cursor.getColumnIndex("climate")));
            simData.setClimateVisibility(cursor.getInt(cursor.getColumnIndex("climateVisibility")));
            // set the time
            int hour = cursor.getInt(cursor.getColumnIndex("timeHour"));
            int minute = cursor.getInt(cursor.getColumnIndex("timeMinute"));
            int second = cursor.getInt(cursor.getColumnIndex("timeSecond"));
            Time time = new Time(hour, minute, second);
            simData.setTime(time);
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
        return userDatas;
    }

    /**
     * Clears all the data in the user database.
     */
    public void clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("delete from data");
        } catch (SQLiteException e) {

        }
    }
}
