package com.example.android.infotainment.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

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
                "steering single                       " +
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
     * Gets all the right turn baseline data turns.
     * @return an array list of the RIGHT turns saved in storage.
     */
    public ArrayList<Turn> getRightTurnData() {
        return getTurnData(Turn.TURN_RIGHT);
    }

    /**
     * Gets all the left turn baseline data turns.
     * @return an array list of the LEFT turns saved in storage.
     */
    public ArrayList<Turn> getLeftTurnData() {
        return getTurnData(Turn.TURN_LEFT);
    }

    /**
     * Gets the next available turn id in the database.
     * @param turnType the type of turn: Turn.LEFT, Turn.RIGHT
     * @return
     */
    public int getNextTurnId(int turnType) {
        String[] where = new String[0];
        SQLiteDatabase db = getReadableDatabase();
        int id = 0;
        String tableName = getTurnTableName(turnType);
        Cursor cursor = db.rawQuery("SELECT turnID from " + tableName , where);
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            // get the last index, and increase it by 1
            id = cursor.getInt(0) + 1;
        }
        db.close();
        cursor.close();
        return id;
    }

    /**
     * Saves a turn in the database.
     * @param turn the turn to save in the databse.
     */
    public void saveTurn(Turn turn) {
        ContentValues values;
        // user specific data
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTurnTableName(turn.getTurnType());
        for(TurnDataPoint point : turn.getTurnDataPoints()) {
            values = new ContentValues();
            values.put("turnID", turn.getId());
            values.put("speed", point.getSpeed());
            values.put("steering", point.getSteering());
            db.insert(tableName, null, values);
        }
        db.close();
    }

    /**
     * Gets the turn data based on the turn type.
     * @param turnType Turn.LEFT or Turn.Right
     * @return the turns associated with that turn type
     */
    private ArrayList<Turn> getTurnData(int turnType) {
        System.out.println("Get turn data");
        String[] where = new String[0];
        SQLiteDatabase db = this.getReadableDatabase();
        String table;
        table = getTurnTableName(turnType);
        // select all the turns
        Cursor cursor = db.rawQuery("SELECT * from " + table, where);
        cursor.moveToFirst();
        Turn turn;
        int previousTurnID = -1;
        ArrayList<Turn> turns = new ArrayList<>();
        turn = new Turn(turnType, previousTurnID);
        while (!cursor.isAfterLast()) {
            // add the data to the object
            int turnID = cursor.getInt(cursor.getColumnIndex("turnID"));
            // create a new turn if the previous turn id is less  than the current data
            if (turnID > previousTurnID) {
                turn = new Turn(turnType, turnID);
            }
            double speed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double steering = cursor.getDouble(cursor.getColumnIndex("steering"));
            // add the point to the turn
            TurnDataPoint dataPoint = new TurnDataPoint(speed, steering);
            turn.addTurnPoint(dataPoint);
            // add then turn to the array list if it was less
            if (turnID > previousTurnID) {
                previousTurnID = turnID;
                turns.add(turn);
            }
            // next line in database
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return turns;
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

    public void clear(int turnType) {
        String tableName = getTurnTableName(turnType);
        getWritableDatabase().execSQL("delete from "+ tableName);
    }

    // TODO: 2/17/2017 return 2d array of the baseline data
    public void getBaselineArray() {

    }
}
