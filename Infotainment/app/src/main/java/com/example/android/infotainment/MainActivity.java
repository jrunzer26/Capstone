package com.example.android.infotainment;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TabHost;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ThreadBeConnected myThreadBeConnected;
    ThreadBeConnected myThreadBeConnected2;
    private UUID myUUID;
    private UUID mySecondUUID;
    private String myName;
    private String myName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        mySecondUUID = UUID.fromString("35a7e360-a5ce-11e6-9598-0800200c9a66");
        myName = myUUID.toString();
        myName2 = mySecondUUID.toString();
        setup();
        TabHost host = (TabHost) findViewById(R.id.tabhost_mainactivity_tabs);
        host.setup();

        TabHost.TabSpec gpsTab = host.newTabSpec(getString(R.string.main_gps));
        gpsTab.setContent(R.id.gpsTab_linear);
        gpsTab.setIndicator(getString(R.string.main_gps));
        host.addTab(gpsTab);

        TabHost.TabSpec radioTab = host.newTabSpec(getString(R.string.main_radio));
        radioTab.setContent(R.id.radioTab_linear);
        radioTab.setIndicator(getString(R.string.main_radio));
        host.addTab(radioTab);

        TabHost.TabSpec menuTab = host.newTabSpec(getString(R.string.main_menu));
        menuTab.setContent(R.id.menuTab_linear);
        menuTab.setIndicator(getString(R.string.main_menu));
        host.addTab(menuTab);
    }

    public void setup(){
        myThreadBeConnected = new ThreadBeConnected(myName, myUUID);
        myThreadBeConnected2 = new ThreadBeConnected(myName2, myUUID);
        myThreadBeConnected.start();
        myThreadBeConnected2.start();
    }
}
