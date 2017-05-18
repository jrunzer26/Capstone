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


    private static final double CRUISE_RATIO_MAX = 30;
    private Context applicationContext;
    private AlertSystem alertSystem;
    private Queue<UserData> userDataLinkedList;
    private Double steering;
    private final int SINGLE_SIMILARITY_BOUND = 650;
    private final int DOUBLE_SIMILARITY_BOUND = 1400;


    // cruise

    private static final double PERCENT_THRESHOLD_CRUISE_UPPER = 3;
    private static final double PERCENT_THRESHOLD_CRUISE_LOWER = 0.00;

    // left steering
    private static final double PERCENT_THRESHOLD_LEFT_UPPER = 1.10;
    private static final double PERCENT_THRESHOLD_LEFT_LOWER = 0.83;

    // right steering
    private static final double PERCENT_THRESHOLD_RIGHT_UPPER = 1.10;
    private static final double PERCENT_THRESHOLD_RIGHT_LOWER = 0.45;

    /* accel near stop upper
    private static final double PERCENT_THRESHOLD_ACCEL_NEAR_STOP_UPPER = 1.3;
    private static final double PERCENT_THRESHOLD_ACCEL_NEAR_STOP_LOWER= 0.5;
    */
    // accel from speed
    private static final double PERCENT_THRESHOLD_ACCEL_FROM_SPEED_UPPER = 1.3;
    private static final double PERCENT_THRESHOLD_ACCEL_FROM_SPEED_LOWER= 0.6;

    // speeding0
    private static final double PERCENT_THRESHOLD_SPEEDING_UPPER = 1.2;
    private static final double PERCENT_THRESHOLD_SPEEDING_LOWER= 0.9;

    // brake
    private static final double PERCENT_THRESHOLD_BRAKE_UPPER = 1.3;
    private static final double PERCENT_THRESHOLD_BRAKE_LOWER= 0.0;

    //VARIABLES AND STRUCTURES REQUIRED FOR THE ALGORITHM;
    private final int WINDOW = 300; //Size of the sliding window
    private final int THRESHOLD = 0; //Difference between window and overall needed to trigger DTW


    private SlidingWindow sw = new SlidingWindow(WINDOW);
    private ArrayList<Double> mean = new ArrayList<Double>();
    private ArrayList<Double> stdDev = new ArrayList<Double>();

    public static final int RADIUS = 60;
    public static final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
    private String[] drivingEvent = new String[2];
    private Baselines baselines;
    private VehicleHistory vehicleHistory;
    private MinData[] md = new MinData[2];
    private MinData minDataSingleDim = new MinData();
    private boolean isDoneSetup = false; //baselines.isSetup()

    //Variables for alert system the threshholds follow the case statements
    private final int FATAL_THRESHHOLD[] ={
            100,
            100,
            100,
            100,
            100,
            100,
            100
    };
    // 2 - cruising
    // 3 - speeding
    private final int WARNING_THRESHHOLD [] ={
            1,
            1,
            2,
            10,
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
        //vehicleHistory = new VehicleHistory(baselines.maxBaselineSize());
        vehicleHistory = new VehicleHistory(50);
        for(int i = 0; i < md.length; i++) {
            md[i] = new MinData();
        }
        for(int i = 0; i < repeatSevere.length; i++){
            repeatSevere [i] = 1;
            eventCounter [i] = 0;
        }
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
    @Override
    public void run() {
        int counter = 0;
        //dataCounter maintains being 1 higher than the index of both the mean and stdDev arrayLists.
        while (true) {
            // check to see if data is available
            if (userDataLinkedList.size() > 0) {
                counter++;
                UserData userData = userDataLinkedList.remove();
                SensorData sensorData = userData.getSensorData();

                //ALGORITHM STARTS HERE
                step1_HeartRateDeviations(sensorData);
                if (stdDev.size() > WINDOW){
                    if(isDoneSetup && step2_HRComparison(sw.getStdDev(), stdDev.get(stdDev.size() -1)) && vehicleHistory.hasEnoughData()) {
                        if (counter % 5 == 0){
                            counter = 0;
                            step3_GetMinSimilarity(baselines, vehicleHistory);

                        }
                    }
                }

            }
        }
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

    /**
     * Determines the standard deviation of entire data set
     * @param sensorData The heart rate from the current data point
     */
    private void step1_HeartRateDeviations(SensorData sensorData){
        double rollingStdDev;
        int dataCounter;
        if (sensorData.getHeartRate() == 0) {
            return;
        }
        dataCounter = stdDev.size() + 1;
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
        }

    }

    /**
     * Determines whether or not the standard deviation, of the heart rate window, is larger than that of the entire data set
     * @param window The standard deviation of the window
     * @param threshold The standard deviation of the entire data set
     * @return True when the window std. dev is larger than that of the entire data set.
     */
    private boolean step2_HRComparison(double window, double threshold){
        return ((window - threshold) > THRESHOLD);
    }

    /**
     * Determines what event the incoming driving event is to be classified as, and determines if an alert should be shown or not
     * @param b: The baseline data
     * @param history: The history of incoming vehicle data.
     */
    private void step3_GetMinSimilarity(Baselines b, VehicleHistory history){

        final int SINGLE_DIM_EVENTS = 2;
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

        /**
         * SINGLE DIMENSION DATA
         */
        minSingle = minSim_singleDimension(b, speedHistory, SINGLE_DIM_EVENTS, dtw);
        boolean alertDetected = false;
        double percentThresholdUpper, percentThresholdLower;
        if (drivingEvent[0] == null)
            drivingEvent[0] = "";
        switch (drivingEvent[0]) {
            case "speeding": {
                percentThresholdLower = PERCENT_THRESHOLD_SPEEDING_LOWER;
                percentThresholdUpper = PERCENT_THRESHOLD_SPEEDING_UPPER;
                break;
            }
            case "brake" : {
                percentThresholdLower = PERCENT_THRESHOLD_BRAKE_LOWER;
                percentThresholdUpper = PERCENT_THRESHOLD_BRAKE_UPPER;
                break;
            }
            /*
            case "accelNearStop" : {
                percentThresholdLower = PERCENT_THRESHOLD_ACCEL_NEAR_STOP_LOWER;
                percentThresholdUpper = PERCENT_THRESHOLD_ACCEL_NEAR_STOP_UPPER;
                break;
            }
            */
            case "accel" : {
                percentThresholdLower = PERCENT_THRESHOLD_ACCEL_FROM_SPEED_LOWER;
                percentThresholdUpper = PERCENT_THRESHOLD_ACCEL_FROM_SPEED_UPPER;
                break;
            }
            default : {
                percentThresholdLower = 0.9;
                percentThresholdUpper = 1.1;
                break;
            }
        }

        if (minSingle == null) {
            if(checkSpeeding(b, speedDevHistory, dtw) > percentThresholdUpper) {
                alertDetected = true;
            }
        } else {
            double ratioDistance = ratioDistance_singleDimension(minDataSingleDim, false);
            if (ratioDistance > percentThresholdUpper || ratioDistance < percentThresholdLower) {
                alertDetected = true;
            }
        }


        /**
         * DOUBLE DIMENSION
         */
        minDouble = minSim_doubleDimension(b, speedHistory, turningHistory, TWO_DIM_EVENTS, dtw);
        if (minDouble[0] == null && minDouble[1] == null) {
            //No event should happen
        } else if (minDouble[0] != null && minDouble[1] == null) {
            //Cruising
            double ratioDistance = ratioDistance_singleDimension(md[0], true);
            if ((ratioDistance < PERCENT_THRESHOLD_CRUISE_LOWER || ratioDistance > PERCENT_THRESHOLD_CRUISE_UPPER) && ratioDistance < CRUISE_RATIO_MAX) {
                if (!alertDetected || (alertDetected && drivingEvent[0].equals("speeding"))) {
                    alertCheck(drivingEvent[1]);
                }
            }
        } else {
            //Turning
            if (turningAlertCheck(ratioDistance_doubleDimension(minDouble, md))) {
                alertDetected = false;
            }
        }
        if (alertDetected) {
            alertCheck(drivingEvent[0]);
        }
    }

    /**
     *
     * @param set Index 0 is steering, index 1 is speed
     * @return: Whether or not to show an alert
     */
    private boolean turningAlertCheck(double[] set) {
        double turningRatio = set[0];
        double speedingRatio = set[1];

        double lowerThresholdSteering, upperThresholdSteering, lowerThresholdSpeed, upperThresholdSpeed;

        if(drivingEvent[1].equals("left")) {
            lowerThresholdSteering = PERCENT_THRESHOLD_LEFT_LOWER;
            upperThresholdSteering = PERCENT_THRESHOLD_LEFT_UPPER;
        } else { // right
            lowerThresholdSteering = PERCENT_THRESHOLD_RIGHT_LOWER;
            upperThresholdSteering = PERCENT_THRESHOLD_RIGHT_UPPER;
        }

        if (turningRatio < lowerThresholdSteering || turningRatio > upperThresholdSteering) {
            alertCheck(drivingEvent[1]);
            return true;
        }
        return false;
    }

    /**
     * Determines whether or not the user is speeding, relative to their average driving speed (which is relative to the speed limit)
     * @param b: The baseline data
     * @param speedDevHistory: the history of speeding data.
     * @param dtw: The DTW class
     * @return The ratio of similarity
     */
    private double checkSpeeding(Baselines b, List speedDevHistory, FastDTW dtw) {
        TimeWarpInfo temp;
        TimeWarpInfo toReturn = null;
        List history = speedDevHistory;
        double[] baseline = b.getSpeeding();
        temp = dtw.getWarpInfoBetween(new TimeSeries(history), new TimeSeries(baseline), RADIUS, distFn);
        if (temp.getDistance() <  SINGLE_SIMILARITY_BOUND) {
            if ((toReturn == null) || temp.getDistance() < toReturn.getDistance()) {
                toReturn = temp;
                minDataSingleDim.setBaseline(Arrays.copyOf(baseline, baseline.length));
                minDataSingleDim.setEvent("speeding");
                minDataSingleDim.setVData(history);
                drivingEvent[0]= "speeding";
            }
        }
        return ratioDistance_singleDimension(minDataSingleDim, false);
    }


    /**
     * Return the most similar TimeWarpInfo
     * @param b: the baseline data
     * @param sHist: The speed history
     * @param events: a count of the number of single dimension events.
     * @param dtw: The DTW class
     * @return The TimeWarpInfo corresponding to the most similar event.
     */
    private TimeWarpInfo minSim_singleDimension(Baselines b, List sHist, int events, FastDTW dtw){
        TimeWarpInfo temp;
        TimeWarpInfo toReturn = null;
        String tempEvent="";
        double[] baseline;
        List history = sHist;
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
                /*
                case 2: {
                    baseline = b.getNearStopAccel();
                    tempEvent="accelNearStop";
                    history = sHist;
                    break;
                }*/
                default: {
                    continue;
                }
            }

            temp = dtw.getWarpInfoBetween(new TimeSeries(history), new TimeSeries(baseline), RADIUS, distFn);
            if (temp.getDistance() <  SINGLE_SIMILARITY_BOUND) {
                if ((toReturn == null) || temp.getDistance() < toReturn.getDistance()) {
                    toReturn = temp;
                    minDataSingleDim.setBaseline(Arrays.copyOf(baseline, baseline.length));
                    minDataSingleDim.setEvent(tempEvent);
                    minDataSingleDim.setVData(history);
                    drivingEvent[0]= tempEvent;
                }
            }
        }
        return toReturn;
    }


    /**
     * @param series
     * @param cruise
     * @return Determines, by how much as a percentage, the vehicle history exceeds baseline
     */
    private double ratioDistance_singleDimension(MinData series, boolean cruise){
        double sum1 = 0.0;
        double sum2 = 0.0;
        double average1;
        double average2;

        for (int i = 0; i < series.getVData().size(); i++){
            sum1+=(double)series.getVData().get(i);
        }
        for (int j = 0; j < series.getBaseline().length; j++){
            sum2+=series.getBaseline()[j];
        }

        //average1 = (sum1/twi.getPath().getTS1().size());
        average1 = (sum1/series.getVData().size());
        if (average1 == 0 & !cruise) {
            return 0;
        } else if (average1 == 0 & cruise) {
            return 1;
        }
        //average2 = (sum2/twi.getPath().getTS2().size());
        average2 = (sum2/series.getBaseline().length);
        return Math.abs((average1/average2));
    }

    /**
     * Index 0 is steering
     * Index 1 is speeding
     * @param b: The baseline data
     * @param speedHist: The history of speed data
     * @param steeringHist: The history of steering data
     * @param events: The count of double dimension events
     * @param dtw: The DTW Class
     * @return: The TimeWarpInfo[] Array containing the steering and speeding data of the most similar event.
     */
    private TimeWarpInfo[] minSim_doubleDimension(Baselines b, List speedHist, List steeringHist, int events, FastDTW dtw){
        TimeWarpInfo[] temp = new TimeWarpInfo[2];
        TimeWarpInfo[] toReturn = new TimeWarpInfo[2];
        double avgDistance;
        String tempEvent = "";
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

            temp[0] = dtw.getWarpInfoBetween(new TimeSeries(steeringHist), new TimeSeries(steeringBaseline), RADIUS, distFn);
            temp[1] = null;
            // this has been changed to only check steering
            avgDistance = temp[0].getDistance();

            if (!tempEvent.equals("cruising")) {
                temp[1] = dtw.getWarpInfoBetween(new TimeSeries(speedHist), new TimeSeries(speedBaseline), RADIUS, distFn);
                //avgDistance = (temp[0].getDistance() + temp[1].getDistance())/2;
            } else {
                //avgDistance = temp[1].getDistance();
            }


            if (avgDistance < DOUBLE_SIMILARITY_BOUND) {
                double maxCalculatedAvg;
                if (toReturn[1] != null) {//Speed considered; Is a turn
                    maxCalculatedAvg = (toReturn[0].getDistance()+toReturn[1].getDistance())/2;
                } else if (toReturn[0] != null && toReturn[1] == null) {//Speed not considered; Is cruise & turn while speed not considered
                    maxCalculatedAvg = toReturn[0].getDistance();
                } else {
                    maxCalculatedAvg = 0;
                }
                if ((toReturn[0] == null) || avgDistance < maxCalculatedAvg) {

                   boolean cruisingPass = false;

                    if (tempEvent.equals("cruising")) {
                        MinData minData = new MinData();
                        minData.setBaseline(steeringBaseline);
                        minData.setEvent(tempEvent);
                        minData.setVData(steeringHist);
                        double ratioDistance = ratioDistance_singleDimension(minData, true);
                        if ((ratioDistance < PERCENT_THRESHOLD_CRUISE_LOWER || ratioDistance > PERCENT_THRESHOLD_CRUISE_UPPER) && ratioDistance < CRUISE_RATIO_MAX) {
                            cruisingPass = true;
                        }
                    }
                    if (!tempEvent.equals("cruising") || cruisingPass) {
                        System.arraycopy(temp, 0, toReturn, 0, 2);

                        md[0].setBaseline(steeringBaseline);
                        md[0].setVData(steeringHist);
                        md[0].setEvent(tempEvent);


                        md[1].setBaseline(speedBaseline);
                        md[1].setVData(speedHist);
                        md[1].setEvent(tempEvent);

                        drivingEvent[1] = tempEvent;
                    }
                }

            }

        }
        return toReturn;
    }

    /**
     *
     * @param twi: Index 0 contains the warp path of steering data, Index 1 contains the warp path of speed data.
     * @param series: Index 0 contains the vehicle data and baseline of the steering wheel, Index 1 contains the vehicle data and time of the speed
     * @return: The ratio similarities of the most similar event
     * DISREGARDS SPEED ATM. INCLUDE IN FURTHER IMPLEMENTATION
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
        if (set1 == 0){
            set1 = 1;
        }

        for (int j = 0; j< twi[1].getPath().getTS2().size(); j++) {
            sum2_1 += (Double) series[1].getVData().get((Integer)twi[1].getPath().getTS1().get(j));
            sum2_2 += series[1].getBaseline()[(Integer)twi[1].getPath().getTS2().get(j)];
        }
        average2_1 = sum2_1 / twi[1].getPath().getTS1().size();
        average2_2 = sum2_2 / twi[1].getPath().getTS2().size();

        set2 = Math.abs((average2_1/average2_2));
        if (set2 == 0){
            set2 = 1;
        }
        double [] sets = {set1, set2};
        return sets;
    }

    /**
     * Checks if an alert should be sent, and if so, sends the signal to show it.
     * @param event: The event
     */
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
        /*} else if(event.equals("accelNearStop")) {
            eventCounter[6]++;
            incomingEvent = 6; */
        } else {
            return ;
        }
        if(eventCounter[incomingEvent] >= FATAL_THRESHHOLD[incomingEvent] * repeatSevere[incomingEvent] ){
            repeatSevere[incomingEvent] += 4;
            alertSystem.alert(applicationContext, alertSystem.ALERT_TYPE_FATAL, incomingEvent);
        }else if(eventCounter[incomingEvent] >= WARNING_THRESHHOLD[incomingEvent]){
            WARNING_THRESHHOLD[incomingEvent] += eventCounter[incomingEvent] * 2;
            alertSystem.alert(applicationContext, alertSystem.ALERT_TYPE_WARNING, incomingEvent);
        } else{}

    }

}
