package com.example.android.infotainment.backend.models;

import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.backend.BaselineDatabaseHelper;
import com.example.android.infotainment.backend.DBA;
import com.example.android.infotainment.backend.UserDatabaseHelper;
import com.example.android.infotainment.utils.Util;

import java.util.ArrayList;

/**
 * Created by 100514374 on 2/17/2017.
 */

public class Baselines {

    private final String TAG = "Baslines";
    private final int TURN_WINDOW_SIZE = 18;
    private final int TURN_PARAMETERS = 2;

    // baselines
    // left & right turn:
    // index [0] = steering
    // index [1] = speed
    private double[][] leftTurnBaseline;
    private double[][] rightTurnBaseline;
    private int[] accelBaseline;
    private int[] brakeBaseline;
    private int[] cruiseBaseline;
    private int[] speedingBaseline;

    private BaselineDatabaseHelper baselineDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;

    public Baselines(Context context) {
        makeBaselineArrays();
        baselineDatabaseHelper = new BaselineDatabaseHelper(context);
        userDatabaseHelper = new UserDatabaseHelper(context);
        if (userDatabaseHelper.getNextTripID() == 2) {
            Log.i(TAG, "first time init");
            dbaFirstTimeInit();
        } else if (userDatabaseHelper.getNextTripID() > 2) {
            dbaInitPreviousSavedTrip();
        } // else : baselines are all null, not enough data
        printBaselines();
    }


    private void makeBaselineArrays() {
        leftTurnBaseline = new double[2][0];
        rightTurnBaseline = new double[2][0];
        accelBaseline = new int[0];
        brakeBaseline = new int[0];
        cruiseBaseline = new int[0];
        speedingBaseline = new int[0];
    }


    private void dbaFirstTimeInit() {
        ArrayList<UserData> allData = userDatabaseHelper.getData();
        ArrayList<Turn> turns = getTurnData(allData);
        dbaLeftAndRightTurns(turns);
    }

    private void dbaInitPreviousSavedTrip() {
        //initArrays();
        ArrayList<UserData> lastTripData = userDatabaseHelper.getLastTripData();
        ArrayList<Turn> turns = getTurnData(lastTripData);
        dbaLeftAndRightTurns(turns);
    }


    /**
     * Inits all the baseline arrays with data from the baseline database.
     */
    /*
    private void initArrays() {
        Turn rightTurn = baselineDatabaseHelper.getRightTurnData(UserData.FLAG_RIGHT_TURN);
        Turn leftTurn = baselineDatabaseHelper.getLeftTurnData(UserData.FLAG_LEFT_TURN);
        Log.i("right", "init: " + rightTurn.size());
        initTurnBaselineArray(rightTurn, rightTurnBaseline);
        Log.i("left", "init: " + leftTurn.size());
        initTurnBaselineArray(leftTurn, leftTurnBaseline);
    }

    private void initTurnBaselineArray(Turn turn, double[][] baseline) {
        ArrayList<TurnDataPoint> dataPoints = turn.getTurnDataPoints();
        for (int i = 0; i < baseline[0].length && i < dataPoints.size(); i++) {
            //Log.i(TAG, "init turn baseline Array: ");
            //Log.i(TAG, "steering: " + dataPoints.get(i).getSteering());
            //Log.i(TAG, "speed: " + dataPoints.get(i).getSpeed());
            baseline[0][i] = dataPoints.get(i).getSteering();
            baseline[1][i] = dataPoints.get(i).getSpeed();
        }
        Util.print2dArray(baseline, "dbBaseline");
    }
    */

    private ArrayList<Turn> getTurnData(ArrayList<UserData> userDatas) {
        ArrayList<Turn> turns = new ArrayList<>();
        Turn currentTurn = null;
        //Log.i(TAG, "userDatas size: " + userDatas.size());
        for (int i = 0; i < userDatas.size(); i++) {
            UserData userData = userDatas.get(i);
            SimData simData = userData.getSimData();

            if (userData.getFlag() != UserData.FLAG_NONE) {
                int turnType;
                if (userData.getFlag() == UserData.FLAG_LEFT_TURN || userData.getFlag() == UserData.FLAG_LEFT_TURN_SPEEDING) {
                    turnType = Turn.TURN_LEFT;
                } else if(userData.getFlag() == UserData.FLAG_RIGHT_TURN || userData.getFlag() == UserData.FLAG_RIGHT_TURN_SPEEDING) {
                    turnType = Turn.TURN_RIGHT;
                } else {
                    turnType = -1;
                }
                if (turnType != -1) {
                    if (currentTurn == null) {
                        currentTurn = new Turn(turnType, 0, userData.getFlag());
                    }
                    currentTurn.addTurnPoint(new TurnDataPoint(simData.getSpeed(),
                            simData.getSteering()));
                }

            } else if (currentTurn != null) {
                turns.add(currentTurn);
                //Log.i(TAG, "turn size: " + currentTurn.size());
                currentTurn = null;
            }
        }
        return turns;
    }

    private void dbaLeftAndRightTurns(ArrayList<Turn> turns) {
        ArrayList<Turn> leftTurns = new ArrayList<>();
        ArrayList<Turn> rightTurns = new ArrayList<>();
        sortTurnData(turns, leftTurns, rightTurns);
        //Log.i(TAG, "dba left");
        dbaTurns(Turn.TURN_LEFT, leftTurns);
        //Log.i(TAG, "dba right");
        dbaTurns(Turn.TURN_RIGHT, rightTurns);
    }

    private void printBaselines() {
        Log.i(TAG, "right turn baseline");
        Util.print2dArray(rightTurnBaseline, TAG);
        Log.i(TAG, "left turn baseline");
        Util.print2dArray(leftTurnBaseline, TAG);
    }

    private void sortTurnData(ArrayList<Turn> turns, ArrayList<Turn> leftTurns, ArrayList<Turn> rightTurns) {
        for (Turn t : turns) {
            if (t.getTurnType() == Turn.TURN_LEFT) {
                leftTurns.add(t);
            } else if (t.getTurnType() == Turn.TURN_RIGHT) {
                rightTurns.add(t);
            }
        }
    }

    /** Baseline specific turn events **/
    private void dbaTurns(int turnType, ArrayList<Turn> turns) {
        int totalTurns = turns.size();
        Turn turnBaseline = baselineDatabaseHelper.getTurnData(turnType, UserData.FLAG_RIGHT_TURN);
        int maxBaselineLength = 0;
        for (Turn t: turns) {
            if (maxBaselineLength < t.size())
                maxBaselineLength = t.size();
        }
        if (turnBaseline != null) {
            if (maxBaselineLength < turnBaseline.size())
                maxBaselineLength = turnBaseline.size();
        }
        //Log.i(TAG, "total turns: " + totalTurns);
        if (totalTurns > 0) {
            double[][] steeringSequence = new double[totalTurns][maxBaselineLength];
            double[][] speedSequence = new double[totalTurns][maxBaselineLength];
            double[] averageSteeringSequence;
            double[] averageSpeedSequence;
            int flag = turns.get(0).getFlag();
            if (flag == UserData.FLAG_LEFT_TURN) {
                leftTurnBaseline = new double[2][maxBaselineLength];
                initBaseline(maxBaselineLength, turnBaseline, leftTurnBaseline);
                averageSteeringSequence = leftTurnBaseline[0];
                averageSpeedSequence = leftTurnBaseline[1];
            } else {
                rightTurnBaseline = new double[2][maxBaselineLength];
                initBaseline(maxBaselineLength, turnBaseline, rightTurnBaseline);
                averageSteeringSequence = rightTurnBaseline[0];
                averageSpeedSequence = rightTurnBaseline[1];
            }
            for (int i = 0; i < turns.size(); i++) {
                Turn turn = turns.get(i);
                int turnSize = turn.size();
                for (int j = 0; j < steeringSequence[0].length; j++) {
                    ArrayList<TurnDataPoint> turnPoints = turn.getTurnDataPoints();
                    if (turnSize > j) {
                        TurnDataPoint turnDataPoint = turnPoints.get(j);
                        steeringSequence[i][j] = turnDataPoint.getSteering();
                        speedSequence[i][j] = turnDataPoint.getSpeed();
                    } else {
                        steeringSequence[i][j] = 0; // for now, shouldn't happen in general
                        speedSequence[i][j] = 0;
                    }
                }
            }
            //Util.print2dArray(steeringSequence, "steering");
            //Util.print2dArray(speedSequence, "speed");
            printBaselines();
            // dba a few times for the average
            DBA.DBA(averageSteeringSequence, steeringSequence);
            DBA.DBA(averageSteeringSequence, steeringSequence);
            DBA.DBA(averageSpeedSequence, speedSequence);
            DBA.DBA(averageSpeedSequence, speedSequence);
            // save the baseline in the db.
            printBaselines();
            overwriteTurnBaseline(averageSteeringSequence, averageSpeedSequence, turnType, flag);
        }
    }

    private void initBaseline(int maxBaselineLength, Turn turnBaseline, double[][] baseline) {
        ArrayList<TurnDataPoint> dataPoints = turnBaseline.getTurnDataPoints();
        for (int i = 0; i < turnBaseline.size() && i < maxBaselineLength; i++) {
            baseline[0][i] = dataPoints.get(i).getSteering();
            baseline[1][i] = dataPoints.get(i).getSpeed();
        }
    }

    private void overwriteTurnBaseline(double[] averageSteeringSequence, double[] averageSpeedSequence, int turnType, int flag) {
        //Log.i(TAG, "overwrite Turn baseline legth" + averageSpeedSequence.length);
        Turn turn = new Turn(turnType, 0, flag);
        for (int i = 0; i < averageSpeedSequence.length; i++) {
            turn.addTurnPoint(new TurnDataPoint(averageSpeedSequence[i], averageSteeringSequence[i]));
        }
        //Log.i(TAG, turn.toString());
        baselineDatabaseHelper.clearTurn(turnType, flag);
        baselineDatabaseHelper.saveTurn(turn);
    }

    public double[][] getLeft(){
        return leftTurnBaseline;
    }

    public double[][] getRight(){
        return rightTurnBaseline;
    }

    public int[] getAccel(){
        return accelBaseline;
    }

    public int[] getBrake(){
        return brakeBaseline;
    }

    public int[] getCruise(){
        return cruiseBaseline;
    }

    public int[] speedingBaseline(){
        return speedingBaseline;
    }


}
