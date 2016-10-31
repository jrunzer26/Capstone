package com.example.android.simulator;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

/**
 * Created by 100520993 on 10/26/2016.
 */

/**
 * Fragment for the environment simulation slider tab.
 */
public class EnvironmentSimulatorFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_environmentsimulator, container, false);

        TabHost host = (TabHost) view.findViewById(R.id.tabhost_environmentsimulator_tabhost);
        host.setup();

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
        return view;
    }
}
