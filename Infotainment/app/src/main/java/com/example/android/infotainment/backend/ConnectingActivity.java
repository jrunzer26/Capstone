package com.example.android.infotainment.backend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.infotainment.MainActivity;
import com.example.android.infotainment.R;

/**
 * Created by 100522058 on 11/12/2016.
 */

/**
 * First activity to start the connection process.
 */
public class ConnectingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
    }

    /**
     * Starts the main activity to start the bluetooth connection.
     * @param view
     */
    public void connect(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(nextScreen);
    }

}
