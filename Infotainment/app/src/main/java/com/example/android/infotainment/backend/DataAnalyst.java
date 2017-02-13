package com.example.android.infotainment.backend;

import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.Turn;
import com.example.android.infotainment.backend.models.TurnDataPoint;
import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by 100520993 on 10/31/2016.
 */

public class DataAnalyst extends Thread implements DataReceiver {
    private String TAG = "Analyst";
    private Context applicationContext;
    private AlertSystem alertSystem;
    private UserDatabaseHelper userDatabaseHelper;
    private Queue<UserData> userDataLinkedList;
    private int userAverage = 70;
    private Double steering;
    private int TURN_WINDOW_SIZE = 19;
    private BaselineDatabaseHelper baselineDatabaseHelper;
    private double[] averageRightTurnSequence;

    /**
     * Analyses data coming in from the data parser and alerts the user.
     * @param applicationContext the current context
     */
    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
        alertSystem = new AlertSystem(applicationContext);
        userDatabaseHelper = new UserDatabaseHelper(applicationContext);
        userDataLinkedList = new ConcurrentLinkedQueue<>();
        baselineDatabaseHelper = new BaselineDatabaseHelper(applicationContext);
        // for sample usage of the base line, uncomment the line below
        //baseLineTest();
        //DBATest();
        dbaInitPreviousSavedTrip();
    }

    private void DBATest() {
        double [][]sequences = new double[2][3];

        double [] averageSequence = new double[3];
        for (int i = 0; i < averageSequence.length; i++) {
            sequences[0][i] = i + 3;
            sequences[1][i] = i *4;
        }

        for(int j=0;j<averageSequence.length;j++){
            averageSequence[j] = j * 2;
        }

        System.out.print("[");
        for(int j=0;j<averageSequence.length;j++){
            System.out.print(averageSequence[j]+" ");
        }
        System.out.println("]");

        System.out.println("after DBA");
        DBA.DBA(averageSequence, sequences);

        System.out.print("[");
        for(int j=0;j<averageSequence.length;j++){
            System.out.print(averageSequence[j]+" ");
        }
        System.out.println("]");

        DBA.DBA(averageSequence, sequences);

        System.out.print("[");
        for(int j=0;j<averageSequence.length;j++){
            System.out.print(averageSequence[j]+" ");
        }
        System.out.println("]");
    }

    /**
     * Sample usage of the base line data.
     */
    private void baseLineTest() {
        int turnID = baselineDatabaseHelper.getNextTurnId(Turn.TURN_LEFT);
        Turn turn = new Turn(Turn.TURN_LEFT, turnID);
        turn.addTurnPoint(new TurnDataPoint(100, 2, 55, 30));
        turn.addTurnPoint(new TurnDataPoint(99, 5, 60, 30));
        baselineDatabaseHelper.saveTurn(turn);
        turnID = baselineDatabaseHelper.getNextTurnId(Turn.TURN_LEFT);
        turn = new Turn(Turn.TURN_LEFT, turnID);
        turn.addTurnPoint(new TurnDataPoint(300, 6, 55, 30));
        baselineDatabaseHelper.saveTurn(turn);
        turnID = baselineDatabaseHelper.getNextTurnId(Turn.TURN_LEFT);
        turn = new Turn(Turn.TURN_LEFT, turnID);
        turn.addTurnPoint(new TurnDataPoint(3560, 6, 55, 30));
        turn.addTurnPoint(new TurnDataPoint(3560, 6, 2838, 30));
        baselineDatabaseHelper.saveTurn(turn);
        ArrayList<Turn> turnData = baselineDatabaseHelper.getLeftTurnData();
        for(Turn t : turnData) {
            Log.i(TAG, t.toString());
        }
    }


    private void dbaInitPreviousSavedTrip() {
        ArrayList<UserData> lastTripData = userDatabaseHelper.getLastTripData();
        /**
        Log.i(TAG, "DBA previous");
        for (UserData data : lastTripData) {
            Log.i(TAG, data.toString());
        }
         */
        ArrayList<Turn> turns = getTurnData(lastTripData);
        Log.i(TAG, "Extracted Turn data");
        int size = TURN_WINDOW_SIZE;
        double steeringPoints[][] = new double[turns.size()][size];
        for (int i = 0; i < turns.size(); i++) {
            Log.i(TAG, turns.get(i).toString());
            Turn turn = turns.get(i);
            System.out.println("hello");
            ArrayList<TurnDataPoint> dataPoints = turn.getTurnDataPoints();
            for (int j = 0; j < size; j++) {
                if (j < dataPoints.size())
                    steeringPoints[i][j] = dataPoints.get(j).getSteering();
                else
                    steeringPoints[i][j] = 0;

            }
        }
        for (int i = 0; i < steeringPoints.length; i++) {
            Log.i(TAG, "Turn Series: ");
            for(int j = 0; j < steeringPoints[0].length; j++) {
                Log.i(TAG, steeringPoints[i][j]+"");
            }
        }
        if (averageRightTurnSequence == null) {
            averageRightTurnSequence = new double[TURN_WINDOW_SIZE];
            // TODO: 2/12/2017 init the average sequences from DB
            for (int i = 0; i < averageRightTurnSequence.length; i++) {
                if (i < steeringPoints.length)
                    averageRightTurnSequence[i] = steeringPoints[0][i];
                else
                    averageRightTurnSequence[i] = 0;
            }
        }

        if (steeringPoints.length > 0) {
            System.out.print("[");
            for (int j = 0; j < averageRightTurnSequence.length; j++) {
                System.out.print(averageRightTurnSequence[j] + " ");
            }
            System.out.println("]");

            System.out.println("after DBA");

            DBA.DBA(averageRightTurnSequence, steeringPoints);

            System.out.print("[");
            for (int j = 0; j < averageRightTurnSequence.length; j++) {
                System.out.print(averageRightTurnSequence[j] + " ");
            }
            System.out.println("]");
        }
    }

    private ArrayList<Turn> getTurnData(ArrayList<UserData> userDatas) {
        ArrayList<Turn> turns = new ArrayList<>();
        Turn currentTurn = null;
        for (int i = 0; i < userDatas.size(); i++) {
            UserData userData = userDatas.get(i);
            SimData simData = userData.getSimData();
            SensorData sensorData = userData.getSensorData();
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
                        currentTurn = new Turn(turnType, 0);
                    }
                    currentTurn.addTurnPoint(new TurnDataPoint(simData.getSpeed(), simData.getTime().getSecond(),
                            sensorData.getHeartRate(), simData.getSteering()));
                }

            } else if (currentTurn != null) {
                turns.add(currentTurn);
                currentTurn = null;
            }
        }
        // add the turn if even if the turn is not finished
        if (currentTurn != null)
            turns.add(currentTurn);
        return turns;
    }

    private int[] getTurnDataWindow() {
        return new int [2];
    }

    /**
     * Append the user data to the queue to be processed.
     * @param userData
     */
    @Override
    public void onReceive(UserData userData) {
        userDataLinkedList.add(userData);
    }

    /**
     * Analyze the heart rate and car data.
     */
    // TODO: Move from rule-based to pattern matching
    @Override
    public void run() {
        while (true) {
            // check to see if data is available
            if (userDataLinkedList.size() > 0) {
                UserData userData = userDataLinkedList.remove();
                System.out.println(userData.toString());
                SensorData sensorData = userData.getSensorData();
                SimData simData = userData.getSimData();
                // TODO: Remove these variables, use the simData object
                Double turn = null;
                if (steering == null) {
                    steering = simData.getSteering();
                } else {
                    turn = calculateTurn(simData.getSteering());
                }
                int deviation = determineHRDeviation(sensorData);
                System.out.println(deviation);
                if(deviation >= 20) {
                    //HR Deviation values come from: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2653595/
                    System.out.println("High deviation occurred");
                    // Create an alert if the user is driving aggressively.
                    if (simData.getSpeed() > 120 && turn != null && turn.doubleValue() >= 15) {
                        alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_FATAL,
                                "Aggressive Driving Detected");
                    } else if (simData.getSpeed() > 120) {
                        alertSystem.alert(applicationContext, AlertSystem.ALERT_TYPE_WARNING,
                                "Be careful!");
                    }
                } else if (deviation>=10 && deviation <20) {
                    System.out.println("Moderate deviation occurred");
                } else {
                    System.out.println("No deviation occurred");
                }
                // TODO: Change to binary conditioning (each bit represents a condition)
            }
        }
    }


    /**
     * Determines deviations in the driver's behaviours
     * TODO: This function should use pattern data matching or alternative learning algorithms in the next semester
     * @param sensorData: The sensor data
     * @return Deviations in the heart rate
     */
    private int determineHRDeviation(SensorData sensorData) {
        int stdDev = 0;
        //Determine the estimated weighted average
        userAverage = (int)Math.round((0.9*userAverage)+(0.1*sensorData.getHeartRate()));
        stdDev = Math.abs(sensorData.getHeartRate() - userAverage);
        return stdDev;
    }


    /**
     * Returns the absolute difference of the steering wheel degree.
     * @param currentSteering
     * @return
     */
    private double calculateTurn(double currentSteering) {
        return Math.abs(steering.doubleValue() - currentSteering);
    }


}
