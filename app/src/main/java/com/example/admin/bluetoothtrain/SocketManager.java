package com.example.admin.bluetoothtrain;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class SocketManager {

    private Thread thread;
    private BluetoothSocket socket;


    public SocketManager() {

    }

    public void manageConnectedSocket(final BluetoothSocket socket){
        this.socket=socket;
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                
            }
        });

        thread.start();
    }
}
