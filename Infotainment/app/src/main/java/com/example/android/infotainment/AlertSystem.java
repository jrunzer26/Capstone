package com.example.android.infotainment;

import android.content.Context;

/**
 * Created by 100520993 on 10/31/2016.
 */

public class AlertSystem {

    /* ALERT TYPES */
    public static final int ALERT_TYPE_WARNING = 1;
    public static final int ALERT_TYPE_FATAL = 2;
    /* DEFAULT MESSAGES */
    private static final String DEFAULT_MESSAGE_WARNING = "Warning! Dangerous Driving Detected.";
    private static final String DEFAULT_MESSAGE_FATAL = "CAUTION. Aggressive and Dangerous Driving detected";
    private static final String DEFAULT_MESSAGE_LIGHT = "Unusual aggressive driving detected.";

    /**
     * Alerts the driver.
     * @param context the current context
     * @param type the type of warning
     */
    public void alert(Context context, int type) {
        // TODO: 10/31/2016 alert system 
    }
    /**
     * Alerts the driver.
     * @param context the current context
     * @param type the type of warning
     * @param message
     */
    public void alert(Context context, int type, String message){
        // TODO: 10/31/2016 alert system
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
}
