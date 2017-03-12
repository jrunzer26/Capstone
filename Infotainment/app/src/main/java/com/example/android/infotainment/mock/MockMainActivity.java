package com.example.android.infotainment.mock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.example.android.infotainment.R;
import com.example.android.infotainment.backend.DataAnalyst;
import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.UserDatabaseHelper;
import com.example.android.infotainment.backend.models.UserData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100520993 on 11/23/2016.
 */

/**
 * Mock Main Activity to be called when intent-filter is changed to simulate the external devices
 */
public class MockMainActivity extends AppCompatActivity {

    private DataParser dataParser;
    private DataAnalyst dataAnalyst;
    private MockCarBluetoothHandler mockCarBluetoothHandler;
    private MockWatchBluetoothHandler mockWatchBluetoothHandler;

    /**
     * Creates the Main activity with the infotainment main display.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init threads and bluetooth connections
        threadSetup();
        // setup the UI
        TabHost host = (TabHost) findViewById(R.id.tabhost_mainactivity_tabs);
        host.setup();
        // gps tab
        TabHost.TabSpec gpsTab = host.newTabSpec(getString(R.string.main_gps));
        gpsTab.setContent(R.id.gpsTab_linear);
        gpsTab.setIndicator(getString(R.string.main_gps));
        host.addTab(gpsTab);
        // radio tab
        TabHost.TabSpec radioTab = host.newTabSpec(getString(R.string.main_radio));
        radioTab.setContent(R.id.radioTab_linear);
        radioTab.setIndicator(getString(R.string.main_radio));
        host.addTab(radioTab);
        // menu tab
        TabHost.TabSpec menuTab = host.newTabSpec(getString(R.string.main_menu));
        menuTab.setContent(R.id.menuTab_linear);
        menuTab.setIndicator(getString(R.string.main_menu));
        host.addTab(menuTab);
        mockCarBluetoothHandler = new MockCarBluetoothHandler(null, MockMainActivity.this, dataParser);
        mockWatchBluetoothHandler = new MockWatchBluetoothHandler(null, MockMainActivity.this, dataParser);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++)
                    mockCarBluetoothHandler.run();
                for(int i = 0; i < 10; i++)
                    mockWatchBluetoothHandler.run();
            }
        }, 0, 1000);
    }

    /**
     * Setup the threads needed for analyzing the data.
     */
    private void threadSetup() {
        // create the DataParser and Analyst
        dataAnalyst = new DataAnalyst(this);
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this);
        dataParser = new DataParser(dataAnalyst, this, userDatabaseHelper.getNextTripID());
        ArrayList<Thread> threads = new ArrayList<>(2);
        threads.add(new Thread(dataAnalyst));
        for (Thread thread : threads) {
            thread.start();
        }
    }
}
