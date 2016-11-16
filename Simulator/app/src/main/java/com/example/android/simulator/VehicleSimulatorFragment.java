package com.example.android.simulator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.simulator.backend.Simulator;

import java.util.Timer;
import java.util.TimerTask;


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
    private Simulator sim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_vehiclesimulator, container, false);

        SeekBar seekBarAcc = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_acceleration);
        SeekBar seekBarDeg = (SeekBar)view.findViewById(R.id.seekBar_vehicleSimulatorDrive_steering);

        seekBarAcc.setMax((ACC_MAX-ACC_MIN)/STEP);
        seekBarDeg.setMax((DEG_MAX-DEG_MIN)/STEP);

        sim = ((MainActivity)this.getActivity()).getSimulator();

        final TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView seekBarDegValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_steering);

        seekBarAcc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarAccValue.setText(String.valueOf(ACC_MIN+(progress * STEP))+" km/h/s");
                sim.setAcceleration((DEG_MIN)+(progress*STEP));
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
                sim.setSteering((DEG_MIN)+(progress*STEP));
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
        }, 0, 1000);

        return view;
    }


    public void updateSpeed(View view) {
        TextView seekBarAccValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_acceleration);
        final TextView speedValue = (TextView)view.findViewById(R.id.textView_vehicleSimulatorDrive_speed);
        double accelerationValue = Double.parseDouble(seekBarAccValue.getText().toString().substring(0, seekBarAccValue.getText().toString().indexOf(' ')));
        double currentSpeed = Double.parseDouble(speedValue.getText().toString().substring(0, speedValue.getText().toString().indexOf(' ')));
        final double speed = currentSpeed + (accelerationValue*0.5);
        Activity temp = getActivity();
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sim.setSpeed(speed);
                speedValue.setText(String.valueOf(speed) + " Km/h");
            }
        });
    }

    public void pause() {
        Toast.makeText(getContext(),"Pause", Toast.LENGTH_SHORT).show();
    }
}
