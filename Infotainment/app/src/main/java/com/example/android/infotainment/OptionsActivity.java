package com.example.android.infotainment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.infotainment.backend.BaselineDatabaseHelper;
import com.example.android.infotainment.backend.UserDatabaseHelper;
import com.example.android.infotainment.mock.MockMainActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void bluetooth(View view){
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(nextScreen);
    }

    public void baselineInit(View view) {
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this);
        BaselineDatabaseHelper baselineDatabaseHelper = new BaselineDatabaseHelper(this);
        baselineDatabaseHelper.clearAllTables();
        userDatabaseHelper.clearAll();
        Intent nextScreen = new Intent(getApplicationContext(), MockMainActivity.class);
        startActivity(nextScreen);
    }

    public void csv(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), MockMainActivity.class);
        startActivity(nextScreen);
    }
}
