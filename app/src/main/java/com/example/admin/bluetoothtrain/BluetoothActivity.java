package com.example.admin.bluetoothtrain;

import android.Manifest;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.bluetoothtrain.Adapter.Rv_Adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "hanwuji";

    private static final int BLUETOOTH_REQUEST_CODE = 99;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothA2dp a2dp;

    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private RecyclerView rv;
    private Rv_Adapter rv_adapter;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getAddress());
                deviceList.add(device);
                rv_adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        registerReceiver();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}
                    , 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: 请求权限成功");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }


    private void initBluetooth() {
        if (bluetoothAdapter == null) {
            //现在设备一般都支持蓝牙。
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
            } else {
                scanBluetooth();
            }
        }
    }

    private void initView() {
        Button btnScanBluetooth = findViewById(R.id.btn_scan);
        btnScanBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBluetooth();
            }
        });

        rv = findViewById(R.id.rv);
        rv_adapter = new Rv_Adapter(this, deviceList);
        rv_adapter.setItemClickListener(new Rv_Adapter.OnItemClickListener() {
            @Override
            public void onClick(BluetoothDevice device) {
                connectDevice(device);
            }
        });
        rv.setAdapter(rv_adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void connectDevice(final BluetoothDevice device){
        bluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (BluetoothProfile.A2DP==profile){
                    a2dp= (BluetoothA2dp) proxy;
                    try{
                        Method connectMethod=BluetoothA2dp.class.getMethod(
                                "connect",BluetoothDevice.class
                        );
                        connectMethod.invoke(a2dp,device);
                    }catch (Exception e){
                        Log.d(TAG, "onServiceConnected: 蓝牙连接出错");
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                a2dp=null;
            }
        },BluetoothProfile.A2DP);
    }

    private void pairedDevice() {

        List<BluetoothDevice> pairedList = new ArrayList<>(bluetoothAdapter.getBondedDevices());//获取已匹配的设备
        for (BluetoothDevice device : pairedList) {
            Log.d(TAG, "pairedDevice: " + device.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                scanBluetooth();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "未打开蓝牙，无法使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanBluetooth() {
        Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
        pairedDevice();
        boolean discovery = bluetoothAdapter.startDiscovery();//启动异步进程来发现设备，回调在receiver里面做。
        if (discovery) {
            Log.d(TAG, "scanBluetooth: 开始扫描");
        } else {
            Log.d(TAG, "scanBluetooth: 未开始扫描");
        }
    }
}
