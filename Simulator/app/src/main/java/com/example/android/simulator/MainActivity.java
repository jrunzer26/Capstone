package com.example.android.simulator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private VehicleSimulatorFragment vehicleSimulatorFragment;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUUID = UUID.fromString("6804a970-a361-11e6-bdf4-0800200c9a66");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setup();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        getSupportActionBar().hide();
    }


    public void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device;
        Log.i("Setup", "we in here");
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice dev: pairedDevices) {
                device = dev;
                Log.e("Name", device.getName());
                if(device.getName().equals("Jason R (Galaxy Tab4)")){
                    Log.i("Device", "Got in here");
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device, myUUID);
                    myThreadConnectBTdevice.start();
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if (position == 0) {
                vehicleSimulatorFragment = new VehicleSimulatorFragment();
                return vehicleSimulatorFragment;
            } else {
                return new EnvironmentSimulatorFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    public void pause(View view) {
        vehicleSimulatorFragment.pause();
    }

    public void cruise(View view) {
        Toast.makeText(this,"Cruise", Toast.LENGTH_SHORT).show();
    }

    public void park(View view) {
        Toast.makeText(this,"Park", Toast.LENGTH_SHORT).show();
    }

    public void drive(View view) {
        Toast.makeText(this,"drive", Toast.LENGTH_SHORT).show();
    }

    public void sendMessage(View view) {
        String message = "SUH DUDE what it do";
        byte[] array = message.getBytes();
        myThreadConnectBTdevice.connectedThread.write(array);
    }

    public void reverse(View view) {
        Toast.makeText(this,"Reverse", Toast.LENGTH_SHORT).show();
    }

    public void sunny(View view) {
        Toast.makeText(this,"Sunny", Toast.LENGTH_SHORT).show();
    }

    public void hail(View view) {
        Toast.makeText(this,"hail", Toast.LENGTH_SHORT).show();
    }

    public void snow(View view) {
        Toast.makeText(this,"snow", Toast.LENGTH_SHORT).show();
    }

    public void rain(View view) {
        Toast.makeText(this,"rain", Toast.LENGTH_SHORT).show();
    }



}
