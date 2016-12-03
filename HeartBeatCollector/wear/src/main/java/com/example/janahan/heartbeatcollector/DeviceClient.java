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

    /**
     * Creates or returns the Device Client instance
     * @param context
     * @return the current global instance of device client
     */
    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }

        return instance;
    }

    private Context context;
    private GoogleApiClient gaClient;
    private ExecutorService eService;
    private SparseLongArray lastSensorData;

    /**
     * Class for Device client
     * @param context
     */
    private DeviceClient(Context context) {
        this.context = context;
        gaClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        eService = Executors.newCachedThreadPool();
        lastSensorData = new SparseLongArray();
    }

    /**
     * Class to prepare data to be sent
     * @param sensorType - the sensor id
     * @param accuracy - the accuracy of the sensor
     * @param timestamp - the time the reading was taken
     * @param values - an array of values from the android wear
     */
    public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        long t = System.currentTimeMillis();
        lastSensorData.put(sensorType, t);
        eService.submit(new Runnable() {
            @Override
            public void run() {
                sendSensorDataInBackground(sensorType, accuracy, timestamp, values);
            }
        });
    }

    /**
     * Sends the sensor data to mobile device in the background
     * @param sensorType - the sensor id
     * @param accuracy - the accuracy of the sensor
     * @param timestamp - the time the reading was taken
     * @param values - an array of values from the android wear
     */
    private void sendSensorDataInBackground(int sensorType, int accuracy, long timestamp, float[] values) {

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);
        dataMap.getDataMap().putInt("accuracy", accuracy);
        dataMap.getDataMap().putLong("time", timestamp);
        dataMap.getDataMap().putFloatArray("value", values);
        PutDataRequest putDataRequest = dataMap.asPutDataRequest();
        send(putDataRequest);
    }

    /**
     * Checks if the watch is connected to the mobile
     * @return if the android wear is connected to the mobile
     */
    private boolean validateConnection() {
        if (gaClient.isConnected()) {
            return true;
        }
        ConnectionResult result = gaClient.blockingConnect(TIMEOUT, TimeUnit.MILLISECONDS);
        return result.isSuccess();
    }

    /**
     * The sending service
     * @param putDataRequest
     */
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
