package com.example.android.simulator;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TimePicker;

import com.example.android.simulator.backend.Simulator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100520993 on 10/26/2016.
 */

/**
 * Fragment for the environment simulation slider tab.
 */
public class EnvironmentSimulatorFragment extends Fragment {

    private Simulator sim;
    public static int seconds;
    public static int hour;
    public static int minute;
    public static int visibility;
    public static int climateFeel;
    public static int severity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_environmentsimulator, container, false);

        TabHost host = (TabHost) view.findViewById(R.id.tabhost_environmentsimulator_tabhost);
        host.setup();
        seconds = 0;
        hour = 0;
        minute = 0;
        visibility = 0;
        climateFeel = 0;
        severity = 0;

        sim = ((MainActivity)this.getActivity()).getSimulator();

        final TimePicker timerPicker = (TimePicker) view.findViewById(R.id.timerPicker_environmentsimulator_timeOfDay);
        // set the contents of the drive tab
        TabHost.TabSpec climateTab = host.newTabSpec(getString(R.string.environmentSimulator_climate));
        climateTab.setContent(R.id.linearLayout_environmentSimulator_climateTab);
        climateTab.setIndicator(getString(R.string.environmentSimulator_climate));
        host.addTab(climateTab);

        // set the contents of the cars tab
        TabHost.TabSpec roadConditionsTab = host.newTabSpec(getString(R.string.environmentSimulatorClimate_roadConditions));
        roadConditionsTab.setContent(R.id.linearLayout_environmentSimulator_roadConditionsTab);
        roadConditionsTab.setIndicator(getString(R.string.environmentSimulatorClimate_roadConditions));
        host.addTab(roadConditionsTab);

        SeekBar seekBarVisibility = (SeekBar)view.findViewById(R.id.seekbar_environmentSimulatorClimate_visibility);
        seekBarVisibility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                visibility = progress;
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



        SeekBar seekBarDensity = (SeekBar)view.findViewById(R.id.seekbar_environmentSimulatorClimate_density);
        seekBarDensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                climateFeel = progress;
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

        SeekBar seekBarSeverity = (SeekBar)view.findViewById(R.id.seekbar_environmentSimulatorRoadConditions_severity);
        seekBarSeverity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                severity = progress;
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
                sim.setSeverity(severity);
                sim.setClimateFeel(climateFeel);
                sim.setVisibility(visibility);
            }
        }, 0, 1000);


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Activity temp = getActivity();
                temp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seconds++;
                        if(seconds == 60){
                            seconds = 0;
                            int min = timerPicker.getMinute();
                            if(min == 59){
                                timerPicker.setMinute(0);
                                int hour = timerPicker.getHour();
                                if(hour == 23){
                                    timerPicker.setHour(1);
                                } else {
                                    timerPicker.setHour(hour+1);
                                }
                            } else {
                                timerPicker.setMinute(min+1);
                            }
                        }
                        hour = timerPicker.getHour();
                        minute = timerPicker.getMinute();
                    }
                });

            }
        }, 0, 1000);

        return view;
    }


}
