package com.example.janahan.heartbeatcollector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    //Setting up the variables for the slider
    final int STEP = 1;
    final int HEART_MAX = 170;
    final int HEART_MIN = 40;
    private int count = 0;
    Button connectButton;
    private SensorData sim;

    /**
     * Declaring the bluetooth variables
     */
    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;

    /**
     * Initialize bluetooth variables and set up Slider
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated);
        View view = findViewById(android.R.id.content);
        count = 0;

        connectButton = (Button)findViewById(R.id.button_connect);

        //Assigning the UUID key
        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setup();

        //Creating the simulator object
        sim = new SensorData(myThreadConnectBTdevice);

        final SeekBar seekBarHeartRate = (SeekBar)view.findViewById(R.id.seekBar_heartRate);
        seekBarHeartRate.setMax((HEART_MAX-HEART_MIN)/STEP);

        final TextView seekBarHeartValue = (TextView)view.findViewById(R.id.textView_currentHeartRate);
        seekBarHeartRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Runs when heartRate seekbar changes
             * @param seekBar
             * @param progress - The current value of the heartRate slider
             * @param fromUser
             */
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

        //Sends the heart Rate data to the simulator object and at 5 seconds excute the run method in the sim object
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
        }, 1000, 1000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateConnection();
            }
        }, 1000, 1000);

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
     * Looks for the server in a list of paired bluetooth on that device
     */
    public void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device;
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice dev: pairedDevices) {
                device = dev;
                if(device.getName().equals("Jason R (Galaxy Tab4)")){
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device, myUUID);
                    myThreadConnectBTdevice.start();
                    break;
                }
            }
        }
    }
}
