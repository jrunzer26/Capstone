package com.example.janahan.heartbeatcollector;

/**
 * Created by 100522058 on 11/20/2016.
 */

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 100522058 on 11/12/2016.
 */

public class ConnectedThread extends Thread {

    //Declares the input and output streams and socket
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final ThreadConnectBTdevice values;


    /**
     * Initializes the input and output stream
     * @param socket - The socket the client is sending and receiving on
     */
    public ConnectedThread(BluetoothSocket socket, ThreadConnectBTdevice temp){
        values = temp;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            //Stores the input stream and output stream into tmpIn and tmpOut
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {}

        //After successfully being able to store the streams into the tmp variables store them into
        //The variables you declared at the start
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    /**
     * Reads messages from the inputStream
     */
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        boolean connected = true;
        //Always running
        while (connected) {
            try {
                //Holds up here until there is something to read
                bytes = mmInStream.read(buffer);
                //Converts the message from bytes into a string
                String readMessage = new String(buffer, 0, bytes);
                System.out.println(readMessage);
            } catch (Exception e) {
                try {
                    mmInStream.close();
                    mmOutStream.close();
                    mmSocket.close();
                    values.isConnected = false;
                    connected = false;
                    values.cancel();
                } catch (IOException w) {

                }
            }
        }
    }

    /**
     * This method is called from the Simulator class and passes in the byte array of all the values
     * from the simulator interface every 5 seconds
     * @param bytes the message you are sending to the server
     */
    public void write(byte[] bytes) {
        try {
            Log.e("Writing", "Message");
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /**
     * Closes the bluetooth socket
     */
    public void cancel() {
        try {
            mmInStream.close();
            mmOutStream.close();
            mmSocket.close();
        } catch (IOException e) {

        }
    }
}
