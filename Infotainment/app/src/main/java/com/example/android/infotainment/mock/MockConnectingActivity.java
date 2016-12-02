package com.example.android.infotainment.mock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.infotainment.MainActivity;
import com.example.android.infotainment.R;

/**
 * Created by 100520993 on 11/12/2016.
 */

/**
 * Mock activity for starting to connect the fake bluetooth devices.
 */
public class MockConnectingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
    }

    public void connect(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MockMainActivity.class);
        startActivity(nextScreen);
    }

}
