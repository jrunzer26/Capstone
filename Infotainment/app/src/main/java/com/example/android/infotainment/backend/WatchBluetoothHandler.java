package com.example.android.infotainment.backend;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.example.android.infotainment.backend.models.SensorData;
import com.example.android.infotainment.backend.models.SimData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class WatchBluetoothHandler extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private final DataInputStream mmDataIS;
    private final BluetoothServerSocket bluetoothServerSocket;

    private final Context act;
    private DataParser dataParser;

    /**
     * Reads incoming data from the watch.
     * @param socket the bluetooth socket
     * @param mainContext the current context
     * @param dataParser the parser to send data to
     */
    public WatchBluetoothHandler(BluetoothSocket socket, Context mainContext, DataParser dataParser, BluetoothServerSocket bluetoothServerSocket){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.bluetoothServerSocket = bluetoothServerSocket;
        act = mainContext;
        this.dataParser = dataParser;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {}
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mmDataIS = new DataInputStream(mmInStream);
    }

    /**
     * Run in the background to collect data from the watch when it is available.
     */
    public void run() {
        // send a keep alive to the watch every second to ensure it does not quit
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String keepAlive = "~";
                    mmOutStream.write(keepAlive.getBytes());
                } catch (IOException e) {}
            }
        }, 0, 1000);

        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                // read in the packaged data from the watch
                for (int i = 0; i < 5; i++) {
                    if (bytes > 0) {
                        byte[] bfcopy = new byte[bytes];
                        System.arraycopy(buffer, 0, bfcopy, 0, bytes);
                        ByteArrayInputStream bais = new ByteArrayInputStream(bfcopy);
                        DataInputStream dis = new DataInputStream(bais);
                        // package the data into an object
                        SensorData sensorData = new SensorData();
                        sensorData.setHeartRate(dis.readInt());
                        // send the data each time to the analyst
                        System.out.println(sensorData);
                        dataParser.sendHRData(sensorData);
                    } else {
                        System.out.println("Not receiving any Data");
                    }
                }
            } catch (IOException e) {
                try {
                    System.out.println("Waiting for a connection");
                    mmSocket = bluetoothServerSocket.accept();
                    mmInStream = mmSocket.getInputStream();
                    mmOutStream = mmSocket.getOutputStream();

                } catch(IOException s) {
                    System.out.println("Hopefully not in here");
                }
            }
        }
    }

    /**
     * Writes data to the watch.
     * @param bytes - the bytes to write
     */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e){}
    }

    /**
     * Closes the connection
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {}
    }
}
