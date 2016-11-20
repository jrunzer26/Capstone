package com.example.janahan.heartbeatcollector;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.janahan.heartbeatcollector.SensorCnst.Sensors;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemoteSensorManager {
    private static final String TAG = "APPHB/RSM";
    private static final int CLIENT_CONNECTION_TIMEOUT = 150000000;
    private static RemoteSensorManager instance;
    private Context context;
    private ExecutorService executorService;
    private Map<Integer,Sensors> sensorMap ;
    private GoogleApiClient googleApiClient;

    public static synchronized RemoteSensorManager getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteSensorManager(context.getApplicationContext());
        }
        return instance;
    }

    private RemoteSensorManager(Context context) {
        this.context = context;
        this.sensorMap = new HashMap<Integer,Sensors>();
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        this.executorService = Executors.newCachedThreadPool();
    }

    public Sensors testSensor(int id) {
        Sensors sensor = sensorMap.get(id);
        if (sensor == null) {
            return null;
        }
        return sensor;
    }
    private Sensors getSensor(int id) {
        Sensors sensor = sensorMap.get(id);
        if (sensor == null) {
            sensor = new Sensors(id);
            sensorMap.put(id,sensor);
        }
        return sensor;
    }

    public synchronized void addSensorData(int sensorType, int accuracy, long timestamp, float[] values) {
        Sensors sensor = getSensor(sensorType);
        if(values == null){
            Log.d(TAG,"Value Empty");
        }else{
            Log.d(TAG, Integer.toString(sensorType));
            sensor.updateSensor(accuracy,timestamp,values);
        }
        Log.d(TAG, "update " );

        //BusProvider.postOnMainThread(new SensorUpdatedEvent(sensor, dataPoint));
    }

    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            Log.d(TAG, "validate" );
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return result.isSuccess();
    }

    public void startMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground("/start");
            }
        });
    }

    public void stopMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground("/stop");
            }
        });
    }

    private void controlMeasurementInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

            Log.d(TAG, "update " );

            for (Node node : nodes) {
                Log.i(TAG, "add node " + node.getDisplayName());
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            Log.w(TAG, "No connection possible");
        }
    }
}
