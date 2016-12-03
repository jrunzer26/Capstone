package com.example.android.simulator.backend;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.simulator.EnvironmentSimulatorFragment;
import com.example.android.simulator.R;
import com.example.android.simulator.ThreadConnectBTdevice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100520993 on 11/3/2016.
 */

public class Simulator implements Runnable, Car {

    /**
     * Initializes all the variables needed for the Simulator class
     */
    private final int CLIMATE_SUNNY = 10;
    private final int CLIMATE_HAIL = 11;
    private final int CLIMATE_SNOWY = 12;
    private final int CLIMATE_RAIN = 13;

    private final int SIGNAL_RIGHT = 20;
    private final int SIGNAL_LEFT = 21;

    private final int CHANGE_RIGHT = 30;
    private final int CHANGE_LEFT = 31;

    private final int ROAD_CON_ICE = 40;
    private final int ROAD_CON_WARM_ICE = 41;
    private final int ROAD_CON_WET = 42;

    private final int ROAD_TYPE_GRAVEL = 50;
    private final int ROAD_TYPE_PAVED = 51;
    private final int ROAD_TYPE_DIRT = 52;


    private boolean gearPark = false;
    private boolean gearReverse = false;
    private boolean gearDrive = false;

    private boolean climateSunny = false;
    private boolean climateHail = false;
    private boolean climateSnowy = false;
    private boolean climateRain = false;

    private boolean roadConIce = false;
    private boolean roadConWarmIce = false;
    private boolean roadConWet = false;

    private boolean roadTypeGravel = false;
    private boolean roadTypePaved = false;
    private boolean roadTypeDirt = false;


    private ArrayList<Double> speed = new ArrayList<>();
    private ArrayList<String> gear = new ArrayList<>();
    private boolean crusieControl = false;
    private boolean pause = false;
    private int signal = 0;
    private ArrayList<Double> steering = new ArrayList<>();
    private ArrayList<Double> acceleration =new ArrayList<>();
    private ArrayList<Integer> climate = new ArrayList<>();
    private ArrayList<Integer> climateVisibility = new ArrayList<>();
    private ArrayList<Integer> roadSeverity = new ArrayList<>();
    private ArrayList<Integer> climateFeel = new ArrayList<>();
    private ArrayList<Integer> timeHour = new ArrayList<>();
    private ArrayList<Integer> timeMinute = new ArrayList<>();
    private ArrayList<Integer> timeSecond =new ArrayList<>();
    private ArrayList<Integer> roadCondition = new ArrayList<>();
    private ArrayList<Integer> roadType =new ArrayList<>();
    private ThreadConnectBTdevice blueTooth;

    private int count;
    private Context context;
    private View view;




    /**
     * Stores data in the buffer;
     */
    private void storeData(byte [] bytes) {
        // TODO: 11/3/2016 Simulator - storeData
    }

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
    }

    /**
     * Store the car simulator values into their respected array
     */
    @Override
    public void run() {
        //Gets the value text box that corrolates to the Accleration and Degree slider
        final TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView seekBarDegValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_steering);
        System.out.println("In the run statement");

        //Checks what gear the user is in and adds it to the gear array
        if(gearPark) {
            gear.add("Park");
        } else if(gearReverse) {
            gear.add("Reverse");
        } else if(gearDrive) {
            gear.add("Drive");
        } else {
            gear.add("Park");
        }

        //Checks what the climate currently is and adds that value to the climate array
        if(climateHail) {
            climate.add(CLIMATE_HAIL);
        } else if(climateSnowy) {
            climate.add(CLIMATE_SNOWY);
        } else if(climateSunny) {
            climate.add(CLIMATE_SUNNY);
        } else if(climateRain) {
            climate.add(CLIMATE_RAIN);
        } else {
            climate.add(CLIMATE_SUNNY);
        }

        //Checks the condition of the road and sets that to the roadCondition array
        if(roadConWet) {
            roadCondition.add(ROAD_CON_WET);
        } else if(roadConWarmIce) {
            roadCondition.add(ROAD_CON_WARM_ICE);
        } else if(roadConIce) {
            roadCondition.add(ROAD_CON_ICE);
        } else {
            roadCondition.add(0);
        }

        //Checks the type of the road is and adds it to the roadType array
        if(roadTypePaved) {
            roadType.add(ROAD_TYPE_PAVED);
        } else if(roadTypeGravel) {
            roadType.add(ROAD_TYPE_GRAVEL);
        } else if(roadTypeDirt) {
            roadType.add(ROAD_TYPE_DIRT);
        } else {
            roadType.add(ROAD_TYPE_PAVED);
        }


        // set the speed of the vehicle
        TextView speedValue = (TextView) view.findViewById(R.id.textView_vehicleSimulatorDrive_speed);
        double currentSpeed = Double.parseDouble(speedValue.getText().toString()
                .substring(0, speedValue.getText().toString().indexOf(' ')));
        this.setSpeed(currentSpeed);

        // set steering
        this.setSteering(Double.parseDouble(seekBarDegValue.getText().toString()
                .substring(0, seekBarDegValue.getText().toString().indexOf(' '))));

        // set the acceleration
        this.setAcceleration(Double.parseDouble(seekBarAccValue.getText().toString()
                .substring(0, seekBarAccValue.getText().toString().indexOf(' '))));

        // set the time
        this.setHour(EnvironmentSimulatorFragment.hour);
        this.setMin(EnvironmentSimulatorFragment.minute);
        this.setSecond(EnvironmentSimulatorFragment.seconds);

        // set the climate Severity, Visibility, and Feel
        this.setSeverity(EnvironmentSimulatorFragment.severity);
        this.setClimateFeel(EnvironmentSimulatorFragment.climateFeel);
        this.setVisibility(EnvironmentSimulatorFragment.visibility);

        count++;
        //When the count is 5 finally send all the data that has been stored in the array
        if (count == 5) {
            count = 0;
            sendData();
        }
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
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(gear.get(i));
                dos.writeUTF(gear.get(i));
                dos.writeBoolean(crusieControl);
                dos.writeBoolean(pause);
                dos.writeDouble(speed.get(i));
                dos.writeDouble(acceleration.get(i));
                dos.writeDouble(steering.get(i));
                dos.writeInt(signal);
                dos.writeInt(climate.get(i));
                dos.writeInt(climateVisibility.get(i));
                dos.writeInt(climateFeel.get(i));
                dos.writeInt(roadSeverity.get(i));
                dos.writeInt(timeHour.get(i));
                dos.writeInt(timeMinute.get(i));
                dos.writeInt(timeSecond.get(i));
                dos.writeInt(roadCondition.get(i));
                dos.writeInt(roadType.get(i));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //If you are connected to the bluetooth server send the message
        if (blueTooth.connectedThread != null) {
            //Store all the values that have been turned into bytes into the bytes array
            byte[] bytes = boas.toByteArray();
            //Send the message off to the bluetooth server
            blueTooth.connectedThread.write(bytes);
        } else {
            System.out.println("There is no connection avaible. Please try again later!");
        }
        //Clears all the arrays of their previous values - has they have been sent to the server already
        gear.clear();
        speed.clear();
        acceleration.clear();
        steering.clear();
        climate.clear();
        climateVisibility.clear();
        climateFeel.clear();
        roadSeverity.clear();
        timeHour.clear();
        timeMinute.clear();
        timeSecond.clear();
        roadCondition.clear();
        roadType.clear();
    }

    /**
     * Sets the gear variable to park
     */
    @Override
    public void park() {
        gearPark = true;
        gearDrive = false;
        gearReverse = false;
    }

    /**
     * Sets the gear variable to reverse
     */
    @Override
    public void reverse() {
        gearReverse = true;
        gearPark = false;
        gearDrive = false;
    }

    /**
     * Sets the gear variable to drive
     */
    @Override
    public void drive() {
        this.gearDrive = true;
        this.gearPark = false;
        this.gearReverse = false;
        System.out.println("The vales in drive method: "+ gearDrive+ " "+ gearPark+ " "+gearReverse);
    }


    /**
     * Sets cruiseControl to true
     */
    @Override
    public void cruise() {
        crusieControl = true;
    }

    /**
     * Sets pause variable to true
     */
    @Override
    public void pause() {
        pause = true;
    }

    /**
     * Adds degree into steering array
     * @param degree - current value of the degree slider
     */
    public void setSteering(double degree){
        steering.add(degree);
    }

    /**
     * Adds accleration into accleration array
     * @param acceleration - current value of the accleration slider
     */
    public void setAcceleration(double acceleration){
        this.acceleration.add(acceleration);
    }

    /**
     * Returns the first index in the steering array
     * @return steering array value
     */
    @Override
    public double getSteering() {
        return steering.get(0);
    }

    /**
     * Sets the signal variable to the value of SIGNAL_LEFT
     */
    @Override
    public void signalLeft() {
        signal = SIGNAL_LEFT;
    }

    /**
     * Sets the signal variable to the value of SIGNAL_RIGHT
     */
    @Override
    public void signalRight() {
        signal = SIGNAL_RIGHT;
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
    public void climateSuuny() {
        climateSunny = true;
        climateSnowy = false;
        climateRain = false;
        climateHail = false;
    }

    /**
     * Sets the correct climateHail variable to true and the rest to false
     */
    public void climateHail() {
        climateSunny = false;
        climateSnowy = false;
        climateRain = false;
        climateHail = true;
    }

    /**
     * Sets the climateSnowy variable to true and the rest to false
     */
    public void climateSnowy() {
        climateSunny = false;
        climateSnowy = true;
        climateRain = false;
        climateHail = false;
    }

    /**
     * Sets the climateRain variable to true and the rest to false
     */
    public void climateRain() {
        climateSunny = false;
        climateSnowy = false;
        climateRain = true;
        climateHail = false;
    }

    /**
     * Adds the current speed to the speed array
     * @param speed - current speed of the system
     */
    public void setSpeed(double speed) {
        this.speed.add(speed);
    }

    /**
     * Sets the roadConIce variable to true and the rest to false
     */
    public void roadConditionIce() {
        roadConIce = true;
        roadConWarmIce = false;
        roadConWet = false;
    }

    /**
     * Sets the roadConWarmIce variable to true and the rest to false
     */
    public void roadConditionWarmIce() {
        roadConIce = false;
        roadConWarmIce = true;
        roadConWet = false;
    }

    /**
     * Sets the roadConWet variable to true and the rest to false
     */
    public void roadConditionWet() {
        roadConIce = false;
        roadConWarmIce = false;
        roadConWet = true;
    }

    /**
     * Sets the roadTypeDirt variable to true and the rest to false
     */
    public void roadTypeDirt() {
        roadTypeDirt = true;
        roadTypeGravel = false;
        roadTypePaved = false;
    }

    /**
     * Sets the roadTypePaved variable to true and the rest to false
     */
    public void roadTypePaved() {
        roadTypeDirt = false;
        roadTypeGravel = false;
        roadTypePaved = true;
    }

    /**
     * Sets the roadTypeGravel variable to true and the rest to false
     */
    public void roadTypeGravel() {
        roadTypeDirt = false;
        roadTypeGravel = true;
        roadTypePaved = false;
    }

    /**
     * Adds the hour to the timeHour array
     * @param hour - the current hour in the system(military time)
     */
    public void setHour(int hour) {
        timeHour.add(hour);
    }

    /**
     * Adds the minute to the timeMinute array
     * @param min - the current minute in the system
     */
    public void setMin(int min) {
        timeMinute.add(min);
    }

    /**
     * Adds the second to the timeSecond array
     * @param second - the current second in the system
     */
    public void setSecond(int second) {
        timeSecond.add(second);
    }

    /**
     * Adds the visibility to the climateVisibility array
     * @param visibility - current visibility of the system
     */
    public void setVisibility(int visibility){
        this.climateVisibility.add(visibility);
    }

    /**
     * Adds the feeling to the climateFeel array
     * @param feeling - current feel of the  system
     */
    public void setClimateFeel(int feeling){
        this.climateFeel.add(feeling);
    }

    /**
     * Adds the severity to the roadSeverity array
     * @param severity - current severity of the system
     */
    public void setSeverity(int severity) {
        this.roadSeverity.add(severity);
    }

}
