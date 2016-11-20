package com.example.janahan.heartbeatcollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 100522058 on 11/20/2016.
 */

public class startUpActivity extends Activity {
    Button simulator;
    Button real;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        simulator = (Button)findViewById(R.id.button_heartRateSimulation);
        real = (Button)findViewById(R.id.button_heartRateReal);
    }

    public void connect(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), SimulatedActivity.class);
        startActivity(nextScreen);
    }

    public void sensor(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(nextScreen);
    }
}
