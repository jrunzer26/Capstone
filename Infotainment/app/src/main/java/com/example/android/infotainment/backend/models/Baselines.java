package com.example.android.infotainment.backend.models;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.infotainment.backend.BaselineDatabaseHelper;
import com.example.android.infotainment.backend.DBA;
import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.FastDTW.dtw.TimeWarpInfo;
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
    private final int ACCELERATION_WINDOW = 10;
    private final int BRAKING_WINDOW = 10;
    private final double ACCLERATION_THRESHOLD_KMpHpS = 2.5;
    private final double BRAKING_THRESHOLD_KMpHpS = -2.5;
    private final double THRESHHOLD_SPEED = 10;
    private final double CRUISING_SIZE = 50;
    private final double SPEEDING_SIZE = 50;

    // baselines
    // left & right turn:
    // index [0] = steering
    // index [1] = speed
    private double[][] leftTurnBaseline;
    private double[][] rightTurnBaseline;
    private double[] accelNearStopBaseline;
    private double[] accelFromSpeedBaseline;
    private double[] brakeBaseline;
    private double[] cruiseBaseline;
    // baseline
    // speeding baseline:
    // index [0] = devPercent
    private double[] speedingBaseline;
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
        accelFromSpeedBaseline = new double[0];
        accelNearStopBaseline = new double[0];
        brakeBaseline = new double[0];
        cruiseBaseline = new double[0];
        speedingBaseline = new double[0];
    }

    /**
     * Performs DBA for the baselines on all trip data in the database.
     */
    private void dbaFirstTimeInit() {
        ArrayList<UserData> allData = userDatabaseHelper.getData();
        startTurnDba(allData);
        startAccelerationDba(allData);
        startBrakingDba(allData);
        startCruisingDba(allData);
        startSpeedingBaseline(allData);
    }



    /**
     * Gathers the last trip data and performs dba on the average on the gathered last trip data.
     */
    private void dbaInitPreviousSavedTrip() {
        ArrayList<UserData> lastTripData = userDatabaseHelper.getLastTripData();
        startTurnDba(lastTripData);
        startAccelerationDba(lastTripData);
        startBrakingDba(lastTripData);
        startCruisingDba(lastTripData);
        startSpeedingBaseline(lastTripData);
    }

    // ######################### Turn Baselines #########################

    private void startTurnDba(ArrayList<UserData> lastTripData) {
        ArrayList<Turn> turns = getTurnData(lastTripData);
        Log.i("turns extracted: ", turns.size()+"");
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
        int lastUserDataTripID = 0;
        if (userDatas.size() > 0)
            lastUserDataTripID = userDatas.get(0).getTripID();
        int prevTurnType = -2;
        for (int i = 0; i < userDatas.size(); i++) {
            UserData userData = userDatas.get(i);
            int tripID = userData.getTripID();
            SimData simData = userData.getSimData();
            int turnType;
            if (userData.getTurnFlag() == UserData.FLAG_LEFT_TURN || userData.getTurnFlag() == UserData.FLAG_LEFT_TURN_SPEEDING) {
                turnType = Turn.TURN_LEFT;
            } else if(userData.getTurnFlag() == UserData.FLAG_RIGHT_TURN || userData.getTurnFlag() == UserData.FLAG_RIGHT_TURN_SPEEDING) {
                turnType = Turn.TURN_RIGHT;
            } else {
                turnType = -1;
            }
            if (userData.getTurnFlag() != UserData.FLAG_NONE || tripID != lastUserDataTripID && turnType != prevTurnType) {
                // assign the turn type based on the flag.
                if (turnType != -1) {
                    lastUserDataTripID = tripID;
                    // add the data to the currentTurn
                    if (currentTurn == null) {
                        currentTurn = new Turn(0, turnType, userData.getTurnFlag());
                    }
                    currentTurn.addTurnPoint(new TurnDataPoint(simData.getSpeed(),
                            simData.getSteering()));
                    prevTurnType = turnType;
                }
                // add to the turn array if there is a new turn detected
            } else if (currentTurn != null) {
                turns.add(currentTurn);
                currentTurn = null;
                lastUserDataTripID = tripID;
                prevTurnType = turnType;
                i--; // go back one to re look at the missed data.
            } else {
                prevTurnType = turnType;
                lastUserDataTripID = tripID;
            }
        }
        if(currentTurn != null) {
            turns.add(currentTurn);
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
        Log.i("extracted right turns ", ""+rightTurns.size());
        dbaTurns(Turn.TURN_LEFT, leftTurns, UserData.FLAG_LEFT_TURN);
        dbaTurns(Turn.TURN_RIGHT, rightTurns, UserData.FLAG_RIGHT_TURN);
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
    private void dbaTurns(int turnType, ArrayList<Turn> turns, int flag) {
        int totalTurns = turns.size();
        Turn turnBaseline;
        int maxBaselineLength = 0;
        for (Turn t: turns) {
            if (maxBaselineLength < t.size())
                maxBaselineLength = t.size();
        }
        turnBaseline = baselineDatabaseHelper.getTurnData(turnType, flag);
        if (turnBaseline != null) {
            if (maxBaselineLength < turnBaseline.size())
                maxBaselineLength = turnBaseline.size();
        }
        int multiplicity;
        if (turnBaseline.size() > 0)
            multiplicity = turnBaseline.getMulti(); // get from baseline
        else
            multiplicity = 0;
        double[][] steeringSequence = new double[totalTurns + multiplicity][];
        double[][] speedSequence = new double[totalTurns + multiplicity][];
        double[] averageSteeringSequence;
        double[] averageSpeedSequence;
        // assign the array based on the flag to reuse code.
        if (flag == UserData.FLAG_LEFT_TURN) {
            leftTurnBaseline = new double[2][maxBaselineLength];
            initTurnBaseline(maxBaselineLength, turnBaseline, leftTurnBaseline, turns);
            averageSteeringSequence = leftTurnBaseline[0];
            averageSpeedSequence = leftTurnBaseline[1];
        } else {
            rightTurnBaseline = new double[2][maxBaselineLength];
            initTurnBaseline(maxBaselineLength, turnBaseline, rightTurnBaseline, turns);
            averageSteeringSequence = rightTurnBaseline[0];
            averageSpeedSequence = rightTurnBaseline[1];
        }
        if (totalTurns > 0) {
            // convert the turns into the 2d sequence arrays
            for (int i = 0; i < turns.size(); i++) {
                Turn turn = turns.get(i);
                int turnSize = turn.size();
                ArrayList<TurnDataPoint> turnPoints = turn.getTurnDataPoints();
                steeringSequence[i] = new double[turnPoints.size()];
                speedSequence[i] = new double[turnPoints.size()];
                for (int j = 0; j < turnSize; j++) {
                    if (turnSize > j) {
                        TurnDataPoint turnDataPoint = turnPoints.get(j);
                        steeringSequence[i][j] = turnDataPoint.getSteering();
                        speedSequence[i][j] = turnDataPoint.getSpeed();
                    }
                }
            }

            if (turnBaseline.size() > 0) {
                ArrayList<TurnDataPoint> baselinePoints = turnBaseline.getTurnDataPoints();
                for (int i = turns.size(); i < multiplicity + turns.size(); i++) {
                    steeringSequence[i] = new double[baselinePoints.size()];
                    speedSequence[i] = new double[baselinePoints.size()];
                    for (int j = 0; j < baselinePoints.size(); j++) {
                        steeringSequence[i][j] = baselinePoints.get(i).getSteering();
                        speedSequence[i][j] = baselinePoints.get(i).getSpeed();
                    }
                }
                // init the average sequence from baseline
                ArrayList<TurnDataPoint> points = turnBaseline.getTurnDataPoints();
                for(int i = 0; i < turnBaseline.size(); i++) {
                    averageSpeedSequence[i] = points.get(i).getSpeed();
                    averageSteeringSequence[i] = points.get(i).getSteering();
                }
            } else {
                // init from the first turn
                Turn t = turns.get(0);
                ArrayList<TurnDataPoint> points = t.getTurnDataPoints();
                for(int i = 0; i < t.size(); i++) {
                    averageSpeedSequence[i] = points.get(i).getSpeed();
                    averageSteeringSequence[i] = points.get(i).getSteering();
                }
            }
            // dba for the average
            DBA.DBA(averageSteeringSequence, steeringSequence);
            DBA.DBA(averageSpeedSequence, speedSequence);
            // save the baseline in the db.
            multiplicity = steeringSequence.length;
            Log.i("turn multi: ", ""+multiplicity);
            overwriteTurnBaselineDB(averageSteeringSequence, averageSpeedSequence, turnType, flag, multiplicity);
        } else {
            Log.i("init from baseline", turnBaseline.size() + "");
            ArrayList<TurnDataPoint> points = turnBaseline.getTurnDataPoints();
            for(int i = 0; i < turnBaseline.size(); i++) {
                averageSpeedSequence[i] = points.get(i).getSpeed();
                averageSteeringSequence[i] = points.get(i).getSteering();
            }
        }
    }


    /**
     * Inits the turn baseline witht the data from the turn.
     * @param maxBaselineLength the max length
     * @param turnBaseline the turn baseline from the db.
     * @param baseline the reference to the left or right turn baseline.
     */
    private void initTurnBaseline(int maxBaselineLength, Turn turnBaseline, double[][] baseline, ArrayList<Turn> turns) {
        if (turnBaseline.size() == 0 && turns.size() > 0) {
            ArrayList<TurnDataPoint> dataPoints = turns.get(0).getTurnDataPoints();
            for (int i = 0; i < turnBaseline.size() && i < maxBaselineLength; i++) {
                baseline[0][i] = dataPoints.get(i).getSteering();
                baseline[1][i] = dataPoints.get(i).getSpeed();
            }
        } else {
            ArrayList<TurnDataPoint> dataPoints = turnBaseline.getTurnDataPoints();
            for (int i = 0; i < turnBaseline.size() && i < maxBaselineLength; i++) {
                baseline[0][i] = dataPoints.get(i).getSteering();
                baseline[1][i] = dataPoints.get(i).getSpeed();
            }
        }
    }

    /**
     * Overwrite the turn baseline in the corresponding database.
     * @param averageSteeringSequence the steering baseline
     * @param averageSpeedSequence the speed baseline
     * @param turnType the type of turn
     * @param flag the flag of the turn
     */
    private void overwriteTurnBaselineDB(double[] averageSteeringSequence, double[] averageSpeedSequence, int turnType, int flag, int multi) {
        Turn turn = new Turn(0, turnType, flag);
        for (int i = 0; i < averageSpeedSequence.length; i++) {
            turn.addTurnPoint(new TurnDataPoint(averageSpeedSequence[i], averageSteeringSequence[i]));
        }
        baselineDatabaseHelper.clearTurn(turnType, flag);
        baselineDatabaseHelper.saveTurn(turn, multi);
    }

    // ######################### Acceleration Baselines #########################

    private void startAccelerationDba(ArrayList<UserData> lastTripData) {
        ArrayList<ArrayList<UserData>> accelTimeseries = getAccelTimeSeries(lastTripData);
        Log.i(TAG, "accel time series length: " + accelTimeseries.size());
        ArrayList<ArrayList<UserData>> fromNearStop = new ArrayList<>();
        ArrayList<ArrayList<UserData>> fromSpeed = new ArrayList<>();
        sortAccelerationData(accelTimeseries, fromNearStop, fromSpeed);
        Log.i(TAG, "accel time series length sorted fromSpeed: " + fromSpeed.size());
        Util.print2dUserDataListSpeed(fromSpeed, TAG);
        Log.i(TAG, "accel time series length sorted fromNearStop: " + fromNearStop.size());
        Util.print2dUserDataListSpeed(fromNearStop, TAG);
        dbaAcceleration(fromNearStop, UserData.FLAG_ACCELERATION_NEAR_STOP);
        dbaAcceleration(fromSpeed, UserData.FLAG_ACCELERATION_FROM_SPEED);
    }

    /**
     * Analyzes the previous user data and sorts the user data into bundles of acceleration time series.
     * @param allData the data to extract from.
     * @return the array list of the selected timeseries.
     */
    private ArrayList<ArrayList<UserData>> getAccelTimeSeries(ArrayList<UserData> allData) {
        boolean currentlyAccelerating = false;
        ArrayList<UserData> currentAccelTimeseries = new ArrayList<>();
        ArrayList<ArrayList<UserData>> allAccelerationTimeseries = new ArrayList<>();
        // change in time for the two data points.
        double deltaTime = DataParser.pollTimeSeconds * ACCELERATION_WINDOW;
        double deltaSpeed;
        double acceleration;
        for (int i = 0; i < allData.size() - ACCELERATION_WINDOW; i++) {
            int tripID1 = allData.get(i).getTripID();
            int tripID2 = allData.get(i + ACCELERATION_WINDOW).getTripID();
            SimData firstPoint = allData.get(i).getSimData();
            SimData lastPoint = allData.get(i + ACCELERATION_WINDOW).getSimData();
            deltaSpeed = lastPoint.getSpeed() - firstPoint.getSpeed();
            acceleration = deltaSpeed / (deltaTime + 0.0);
            if (currentlyAccelerating && acceleration < ACCLERATION_THRESHOLD_KMpHpS || tripID1 != tripID2) {
                currentlyAccelerating = false;
                // add and reset the current time series
                if (currentAccelTimeseries.size() > 0) {
                    allAccelerationTimeseries.add(currentAccelTimeseries);
                }
                currentAccelTimeseries = new ArrayList<>();
            } else if(acceleration >= ACCLERATION_THRESHOLD_KMpHpS) {
                if (!currentlyAccelerating) {
                    // add all 10 points to the accel series if we detect the first acceleration occurrence.
                    for (int j = i; j < ACCELERATION_WINDOW + i; j++) {
                        currentAccelTimeseries.add(allData.get(j));
                    }
                    currentlyAccelerating = true;
                } else {
                    // add the last point to the list to avoid duplication.
                    currentAccelTimeseries.add(allData.get(i + ACCELERATION_WINDOW));
                }
            }
        }
        // add the missed timeseries if the data is the side of the for loop and the user doesn't stop accelerating
        if (currentlyAccelerating) {
            allAccelerationTimeseries.add(currentAccelTimeseries);
        }
        return allAccelerationTimeseries;
    }


    /**
     * Sorts the timeseries related to acceleration into near stop or from speed data
     * @param accelTimeSeries the acceleration sequences
     * @param fromNearStop the empty list to add the timeseries to
     * @param fromSpeed the empty list to add the timeseries to
     */
    private void sortAccelerationData(ArrayList<ArrayList<UserData>> accelTimeSeries,
                                      ArrayList<ArrayList<UserData>> fromNearStop,
                                      ArrayList<ArrayList<UserData>> fromSpeed) {
        // iterate through the list and associate each from a near stop or from speed.
        for (int i = 0; i < accelTimeSeries.size(); i++) {
            // check the first point in the acceleration time series, and see if the speed if close to
            // stopping
            ArrayList<UserData> currentUserData = accelTimeSeries.get(i);
            if (accelTimeSeries.get(i).get(0).getSimData().getSpeed() < THRESHHOLD_SPEED) {
                fromNearStop.add(currentUserData);
            } else { // >= threshold
                fromSpeed.add(currentUserData);
            }
        }
    }

    /**
     * Performs DBA with the timeseries of user data provided.
     * @param accelTimeSeries near stop list or from speed list
     * @param flag the type of acceleration
     */
    private void dbaAcceleration(ArrayList<ArrayList<UserData>> accelTimeSeries, int flag) {
        Util.print2dUserDataListSpeed(accelTimeSeries, "ACCEL TIMEE SERIES");
        // perform dba
        int totalTimeSeries = accelTimeSeries.size();
        double[] baseline;
        double[] baselineContents = baselineDatabaseHelper.getAccelerationBaseline(flag);
        int maxBaselineLength = baselineContents.length;
        // find the max size
        for (int i = 0; i < accelTimeSeries.size(); i++) {
            int size = accelTimeSeries.get(i).size();
            if (maxBaselineLength < size)
                maxBaselineLength = size;
        }

        // assign the baseline array depending on the flag
        if (flag == UserData.FLAG_ACCELERATION_NEAR_STOP) {
            accelNearStopBaseline = new double[maxBaselineLength];
            baseline = accelNearStopBaseline;

        } else {
            accelFromSpeedBaseline = new double[maxBaselineLength];
            baseline = accelFromSpeedBaseline;
        }
        // init the baselime from the db
        /*
        for (int i = 0; i < baselineContents.length; i++) {
            baseline [i] = baselineContents[i];
        }
        */
        if (totalTimeSeries > 0) {
            // convert the turns into the 2d sequence arrays
            int multiplicityBaseline = baselineDatabaseHelper.getMultiplicityFromTable(
                    baselineDatabaseHelper.getAccelerationTableName(flag));
            Log.i(TAG, "multi accel: " + multiplicityBaseline);
            double[][] series2;
            if (baselineContents.length > 0) {
                Log.i(TAG, "not null");
                series2 = new double[multiplicityBaseline + accelTimeSeries.size()][];
            } else
                series2 = new double[accelTimeSeries.size()][];

            for (int i = 0; i < accelTimeSeries.size(); i++) {
                ArrayList<UserData> accelData = accelTimeSeries.get(i);
                double[] currentSeries = new double[accelData.size()];
                for (int j = 0; j < accelData.size(); j++) {
                    currentSeries[j] = accelData.get(j).getSimData().getSpeed();
                }
                series2[i] = currentSeries;
            }
            if (baselineContents.length > 0) {
                for (int i = accelTimeSeries.size(); i < series2.length; i++) {
                    series2[i] = baselineContents;
                }
                for (int i = 0; i < baselineContents.length; i++) {
                    baseline[i] = baselineContents[i];
                }

            } else {
                for (int i = 0; i < series2[0].length; i++) {
                    baseline[i] = series2[0][i];
                }
            }
            multiplicityBaseline = series2.length;
            Util.printArray(baselineContents, "Baseline conents before");
            Util.print2dArray(series2, "TIMESERIES accel flag: " + flag);
            Util.printArray(baseline, "baseline before");
            // dba a few times for the average
            DBA.DBA(baseline, series2);

            Util.printArray(baseline, "baseline contents after");
            // save the baseline in the db.
            baselineDatabaseHelper.overWriteAccelerationBaseline(baseline, flag, multiplicityBaseline);
        } else {
            for (int i = 0; i < baselineContents.length; i++) {
                baseline[i] = baselineContents[i];
            }
        }
    }

    // ######################### Braking Baseline #########################

    /**
     * Starts the braking dba sequence
     * @param data the data to perform the dba on.
     */
    private void startBrakingDba(ArrayList<UserData> data) {
        ArrayList<ArrayList<UserData>> brakeTimeSeries = getBrakeTimeSeries(data);
        Log.i(TAG, "brake extracted length: " + brakeTimeSeries.size());
        dbaBraking(brakeTimeSeries);
    }

    /**
     * Analyzes the previous user data and sorts the user data into bundles of acceleration time series.
     * @param allData the data to extract from.
     * @return the array list of the selected timeseries.
     */
    private ArrayList<ArrayList<UserData>> getBrakeTimeSeries(ArrayList<UserData> allData) {
        boolean currentlyAccelerating = false;
        ArrayList<UserData> currentAccelTimeseries = new ArrayList<>();
        ArrayList<ArrayList<UserData>> allAccelerationTimeseries = new ArrayList<>();
        // change in time for the two data points.
        double deltaTime = DataParser.pollTimeSeconds * BRAKING_WINDOW;
        double deltaSpeed;
        double acceleration;
        for (int i = 0; i < allData.size() - BRAKING_WINDOW; i++) {
            int tripID1 = allData.get(i).getTripID();
            int tripID2 = allData.get(i + BRAKING_WINDOW).getTripID();
            SimData firstPoint = allData.get(i).getSimData();
            SimData lastPoint = allData.get(i + BRAKING_WINDOW).getSimData();
            deltaSpeed = lastPoint.getSpeed() - firstPoint.getSpeed();
            acceleration = deltaSpeed / (deltaTime + 0.0);
            if (currentlyAccelerating && acceleration > BRAKING_THRESHOLD_KMpHpS || tripID1 != tripID2) {
                currentlyAccelerating = false;
                // add and reset the current time series
                if (currentAccelTimeseries.size() > 0) {
                    allAccelerationTimeseries.add(currentAccelTimeseries);
                }
                currentAccelTimeseries = new ArrayList<>();
            } else if(acceleration <= BRAKING_THRESHOLD_KMpHpS) {
                if (!currentlyAccelerating) {
                    // add all 10 points to the accel series if we detect the first acceleration occurrence.
                    for (int j = i; j < BRAKING_WINDOW + i; j++) {
                        currentAccelTimeseries.add(allData.get(j));
                    }
                    currentlyAccelerating = true;
                } else {
                    // add the last point to the list to avoid duplication.
                    currentAccelTimeseries.add(allData.get(i + BRAKING_WINDOW));
                }
            }
        }
        // add the missed timeseries if the data is the side of the for loop and the user doesn't stop accelerating
        if (currentlyAccelerating) {
            allAccelerationTimeseries.add(currentAccelTimeseries);
        }
        return allAccelerationTimeseries;
    }

    /**
     * Performs braking dba on the data
     * @param brakingTimeSeries the series to perform dba on.
     */
    private void dbaBraking(ArrayList<ArrayList<UserData>> brakingTimeSeries) {
        int totalTimeSeries = brakingTimeSeries.size();

        double[] baseline = baselineDatabaseHelper.getBrakingBaseline();
        // see if the max is still greater than the baseline length
        int max = baseline.length;
        for(int i = 0; i < brakingTimeSeries.size(); i++) {
            int size = brakingTimeSeries.get(i).size();
            if (size > max)
                max = size;
        }
        Log.i("total time series", totalTimeSeries+"");
        if (totalTimeSeries > 0) {
            double[][] brakingSeries = new double[totalTimeSeries][];
            // convert the turns into the 2d sequence arrays
            for (int i = 0; i < brakingTimeSeries.size(); i++) {
                ArrayList<UserData> accelData = brakingTimeSeries.get(i);
                int accelSize = accelData.size();
                double[] series = new double[accelSize];
                // convert to array.
                for (int j = 0; j < accelSize; j++) {
                    series[j] = accelData.get(j).getSimData().getSpeed();
                }
                brakingSeries[i] = series;
            }
            int multiplicityBaseline = baselineDatabaseHelper.getBrakeMulti();
            Log.i("brake multi: ", ""+multiplicityBaseline);
            brakeBaseline = new double[max];
            if (baseline.length == 0) {
                Log.i("BRAKE", "baseline length = 0");
                for(int i = 0; i < brakingSeries[0].length; i++) {
                    brakeBaseline[i] = brakingSeries[0][i];
                }
            } else {
                Log.i("BRAKE", "MERGE BASELINES");
                double[][] series2 = new double[multiplicityBaseline + brakingSeries.length][];
                for (int i = multiplicityBaseline; i < series2.length; i++) {
                    series2[i] = brakingSeries[i - multiplicityBaseline];
                }
                for(int i = 0; i < multiplicityBaseline; i++) {
                    series2[i] = baseline;
                }
                brakeBaseline = new double[max];
                for(int i = 0; i < baseline.length; i++) {
                    brakeBaseline[i] = baseline[i];
                }
            }
            DBA.DBA(brakeBaseline, brakingSeries);
            Util.print2dArray(brakingSeries, "TIMESERIES");
            Util.printArray(brakeBaseline, "brake baseline");
            // save the baseline in the db.
            multiplicityBaseline = brakingSeries.length;
            baselineDatabaseHelper.overWriteBrakingBaseline(brakeBaseline, multiplicityBaseline);
        } else {
            brakeBaseline = baseline;
        }
    }

    // ##################### DTW & DBA Alggorithm ##############################

    /**
     * Find the warping result from the dtw warped path indexes.
     * @param info the path info based on the dtw of the time series.
     * @param timeSeries1 the original time series.
     * @param timeSeries2
     * @param warpTimeSeries1 the array large enough for the warped time series.
     * @param warpTimeSeries2
     */
    private void pathToArray(TimeWarpInfo info, double[] timeSeries1, double[] timeSeries2, double[] warpTimeSeries1, double[] warpTimeSeries2) {
        ArrayList<Integer> timeSeries1Path = info.getPath().getTS1();
        ArrayList<Integer> timeSeries2Path = info.getPath().getTS2();
        Util.printArray(timeSeries1, "timeSeries1");
        Util.printArray(timeSeries2, "timeseries2");
        for (int i = 0; i < timeSeries1Path.size(); i++) {
            warpTimeSeries1 [i] = timeSeries1[timeSeries1Path.get(i)];
            warpTimeSeries2 [i] = timeSeries2[timeSeries2Path.get(i)];
        }
        Util.printArray(warpTimeSeries1, "warpSeries1");
        Util.printArray(warpTimeSeries2, "warpseries2");

    }

    // ###### Cruising Baseline #############

    private void startCruisingDba(ArrayList<UserData> data) {
        ArrayList<ArrayList<UserData>> extractedData = extractCruisingData(data);
        Util.print2dUserDataListSteering(extractedData, "extracted cruising");
        dbaCruise(extractedData);

    }


    private ArrayList<ArrayList<UserData>> extractCruisingData(ArrayList<UserData> data) {
        ArrayList<ArrayList<UserData>> extractedData = new ArrayList<>();
        ArrayList<UserData> currentUserData = null;
        int count = 0;
        for(int i = 0; i < data.size(); i++) {
            if (currentUserData == null) {
                currentUserData = new ArrayList<>();
                count = 0;
            }
            UserData userData = data.get(i);
            if (count < CRUISING_SIZE && (userData.getTurnFlag() != UserData.FLAG_LEFT_TURN && userData.getTurnFlag() != UserData.FLAG_RIGHT_TURN)) {
                count++;
                currentUserData.add(data.get(i));
            } else if ((userData.getTurnFlag() == UserData.FLAG_LEFT_TURN || userData.getTurnFlag() == UserData.FLAG_RIGHT_TURN) && count > 0) {
                extractedData.add(currentUserData);
                currentUserData = null;
            } else if(count > 0){ // add the data before the turn
                extractedData.add(currentUserData);
                currentUserData = null;
            }
        }
        if (currentUserData != null) {
            extractedData.add(currentUserData);
        }
        return extractedData;
    }

    private void dbaCruise(ArrayList<ArrayList<UserData>> cruiseData) {
        int totalTimeSeries = cruiseData.size();

        double[] baseline = baselineDatabaseHelper.getCruisingBaseline();
        // see if the max is still greater than the baseline length
        int max = baseline.length;
        for(int i = 0; i < cruiseData.size(); i++) {
            int size = cruiseData.get(i).size();
            if (size > max)
                max = size;
        }
        Log.i("total time series", totalTimeSeries+"");
        if (totalTimeSeries > 0) {
            double[][] cruiseSeries = new double[totalTimeSeries][];
            // convert the turns into the 2d sequence arrays
            for (int i = 0; i < cruiseData.size(); i++) {
                ArrayList<UserData> currentCruiseData = cruiseData.get(i);
                int accelSize = currentCruiseData.size();
                double[] series = new double[accelSize];
                // convert to array.
                for (int j = 0; j < accelSize; j++) {
                    series[j] = currentCruiseData.get(j).getSimData().getSteering();
                }
                cruiseSeries[i] = series;
            }
            int multiplicityBaseline = baselineDatabaseHelper.getCruiseMulti();
            Log.i("cruise multi: ", ""+multiplicityBaseline);
            cruiseBaseline = new double[max];
            if (baseline.length == 0) {
                Log.i("Cruise", "baseline length = 0");
                for(int i = 0; i < cruiseSeries[0].length; i++) {
                    cruiseBaseline[i] = cruiseSeries[0][i];
                }
            } else {
                Log.i("cruise", "MERGE BASELINES");
                double[][] series2 = new double[multiplicityBaseline + cruiseSeries.length][];
                for (int i = multiplicityBaseline; i < series2.length; i++) {
                    series2[i] = cruiseSeries[i - multiplicityBaseline];
                }
                for(int i = 0; i < multiplicityBaseline; i++) {
                    series2[i] = baseline;
                }
                cruiseBaseline = new double[max];
                for(int i = 0; i < baseline.length; i++) {
                    cruiseBaseline[i] = baseline[i];
                }
            }
            DBA.DBA(cruiseBaseline, cruiseSeries);
            Util.print2dArray(cruiseSeries, "cruise TIMESERIES");
            Util.printArray(cruiseBaseline, "cruise baseline");
            // save the baseline in the db.
            multiplicityBaseline = cruiseSeries.length;
            baselineDatabaseHelper.overWriteCruisingBaseline(cruiseBaseline, multiplicityBaseline);
        } else {
            cruiseBaseline = baseline;
        }
    }
    // ##### Speeding Baseline ###########

    private void startSpeedingBaseline(ArrayList<UserData> data) {
        ArrayList<ArrayList<UserData>> extractedSpeeding = extractSpeedingData(data);
        Util.print2dUserDataListSpeed(extractedSpeeding, "extracted speeding");
        dbaSpeeding(extractedSpeeding);
    }

    private ArrayList<ArrayList<UserData>> extractSpeedingData(ArrayList<UserData> allData) {
        boolean currentlyAccelerating = false;
        boolean currentlyDecelerating = false;
        boolean firstOccuranceSpeeding = true;
        ArrayList<UserData> currentSpeedingTimeseries = new ArrayList<>();
        ArrayList<ArrayList<UserData>> allSpeedingTimeseries = new ArrayList<>();
        // change in time for the two data points.
        double deltaTime = DataParser.pollTimeSeconds * ACCELERATION_WINDOW;
        double deltaSpeed;
        double acceleration;
        int count = 0;
        for (int i = 0; i < allData.size() - ACCELERATION_WINDOW; i++) {
            int tripID1 = allData.get(i).getTripID();
            int tripID2 = allData.get(i + ACCELERATION_WINDOW).getTripID();
            SimData firstPoint = allData.get(i).getSimData();
            SimData lastPoint = allData.get(i + ACCELERATION_WINDOW).getSimData();
            deltaSpeed = lastPoint.getSpeed() - firstPoint.getSpeed();
            acceleration = deltaSpeed / (deltaTime + 0.0);
            if (currentlyAccelerating && acceleration < ACCLERATION_THRESHOLD_KMpHpS || tripID1 != tripID2) {
                currentlyAccelerating = false;
            } else if (acceleration >= ACCLERATION_THRESHOLD_KMpHpS) {
                currentlyAccelerating = true;
                firstOccuranceSpeeding = true;
            }

            if (currentlyDecelerating && acceleration > BRAKING_THRESHOLD_KMpHpS || tripID1 != tripID2) {
                currentlyDecelerating = false;
            } else if (acceleration <= BRAKING_THRESHOLD_KMpHpS) {
                currentlyDecelerating = true;
                firstOccuranceSpeeding = true;
            }
            Log.i(" BRAKING", "currently accel: " + currentlyAccelerating + " currently decel: " + currentlyDecelerating + " count: " + count);
            if (!currentlyAccelerating && !currentlyDecelerating && !firstOccuranceSpeeding && count < SPEEDING_SIZE - 1) {
                currentSpeedingTimeseries.add(allData.get(i + ACCELERATION_WINDOW));
                Log.i("increasing timeseries: ", "count: " + count);
                count++;
            } else if (!currentlyAccelerating && !currentlyAccelerating && firstOccuranceSpeeding) {
                for (int j = i; j < ACCELERATION_WINDOW + i; j++) {
                    currentSpeedingTimeseries.add(allData.get(j));
                    count++;
                }
                firstOccuranceSpeeding = false;
            } else if (count > 0) {
                if (!currentlyAccelerating && !currentlyDecelerating)
                    currentSpeedingTimeseries.add(allData.get(i + ACCELERATION_WINDOW));
                Log.i("adding", "count : " + count);
                allSpeedingTimeseries.add(currentSpeedingTimeseries);
                currentSpeedingTimeseries = new ArrayList<>();
                count = 0;
            }

        }
        if (count > 0) {
            allSpeedingTimeseries.add(currentSpeedingTimeseries);
        }
        return allSpeedingTimeseries;
    }

    private void dbaSpeeding(ArrayList<ArrayList<UserData>> extractedSpeeding) {
        for (int i = 0; i < extractedSpeeding.size(); i++) {
            Log.i("sizes", "extracted size: " + extractedSpeeding.get(i).size());
        }

        double[] baseline = baselineDatabaseHelper.getCruisingBaseline();
        // see if the max is still greater than the baseline length
        int max = baseline.length;
        for(int i = 0; i < cruiseData.size(); i++) {
            int size = cruiseData.get(i).size();
            if (size > max)
                max = size;
        }
        Log.i("total time series", totalTimeSeries+"");
        if (totalTimeSeries > 0) {
            double[][] cruiseSeries = new double[totalTimeSeries][];
            // convert the turns into the 2d sequence arrays
            for (int i = 0; i < cruiseData.size(); i++) {
                ArrayList<UserData> currentCruiseData = cruiseData.get(i);
                int accelSize = currentCruiseData.size();
                double[] series = new double[accelSize];
                // convert to array.
                for (int j = 0; j < accelSize; j++) {
                    series[j] = currentCruiseData.get(j).getSimData().getSteering();
                }
                cruiseSeries[i] = series;
            }
            int multiplicityBaseline = baselineDatabaseHelper.getCruiseMulti();
            Log.i("cruise multi: ", ""+multiplicityBaseline);
            cruiseBaseline = new double[max];
            if (baseline.length == 0) {
                Log.i("Cruise", "baseline length = 0");
                for(int i = 0; i < cruiseSeries[0].length; i++) {
                    cruiseBaseline[i] = cruiseSeries[0][i];
                }
            } else {
                Log.i("cruise", "MERGE BASELINES");
                double[][] series2 = new double[multiplicityBaseline + cruiseSeries.length][];
                for (int i = multiplicityBaseline; i < series2.length; i++) {
                    series2[i] = cruiseSeries[i - multiplicityBaseline];
                }
                for(int i = 0; i < multiplicityBaseline; i++) {
                    series2[i] = baseline;
                }
                cruiseBaseline = new double[max];
                for(int i = 0; i < baseline.length; i++) {
                    cruiseBaseline[i] = baseline[i];
                }
            }
            DBA.DBA(cruiseBaseline, cruiseSeries);
            Util.print2dArray(cruiseSeries, "cruise TIMESERIES");
            Util.printArray(cruiseBaseline, "cruise baseline");
            // save the baseline in the db.
            multiplicityBaseline = cruiseSeries.length;
            baselineDatabaseHelper.overWriteCruisingBaseline(cruiseBaseline, multiplicityBaseline);
        } else {
            cruiseBaseline = baseline;
        }


    }

    /**
     * Prints all the baselines.
     */
    public void printBaselines() {
        Log.i(TAG, "right turn baseline");
        Util.print2dArray(rightTurnBaseline, TAG);
        Log.i(TAG, "left turn baseline");
        Util.print2dArray(leftTurnBaseline, TAG);
        Log.i(TAG, "From Near stop accel baseline");
        Util.printArray(accelNearStopBaseline, TAG);
        Log.i(TAG, "From Speed accel baseline");
        Util.printArray(accelFromSpeedBaseline, TAG);
        Log.i(TAG, "Braking baseline");
        Util.printArray(brakeBaseline, TAG);
        Log.i(TAG, "Cruising baseline");
        Util.printArray(cruiseBaseline, TAG);
        Log.i(TAG, "Speeding baseline");
        Util.printArray(speedingBaseline, TAG);
    }

    public double[][] getLeft(){
        return leftTurnBaseline;
    }

    public double[][] getRight(){
        return rightTurnBaseline;
    }

    public double[] getNearStopAccel(){
        return accelNearStopBaseline;
    }

    public double[] getAccelFromSpeedBaseline() { return accelFromSpeedBaseline; }

    public double[] getBrake(){
        return brakeBaseline;
    }

    public double[] getCruise(){
        return cruiseBaseline;
    }

    public double[] getSpeeding(){
        return speedingBaseline;
    }

    public boolean isSetup() {
        return leftTurnBaseline[0].length != 0 &&
                rightTurnBaseline[0].length != 0 &&
                accelFromSpeedBaseline.length != 0 &&
                accelNearStopBaseline.length != 0 &&
                brakeBaseline.length != 0 && speedingBaseline.length != 0;
    }
}
