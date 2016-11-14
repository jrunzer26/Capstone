package com.example.android.infotainment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ConnectingActivity extends Activity {
    Button connect;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
        connect = (Button)findViewById(R.id.connect_button);
    }

    public void connect(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(nextScreen);
    }

}
