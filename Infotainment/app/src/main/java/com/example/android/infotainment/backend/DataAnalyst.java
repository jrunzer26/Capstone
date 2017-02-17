package com.example.android.infotainment.backend;

import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.Turn;
import com.example.android.infotainment.backend.models.TurnDataPoint;
import com.example.android.infotainment.backend.models.UserData;
import com.example.android.infotainment.backend.models.SlidingWindow;

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

    private double[][] rightTurnBasline;
    private double[][] leftTurnBaseline;

    private BaselineDatabaseHelper baselineDatabaseHelper;

    //VARIABLES AND STRUCTURES REQUIRED FOR THE ALGORITHM;
    private final int WINDOW = 5; //Size of the sliding window
    private final int THRESHOLD = 0; //Difference between window and overall needed to trigger DTW
    private SlidingWindow sw = new SlidingWindow(WINDOW);
    private ArrayList<Double> mean = new ArrayList<Double>();
    private ArrayList<Double> stdDev = new ArrayList<Double>();

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
        if (userDatabaseHelper.getNextTripID() == 3) {
            dbaFirstTimeInit();
        } else {
            dbaInitPreviousSavedTrip();
        }
    }

    private void dbaFirstTimeInit() {
        ArrayList<UserData> allData = userDatabaseHelper.getData();
        ArrayList<Turn> turns = getTurnData(allData);
        baselineLeftAndRightTurns(turns);
        ArrayList<Turn> rightTurnResults = baselineDatabaseHelper.getRightTurnData();
        ArrayList<Turn> leftTurnResults = baselineDatabaseHelper.getLeftTurnData();
        Log.i(TAG, "right turn results");
        for(Turn t : rightTurnResults) {
            Log.i(TAG, t.toString());
        }
        Log.i(TAG, "left turn results");
        for(Turn t : leftTurnResults) {
            Log.i(TAG, t.toString());
        }
    }

    private void baselineLeftAndRightTurns(ArrayList<Turn> turns) {
        ArrayList<Turn> leftTurns = new ArrayList<>();
        ArrayList<Turn> rightTurns = new ArrayList<>();
        sortTurnData(turns, leftTurns, rightTurns);
        baselineTurns(Turn.TURN_LEFT, leftTurns);
        baselineTurns(Turn.TURN_RIGHT, rightTurns);
    }

    /** Baseline specific turn events **/
    private void baselineTurns(int turnType, ArrayList<Turn> turns) {
        // find the max length of the sequences
        int max = -1;
        int totalTurns = turns.size();
        for(int i = 0; i < totalTurns; i++) {
            int size = turns.get(i).size();
            if (max < size) {
                max = size;
            }
        }
        if (max != -1) {
            double[][] steeringSequence = new double[totalTurns][TURN_WINDOW_SIZE];
            double[][] speedSequence = new double[totalTurns][TURN_WINDOW_SIZE];
            double[] averageSteeringSequence;
            double[] averageSpeedSequence;
            int flag = turns.get(0).getFlag();
            if (flag == UserData.FLAG_LEFT_TURN ) {
                averageSteeringSequence = leftTurnBaseline[0];
                averageSpeedSequence = leftTurnBaseline[1];
            }
            else {
                averageSteeringSequence = rightTurnBasline[0];
                averageSpeedSequence = rightTurnBasline[1];
            }

            for (int i = 0; i < turns.size(); i++) {
                Turn turn = turns.get(i);
                int turnSize = turn.size();
                for (int j = 0; j < steeringSequence[0].length; j++) {
                    ArrayList<TurnDataPoint> turnPoints = turn.getTurnDataPoints();
                    if (turnSize > j) {
                        TurnDataPoint turnDataPoint= turnPoints.get(j);
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

            overwriteBaseline(averageSteeringSequence, turnType);
            // take baseline here

        }
    }

    // for now we are just going to not add hr data, otherwise take the hr data in parallel
    private void overwriteTurnBaseline(double[] averageSteeringSequence, double[] averageSpeedSequence, int turnType) {
        Turn turn = new Turn(turnType, 0);
        for (int i = 0; i < averageSpeedSequence.length; i++) {
            turn.addTurnPoint(new TurnDataPoint(averageSpeedSequence[i], averageSteeringSequence[i]));
        }
        baselineDatabaseHelper.clear(turnType);
        baselineDatabaseHelper.saveTurn(turn);
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

    private void dbaInitPreviousSavedTrip() {
        ArrayList<UserData> lastTripData = userDatabaseHelper.getLastTripData();
        ArrayList<Turn> turns = getTurnData(lastTripData);
        baselineLeftAndRightTurns(turns);
        ArrayList<Turn> rightTurnResults = baselineDatabaseHelper.getRightTurnData();
        ArrayList<Turn> leftTurnResults = baselineDatabaseHelper.getLeftTurnData();
        Log.i(TAG, "right turn results");
        for(Turn t : rightTurnResults) {
            Log.i(TAG, t.toString());
        }
        Log.i(TAG, "left turn results");
        for(Turn t : leftTurnResults) {
            Log.i(TAG, t.toString());
        }
    }

    private ArrayList<Turn> getTurnData(ArrayList<UserData> userDatas) {
        ArrayList<Turn> turns = new ArrayList<>();
        Turn currentTurn = null;
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
                currentTurn = null;
            }
        }
        // add the turn if even if the turn is not finished
        if (currentTurn != null)
            turns.add(currentTurn);
        return turns;
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
        int counter = 0;
        //dataCounter maintains being 1 higher than the index of both the mean and stdDev arrayLists.
        while (true) {
            // check to see if data is available
            if (userDataLinkedList.size() > 0) {
                counter++;
                UserData userData = userDataLinkedList.remove();
                System.out.println(userData.toString());
                SensorData sensorData = userData.getSensorData();
                SimData simData = userData.getSimData();


                //ALGORITHM STARTS HERE
                step1_HeartRateDeviations(sensorData, counter);
                if(step2_HRComparison(sw.getStdDev(), stdDev.get(stdDev.size() -1))) {
                    //RUN FOLLOWING STEPS
                }




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


    /** DEPRECIATED
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
     * Determines the Mean given a new entry to the data stream
     * @param value: the new value
     * @return the mean
     */
    private double findMean(int value){
        if (mean.size() == 0){
            return value;
        }
        return ((mean.get(mean.size()-1)*mean.size()) + value)/(mean.size()+1);
    }

    private void step1_HeartRateDeviations(SensorData sensorData, int dataCounter){
        System.out.println("Step1");
        double rollingStdDev = 0;
        sw.add(sensorData.getHeartRate());
        mean.add(findMean(sensorData.getHeartRate()));
        if(dataCounter == 1){
            stdDev.add(0.0);
        }else {
            double step1 = (dataCounter-2) *(stdDev.get(dataCounter-2)) * (stdDev.get(dataCounter-2));
            double step2 = (dataCounter-1)*((mean.get(dataCounter-2) - mean.get(dataCounter-1)) * (mean.get(dataCounter-2) - mean.get(dataCounter-1)));
            double step3 = (sensorData.getHeartRate() - mean.get(dataCounter-1)) * (sensorData.getHeartRate() - mean.get(dataCounter-1));
            rollingStdDev = Math.sqrt((step1 + step2 + step3) / (dataCounter-1));
            stdDev.add(rollingStdDev);
            System.out.println(rollingStdDev);
        }

    }

    private boolean step2_HRComparison(double window, double threshold){
        return ((window - threshold) > THRESHOLD);
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
