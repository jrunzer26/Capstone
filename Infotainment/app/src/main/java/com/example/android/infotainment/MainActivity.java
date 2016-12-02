package com.example.android.infotainment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.android.infotainment.backend.DataAnalyst;
import com.example.android.infotainment.backend.DataParser;
import com.example.android.infotainment.backend.ThreadBeConnected;
import com.example.android.infotainment.backend.UserDatabaseHelper;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ThreadBeConnected watchThread;
    ThreadBeConnected carThread;
    private UUID watchUUID;
    private UUID carUUID;
    private String watchName;
    private String carName;
    private DataParser dataParser;
    private DataAnalyst dataAnalyst;

    /**
     * Main activity showing the main infotainment screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        watchUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        carUUID = UUID.fromString("5fadfabe-166f-4607-a872-4a84c3546adb");
        watchName = watchUUID.toString();
        carName = carUUID.toString();
        // init threads and bluetooth connections
        threadSetup();
        bluetoothSetup();
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
    }

    /**
     * Setup the threads needed for analyzing the data.
     */
    private void threadSetup() {
        // create the DataParser and Analyst
        dataAnalyst = new DataAnalyst(this);
        dataParser = new DataParser(dataAnalyst, this, new UserDatabaseHelper(this).getNextTripID());
        ArrayList<Thread> threads = new ArrayList<>(2);
        threads.add(new Thread(dataAnalyst));
        for (Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * Setup the bluetooth connection.
     */
    private void bluetoothSetup(){
        watchThread = new ThreadBeConnected(watchName, watchUUID, MainActivity.this, dataParser, false);
        carThread = new ThreadBeConnected(carName, carUUID, MainActivity.this, dataParser, true);
        watchThread.start();
        carThread.start();
    }
}
