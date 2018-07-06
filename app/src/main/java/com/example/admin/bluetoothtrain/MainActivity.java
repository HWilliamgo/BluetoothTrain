package com.example.admin.bluetoothtrain;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_hanwuji";
    private static final int REQUEST_ENABLE_BT = 10;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {//注册系统广播监听器，监听startDiscovery开启的异步进程发起的广播。
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //当发现了一个设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //从Intent中获取BluetoothDevice对象....
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + "发现蓝牙设备!" + "名字：" + device.getName() + "  地址：" + device.getAddress());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        enableBluetooth();//打开蓝牙

        registerReceiver();//注册广播监听器

        initView();//初始化View

    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "onCreate: " + "设备不支持蓝牙");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Log.d(TAG, "onCreate: " + "蓝牙处于停用状态,尝试开启蓝牙");
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "onCreate: " + "蓝牙开启成功");
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }


    private void initView() {
        findViewById(R.id.btn_startDiscovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean startSuccess=mBluetoothAdapter.startDiscovery();//开启异步进程
                if (startSuccess){
                    Log.d(TAG, "启动发现操作成功");
                }else {
                    Log.d(TAG, "启动发现操作失败");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_ENABLE_BT == requestCode) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + "蓝牙开启成功");


                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice devices : pairedDevices) {
                        Log.d(TAG, "遍历所有已配对的设备：");
                        Log.d(TAG, "名字： " + devices.getName() + "  地址： " + devices.getAddress());
                    }
                }else {
                    Log.d(TAG, "没有发现已配对的设备");
                }

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: " + "蓝牙开启失败");
            }
        }
    }
}
