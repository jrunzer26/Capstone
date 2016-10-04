package com.example.android.bluetoothsniffer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enableBluetooth(View view) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView textview_bluetooth = (TextView) findViewById(R.id.textview_bluetooth);
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            textview_bluetooth.setText("Bluetooth enabled");
        }

        if (mBluetoothAdapter.isEnabled()) {
            showMacAddresses(mBluetoothAdapter, textview_bluetooth);
        }
    }

    private void showMacAddresses(BluetoothAdapter adapter, TextView textView) {
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                textView.setText(textView.getText() + "\n" + device.getName() + " " + device.getAddress());
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            TextView textview_bluetooth = (TextView) findViewById(R.id.textview_bluetooth);
            if (resultCode == RESULT_OK) {
                textview_bluetooth.setText("Bluetooth Enabled");
            } else {
                textview_bluetooth.setText("Bluetooth Disabled");
            }

        }
    }
}

class BluetoothServer extends Thread {
    private final BluetoothServerSocket socket;
    private final String

    public BluetoothServer {
        BluetoothServerSocket tmp = null;
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        }
    }
}
