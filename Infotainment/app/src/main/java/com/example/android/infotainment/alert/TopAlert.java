package com.example.android.infotainment.alert;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.android.infotainment.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by 100520993 on 11/14/2016.
 */

public class TopAlert implements Alert {

    private WindowManager windowManager;
    private WindowManager.LayoutParams defaultParams;
    private View alertView;
    private LayoutInflater layoutInflater;
    private String message;
    private int type;

    /**
     * Creates an alert that is shown at the top of the device.
     * @param context the current context
     * @param type the type of alert
     * @param message the message to display
     */
    public TopAlert(Context context, int type, String message) {
        this.message = message;
        this.type = type;
        // get the window manager, and set the default parameters for the alert layout
        windowManager = (WindowManager) context.getSystemService(Activity.WINDOW_SERVICE);
        defaultParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, 100, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        defaultParams.gravity = Gravity.TOP;
        // inflate the custom alert layout
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        alertView = layoutInflater.inflate(R.layout.window_top_alert, null);
        int colour = 0;
        // customize the alert based on the alert type
        if (type == AlertSystem.ALERT_TYPE_FATAL)
            colour = ContextCompat.getColor(context, R.color.alertSystem_redWarning);
        else if (type == AlertSystem.ALERT_TYPE_WARNING) {
            colour = ContextCompat.getColor(context, R.color.alertSystem_yellowWarning);
        }
        alertView.findViewById(R.id.linearLayout_alertSystem_container).setBackgroundColor(colour);
        ((TextView)alertView.findViewById(R.id.textview_alertSystem_message)).setText(message);
        // enable the dismiss button
        alertView.findViewById(R.id.button_alertSystem_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }


    /**
     * Adds the alert to the window.
     */
    @Override
    public void show() {
        // post to the main looper UI thread to add to the window
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                windowManager.addView(alertView, defaultParams);
            }
        });

    }

    /**
     * Removes the alert from the window.
     */
    @Override
    public void hide() {
        if (alertView != null) {
            // post to the main looper UI thread to add to the window
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    windowManager.removeView(alertView);
                    alertView = null;
                }
            });
        }
    }

    /**
     * Determines if the two objects are equal.
     * @param object the second object.
     * @return true if the same.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        else if(!(object instanceof TopAlert)) {
            return false;
        } else {
            TopAlert topAlert = (TopAlert) object;
            if (this == topAlert) {
                return true;
            } else if(this.message.equals(topAlert.getMessage()) && this.type == topAlert.getType()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the message of the top alert.
     * @return the message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the type of message.
     * @return the type.
     */
    public int getType() {
        return type;
    }
}
