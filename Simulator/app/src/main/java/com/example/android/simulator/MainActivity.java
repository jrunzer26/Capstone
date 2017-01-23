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

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.simulator.backend.Simulator;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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

    /**
     * Declaring the bluetooth variables
     */
    UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    BluetoothAdapter bluetoothAdapter;
    private Simulator sim;

    /**
     * Initialize bluetooth variables and set up Toolbar
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(android.R.id.content);

        //Assigning the UUID key
        myUUID = UUID.fromString("5fadfabe-166f-4607-a872-4a84c3546adb");
        //Creating the bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setup();


        //Creating the simulator object
        System.out.println("The value of mtThread shit is: "+ myThreadConnectBTdevice.connectedThread);
        sim = new Simulator(this, view, myThreadConnectBTdevice);
        System.out.println("The value of mtThread shit is: "+ myThreadConnectBTdevice.connectedThread);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        getSupportActionBar().hide();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sim.run();
            }
        }, 2000, 1000);

    }

    /**
     * Closes the connection to the bluetooth server
     */
    @Override
    protected void onStop() {
        super.onStop();
        //Closes the connection to the bluetooth server
        myThreadConnectBTdevice.cancel();
    }

    /**
     * Looks for the server in a list of paired bluetooth on that device
     */
    public void setup() {
        //Retreives all the paired bluetooth devices on the device
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device;
        if(pairedDevices.size() > 0) {
            //Goes though all the bluetooth devices that are paired on the device being used
            for (BluetoothDevice dev: pairedDevices) {
                device = dev;
                //Checks if one of the devices is "Jason R (Galaxy Tab4) -> this is the server tablet
                if(device.getName().equals("Jason R (Galaxy Tab4)")){
                    //Creates an object and passes in the server's tablet name and it's UUID
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device, myUUID);
                    //Starts the thread in the ThreadConnectBTdevice object
                    myThreadConnectBTdevice.start();
                    break;
                }
            }
        }
    }

    /**
     * Adds items to the action bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks
     * @param item
     * @return the fragment
     */
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
     *
     * @return the simulator object
     */
    public Simulator getSimulator(){
        return this.sim;
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * instantiate the fragment for the given page
         * @param position
         * @return Placeholder Fragment
         */
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

        /**
         * Shows only two page sections
         * @return the total number of page sections
         */
        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    /**
     * Calls simulator pause function
     * @param view
     */
    public void pause(View view) {
        sim.pause();
        vehicleSimulatorFragment.pause();
    }

    /**
     * Calls simulator cruise function
     * @param view
     */
    public void cruise(View view) {
        sim.cruise();

        Toast.makeText(this,"Cruise", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator park function
     * @param view
     */
    public void park(View view) {
        sim.park();
        Toast.makeText(this,"Park", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator drive function
     * @param view
     */
    public void drive(View view) {
        sim.drive();
        Toast.makeText(this,"drive", Toast.LENGTH_SHORT).show();
    }


    public void reverse(View view) {
        sim.reverse();
        Toast.makeText(this,"Reverse", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateSunny function
     * @param view
     */
    public void sunny(View view) {
        sim.climateSuuny();
        Toast.makeText(this,"Sunny", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateHail function
     * @param view
     */
    public void hail(View view) {
        sim.climateHail();
        Toast.makeText(this,"hail", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateSnowy function
     * @param view
     */
    public void snow(View view) {
        sim.climateSnowy();
        Toast.makeText(this,"snow", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateRain function
     * @param view
     */
    public void rain(View view) {
        sim.climateRain();
        Toast.makeText(this,"rain", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateIce function
     * @param view
     */
    public void ice(View view) {
        sim.roadConditionIce();
        Toast.makeText(this,"ice", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator warm_ice function
     * @param view
     */
    public void warm_ice(View view) {
        sim.roadConditionWarmIce();
        Toast.makeText(this,"warm_ice", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator climateSnowy function
     * @param view
     */
    public void wet(View view) {
        sim.roadConditionWet();
        Toast.makeText(this,"wet", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator roadTypePaved function
     * @param view
     */
    public void paved(View view) {
        sim.roadTypePaved();
        Toast.makeText(this,"paved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator roadTypeDirt function
     * @param view
     */
    public void dirt(View view) {
        sim.roadTypeDirt();
        Toast.makeText(this,"dirt", Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls simulator roadTypeGravel function
     * @param view
     */
    public void gravel(View view) {
        sim.roadTypeGravel();
        Toast.makeText(this,"gravel", Toast.LENGTH_SHORT).show();
    }

}
