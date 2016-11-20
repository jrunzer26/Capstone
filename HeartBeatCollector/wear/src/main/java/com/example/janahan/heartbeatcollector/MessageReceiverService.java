package com.example.janahan.heartbeatcollector;

/**
 * Created by janahan on 07/11/16.
 */
import android.content.Intent;
import android.hardware.Sensor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "APPWB/MRS";
    private DeviceClient deviceClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Create Client");
        deviceClient = DeviceClient.getInstance(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.v(TAG, "Data Change");
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, messageEvent.getPath());
        if (messageEvent.getPath().equals("/start")) {
            Log.v(TAG, "Recieved");
            startService(new Intent(this, SensorService.class));
        }

        if (messageEvent.getPath().equals("/stop")) {
            stopService(new Intent(this, SensorService.class));
        }
    }
}
