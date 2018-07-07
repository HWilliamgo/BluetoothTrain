package com.example.admin.bluetoothtrain;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;
import java.util.jar.Attributes;

public class AcceptThread extends Thread {
    private static final String NAME= "hanwuji";
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread (BluetoothAdapter bluetoothAdapter){
        BluetoothServerSocket tmp=null;

        try {
//            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord()
            tmp=bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID.fromString(NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmServerSocket=tmp;
    }


    @Override
    public void run() {
        BluetoothSocket  socket=null;
        while (true){
            try{
                socket=mmServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            if (socket!=null){
//                manageConnectedSocket(socket);//在一个单独的线程再去操作这个socket
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                break;
            }

        }
    }

    public void cancel(){
        try{
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
