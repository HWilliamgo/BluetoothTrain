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
import android.os.ParcelUuid;
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

import com.example.admin.bluetoothtrain.Adapter.RvPairedDeviceAdapter;
import com.example.admin.bluetoothtrain.Adapter.Rv_Adapter;
import com.example.admin.bluetoothtrain.Utils.BluetoothProfileConnector;
import com.example.admin.bluetoothtrain.Utils.BluetoothUuidHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "hanwuji";

    private static final int BLUETOOTH_REQUEST_CODE = 99;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private List<BluetoothDevice> deviceList = new ArrayList<>();//由startDiscovery发现的设备
    private List<BluetoothDevice> pairedDeviceList = new ArrayList<>();//配对了的设备
    private List<Integer> profileList;//设备所支持的Profile数组

    private Rv_Adapter rv_adapter;
    private RvPairedDeviceAdapter rvPairedDeviceAdapter;

    /**
     * 广播监听器，监听系统因为从远程设备获取到信息而发出的回调。
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//发现设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
                rv_adapter.notifyDataSetChanged();
            }
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (BluetoothDevice.BOND_BONDED == intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)) {//绑定成功
                    BluetoothDevice pairedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//取得绑定设备
                    pairedDeviceList.add(pairedDevice);//将绑定设备保存到数组中。
                    rvPairedDeviceAdapter.notifyDataSetChanged();//更新列表
                    Log.d(TAG, "onReceive: 新匹配到设备："+pairedDevice.toString());
                }
            }
            if (BluetoothDevice.ACTION_UUID.equals(action)){//要在这里发起连接设备的操作（根据设备支持的Profile）
                BluetoothDevice deviceWithUuid=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ParcelUuid[] uuids=deviceWithUuid.getUuids();//获取设备支持的UUID数组
                profileList=BluetoothUuidHelper.uuidsToProfile(uuids);
                Log.d(TAG, "onReceive: "+deviceWithUuid.toString());
                for (Integer profile:profileList){
                    Log.d(TAG, "onReceive: 设备支持的Profile"+profile);
                }
                //connect remote device with the Profile we get
                BluetoothProfileConnector.connect(deviceWithUuid,profileList,bluetoothAdapter,context);
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
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: 请求权限成功");
                } else {
                    Toast.makeText(this, "权限请求失败，app无法正常工作", Toast.LENGTH_SHORT).show();
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
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//监听：发现到设备,由BluetoothAdapter#startDiscovery发起
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//监听：配对设备，由BlueToothDevice#createBond()发起
        filter.addAction(BluetoothDevice.ACTION_UUID);//监听：获取远程设备支持的UUID，由BluetoothDevice#fetchUuidsWithSdp()发起
        registerReceiver(receiver, filter);
    }

    private void initBluetooth() {
        if (bluetoothAdapter == null) {
            //小武是支持蓝牙的。
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

        RecyclerView rv = findViewById(R.id.rv);
        rv_adapter = new Rv_Adapter(this, deviceList);
        rv_adapter.setItemClickListener(new Rv_Adapter.OnItemClickListener() {
            @Override
            public void onClick(BluetoothDevice device) {
                //配对设备
                boolean beginBond=device.createBond();
                if (beginBond){
                    Toast.makeText(BluetoothActivity.this, "开始配对", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(BluetoothActivity.this, "配对失败", Toast.LENGTH_SHORT).show();
                }

                //获取远程设备支持的UUID数组。在BroadcastReceiver中处理系统回调。
                device.fetchUuidsWithSdp();

            }
        });
        rv.setAdapter(rv_adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView rvPairedDevice = findViewById(R.id.rv_paired);
        rvPairedDeviceAdapter=new RvPairedDeviceAdapter(pairedDeviceList,this);
        rvPairedDevice.setAdapter(rvPairedDeviceAdapter);
        rvPairedDevice.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getPairedDevice() {//取得已经匹配的设备
        pairedDeviceList.addAll(bluetoothAdapter.getBondedDevices());
        rvPairedDeviceAdapter.notifyDataSetChanged();
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
        getPairedDevice();
        boolean discovery = bluetoothAdapter.startDiscovery();//启动异步进程来发现设备，回调在receiver里面做。
        if (discovery) {
            Log.d(TAG, "scanBluetooth: 开始扫描");
        } else {
            Log.d(TAG, "scanBluetooth: 未开始扫描");
        }
    }
}
