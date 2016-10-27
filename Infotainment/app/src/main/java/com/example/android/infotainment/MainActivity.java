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

        TabHost.TabSpec gpsTab = host.newTabSpec("GPS");
        gpsTab.setContent(R.id.gpsTab_linear);
        gpsTab.setIndicator("GPS");
        host.addTab(gpsTab);

        TabHost.TabSpec radioTab = host.newTabSpec("Radio");
        radioTab.setContent(R.id.radioTab_linear);
        radioTab.setIndicator("Radio");
        host.addTab(radioTab);

        TabHost.TabSpec menuTab = host.newTabSpec("Menu");
        menuTab.setContent(R.id.menuTab_linear);
        menuTab.setIndicator("Menu");
        host.addTab(menuTab);
    }
}
