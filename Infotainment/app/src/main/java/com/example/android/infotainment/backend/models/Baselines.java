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
    // number of rows in the turn baselines
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
    // baseline
    // speeding baseline:
    // index [0] = devPercent
    private int[] speedingBaseline;
    //For the following two 2-dimensional arrays, the 0th index represents the steering data, and the 1st index represents the speed data.
    // baseline db helpers
    private BaselineDatabaseHelper baselineDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;

    /**
     * Creates the Baslines from the db or from previous trip data.
     * @param context - the current context
     */
    public Baselines(Context context) {
        makeBaselineArrays();
        // create the db helpers
        baselineDatabaseHelper = new BaselineDatabaseHelper(context);
        userDatabaseHelper = new UserDatabaseHelper(context);
        // init the baselines depending on the current trip id.
        if (userDatabaseHelper.getNextTripID() == 2) {
            dbaFirstTimeInit();
        } else if (userDatabaseHelper.getNextTripID() > 2) {
            dbaInitPreviousSavedTrip();
        } // else : baselines are all size 0, not enough data
    }

    /**
     * Initializes the baseline arrays with size 0.
     */
    private void makeBaselineArrays() {
        leftTurnBaseline = new double[TURN_PARAMETERS][0];
        rightTurnBaseline = new double[TURN_PARAMETERS][0];
        accelBaseline = new int[0];
        brakeBaseline = new int[0];
        cruiseBaseline = new int[0];
        speedingBaseline = new int[0];
    }

    // ######################### Turn Baselines #########################

    /**
     * Performs DBA for the baselines on all trip data in the database.
     */
    private void dbaFirstTimeInit() {
        ArrayList<UserData> allData = userDatabaseHelper.getData();
        ArrayList<Turn> turns = getTurnData(allData);
        //ArrayList<>
        dbaLeftAndRightTurns(turns);
        //dbaAcceleration(accelerationPoints);
    }

    /**
     * Gathers the last trip data and performs dba on the average on the gathered last trip data.
     */
    private void dbaInitPreviousSavedTrip() {
        ArrayList<UserData> lastTripData = userDatabaseHelper.getLastTripData();
        ArrayList<Turn> turns = getTurnData(lastTripData);
        dbaLeftAndRightTurns(turns);
    }

    /**
     * Extracts all the turn data from the rows of the user database.
     * @param userDatas the rows from the database.
     * @return the extracted turns
     */
    private ArrayList<Turn> getTurnData(ArrayList<UserData> userDatas) {
        ArrayList<Turn> turns = new ArrayList<>();
        Turn currentTurn = null;
        for (int i = 0; i < userDatas.size(); i++) {
            UserData userData = userDatas.get(i);
            SimData simData = userData.getSimData();
            if (userData.getFlag() != UserData.FLAG_NONE) {
                int turnType;
                // assign the turn type based on the flag.
                if (userData.getFlag() == UserData.FLAG_LEFT_TURN || userData.getFlag() == UserData.FLAG_LEFT_TURN_SPEEDING) {
                    turnType = Turn.TURN_LEFT;
                } else if(userData.getFlag() == UserData.FLAG_RIGHT_TURN || userData.getFlag() == UserData.FLAG_RIGHT_TURN_SPEEDING) {
                    turnType = Turn.TURN_RIGHT;
                } else {
                    turnType = -1;
                }
                if (turnType != -1) {
                    // add the data to the currentTurn
                    if (currentTurn == null) {
                        currentTurn = new Turn(turnType, 0, userData.getFlag());
                    }
                    currentTurn.addTurnPoint(new TurnDataPoint(simData.getSpeed(),
                            simData.getSteering()));
                }
                // add to the turn array if there is a new turn detected
            } else if (currentTurn != null) {
                turns.add(currentTurn);
                currentTurn = null;
            }
        }
        return turns;
    }

    /**
     * Performs DBA on both the left and the right turn data for the baseline
     * @param turns the left and right turn data.
     */
    private void dbaLeftAndRightTurns(ArrayList<Turn> turns) {
        ArrayList<Turn> leftTurns = new ArrayList<>();
        ArrayList<Turn> rightTurns = new ArrayList<>();
        sortTurnData(turns, leftTurns, rightTurns);
        dbaTurns(Turn.TURN_LEFT, leftTurns);
        dbaTurns(Turn.TURN_RIGHT, rightTurns);
    }

    /**
     * Sorts the turn data into the left and right turns. Thank you pass by reference.
     * @param turns the turns to sort
     * @param leftTurns the array list to contain the left turns
     * @param rightTurns the array list to contain the right turns
     */
    private void sortTurnData(ArrayList<Turn> turns, ArrayList<Turn> leftTurns, ArrayList<Turn> rightTurns) {
        for (Turn t : turns) {
            if (t.getTurnType() == Turn.TURN_LEFT) {
                leftTurns.add(t);
            } else if (t.getTurnType() == Turn.TURN_RIGHT) {
                rightTurns.add(t);
            }
        }
    }

    /**
     * Performs DBA on a SORTED turn array.
     * @param turnType the turn type of the data.
     * @param turns the sorted turn array sequences.
     */
    private void dbaTurns(int turnType, ArrayList<Turn> turns) {
        int totalTurns = turns.size();
        Turn turnBaseline;
        int maxBaselineLength = 0;
        for (Turn t: turns) {
            if (maxBaselineLength < t.size())
                maxBaselineLength = t.size();
        }

        if (totalTurns > 0) {
            double[][] steeringSequence = new double[totalTurns][maxBaselineLength];
            double[][] speedSequence = new double[totalTurns][maxBaselineLength];
            double[] averageSteeringSequence;
            double[] averageSpeedSequence;
            int flag = turns.get(0).getFlag();
            turnBaseline = baselineDatabaseHelper.getTurnData(turnType, flag);
            if (turnBaseline != null) {
                if (maxBaselineLength < turnBaseline.size())
                    maxBaselineLength = turnBaseline.size();
            }
            // assign the array based on the flag to reuse code.
            if (flag == UserData.FLAG_LEFT_TURN) {
                leftTurnBaseline = new double[2][maxBaselineLength];
                initTurnBaseline(maxBaselineLength, turnBaseline, leftTurnBaseline);
                averageSteeringSequence = leftTurnBaseline[0];
                averageSpeedSequence = leftTurnBaseline[1];
            } else {
                rightTurnBaseline = new double[2][maxBaselineLength];
                initTurnBaseline(maxBaselineLength, turnBaseline, rightTurnBaseline);
                averageSteeringSequence = rightTurnBaseline[0];
                averageSpeedSequence = rightTurnBaseline[1];
            }
            // convert the turns into the 2d sequence arrays
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
            // dba a few times for the average
            DBA.DBA(averageSteeringSequence, steeringSequence);
            DBA.DBA(averageSteeringSequence, steeringSequence);
            DBA.DBA(averageSpeedSequence, speedSequence);
            DBA.DBA(averageSpeedSequence, speedSequence);
            // save the baseline in the db.
            overwriteTurnBaselineDB(averageSteeringSequence, averageSpeedSequence, turnType, flag);
        }
    }


    /**
     * Inits the turn baseline witht the data from the turn.
     * @param maxBaselineLength the max length
     * @param turnBaseline the turn baseline from the db.
     * @param baseline the reference to the left or right turn baseline.
     */
    private void initTurnBaseline(int maxBaselineLength, Turn turnBaseline, double[][] baseline) {
        ArrayList<TurnDataPoint> dataPoints = turnBaseline.getTurnDataPoints();
        for (int i = 0; i < turnBaseline.size() && i < maxBaselineLength; i++) {
            baseline[0][i] = dataPoints.get(i).getSteering();
            baseline[1][i] = dataPoints.get(i).getSpeed();
        }
    }

    /**
     * Overwrite the turn baseline in the corresponding database.
     * @param averageSteeringSequence the steering baseline
     * @param averageSpeedSequence the speed baseline
     * @param turnType the type of turn
     * @param flag the flag of the turn
     */
    private void overwriteTurnBaselineDB(double[] averageSteeringSequence, double[] averageSpeedSequence, int turnType, int flag) {
        Turn turn = new Turn(turnType, 0, flag);
        for (int i = 0; i < averageSpeedSequence.length; i++) {
            turn.addTurnPoint(new TurnDataPoint(averageSpeedSequence[i], averageSteeringSequence[i]));
        }
        baselineDatabaseHelper.clearTurn(turnType, flag);
        baselineDatabaseHelper.saveTurn(turn);
    }

    // ######################### Acceleration/Braking Baselines #########################

    private void initAccelerationAndDecelerationBaseline() {

    }

    /**
     * Prints all the baselines.
     */
    public void printBaselines() {
        Log.i(TAG, "right turn baseline");
        Util.print2dArray(rightTurnBaseline, TAG);
        Log.i(TAG, "left turn baseline");
        Util.print2dArray(leftTurnBaseline, TAG);
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

    public int[] getSpeeding(){
        return speedingBaseline;
    }

    public boolean isSetup() {
        return leftTurnBaseline[0].length != 0 && rightTurnBaseline[0].length != 0
                && accelBaseline.length != 0 && brakeBaseline.length != 0 && speedingBaseline.length != 0;
    }
}
