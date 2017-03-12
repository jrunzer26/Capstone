package com.example.janahan.heartbeatcollector;

import android.app.Activity;
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
    private String TAG = "APPHB/MA";
    private SensorData sim;
    private int count = 0;
    Button connectButton;
    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;

    /**
     * Startup for the Wearable Sensor data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rsm = RemoteSensorManager.getInstance(this);

        final Button button1 = (Button) findViewById(R.id.start);
        final Button button2 = (Button) findViewById(R.id.stop);
        connectButton = (Button) findViewById(R.id.button_connection);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rsm.startMeasurement();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rsm.stopMeasurement();
            }
        });

        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setup();
        sim = new SensorData(myThreadConnectBTdevice);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Sensors s = rsm.getSensor(21);
                if(s == null) {
                    Log.d(TAG,"Null");
                    //  System.out.println("In the null statement");
                } else {
                    if(s.getValues() != null) {
                        int l = (int) s.getValues()[0];
                        sim.setHeartRate(l);
                        System.out.println(l);
                        count++;
                        if (count == 5) {
                            count = 0;
                            sim.run();
                        }
                      //  t1.setText(l);
                    }
                }
            }
        }, 0, 100);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateConnection();
            }
        }, 2000, 1000);

        connectButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                BluetoothDevice device;
                if(pairedDevices.size() > 0) {
                    for (BluetoothDevice dev: pairedDevices) {
                        device = dev;
                        if(device.getName().equals("Jason R (Galaxy Tab4)")){
                            myThreadConnectBTdevice.reconnect(device, myUUID);
                            break;
                        }
                    }
                }
            }
        });

    }


    private void updateConnection() {
        Activity temp = this;
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(myThreadConnectBTdevice.getIsConnected()) {
                    connectButton.setText("Connected");
                } else {
                    connectButton.setText("Connect");
                }
            }
        });
    }
    /**
     * Initial set up for blue tooth
     */
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
