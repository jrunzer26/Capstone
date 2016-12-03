package com.example.janahan.heartbeatcollector;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;

/**
 * Created by janahan on 08/11/16.
 */

public class SensorRecieverService extends WearableListenerService{
    private static final String TAG = "APPHB/SRS";
    private RemoteSensorManager sensorManager;

    /**
     * Checks if a android wear device disconnects
     * @param peer
     */
    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    /**
     * Standard onCreate method
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = RemoteSensorManager.getInstance(this);
    }

    /**
     * Checks if a android wear device connects
     * @param peer
     */
    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        sensorManager = RemoteSensorManager.getInstance(this);
        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    /**
     * Checks if android wear send changed data
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        int sensor, accuracy;
        long timestamp;
        float [] values;
        DataMap mapData;
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                if (path.startsWith("/sensors/")) {
                    sensor = Integer.parseInt(uri.getLastPathSegment());
                    mapData = DataMapItem.fromDataItem(dataItem).getDataMap();
                    if(mapData != null){
                        accuracy = mapData.getInt("accuracy");
                        timestamp = mapData.getLong("time");
                        values = mapData.getFloatArray("value");
                        sensorManager.addSensorData(sensor, accuracy, timestamp, values);
                    }
                }
            }
        }
    }
}
