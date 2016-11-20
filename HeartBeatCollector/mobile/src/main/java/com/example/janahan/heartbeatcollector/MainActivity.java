package com.example.janahan.heartbeatcollector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.janahan.heartbeatcollector.SensorCnst.Sensors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RemoteSensorManager rsm;
    private ScheduledExecutorService mScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("APPHB/100", "Start" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rsm = RemoteSensorManager.getInstance(this);
        final TextView t1 = (TextView)findViewById(R.id.value);
        final TextView t2 = (TextView)findViewById(R.id.statusID);
        final Button button1 = (Button) findViewById(R.id.start);
        final Button button2 = (Button) findViewById(R.id.stop);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rsm.startMeasurement();
                t2.setText("Start");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rsm.stopMeasurement();
                t2.setText("Stop");
            }
        });
        mScheduler = Executors.newScheduledThreadPool(1);
        mScheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Sensors s = rsm.testSensor(21);
                        String l = Float.toString(s.getValues()[0]);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "SRP " + Integer.toString(s.getSensorID()) + " " + Integer.toString(s.getAccuracy()) + " " + Long.toString(s.getTimeStamp()) + " " + l);
                        sendIntent.setType("text/plain");
                        startService(sendIntent);
                        t1.setText(l);

                    }
                }, 3, 2, TimeUnit.SECONDS);
    }



}
