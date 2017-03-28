package com.example.android.infotainment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.infotainment.backend.BaselineDatabaseHelper;
import com.example.android.infotainment.backend.UserDatabaseHelper;
import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;
import com.example.android.infotainment.backend.models.UserData;
import com.example.android.infotainment.mock.MockMainActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        initBaseline(userDatabaseHelper);
        Toast.makeText(this, "done!", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    private void initBaseline(UserDatabaseHelper userDatabaseHelper) {
        BufferedReader br;
        AssetManager AM = getAssets();
        try{
            ArrayList<SimData> simDatas = new ArrayList<>();
            String line;
            InputStream is = AM.open("baselineInput.csv");
            br= new BufferedReader(new InputStreamReader(is));
            br.readLine();
            int lineCount = 0;
            while((line = br.readLine())!= null){
                simDatas.add(lineCount, new SimData());
                String[] RowData = line.split(",");
                simDatas.get(lineCount).setSpeed((int)Math.round(Double.parseDouble(RowData[0])));
                simDatas.get(lineCount).setSteering(Double.parseDouble(RowData[1]));
                lineCount++;
            }
            lineCount=0;
            for (int i = 0; i < simDatas.size(); i++) {
                Log.i("data", i+"");
                UserData u = new UserData();
                u.setSimData(simDatas.get(i));
                u.setSensorData(new SensorData());
                userDatabaseHelper.insertSimData(u);
            }
        } catch(FileNotFoundException e){
            System.out.println("File not found!\n" + e);
        } catch (IOException e){
            System.out.println("Inputstream failed\n" + e);
        }
    }

    public void csv(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), MockMainActivity.class);
        startActivity(nextScreen);
    }
}
