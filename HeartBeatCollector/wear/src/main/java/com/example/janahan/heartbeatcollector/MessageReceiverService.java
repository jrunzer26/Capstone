package com.example.janahan.heartbeatcollector;

/**
 * Created by Janahan on 07/11/16.
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

    /**
     * Standard on create method
     */
    @Override
    public void onCreate() {
        super.onCreate();
        deviceClient = DeviceClient.getInstance(this);
    }

    /**
     * Standard onDataChange
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
    }


    /**
     * Waits for start or stop from the client
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, messageEvent.getPath());
        if (messageEvent.getPath().equals("/start")) {
            startService(new Intent(this, SensorService.class));
        }
        if (messageEvent.getPath().equals("/stop")) {
            stopService(new Intent(this, SensorService.class));
        }
    }
}
