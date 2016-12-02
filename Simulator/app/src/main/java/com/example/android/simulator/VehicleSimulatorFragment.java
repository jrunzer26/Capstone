package com.example.android.simulator;

import android.app.Activity;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


/**
 * Created by 100520993 on 10/25/2016.
 */


/**
 * Fragment for the vehicle simulation slider tab.
 */
public class VehicleSimulatorFragment extends Fragment {
    final int STEP = 1;
    final int ACC_MAX = 50;
    final int ACC_MIN = -15;
    final int DEG_MAX = 30;
    final int DEG_MIN = -30;
    final int REFRESH_RATE = 100;
    private Simulator sim;
    private final int seekBarZeroProgress = 15;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_vehiclesimulator, container, false);

        final SeekBar seekBarAcc = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_acceleration);
        SeekBar seekBarDeg = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_steering);

        seekBarAcc.setMax((ACC_MAX-ACC_MIN)/STEP);
        seekBarDeg.setMax((DEG_MAX-DEG_MIN)/STEP);

        sim = ((MainActivity)this.getActivity()).getSimulator();

        final TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView seekBarDegValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_steering);

        buttonListenerInit(view);

        seekBarAcc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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


        seekBarDeg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               updateSpeed(view);
            }
        }, 0, REFRESH_RATE);

        return view;
    }

    private void buttonListenerInit(View view) {
        ((Button) view.findViewById(R.id.button_vehicleSimDrive_cruise)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cruise();
            }
        });

        //((RadioButton) view)
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

    public void pause() {
        Toast.makeText(getContext(),"Pause", Toast.LENGTH_SHORT).show();
    }
}
