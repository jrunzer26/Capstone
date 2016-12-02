package com.example.janahan.heartbeatcollector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.janahan.heartbeatcollector.SensorCnst.SensorData;
import com.example.janahan.heartbeatcollector.SensorCnst.Sensors;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RemoteSensorManager rsm;
    private ScheduledExecutorService mScheduler;

    private SensorData sim;
    private int count = 0;

    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;


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

        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //setup();

        sim = new SensorData(myThreadConnectBTdevice);


      /*  new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Sensors s = rsm.testSensor(21);
                    int heartRate = (int) Math.round(s.getValues()[0]);
                    t1.setText(Integer.toString(heartRate));
                    System.out.println(heartRate);
                    sim.setHeartRate(heartRate);
                    count++;
                    if (count == 5) {
                        count = 0;
                        sim.run();
                    }
                } catch(Exception e) {
                    System.out.println("The system is not receiving a heart rate from the watch");
                }
            }
        }, 0, 1000);*/


        mScheduler = Executors.newScheduledThreadPool(1);
        mScheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        //use the two lines below this one for the data
                       // System.out.println("Just before the sensor");
                        Sensors s = rsm.testSensor(21);
                       // System.out.println("The value of s is: "+ s);
                        if(s == null) {
                          //  System.out.println("In the null statement");
                        } else {
                            String l = Float.toString(s.getValues()[0]);
                           // System.out.println("The value of l is: "+ l);
                            Intent sendIntent = new Intent();
                           // System.out.println("after the intent");
                            sendIntent.setAction(Intent.ACTION_SEND);
                           // System.out.println("send INTENT");
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "SRP " + Integer.toString(s.getSensorID()) + " " + Integer.toString(s.getAccuracy()) + " " + Long.toString(s.getTimeStamp()) + " " + l);
                          //  System.out.println("After the put extra");
                            sendIntent.setType("text/plain");
                           // System.out.println("Set type");
                            startService(sendIntent);
                          //  System.out.println("Start Service");
                            t1.setText(l);
                           // System.out.println("Setting the text");
                        }

                    }
                }, 3, 2, TimeUnit.SECONDS);
    }

    public void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device;
        Log.i("Setup", "we in here");
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice dev: pairedDevices) {
                device = dev;
                Log.e("Name", device.getName());
                if(device.getName().equals("Jason R (Galaxy Tab4)")){
                    Log.i("Device", "Got in here");
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device, myUUID);
                    myThreadConnectBTdevice.start();
                    break;
                }
            }
        }
    }



}
