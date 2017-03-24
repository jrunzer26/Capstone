package com.example.android.simulator.backend;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.simulator.EnvironmentSimulatorFragment;
import com.example.android.simulator.R;
import com.example.android.simulator.ThreadConnectBTdevice;
import com.example.android.simulator.backend.models.SimData;
import com.example.android.simulator.backend.models.Time;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import static com.example.android.simulator.backend.models.SimData.*; // import all constants

/**
 * Created by 100520993 on 11/3/2016.
 */


// TODO: Clean this class (non priority)

public class Simulator implements Runnable, Car {

    private final int SENDING_SIZE = 10;
    private ArrayList<SimData> simDataArrayList = new ArrayList<>();
    private SimData currentSimData;
    private ThreadConnectBTdevice blueTooth;
    private int count;
    private Context context;
    private View view;

    /**
     * Stores all the variables for later use
     * @param context - Current context of the system
     * @param view - Current view of the application
     * @param device - the device you are connected to
     */
    public Simulator(Context context, View view, ThreadConnectBTdevice device) {
        //Stores the device you are connected to
        blueTooth = device;
        this.context = context;
        this.view = view;
        count = 0;
        currentSimData = new SimData();
    }

    /**
     * Store the car simulator values into their respected array
     */
    @Override
    public void run() {
        //Gets the value text box that corrolates to the Accleration and Degree slider
        System.out.println("In the run statement");
        setCurrentSimDataSpeed();
        setCurrentSimDataSteering();
        setCurrentSimDataAcceleration();
        setCurrentSimDataTime();
        setCurrentSimSpeedLimit();
        // set the climate Severity, Visibility, and Feel
        currentSimData.setRoadSeverity(EnvironmentSimulatorFragment.severity);
        currentSimData.setClimateDensity(EnvironmentSimulatorFragment.climateFeel);
        currentSimData.setClimateVisibility(EnvironmentSimulatorFragment.visibility);
        // send a copy of the current state to the output queue
        simDataArrayList.add(currentSimData.copy());
        count++;
        if (count == SENDING_SIZE) {
            count = 0;
            sendData();
        }
    }

    /**
     * Sets the SimData Time.
     */
    private void setCurrentSimDataTime() {
        Time time = new Time(EnvironmentSimulatorFragment.hour,
                EnvironmentSimulatorFragment.minute,
                EnvironmentSimulatorFragment.seconds);
        currentSimData.setTime(time);
    }

    /**
     * Sets the Sim Data Steering degree.
     */
    private void setCurrentSimDataSteering() {
        final TextView seekBarDegValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_steering);
        double steering = Double.parseDouble(seekBarDegValue.getText().toString()
                .substring(0, seekBarDegValue.getText().toString().indexOf(' ')));
        Log.i("steering", steering+"");
        currentSimData.setSteering(steering);
    }

    /**
     * Sets the sim data acceleration
     */
    private void setCurrentSimDataAcceleration() {
        final TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        double acceleration = Double.parseDouble(seekBarAccValue.getText().toString()
                        .substring(0, seekBarAccValue.getText().toString().indexOf(' ')));
        currentSimData.setAcceleration(acceleration);
    }

    private void setCurrentSimSpeedLimit() {
        final TextView speedLimit = (TextView)view.findViewById(R.id.textView5);
        double speedLim = Double.parseDouble(speedLimit.getText().toString().substring(0, speedLimit.getText().toString().indexOf(' ')));
        currentSimData.setSpeedLimit(speedLim);
    }

    /**
     * Sets the sim data speed.
     */
    private void setCurrentSimDataSpeed() {
        TextView speedValue = (TextView) view.findViewById(R.id.textView_vehicleSimulatorDrive_speed);
        double currentSpeed = Double.parseDouble(speedValue.getText().toString()
                .substring(0, speedValue.getText().toString().indexOf(' ')));
        currentSimData.setSpeed(currentSpeed);
    }

    /**
     * Polls for data every 5 seconds.
     * Then sends the data to the server
     */
    public void sendData() {
        //Sets up the variables that will allow you to convert the data into bytes
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        //Run 5 times each time grabbing that element in the array's and turning them into bytes
        for (int i = 0; i < 10; i++) {
            SimData iteratedSimData = simDataArrayList.get(i);
            try {
                writeSimDataToDos(dos, iteratedSimData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //If you are connected to the bluetooth server send the message
        if (blueTooth.getIsConnected()) {
            //Store all the values that have been turned into bytes into the bytes array
            byte[] bytes = boas.toByteArray();
            //Send the message off to the bluetooth server
            blueTooth.connectedThread.write(bytes);
        } else {
            System.out.println("There is no connection available. Please try again later!");
        }
        //Clears all the arrays of their previous values - has they have been sent to the server already
        simDataArrayList.clear();
    }

    /**
     * Writes the sim data to the data output stream
     * @param dos the data output stream
     * @param iteratedSimData the sim data to send over the stream
     * @throws IOException
     */
    private void writeSimDataToDos(DataOutputStream dos, SimData iteratedSimData) throws IOException {
        dos.writeUTF(iteratedSimData.getGear());
        dos.writeBoolean(iteratedSimData.isCruseControl());
        dos.writeBoolean(iteratedSimData.isPaused());
        dos.writeDouble(iteratedSimData.getSpeed());
        dos.writeDouble(iteratedSimData.getAcceleration());
        Log.i("steering1111", iteratedSimData.getSteering() +"");
        dos.writeDouble(iteratedSimData.getSteering());
        dos.writeInt(iteratedSimData.getSignal());
        dos.writeInt(iteratedSimData.getClimate());
        dos.writeInt(iteratedSimData.getClimateVisibility());
        dos.writeInt(iteratedSimData.getClimateDensity());
        dos.writeInt(iteratedSimData.getRoadSeverity());
        Time time = iteratedSimData.getTime();
        dos.writeInt(time.getHour());
        dos.writeInt(time.getMinute());
        dos.writeInt(time.getSecond());
        dos.writeInt(iteratedSimData.getRoadCondition());
        dos.writeInt(iteratedSimData.getRoadType());
        dos.writeDouble(iteratedSimData.getSpeedLimit());
    }

    /**
     * Sets the gear variable to park
     */
    @Override
    public void park() {
        currentSimData.setGear(PARK);
    }

    /**
     * Sets the gear variable to reverse
     */
    @Override
    public void reverse() {
        currentSimData.setGear(REVERSE);
    }

    /**
     * Sets the gear variable to drive
     */
    @Override
    public void drive() {
        currentSimData.setGear(DRIVE);
    }


    /**
     * Sets cruiseControl to true
     */
    @Override
    public void cruise() {
        currentSimData.setCruseControl(!currentSimData.isCruseControl());
    }

    /**
     * Sets pause variable to true
     */
    @Override
    public void pause() {
        currentSimData.setPause(!currentSimData.isPaused());
    }

    @Override
    public double getSteering() {
        return 0;
    }

    /**
     * Sets the signal variable to the value of SIGNAL_LEFT
     */
    @Override
    public void signalLeft() {
        currentSimData.setSignal(SIGNAL_LEFT);
    }

    /**
     * Sets the signal variable to the value of SIGNAL_RIGHT
     */
    @Override
    public void signalRight() {
        currentSimData.setSignal(SIGNAL_RIGHT);
    }

    @Override
    public void changeLeft() {

    }

    @Override
    public void changeRight() {

    }

    /**
     * Sets the climateSunny variable to true and the rest to false
     */
    public void climateSunny() {
        currentSimData.setClimate(CLIMATE_SUNNY);
    }

    /**
     * Sets the correct climateHail variable to true and the rest to false
     */
    public void climateHail() {
        currentSimData.setClimate(CLIMATE_HAIL);
    }

    /**
     * Sets the climateSnowy variable to true and the rest to false
     */
    public void climateSnowy() {
        currentSimData.setClimate(CLIMATE_SNOWY);
    }

    /**
     * Sets the climateRain variable to true and the rest to false
     */
    public void climateRain() {
        currentSimData.setClimate(CLIMATE_RAIN);
    }

    /**
     * Adds the current speed to the speed array
     * @param speed - current speed of the system
     */
    public void setSpeed(double speed) {
        currentSimData.setSpeed(speed);
    }

    /**
     * Sets the roadConIce variable to true and the rest to false
     */
    public void roadConditionIce() {
        currentSimData.setRoadCondition(ROAD_CON_ICE);
    }

    /**
     * Sets the roadConWarmIce variable to true and the rest to false
     */
    public void roadConditionWarmIce() {
        currentSimData.setRoadCondition(ROAD_CON_WARM_ICE);
    }

    /**
     * Sets the roadConWet variable to true and the rest to false
     */
    public void roadConditionWet() {
        currentSimData.setRoadCondition(ROAD_CON_WET);
    }

    /**
     * Sets the roadTypeDirt variable to true and the rest to false
     */
    public void roadTypeDirt() {
        currentSimData.setRoadType(ROAD_TYPE_DIRT);
    }

    /**
     * Sets the roadTypePaved variable to true and the rest to false
     */
    public void roadTypePaved() {
        currentSimData.setRoadType(ROAD_TYPE_PAVED);
    }

    /**
     * Sets the roadTypeGravel variable to true and the rest to false
     */
    public void roadTypeGravel() {
        currentSimData.setRoadType(ROAD_TYPE_GRAVEL);
    }

    /**
     * Adds the visibility to the climateVisibility array
     * @param visibility - current visibility of the system
     */
    public void setVisibility(int visibility){
        currentSimData.setClimateVisibility(visibility);
    }

    /**
     * Adds the severity to the roadSeverity array
     * @param severity - current severity of the system
     */
    public void setSeverity(int severity) {
        currentSimData.setRoadSeverity(severity);
    }

}
