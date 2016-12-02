package com.example.android.simulator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 100522058 on 11/12/2016.
 * Purpose of this activity is to make this app wait until the server is setup and running
 */

public class ConnectToServerActivity extends Activity{
    Button connect;

    /**
     * Shows a button
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        connect = (Button)findViewById(R.id.connect_button);
    }

    /**
     * Loads the main Activity on button click
     * @param view
     */
    public void connect(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        //When the button is click it will take you to the main activity and start up the connection to the server
        startActivity(nextScreen);
    }
}
