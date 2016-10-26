package com.example.android.simulator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;


/**
 * Created by 100520993 on 10/25/2016.
 */


/**
 * Fragment for the vehicle simulation slider tab.
 */
public class VehicleSimulatorFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vehiclesimulator, container, false);

        TabHost host = (TabHost) view.findViewById(R.id.tabhost_vehiclesimulator_tabs);
        host.setup();

        // set the contents of the drive tab
        TabHost.TabSpec driveTab = host.newTabSpec(getString(R.string.vehicleSimulator_drive));
        driveTab.setContent(R.id.linearlayout_vehiclesimulator_drive);
        driveTab.setIndicator(getString(R.string.vehicleSimulator_drive));
        host.addTab(driveTab);

        // set the contents of the cars tab
        TabHost.TabSpec carTab = host.newTabSpec(getString(R.string.vehicleSimulator_cars));
        carTab.setContent(R.id.linearlayout_vehiclesimulator_cars);
        carTab.setIndicator(getString(R.string.vehicleSimulator_cars));
        host.addTab(carTab);

        return view;
    }
}
