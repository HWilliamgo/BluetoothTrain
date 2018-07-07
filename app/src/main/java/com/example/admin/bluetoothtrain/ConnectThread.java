package com.example.admin.bluetoothtrain;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {


    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mbluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        mbluetoothAdapter = bluetoothAdapter;

//        tmp = device.createRfcommSocketToServiceRecord(MY_UUID);


        mmSocket = tmp;
    }

    @Override
    public void run() {
        super.run();
        mbluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mmSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
//        manageConnectedSocket(mmSocket);//在独立的线程去管理这个连接
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
