package com.example.admin.bluetoothtrain;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.bluetoothtrain.Adapter.Rv_Adapter;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "hanwuji";

    private static final int BLUETOOTH_REQUEST_CODE = 99;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private RecyclerView rv;
    private Rv_Adapter rv_adapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: "+device.getAddress());
                deviceList.add(device);
                rv_adapter.notifyDataSetChanged();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initView();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void registerReceiver(){
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(receiver,filter);
    }


    private void initBluetooth() {
        if (bluetoothAdapter == null) {
            //现在设备一般都支持蓝牙。
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
            }else {
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

        rv=findViewById(R.id.rv);
        rv_adapter=new Rv_Adapter(this,deviceList);
        rv_adapter.setItemClickListener(new Rv_Adapter.OnItemClickListener() {
            @Override
            public void onClick(BluetoothDevice device) {
                //点击Item之后，就要发起连接。
                ConnectThread connectThread=new ConnectThread(device,bluetoothAdapter);
            }
        });
        rv.setAdapter(rv_adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void pairedDevice() {

        List<BluetoothDevice> pairedList = new ArrayList<>(bluetoothAdapter.getBondedDevices());//获取已匹配的设备


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

    private void scanBluetooth(){
        Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
        pairedDevice();
        bluetoothAdapter.startDiscovery();//启动异步进程来发现设备，回调在receiver里面做。
    }
}
