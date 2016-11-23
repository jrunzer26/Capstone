package com.example.android.infotainment.mock;

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

public class MockConnectingActivity extends Activity {
    Button connect;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
        connect = (Button)findViewById(R.id.connect_button);
    }

    public void connect(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MockMainActivity.class);
        startActivity(nextScreen);
    }

}
