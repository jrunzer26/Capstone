package com.example.janahan.heartbeatcollector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.janahan.heartbeatcollector.SensorCnst.SensorData;

import org.w3c.dom.Text;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by 100522058 on 11/20/2016.
 */

public class SimulatedActivity extends Activity{

    final int STEP = 1;
    final int HEART_MAX = 170;
    final int HEART_MIN = 40;
    private int count = 0;

    private SensorData sim;

    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated);
        View view = findViewById(android.R.id.content);
        count = 0;
        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setup();

        sim = new SensorData(myThreadConnectBTdevice);

        final SeekBar seekBarHeartRate = (SeekBar)view.findViewById(R.id.seekBar_heartRate);
        seekBarHeartRate.setMax((HEART_MAX-HEART_MIN)/STEP);

        final TextView seekBarHeartValue = (TextView)view.findViewById(R.id.textView_currentHeartRate);
        seekBarHeartRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarHeartValue.setText(String.valueOf(HEART_MIN+(progress * STEP)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sim.setHeartRate(Integer.parseInt(seekBarHeartValue.getText().toString()));
                count++;
                if (count == 5) {
                    count = 0;
                    sim.run();
                }

            }
        }, 0, 1000);
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
