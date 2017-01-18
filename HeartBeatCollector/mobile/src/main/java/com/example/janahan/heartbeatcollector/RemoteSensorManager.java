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

    /**
     * Create new instance if none exists. Returns new instance if it exists
     * @param context
     * @return the instance of RemoteSensorManager running in the program
     */
    public static synchronized RemoteSensorManager getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteSensorManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Class for RemoteSensorManager
     * @param context
     */
    private RemoteSensorManager(Context context) {
        this.context = context;
        this.sensorMap = new HashMap<Integer,Sensors>();
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        this.executorService = Executors.newCachedThreadPool();
    }

    // TODO: Check if removable
    public Sensors testSensor(int id) {
        Sensors sensor = sensorMap.get(id);
        if (sensor == null) {
            Log.i(TAG,"Created");
            return null;
        }

        return sensor;
    }

    /**
     * Gets the sensor object or creates a new own
     * @param id - the sensor id
     * @return the sensor object the id is associated with
     */
    public Sensors getSensor(int id) {
        Sensors sensor = sensorMap.get(id);
        if (sensor == null) {
            sensor = new Sensors(id);
            Log.i(TAG,"Created");
            sensorMap.put(id,sensor);
        }else{
            Log.i(TAG,"Not Null");
        }
        return sensor;
    }

    /**
     * Updates the sensor object with new values
     * @param sensorType - the sensor id
     * @param accuracy - the accuracy of the sensor
     * @param timestamp - the time the reading was taken
     * @param values - an array of values from the android wear
     */
    public synchronized void addSensorData(int sensorType, int accuracy, long timestamp, float[] values) {
        Sensors sensor = getSensor(sensorType);
        if(values != null){
            sensor.updateSensor(accuracy,timestamp,values);
        }
    }

    /**
     * Checks if there is a connection to the android wear.
     * If not it tries to reconnect.
     * @return Whether the connection is true
     */
    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }
        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return result.isSuccess();
    }

    /**
     * Generic call method to start getting measurements
     */
    public void startMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground("/start");
            }
        });
    }

    /**
     * Generic call method to stop getting measurements
     */
    public void stopMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground("/stop");
            }
        });
    }

    /**
     * Broadcasts messages to android
     * @param path - The type of command to broadcast to wearable nodes
     */
    private void controlMeasurementInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();
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
