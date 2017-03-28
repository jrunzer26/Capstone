package com.example.android.simulator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

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

        try {
            String[] files = getAssets().list("");
            ArrayList<String> prunedFiles = new ArrayList<>();
            for (String file : files) {
                if (file.contains(".csv")) {
                    prunedFiles.add(file);
                }
            }
            ArrayAdapter<String> textFiles = new ArrayAdapter<>(ConnectToServerActivity.this, android.R.layout.simple_spinner_item, prunedFiles);
            textFiles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner s = (Spinner) findViewById(R.id.spinner_csv);
            s.setAdapter(textFiles);
        } catch(Exception e) {

        }


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

    public void csvConnect(View view) {
        Spinner s = (Spinner) findViewById(R.id.spinner_csv);
        String file = (String) s.getSelectedItem();
        //Toast.makeText(this, file, Toast.LENGTH_LONG).show();
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        nextScreen.putExtra("FILE_NAME", file);
        //When the button is click it will take you to the main activity and start up the connection to the server
        startActivity(nextScreen);
    }
}
