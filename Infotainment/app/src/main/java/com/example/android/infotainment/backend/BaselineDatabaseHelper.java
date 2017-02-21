package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.Time;
import com.example.android.infotainment.backend.models.Turn;
import com.example.android.infotainment.backend.models.TurnDataPoint;


import java.util.ArrayList;

/**
 * Created by 100520993 on 1/30/2017.
 */

public class BaselineDatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "USER_DATABASE";
    private static final String RIGHT_TURN = "RightTurn";
    private static final String LEFT_TURN = "LeftTurn";
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
                "flag int                              " +
                ")                                     ";
        db.execSQL("CREATE TABLE " + RIGHT_TURN + turnSQLAttributes);
        db.execSQL("CREATE TABLE " + LEFT_TURN + turnSQLAttributes);
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

    /**
     * Saves a turn in the database.
     * @param turn the turn to save in the databse.
     */
    public void saveTurn(Turn turn) {
        //Log.i("before save", "saveTurn: " + turn.getFlag());
        //printTable(turn.getTurnType());
        //Log.i("baselineDB", "saveTurn size: " + turn.size() + "table name: " + getTurnTableName(turn.getTurnType()));
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
            db.insert(tableName, null, values);
        }
        db.close();
        //printTable(turn.getTurnType());
    }

    /**
     * Gets the turn data based on the turn type.
     * @param turnType Turn.LEFT or Turn.Right
     * @return the turns associated with that turn type
     */
    public Turn getTurnData(int turnType, int flag) {
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        String table;
        table = getTurnTableName(turnType);
        // select all the turns
        Cursor cursor;
        String[] columns = {"flag", "speed", "steering"};
        String selection = "flag = ?";
        //printTable(turnType);
        try {
            cursor = db.rawQuery("SELECT * from " + table /*+ " where flag LIKE ?"*/, where);
            //cursor = db.query(table, columns, selection, where, null, null, null);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + table + " where flag = ?", where);
        }

        cursor.moveToFirst();
        Turn turn;
        int previousTurnID = -1;
        turn = new Turn(turnType, previousTurnID, flag);
        System.out.println("Get turn data, size: " + cursor.getCount());
        while (!cursor.isAfterLast()) {
            // add the data to the object
            int turnID = cursor.getInt(cursor.getColumnIndex("turnID"));
            int queryFlag = cursor.getInt(cursor.getColumnIndex("flag"));
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            // add the point to the turn
            TurnDataPoint dataPoint = new TurnDataPoint(speed, steering);
            turn.addTurnPoint(dataPoint);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        return turn;
    }

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
     * @param turnType
     * @return
     */
    @NonNull
    private String getTurnTableName(int turnType) {
        String table;
        if (turnType == Turn.TURN_LEFT)
            table = LEFT_TURN;
        else
            table = RIGHT_TURN;
        return table;
    }

    public void printTable(int turnType) {
        String table = getTurnTableName(turnType);
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        table = getTurnTableName(turnType);
        // select all the turns
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * from " + table, where);
        } catch (SQLiteException e) {
            onCreate(getWritableDatabase());
            cursor = db.rawQuery("SELECT * from " + table, where);
        }
        //Log.i("cursor", "print table length: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // add the data to the object
            int turnID = cursor.getInt(cursor.getColumnIndex("turnID"));
            int queryFlag = cursor.getInt(cursor.getColumnIndex("flag"));
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            // add the point to the turn
            //Log.i("print", "turnid: " + turnID + " flag: " + queryFlag + " speed: " + speed + " steering: " + steering);
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
    }
}
