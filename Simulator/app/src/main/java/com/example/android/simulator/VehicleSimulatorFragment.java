package com.example.android.simulator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.simulator.backend.Simulator;

import java.math.BigDecimal;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Handler;


/**
 * Created by 100520993 on 10/25/2016.
 */


/**
 * Fragment for the vehicle simulation slider tab.
 */
public class VehicleSimulatorFragment extends Fragment {

    /**
     * Declaring variables for the accleration and steering slider
     */
    final int STEP = 1;
    final int ACC_MAX = 50;
    final int ACC_MIN = -15;
    final int DEG_MAX = 180;
    final int DEG_MIN = -180;
    final int REFRESH_RATE = 100;
    private Simulator sim;
    private ThreadConnectBTdevice BTdevice;
    private UUID myUUID;
    private BluetoothAdapter bluetoothAdapter;
    private final int seekBarZeroProgress = 15;
    Button connectButton;
    Button incSpeedLimit;
    Button decSpeedLimit;

    /**
     * Creates the savedInstanceState
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view of the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_vehiclesimulator, container, false);

        final SeekBar seekBarAcc = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_acceleration);
        SeekBar seekBarDeg = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_steering);
        //Sets the maximum value for the Accleration and Degree slider
        seekBarAcc.setMax((ACC_MAX-ACC_MIN)/STEP);
        seekBarDeg.setMax((DEG_MAX-DEG_MIN)/STEP);
        seekBarDeg.setProgress(181);

        connectButton = (Button)view.findViewById(R.id.button_reConnect);
        incSpeedLimit = (Button)view.findViewById(R.id.button_incSpeedLimit);
        decSpeedLimit = (Button)view.findViewById(R.id.button_decSpeedLimit);

        //Retreive the Simulator object that was created in the main and use it in this fragment
        sim = ((MainActivity)this.getActivity()).getSimulator();
        BTdevice = ((MainActivity)this.getActivity()).myThreadConnectBTdevice;
        myUUID = ((MainActivity)this.getActivity()).myUUID;
        bluetoothAdapter = ((MainActivity)this.getActivity()).bluetoothAdapter;

        final TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView seekBarDegValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_steering);
        final TextView currentSpeedLimit = (TextView)view.findViewById(R.id.textView5);


        buttonListenerInit(view);

        seekBarAcc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            /**
             * Runs when Accleration seekbar changes
             * @param seekBar
             * @param progress - The current value of the accleration slider
             * @param fromUser
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Set the accleration test to what it is on the slider
                seekBarAccValue.setText(String.valueOf(ACC_MIN+(progress * STEP))+" km/h/s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateConnection(view);
            }
        }, 2000, REFRESH_RATE);

        seekBarDeg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            /**
             * Runs when Degree seekbar changes
             * @param seekBar
             * @param progress - The current value of the degree slider
             * @param fromUser
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Set the degree test to what it is on the slider
                seekBarDegValue.setText(String.valueOf(DEG_MIN+(progress * STEP))+" Deg");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //double degValue = Double.parseDouble(seekBarDegValue.getText().toString().substring(0, seekBarDegValue.getText().toString().indexOf(' ')));
               // sim.setSteering(degValue);
            }
        });

        TabHost host = (TabHost) view.findViewById(R.id.tabhost_vehiclesimulator_tabs);
        host.setup();

        // set the contents of the drive tab
        TabHost.TabSpec driveTab = host.newTabSpec(getString(R.string.vehicleSimulator_drive));
        driveTab.setContent(R.id.linearlayout_vehiclesimulator_drive);
        driveTab.setIndicator(getString(R.string.vehicleSimulator_drive));
        host.addTab(driveTab);

        // set the contents of the cars tab
        TabHost.TabSpec roadConditionsTab = host.newTabSpec(getString(R.string.vehicleSimulator_cars));
        roadConditionsTab.setContent(R.id.linearlayout_vehiclesimulator_cars);
        roadConditionsTab.setIndicator(getString(R.string.vehicleSimulator_cars));
        host.addTab(roadConditionsTab);

        //Update the current speed of the car based off of the accleration every 1ms
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               updateSpeed(view);
            }
        }, 0, REFRESH_RATE);


        incSpeedLimit.setOnClickListener( new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               int speed = Integer.parseInt(currentSpeedLimit.getText().toString().substring(0, currentSpeedLimit.getText().toString().indexOf(' ')));
               int newSpeed = speed + 10;
               currentSpeedLimit.setText(String.valueOf(newSpeed) + " Km/h");

           }
        });

        decSpeedLimit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = Integer.parseInt(currentSpeedLimit.getText().toString().substring(0, currentSpeedLimit.getText().toString().indexOf(' ')));
                int newSpeed = speed - 10;
                currentSpeedLimit.setText(String.valueOf(newSpeed) + " Km/h");
            }
        });


        connectButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                BluetoothDevice device;
                if(pairedDevices.size() > 0) {
                    //Goes though all the bluetooth devices that are paired on the device being used
                    for (BluetoothDevice dev: pairedDevices) {
                        device = dev;
                        //Checks if one of the devices is "Jason R (Galaxy Tab4) -> this is the server tablet
                        if(device.getName().equals("Jason R (Galaxy Tab4)")){
                            //Creates an object and passes in the server's tablet name and it's UUID
                            //Starts the thread in the ThreadConnectBTdevice object
                            BTdevice.reconnect(device, myUUID);
                            break;
                        }
                    }
                }
            }
        });

        return view;
    }


    /**
     * This method calculates the speed based off of the accleration and the current speed
     * @param view - is the view of this fragment
     */
    private void buttonListenerInit(View view) {
        ((Button) view.findViewById(R.id.button_vehicleSimDrive_cruise)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cruise();
            }
        });

        //((RadioButton) view)
    }

    private void updateConnection(View view) {
        Activity temp = getActivity();
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BTdevice.getIsConnected()) {
                    connectButton.setText("Connected");
                } else {
                    connectButton.setText("Connect");
                }
            }
        });
    }

    private void cruise() {
        ((SeekBar) getView().findViewById(R.id.seekBar_vehicleSimulatorDrive_acceleration)).setProgress(seekBarZeroProgress);
    }

    private void updateGear(View view, double speed) {
        final RadioButton park = ((RadioButton) view.findViewById(R.id.radioButton_vehicleDrive_park));
        final RadioButton reverse = ((RadioButton) view.findViewById(R.id.radioButton_vehicleDrive_reverse));
        final RadioButton drive = ((RadioButton) view.findViewById(R.id.radioButton_vehicleDrive_drive));

        if (speed != 0 && park.isChecked()) {
            android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    park.setChecked(false);
                    drive.callOnClick();
                    drive.setChecked(true);
                }
            });

        }
    }

    public void updateSpeed(View view) {
        TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView speedValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_speed);
        double accelerationValue = Double.parseDouble(seekBarAccValue.getText().toString().substring(0, seekBarAccValue.getText().toString().indexOf(' ')));
        double currentSpeed = Double.parseDouble(speedValue.getText().toString().substring(0, speedValue.getText().toString().indexOf(' ')));
        double time = REFRESH_RATE / 1000.0; // convert time passed to seconds
        double speed = currentSpeed + (accelerationValue * time);
        updateGear(view, speed);

        if (speed > 200) {
            speed = 200;
        } else if (speed <  -15) {
            speed = -15;
        }
        final Double speedDouble = new Double(speed);
        Activity temp = getActivity();
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Double formatedSpeed = BigDecimal.valueOf(speedDouble).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                speedValue.setText(String.valueOf(formatedSpeed) + " Km/h");
            }
        });
    }

    /**
     * Pauses the fragment
     */
    public void pause() {
        Toast.makeText(getContext(),"Pause", Toast.LENGTH_SHORT).show();
    }
}
