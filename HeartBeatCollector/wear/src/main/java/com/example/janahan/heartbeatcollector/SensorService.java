package com.example.janahan.heartbeatcollector;

/**
 * Created by janahan on 07/11/16.
 */
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "APPWB/SS";
    private final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;
    SensorManager mSensorM;
    private Sensor mHeartrateS;
    private DeviceClient client;
    private ScheduledExecutorService mScheduler;

    /**
     * Standard onCreate
     * Starts up schedulers for sensor
     */
    @Override
    public void onCreate() {
        super.onCreate();
        client = DeviceClient.getInstance(this);
        mSensorM = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartrateS = mSensorM.getDefaultSensor(SENS_HEARTRATE);
        if (mSensorM != null) {
            if (mHeartrateS != null) {
                    final int measurementDuration   = 10;   // Seconds
                    final int measurementBreak      = 5;    // Seconds
                    mScheduler = Executors.newScheduledThreadPool(1);
                    mScheduler.scheduleAtFixedRate(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mSensorM.registerListener(SensorService.this, mHeartrateS, SensorManager.SENSOR_DELAY_NORMAL);
                                    try {
                                        Thread.sleep(measurementDuration * 1000);
                                    } catch (InterruptedException e) {
                                        Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
                                    }
                                    mSensorM.unregisterListener(SensorService.this, mHeartrateS);
                                }
                            }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);

                } else {
                    Log.d(TAG, "No Heartrate Sensor found");
                }

        }
    }

    /**
     * Standard onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement();
    }

    /**
     * Standard onBind
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Stop recording a sensors value
     */
    private void stopMeasurement() {
        if (mSensorM != null) {
            mSensorM.unregisterListener(this);
        }
        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
    }

    /**
     * Checks if the sensor data changed
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
