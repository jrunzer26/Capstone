package com.example.android.infotainment.alert;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.android.infotainment.R;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class AlertSystem {

    /* ALERT TYPES */
    public static final int ALERT_TYPE_WARNING = 1;
    public static final int ALERT_TYPE_FATAL = 2;
    private Alert currentAlert = null; //stores the current alert
    private int extendedTime = 0;
    private final int ALERT_TIME = 10000; // seconds
    private SoundPool soundPool;
    private int fatalSound;

    // Default messages in strings

    public AlertSystem(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        fatalSound = soundPool.load(context, R.raw.alert, 1);
    }
    /**
     * Alerts the driver.
     * @param context the current context
     * @param type the type of warning
     */

    public void alert(Context context, int type) {
        String message =  context.getResources().getString(R.string.alertSystem_default_warning);
        alert(context, type, message);
    }
    /**
     * Alerts the driver.
     * @param context the current context
     * @param type the type of warning
     * @param message
     */
    public void alert(final Context context, int type, String message){

        if (currentAlert == null) {
            currentAlert = new TopAlert(context, type, message);
            currentAlert.show();
            soundPool.play(fatalSound, 1.0f, 1.0f, 1, 0, 0);
            new Timer(currentAlert).start();
        } else {
            if (!currentAlert.getMessage().equals(message)) {
                currentAlert.hide();
                currentAlert = new TopAlert(context, type, message);
                currentAlert.show();
                new Timer(currentAlert).start();
            } else {
                System.out.println("extending time");
                extendedTime = ALERT_TIME;
            }
        }
    }

    /**
     * Alerts the driver including HR.
     * @param context the current context
     * @param type the type of warning - ALERT_TYPE_WARNING, ALERT_TYPE_FATAL
     * @param message the message to the user
     * @param heartRate the current heart rate of the user
     */
    public void alert(Context context, int type, String message, int heartRate) {
        // TODO: 10/31/2016 alert system
    }

    /**
     * Makes the alerts dissapear after a set amount of time unless the alert is created again.
     */
    private class Timer extends Thread {
        private Alert alert;
        public Timer (Alert alert) {
            this.alert = alert;
        }
        @Override
        public void run() {
            super.run();
            extendedTime = ALERT_TIME;
            System.out.println("starting timer: " + extendedTime);
            while(extendedTime > 0) {
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                extendedTime -= ALERT_TIME;
                System.out.println("extended time: " + extendedTime);
            }
            if (currentAlert != null && currentAlert.equals(alert)) {
                currentAlert.hide();
                currentAlert = null;
            }
        }
    }
}

