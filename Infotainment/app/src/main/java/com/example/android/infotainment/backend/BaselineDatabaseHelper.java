package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.infotainment.backend.models.Turn;
import com.example.android.infotainment.backend.models.TurnDataPoint;
import com.example.android.infotainment.backend.models.UserData;

/**
 * Created by 100520993 on 1/30/2017.
 */

public class BaselineDatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "USER_DATABASE";
    private static final String RIGHT_TURN = "RightTurn";
    private static final String LEFT_TURN = "LeftTurn";
    private static final String ACCEL_NEAR_STOPTABLE = "AccelNearStop";
    private static final String ACCEL_FROM_SPEED = "AccelFromSpeed";
    private static final String BRAKE_TABLE = "BRAKE";
    private static final String CRUISE_TABLE = "CRUISE";
    private static final String SPEEDING_TABLE = "SPEEDING";
    private static final int DATABASE_VERSION = 1;

    public BaselineDatabaseHelper(Context context) {
        super(context, NAME, null, DATABASE_VERSION);
    }

    /**
     * Creat the database on the first run of the app.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String turnSQLAttributes = "(                  " +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "turnID int,                           " +
                "speed single,                         " +
                "steering single,                      " +
                "flag int,                             " +
                "multi int                             " +
                ")                                     ";
        db.execSQL("CREATE TABLE " + RIGHT_TURN + turnSQLAttributes);
        db.execSQL("CREATE TABLE " + LEFT_TURN + turnSQLAttributes);
        String accelerate_brakingSQLAttributes = "( " +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "turnID int,                           " +
                "speed single,                         " +
                "flag int,                             " +
                "multi int                             " +
                ")                                     ";
        db.execSQL("CREATE TABLE " + ACCEL_FROM_SPEED + accelerate_brakingSQLAttributes);
        db.execSQL("CREATE TABLE " + ACCEL_NEAR_STOPTABLE + accelerate_brakingSQLAttributes);
        db.execSQL("CREATE TABLE " + BRAKE_TABLE + accelerate_brakingSQLAttributes);
        String cruisingSQLQAttributes ="( " +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "turnID int,                           " +
                "steering single,                      " +
                "flag int,                             " +
                "multi int                             " +
                ")                                     ";
        db.execSQL("CREATE TABLE " + CRUISE_TABLE + cruisingSQLQAttributes);
        String speedingSQLAttributes = "( " +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "turnID int,                           " +
                "devPercent double,                    " +
                "multi int                             " +
                ")                                     ";
        db.execSQL("CREATE TABLE " + SPEEDING_TABLE + speedingSQLAttributes);
    }


    /**
     * Upgrades the db.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ######################### Turn Baselines #########################

    /**
     * Saves a turn in the database.
     * @param turn the turn to save in the databse.
     */
    public void saveTurn(Turn turn, int multi) {
        ContentValues values;
        // user specific data
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTurnTableName(turn.getTurnType());
        for(TurnDataPoint point : turn.getTurnDataPoints()) {
            values = new ContentValues();
            values.put("flag", turn.getFlag());
            values.put("turnID", turn.getId());
            values.put("speed", point.getSpeed());
            values.put("steering", point.getSteering());
            values.put("multi", multi);
            db.insert(tableName, null, values);
        }
        db.close();
    }

    /**
     * Gets the turn data based on the turn type.
     * @param turnType Turn.LEFT or Turn.Right
     * @return the turns associated with that turn type
     */
    public Turn getTurnData(int turnType, int flag) {
        //String[] where = new String[0];
        String[] where = {Integer.toString(flag)};
        SQLiteDatabase db = this.getReadableDatabase();
        String table;
        table = getTurnTableName(turnType);
        // select all the turns
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * from " + table + " where \"flag\" = ?", where);
            //cursor = db.query(table, columns, selection, where, null, null, null);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + table + " where flag = ?", where);
        }

        cursor.moveToFirst();
        Turn turn;
        int previousTurnID = -1;
        turn = new Turn(turnType, previousTurnID, flag);
        int multi = 0;
        while (!cursor.isAfterLast()) {
            // add the data to the object
            int turnID = cursor.getInt(cursor.getColumnIndex("turnID"));
            int queryFlag = cursor.getInt(cursor.getColumnIndex("flag"));
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            multi = cursor.getInt(cursor.getColumnIndex("multi"));
            // add the point to the turn
            TurnDataPoint dataPoint = new TurnDataPoint(speed, steering);
            turn.addTurnPoint(dataPoint);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        turn.setMulti(multi);
        return turn;
    }

    /**
     * Clears the turn from the database.
     * @param turnType the type of turn
     * @param flag the flag turn to delete
     */
    public void clearTurn(int turnType, int flag) {
        String tableName = getTurnTableName(turnType);
        String [] where = {flag + ""};
        try {
            getWritableDatabase().execSQL("delete from " + tableName + " where flag = ?", where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            getWritableDatabase().execSQL("delete from " + tableName + " where flag = ?", where);
        }
    }

    /**
     * Gets the turn table name based on the type.
     * @param turnType the turn type
     * @return the table name
     */
    @NonNull
    public String getTurnTableName(int turnType) {
        String table;
        if (turnType == Turn.TURN_LEFT)
            table = LEFT_TURN;
        else
            table = RIGHT_TURN;
        return table;
    }

    /**
     * Prints the table specified by the turn
     * @param turnType the turn table to print
     */
    public void printTurnTable(int turnType) {
        String table = getTurnTableName(turnType);
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        // select all the turns
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * from " + table, where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + table, where);
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // add the data to the object
            int turnID = cursor.getInt(cursor.getColumnIndex("turnID"));
            int queryFlag = cursor.getInt(cursor.getColumnIndex("flag"));
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            Log.i("print", "turnid: " + turnID + " flag: " + queryFlag + " speed: " + speed + " steering: " + steering);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
    }

    // ######################### Acceleration Baselines #########################


    /**
     * Returns the acceleration baseline from the baseline db.
     * @param flag the type of accel baseline to retreive
     * @return the array containing the speed baseline timeseries.
     */
    public double[] getAccelerationBaseline(int flag) {
        String table;
        table = getAccelerationTableName(flag);
        // select all the turns
        return getDoubleArrayFromTable(table, "speed");
    }

    private double[] getDoubleArrayFromTable(String table, String col) {
        String [] where = {};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * from " + table, where);
        } catch (SQLiteException e) {
            e.printStackTrace();
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + table, where);
        }
        cursor.moveToFirst();
        int size = cursor.getCount();
        double [] contents = new double[size];
        int count = 0;
        while (!cursor.isAfterLast()) {
            // add the data to the object
            double speed = cursor.getDouble(cursor.getColumnIndex(col));

            // add the point to the turn
            contents[count] = speed;
            count++;
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        return contents;
    }

    public int getMultiplicityFromTable(String tableName) {
        String [] where = {};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        int multi = 0;
        try {
            cursor = db.rawQuery("SELECT * from " + tableName, where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + tableName, where);
        }
        cursor.moveToFirst();
        int size = cursor.getCount();
        double [] contents = new double[size];
        int count = 0;
        if(!cursor.isAfterLast()) {
            // add the data to the object
            multi = cursor.getInt(cursor.getColumnIndex("multi"));
        }
        cursor.close();
        return multi;
    }


    /**
     * Overwrites the acceelration baseline.
     * @param speeds the data to write.
     * @param flag the flag for the specific table.
     */
    public void overWriteAccelerationBaseline(double[] speeds, int flag, int multi) {
        String tableName = getAccelerationTableName(flag);
        clearTable(tableName);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        for (double data : speeds) {
            values = new ContentValues();
            values.put("speed", data);
            values.put("multi", multi);
            db.insert(tableName, null, values);
        }
        db.close();
    }

    /**
     * Clears a table.
     * @param tableName the table name.
     */
    private void clearTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + tableName);
    }

    /**
     * Gets the acceleration table name.
      * @param flag the flag of the accel table.
     * @return the table name.
     */
    public String getAccelerationTableName(int flag) {
        if (flag == UserData.FLAG_ACCELERATION_FROM_SPEED) {
            return ACCEL_FROM_SPEED;
        } else if (flag == UserData.FLAG_ACCELERATION_NEAR_STOP) {
            return ACCEL_NEAR_STOPTABLE;
        } else {
            return null;
        }
    }

    // ######################### Braking Baselines #########################

    /**
     * Overwrites te current brake baseline.
     * @param speeds the speeds to add to the db
     */
    public void overWriteBrakingBaseline(double[] speeds, int multi) {
        clearTable(BRAKE_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        for (double data : speeds) {
            values = new ContentValues();
            values.put("speed", data);
            values.put("multi", multi);
            db.insert(BRAKE_TABLE, null, values);
        }
        db.close();
    }


    public int getBrakeMulti() {
        return getMultiplicityFromTable(BRAKE_TABLE);
    }

    /**
     * Returns the braking baseline.
     * @return the data.
     */
    public double[] getBrakingBaseline() {
        return getDoubleArrayFromTable(BRAKE_TABLE, "speed");
    }

    // ######################### Cruising Baselines #########################

    public double[] getCruisingBaseline() {
        return getDoubleArrayFromTable(CRUISE_TABLE, "steering");
    }

    public void overWriteCruisingBaseline(double[] steering, int multi) {
        clearTable(CRUISE_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        for (double data : steering) {
            values = new ContentValues();
            values.put("steering", data);
            values.put("multi", multi);
            db.insert(CRUISE_TABLE, null, values);
        }
        db.close();
    }

    public int getCruiseMulti() {
        return getMultiplicityFromTable(CRUISE_TABLE);
    }

    // ######################### Speeding Baselines #########################

    public double[] getSpeedingBaseline() {
        return getDoubleArrayFromTable(SPEEDING_TABLE, "devPercent");
    }

    public void overWriteSpeedingBaseline(double[] devPercents, int multi) {
        clearTable(SPEEDING_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;
        for (double data : devPercents) {
            values = new ContentValues();
            values.put("devPercent", data);
            values.put("multi", multi);
            db.insert(SPEEDING_TABLE, null, values);
        }
        db.close();
    }

    public int getSpeedingMulti() {
        return getMultiplicityFromTable(SPEEDING_TABLE);
    }
}
