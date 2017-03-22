package com.example.android.infotainment.backend;

import android.content.Context;
import android.util.Log;

import com.example.android.infotainment.alert.AlertSystem;
import com.example.android.infotainment.backend.FastDTW.dtw.FastDTW;
import com.example.android.infotainment.backend.models.Baselines;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;
import com.example.android.infotainment.backend.models.SlidingWindow;
import com.example.android.infotainment.backend.models.MinData;
import com.example.android.infotainment.backend.FastDTW.dtw.TimeWarpInfo;
import com.example.android.infotainment.backend.FastDTW.util.DistanceFunction;
import com.example.android.infotainment.backend.FastDTW.util.DistanceFunctionFactory;
import com.example.android.infotainment.backend.FastDTW.timeseries.TimeSeries;
import com.example.android.infotainment.backend.models.VehicleHistory;
import com.example.android.infotainment.utils.Util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    private final int SINGLE_SIMILARITY_BOUND = 500;
    private final int DOUBLE_SIMILARITY_BOUND = 1000;


    //VARIABLES AND STRUCTURES REQUIRED FOR THE ALGORITHM;
    private final int WINDOW = 50; //Size of the sliding window
    //private final int THRESHOLD = 0; //Difference between window and overall needed to trigger DTW
    private final int THRESHOLD = -1;

    private SlidingWindow sw = new SlidingWindow(WINDOW);
    private ArrayList<Double> mean = new ArrayList<Double>();
    private ArrayList<Double> stdDev = new ArrayList<Double>();
    private static final double PERCENT_THRESHOLD = 0.1;
    public static final int RADIUS = 30;
    public static final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
    private String[] drivingEvent = new String[2];
    private Baselines baselines;
    private VehicleHistory vehicleHistory;
    private MinData[] md = new MinData[2];
    private MinData minDataSingleDim = new MinData();
    private boolean isDoneSetup = false; //baselines.isSetup()

    //Variables for alert system the threshholds follow the case statements
    private final int FATAL_THRESHHOLD[] ={
            500,
            500,
            500,
            500,
            500,
            500,
            500
    };
    // 3 - speeding
    private final int WARNING_THRESHHOLD [] ={
            1,
            1,
            1,
            100,
            1,
            1,
            1,
    };
    private int [] repeatSevere = new int [9];
    private int[] eventCounter = new int[9];
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
        isDoneSetup = baselines.isSetup();
        vehicleHistory = new VehicleHistory(baselines.maxBaselineSize());
        for(int i = 0; i < md.length; i++) {
            md[i] = new MinData();
        }
        for(int i = 0; i < repeatSevere.length; i++){
            repeatSevere [i] = 1;
            eventCounter [i] = 0;
        }
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
        vehicleHistory.insertData(userData);
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
                //System.out.println(userData.toString());
                System.out.println("=======================NEXT STEP==================");
                SensorData sensorData = userData.getSensorData();
                SimData simData = userData.getSimData();

                //ALGORITHM STARTS HERE
                step1_HeartRateDeviations(sensorData, counter);


                Log.i(" isDone: ", isDoneSetup + "");
                Log.i(" hasEnoughData: ", vehicleHistory.hasEnoughData() +"");
                Log.i(" hrCompare", step2_HRComparison(sw.getStdDev(), stdDev.get(stdDev.size() -1)) + "");
                if(isDoneSetup && step2_HRComparison(sw.getStdDev(), stdDev.get(stdDev.size() -1)) && vehicleHistory.hasEnoughData()) {
                    step3_GetMinSimilarity(baselines, vehicleHistory);
                    //We now know what is the most similar
                    //Pass this into a ratio checker
                } else { //Setup not done, or no deviation
                    //Record to the database
                }
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
        Log.i("sensor data", sensorData.getHeartRate()+"");
        if(dataCounter == 1){
            stdDev.add(0.0);
        }else {
            double step1 = (dataCounter-2) *(stdDev.get(dataCounter-2)) * (stdDev.get(dataCounter-2));
            double step2 = (dataCounter-1)*((mean.get(dataCounter-2) - mean.get(dataCounter-1)) * (mean.get(dataCounter-2) - mean.get(dataCounter-1)));
            double step3 = (sensorData.getHeartRate() - mean.get(dataCounter-1)) * (sensorData.getHeartRate() - mean.get(dataCounter-1));
            rollingStdDev = Math.sqrt((step1 + step2 + step3) / (dataCounter-1));
            stdDev.add(rollingStdDev);
            //System.out.println(rollingStdDev);
        }

    }

    private boolean step2_HRComparison(double window, double threshold){
        Log.i(" window", window+"");
        Log.i(" threshold", threshold+"");
        //####################################### UNCOMMENT IN REAL IMPLEMENTATION
        //return ((window - threshold) > THRESHOLD);
        return true;
    }

    /**
     * Returns the absolute difference of the steering wheel degree.
     * @param currentSteering
     * @return
     */
    private double calculateTurn(double currentSteering) {
        return Math.abs(steering.doubleValue() - currentSteering);
    }

    private void step3_GetMinSimilarity(Baselines b, VehicleHistory history){
        final int SINGLE_DIM_EVENTS = 3;
        final int TWO_DIM_EVENTS = 3;
        TimeWarpInfo minSingle;
        TimeWarpInfo[] minDouble;
        FastDTW dtw = new FastDTW();

        List<Double> speedHistory = new ArrayList<>();
        for(int i = 0; i < history.getSpeedHistory().size(); i++) {
            try {
                speedHistory.add(history.getSpeedHistory().get(i));
            } catch(IndexOutOfBoundsException e) {
                speedHistory.add(history.getSpeedHistory().get(i-1));
            }
        }
        List<Double> speedDevHistory = new ArrayList<>();
        for(int i = 0; i < history.getSpeedingDevHistory().size(); i++) {
            try {
                speedDevHistory.add(history.getSpeedingDevHistory().get(i));
            } catch(IndexOutOfBoundsException e) {
                speedDevHistory.add(history.getSpeedingDevHistory().get(i-1));
            }
        }

        List<Double> turningHistory = new ArrayList<>();
        for(int i = 0; i < history.getTurningHistory().size(); i++) {
            try {
                turningHistory.add(history.getTurningHistory().get(i));
            } catch(IndexOutOfBoundsException e) {
                turningHistory.add(history.getTurningHistory().get(i-1));
            }
        }

        minSingle = minSim_singleDimension(b, speedHistory, speedDevHistory, SINGLE_DIM_EVENTS, dtw);

        boolean alertDetected = false;
        if( minSingle != null) {
            Log.i("minSingle not null", ratioDistance_singleDimension(minSingle, minDataSingleDim, false)+" > "+PERCENT_THRESHOLD);
            Log.i(" event", drivingEvent[0]);
        }
        if (minSingle == null) {
            Log.i("minSingle null", checkSpeeding(b, speedDevHistory, dtw)+" > "+PERCENT_THRESHOLD);
            if(checkSpeeding(b, speedDevHistory, dtw) > PERCENT_THRESHOLD) {
                //alertCheck(drivingEvent[0]);
                alertDetected = true;
            }
        } else if (ratioDistance_singleDimension(minSingle, minDataSingleDim, false) > PERCENT_THRESHOLD) {
            Log.i(" driving event 0", drivingEvent[0]);
            //alertCheck(drivingEvent[0]);
            alertDetected = true;
        }

        minDouble = minSim_doubleDimension(b, speedHistory, turningHistory, TWO_DIM_EVENTS, dtw);
        if (minDouble[0] != null && minDouble[1] != null) {
            Log.i("comparison", drivingEvent[1] + " " + ratioDistance_doubleDimension(minDouble, md) +" > " + PERCENT_THRESHOLD);
        } else if (minDouble[1] != null && minDouble[0] == null) {
            Log.i("comparison",  drivingEvent[1] + " " +ratioDistance_singleDimension(minDouble[1], md[1], true) +" > " + PERCENT_THRESHOLD);
        }
        if (minDouble[0] == null && minDouble[1] == null) {
            //alertCheck("none");
        } else if (minDouble[1] != null && minDouble[0] == null) {
            Log.i("in minDouble[1]", "test");
            if (ratioDistance_singleDimension(minDouble[1], md[1], true) > PERCENT_THRESHOLD) {
                alertCheck(drivingEvent[1]);
                alertDetected = false;
            }
        } else {
            turningAlertCheck(ratioDistance_doubleDimension(minDouble, md));
        }
        if (alertDetected) {
            alertCheck(drivingEvent[0]);
        }
        //alertCheck(drivingEvent[1]);
    }

    // [0] = speeding
    // [1] = steering
    private void turningAlertCheck(double[] set) {
        Util.printArray(set, "sets lol");
        double turningRatio = set[1];
        double speedingRatio = set[0];
        Log.i("ratios", "turning: " + turningRatio + " speedingRatio: " + speedingRatio);
        double lowerThresholdSteering, upperThresholdSteering, lowerThresholdSpeed, upperThresholdSpeed;

        if(drivingEvent[1].equals("left")) {
            lowerThresholdSteering = 0.83;
            upperThresholdSteering = 1.05;
        } else { // right
            lowerThresholdSteering = 0.83;
            upperThresholdSteering = 1.05;
        }
        if (turningRatio < lowerThresholdSteering) {
            alertCheck(drivingEvent[1]);
        } else if (turningRatio > upperThresholdSteering) {
            alertCheck(drivingEvent[1]);
        }
    }

    private double checkSpeeding(Baselines b, List speedDevHistory, FastDTW dtw) {
        TimeWarpInfo temp;
        TimeWarpInfo toReturn = null;
        String baselineString = "";
        String eventString = "";
        List history = speedDevHistory;
        double[] baseline = b.getSpeeding();
        for(int j =0; j < baseline.length; j++) {
            baselineString += baseline[j] + "\t";
        }
        Log.i("Speeding baseline", baselineString);
        for(int j =0; j < history.size(); j++) {
            eventString += history.get(j) + "\t";
        }
        Log.i("Speeding veh", eventString);
        temp = dtw.getWarpInfoBetween(new TimeSeries(history), new TimeSeries(baseline), RADIUS, distFn);
        Log.i(" dtw", "speeding sim: " + temp.getDistance() + " < " + SINGLE_SIMILARITY_BOUND);
        if (temp.getDistance() <  SINGLE_SIMILARITY_BOUND) {
            if ((toReturn == null) || temp.getDistance() < toReturn.getDistance()) {
                toReturn = temp;
                //Log.i(" toReturn", temp.getPath().toString()+"");
                minDataSingleDim.setBaseline(Arrays.copyOf(baseline, baseline.length));
                minDataSingleDim.setEvent("speeding");
                minDataSingleDim.setVData(history);
                drivingEvent[0]= "speeding";
                Log.i("event changed", "speeding");
                //Log.i(" in if", minDataSingleDim.getBaseline().length+"" + " event: " + tempEvent);
            }
        }
        return ratioDistance_singleDimension(toReturn, minDataSingleDim, false);
    }


    private TimeWarpInfo minSim_singleDimension(Baselines b, List sHist, List speedDevHist, int events, FastDTW dtw){
        TimeWarpInfo temp;
        TimeWarpInfo toReturn = null;
        String tempEvent="";
        double[] baseline;
        List history = sHist;
        Log.i("sHist length: ", sHist.size()+"");
        for (int i = 0; i< events; i++){
            switch (i) {
                case 0: {
                    //Acceleration
                    baseline = b.getAccelFromSpeedBaseline();
                    tempEvent="accel";
                    history = sHist;
                    break;
                }
                case 1: {
                    //Braking
                    baseline = b.getBrake();
                    tempEvent="brake";
                    history = sHist;
                    break;
                }
                case 2: {
                    baseline = b.getNearStopAccel();
                    tempEvent="accelNearStop";
                    history = sHist;
                    break;
                }
                default: {
                    continue;
                }
            }
            //Log.i(" minSingle " + i, sHist.size() + " " + baseline.length);
            Log.i(" temp event", tempEvent + " length: " + history.size());
            String baselineString = "";
            String eventString = "";
            for(int j =0; j < baseline.length; j++) {
                baselineString += baseline[j] + "\t";
            }
            Log.i(tempEvent + " baseline", baselineString);
            for(int j =0; j < history.size(); j++) {
                eventString += history.get(j) + "\t";

            }
            Log.i(tempEvent + "veh", eventString);

            temp = dtw.getWarpInfoBetween(new TimeSeries(history), new TimeSeries(baseline), RADIUS, distFn);
            Log.i(" dtw", tempEvent + " sim: " + temp.getDistance() + " < " + SINGLE_SIMILARITY_BOUND);
            if (temp.getDistance() <  SINGLE_SIMILARITY_BOUND) {
                if ((toReturn == null) || temp.getDistance() < toReturn.getDistance()) {
                    toReturn = temp;
                    //Log.i(" toReturn", temp.getPath().toString()+"");
                    minDataSingleDim.setBaseline(Arrays.copyOf(baseline, baseline.length));
                    minDataSingleDim.setEvent(tempEvent);
                    minDataSingleDim.setVData(history);
                    drivingEvent[0]= tempEvent;
                    Log.i("event changed", tempEvent);
                    System.out.println("Array");
                    System.out.println(history.toArray());
                    //Log.i(" in if", minDataSingleDim.getBaseline().length+"" + " event: " + tempEvent);
                }
            }
            //Log.i(" print: " + i, minDataSingleDim.toString());
            //Log.i(" toReturn", toReturn.getPath().toString()+"");
        }
        //Log.i(" toReturn FINAL", toReturn.getPath().toString()+"");
        return toReturn;
    }


    private double ratioDistance_singleDimension(TimeWarpInfo twi, MinData series, boolean cruise){
        double sum1 = 0.0;
        double sum2 = 0.0;
        double average1;
        double average2;

        //Log.i(" print", series.toString());
        //Log.i(" twi", "vehicle: " + series.getVData().size() + " baseline: " + series.getBaseline().length);
        //Log.i(" ts", "ts1: " + twi.getPath().getTS1().size() + " ts2: " + twi.getPath().getTS2().size());
        Util.printArray(series.getBaseline(), "BASELINE RATIO");
        Util.printList(series.getVData(), "VEHICLE DATA");
        /*
        for(int i = 0; i < twi.getPath().getTS1().size(); i++) {
            Log.i("data: ", i+ " " + (Integer)twi.getPath().getTS1().get(i) + " " + (Integer)twi.getPath().getTS2().get(i));
        }
        */
        for (int i = 0; i < twi.getPath().getTS1().size(); i++) {

            sum1 += (double) series.getVData().get((Integer) twi.getPath().getTS1().get(i));
            sum2 += series.getBaseline()[(Integer) twi.getPath().getTS2().get(i)];
        }
        average1 = (sum1/twi.getPath().getTS1().size());
        Log.i(" average 1", average1 + " == 0" );
        if (average1 == 0 & !cruise) {

            return 0;
        } else if (average1 == 0 & cruise) {
            return 1;
        }
        average2 = (sum2/twi.getPath().getTS2().size());

        return Math.abs((average1/average2));
    }

    private TimeWarpInfo[] minSim_doubleDimension(Baselines b, List speedHist, List steeringHist, int events, FastDTW dtw){
        TimeWarpInfo[] temp = new TimeWarpInfo[2];
        TimeWarpInfo[] toReturn = new TimeWarpInfo[2];
        double avgDistance;
        String tempEvent;
        double[] speedBaseline;
        double[] steeringBaseline;
        for (int i = 0; i< events; i++){
            switch (i) {
                case 0: {
                    //Left Turns
                    speedBaseline=b.getLeft()[1]; // speed
                    steeringBaseline=b.getLeft()[0]; // steering
                    tempEvent = "left";
                    break;
                }
                case 1: {
                    //Right Turns
                    speedBaseline=b.getRight()[1];
                    steeringBaseline=b.getRight()[0];
                    tempEvent="right";
                    break;
                }
                case 2: {
                    steeringBaseline =  b.getCruise();
                    speedBaseline = null;
                    tempEvent="cruising";
                    break;
                }
                default: {
                    continue;
                }
            }

            temp[1] = dtw.getWarpInfoBetween(new TimeSeries(steeringHist), new TimeSeries(steeringBaseline), RADIUS, distFn);
            temp[0] = null;
            // this has been changed to only check steering
            avgDistance = temp[1].getDistance();
            if (!tempEvent.equals("cruising")) {
                temp[0] = dtw.getWarpInfoBetween(new TimeSeries(speedHist), new TimeSeries(speedBaseline), RADIUS, distFn);

                //avgDistance = (temp[0].getDistance() + temp[1].getDistance())/2;

            } else {
                avgDistance = temp[1].getDistance();
            }

            Log.i(" temp event", tempEvent);

            if (temp[0] != null) {
                Util.printArray(speedBaseline, "speed Baseline");
                Util.printList(speedHist, "speed history");
            }
            Util.printArray(steeringBaseline, "steering Baseline");
            Util.printList(steeringHist, "steering history");
            Log.i(" avg distance comp", avgDistance+ " < " + DOUBLE_SIMILARITY_BOUND);

            if (avgDistance < DOUBLE_SIMILARITY_BOUND) {
                double maxCalculatedAvg;
                if (toReturn[0] != null) {
                    maxCalculatedAvg = (toReturn[0].getDistance()+toReturn[1].getDistance())/2;
                } else if (toReturn[1] != null) {
                    maxCalculatedAvg = toReturn[1].getDistance();
                } else {
                    maxCalculatedAvg = 0;
                }
                if ((toReturn[1] == null) || avgDistance < maxCalculatedAvg) {
                    System.arraycopy(temp, 0, toReturn, 0, 2);
                    Log.i("set md: 1", tempEvent);
                    md[0].setBaseline(speedBaseline);
                    md[0].setVData(speedHist);
                    md[0].setEvent(tempEvent);

                    md[1].setBaseline(steeringBaseline);
                    md[1].setVData(steeringHist);
                    md[1].setEvent(tempEvent);

                    drivingEvent[1] = tempEvent;
                }

            }
        }
        return toReturn;
    }

    /**
     *
     * @param twi: Index 0 contains the warp path of steering data, Index 1 contains the warp path of speed data.
     * @param series: Index 0 contains the vehicle data and baseline of the steering wheel, Index 1 contains the vehicle data and time of the speed
     * @return
     */
    private double[] ratioDistance_doubleDimension(TimeWarpInfo[] twi, MinData[] series){
        double sum1_1 = 0.0, sum1_2 = 0.0, sum2_1 = 0.0, sum2_2 = 0.0;
        double average1_1;
        double average1_2;
        double average2_1;
        double average2_2;

        double set1;
        double set2;

        for (int i = 0; i < twi[0].getPath().getTS1().size(); i++){
            sum1_1 += (Double) series[0].getVData().get((Integer)twi[0].getPath().getTS1().get(i));
            sum1_2 += series[0].getBaseline()[(Integer)twi[0].getPath().getTS2().get(i)];
        }
        average1_1 = sum1_1 / twi[0].getPath().getTS1().size();
        average1_2 = sum1_2 / twi[0].getPath().getTS2().size();

        set1 = Math.abs((average1_1/average1_2));

        for (int j = 0; j< twi[1].getPath().getTS2().size(); j++) {
            sum2_1 += (Double) series[1].getVData().get((Integer)twi[1].getPath().getTS1().get(j));
            sum2_2 += series[1].getBaseline()[(Integer)twi[1].getPath().getTS2().get(j)];
        }
        average2_1 = sum2_1 / twi[1].getPath().getTS1().size();
        average2_2 = sum2_2 / twi[1].getPath().getTS2().size();

        set2 = Math.abs((average2_1/average2_2));
        Log.i("sets", "set1: " + set1 + " set2: " + set2);
        double [] sets = {set1, set2};
        return sets;
    }

    private void alertCheck(String event){
        int incomingEvent = -1;

        if (event.equals("none")){
            return;
        }
        if (event.equals("accel")){
            eventCounter[0]++;
            incomingEvent = 0;
        }  else if (event.equals("brake")) {
            eventCounter[1]++;
            incomingEvent = 1;
        } else if (event.equals("cruising")) {
            eventCounter[2]++;
            incomingEvent = 2;
        } else if (event.equals("speeding")) {
            eventCounter[3]++;
            incomingEvent = 3;
        } else if (event.equals("left")) {
            eventCounter[4]++;
            incomingEvent = 4;
        } else if (event.equals("right")) {
            eventCounter[5]++;
            incomingEvent = 5;
        } else if(event.equals("accelNearStop")) {
            eventCounter[6]++;
            incomingEvent = 6;
        } else {
            System.out.println("ERROR IN ALERTCHECK: String invalid! " + event);
            return ;
        }
        System.out.println("------------------------EVENT: " + event +" " + incomingEvent + " " + eventCounter[incomingEvent] + " repeatSevere: " + repeatSevere[incomingEvent]);
        Log.i("event if", eventCounter[incomingEvent]  + " >= " +  FATAL_THRESHHOLD[incomingEvent] * repeatSevere[incomingEvent]);
        Log.i("event if2", eventCounter[incomingEvent]+" >= "+WARNING_THRESHHOLD[incomingEvent]);
        if(eventCounter[incomingEvent] >= FATAL_THRESHHOLD[incomingEvent] * repeatSevere[incomingEvent] ){
            repeatSevere[incomingEvent]++;
            Log.i(" alert", "FATAL");
            alertSystem.alert(applicationContext, alertSystem.ALERT_TYPE_FATAL, incomingEvent);
        }else if(eventCounter[incomingEvent] >= WARNING_THRESHHOLD[incomingEvent]){
            Log.i(" alert", "WARNING");
            alertSystem.alert(applicationContext, alertSystem.ALERT_TYPE_WARNING, incomingEvent);
        } else
            Log.i(" alert", "NONE");

    }

}
