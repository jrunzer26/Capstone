package com.example.janahan.heartbeatcollector;

/**
 * Created by janahan on 07/11/16.
 */
import android.content.Context;
import android.util.Log;
import android.util.SparseLongArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeviceClient {
    private static final String TAG = "APPWB/DeviceC";
    private static final int TIMEOUT = 1500000000;

    public static DeviceClient instance;
    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            Log.v(TAG, "Start");
            instance = new DeviceClient(context.getApplicationContext());
        }

        return instance;
    }

    private Context context;
    private GoogleApiClient gaClient;
    private ExecutorService eService;
    private SparseLongArray lastSensorData;

    private DeviceClient(Context context) {
        Log.v(TAG, "New Client");
        this.context = context;
        gaClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        eService = Executors.newCachedThreadPool();
        lastSensorData = new SparseLongArray();
    }

    public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        Log.v(TAG, "Send data");
        long t = System.currentTimeMillis();
        long lastTimestamp;
        try{
            lastTimestamp = lastSensorData.get(sensorType);
        }catch(NullPointerException e){
            lastTimestamp = 0;
        }
        long timeAgo = t - lastTimestamp;
        lastSensorData.put(sensorType, t);
        eService.submit(new Runnable() {
            @Override
            public void run() {
                sendSensorDataInBackground(sensorType, accuracy, timestamp, values);
            }
        });
    }

    private void sendSensorDataInBackground(int sensorType, int accuracy, long timestamp, float[] values) {

        Log.v(TAG, "send data background");
        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);
        dataMap.getDataMap().putInt("accuracy", accuracy);
        dataMap.getDataMap().putLong("time", timestamp);
        dataMap.getDataMap().putFloatArray("value", values);
        Log.v(TAG,"Values"+ Float.toString(values[0]));
        PutDataRequest putDataRequest = dataMap.asPutDataRequest();
        send(putDataRequest);
    }

    private boolean validateConnection() {
        if (gaClient.isConnected()) {

            Log.v(TAG, "Connected");
            return true;
        }

        ConnectionResult result = gaClient.blockingConnect(TIMEOUT, TimeUnit.MILLISECONDS);
        return result.isSuccess();
    }

    private void send(PutDataRequest putDataRequest) {
        if (validateConnection()) {
            Wearable.DataApi.putDataItem(gaClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.v(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());

                }
            });
        }
    }
}
