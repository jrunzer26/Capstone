package com.example.android.infotainment.backend;

import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.FastDTW.dtw.FastDTW;
import com.example.android.infotainment.backend.FastDTW.dtw.WarpPath;
import com.example.android.infotainment.backend.models.Baselines;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.Turn;
import com.example.android.infotainment.backend.models.TurnDataPoint;
import com.example.android.infotainment.backend.models.UserData;
import com.example.android.infotainment.backend.models.SlidingWindow;
import com.example.android.infotainment.backend.FastDTW.dtw.TimeWarpInfo;
import com.example.android.infotainment.backend.FastDTW.util.DistanceFunction;
import com.example.android.infotainment.backend.FastDTW.util.DistanceFunctionFactory;
import com.example.android.infotainment.backend.FastDTW.timeseries.TimeSeries;
import com.example.android.infotainment.backend.models.VehicleHistory;
import com.example.android.infotainment.utils.Util;


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
    private Queue<UserData> userDataLinkedList;
    private int userAverage = 70;
    private Double steering;

    //VARIABLES AND STRUCTURES REQUIRED FOR THE ALGORITHM;
    private final int WINDOW = 5; //Size of the sliding window
    private final int THRESHOLD = 0; //Difference between window and overall needed to trigger DTW

    private SlidingWindow sw = new SlidingWindow(WINDOW);
    private ArrayList<Double> mean = new ArrayList<Double>();
    private ArrayList<Double> stdDev = new ArrayList<Double>();
    public static final int RADIUS = 30;
    public static final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
    private String[] drivingEvent = new String[2];
    private int[] eventCounter = new int[6];
    private Baselines baselines;
    private VehicleHistory vsh = new VehicleHistory();
    private final boolean isDoneSetup = false; //baselines.isSetup()

    /**
     * Analyses data coming in from the data parser and alerts the user.
     * @param applicationContext the current context
     */
    public DataAnalyst(Context applicationContext) {
        this.applicationContext = applicationContext;
        alertSystem = new AlertSystem(applicationContext);
        userDataLinkedList = new ConcurrentLinkedQueue<>();
        baselines = new Baselines(applicationContext);
        baselines.printBaselines();
        double[][] testData = {
                {1},
                {2, 2},
                {3, 3, 3},
                {4, 4, 4, 3}
        };
        //double [] test1 = {1,1};
        //DBA.DBA(test1, testData);
       // Util.printArray(test1, "ALL DBA");
        //Util.printArray(baselines.dtwPairAlg(testData), "PAIR ALG RESULT");
        //Util.print2dArray(baselines.getRight(), TAG);
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


            /*

                //ALGORITHM STARTS HERE
                step1_HeartRateDeviations(sensorData, counter);


                if(isDoneSetup && step2_HRComparison(sw.getStdDev(), stdDev.get(stdDev.size() -1))) {
                    alertCheck(step3_GetMinSimilarity(baselines, vsh));
                    for (int event = 0; event<eventCounter.length; event++){
                        if (eventCounter[event] %2 == 0 && eventCounter[event] <5){
                            //Mild system alert

                        } else if (eventCounter[event]>=5){
                            //Severe system alert.

                        }
                    }
                } else { //Setup not done, or no deviation
                    //Record to the database
                }





    */
                // TODO: Remove these variables, use the simData object
                Double turn = null;
                if (steering == null) {
                    steering = simData.getSteering();
                } else {
                    turn = calculateTurn(simData.getSteering());
                }
                int deviation = determineHRDeviation(sensorData);
                System.out.println("deviation: " + deviation);
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

    private String step3_GetMinSimilarity(Baselines b, VehicleHistory history){
        final int SINGLE_DIM_EVENTS = 4;
        final int TWO_DIM_EVENTS = 2;
        TimeWarpInfo minSingle;
        TimeWarpInfo[] minDouble;
        FastDTW dtw = new FastDTW();

        ArrayList<Object> speedHistory = new ArrayList<Object>(history.getSpeedHistory());
        ArrayList<Object> turningHistory = new ArrayList<Object>(history.getTurningHistory());

        minSingle = minSim_singleDimension(b, speedHistory, turningHistory, SINGLE_DIM_EVENTS, dtw);
        minDouble = minSim_doubleDimension(b, speedHistory, turningHistory, TWO_DIM_EVENTS, dtw);
        if (minSingle.getDistance() >= (minDouble[0].getDistance() + minDouble[1].getDistance())/2){
            return (drivingEvent[0]);
        } else {
            return(drivingEvent[1]);
        }

    }

    private TimeWarpInfo minSim_singleDimension(Baselines b, ArrayList sHist, ArrayList tHist, int events, FastDTW dtw){
        TimeWarpInfo temp;
        TimeWarpInfo toReturn = null;
        String tempEvent="";
        for (int i = 0; i< events; i++){
            switch (i) {
                case 0: {
                    //Acceleration
                    temp = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getAccelFromSpeedBaseline()), RADIUS, distFn);
                    tempEvent="accel";
                    break;
                }
                case 1: {
                    //Braking
                    temp = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getBrake()), RADIUS, distFn);
                    tempEvent="braking";
                    break;
                }
                case 2: {
                    //Cruise
                    temp = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getCruise()), RADIUS, distFn);
                    tempEvent="cruise";
                    break;
                }
                case 3: {
                    //Speeding
                    temp = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getSpeeding()), RADIUS, distFn);
                    tempEvent="speeding";
                    break;
                }
                default: {
                    continue;
                }
            }
            if (temp.getDistance() < 10000 || temp.getDistance() < toReturn.getDistance()){
                toReturn = temp;
                drivingEvent[0]= tempEvent;

            }
        }
        return toReturn;
    }

    private TimeWarpInfo[] minSim_doubleDimension(Baselines b, ArrayList sHist, ArrayList tHist, int events, FastDTW dtw){
        TimeWarpInfo[] temp = new TimeWarpInfo[2];
        TimeWarpInfo[] toReturn = new TimeWarpInfo[2];
        double avgDistance;
        String tempEvent;
        for (int i = 0; i< events; i++){
            switch (i) {
                case 0: {
                    //Left Turns
                    temp[0] = dtw.getWarpInfoBetween(new TimeSeries(tHist), new TimeSeries(b.getLeft()[0]), RADIUS, distFn);
                    temp[1] = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getLeft()[1]), RADIUS, distFn);
                    tempEvent = "left";
                    break;
                }
                case 1: {
                    //Right Turns
                    temp[0] = dtw.getWarpInfoBetween(new TimeSeries(tHist), new TimeSeries(b.getRight()[0]), RADIUS, distFn);
                    temp[1] = dtw.getWarpInfoBetween(new TimeSeries(sHist), new TimeSeries(b.getRight()[1]), RADIUS, distFn);
                    tempEvent="right";
                    break;
                }

                default: {
                    continue;
                }
            }
            avgDistance = (temp[0].getDistance() + temp[1].getDistance())/2;

            if (avgDistance < 10000 || avgDistance < (toReturn[0].getDistance()+toReturn[1].getDistance())/2){
                System.arraycopy(temp, 0, toReturn, 0, 2);
                drivingEvent[1] = tempEvent;
            }
        }
        return toReturn;
    }

    private void alertCheck(String event){
        if (event.equals("accel")){
            eventCounter[0]++;
        } else if (event.equals("brake")) {
            eventCounter[1]++;
        } else if (event.equals("cruise")) {
            eventCounter[2]++;
        } else if (event.equals("speeding")) {
            eventCounter[3]++;
        } else if (event.equals("left")) {
            eventCounter[4]++;
        } else if (event.equals("right")) {
            eventCounter[5]++;
        } else {
            System.out.println("ERROR IN ALERTCHECK: String invalid!");
        }
    }

}
