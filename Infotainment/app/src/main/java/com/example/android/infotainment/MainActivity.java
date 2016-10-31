package com.example.android.infotainment;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
