package com.example.admin.bluetoothtrain.Utils;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class BluetoothProfileConnector {
    public static void connect(final BluetoothDevice device, List<Integer> profileList, BluetoothAdapter adapter, final Context context) {
        if (profileList == null || profileList.size() == 0) {
            return;
        }

        for (int i = 0; i < profileList.size(); i++) {
            Integer profile = profileList.get(i);

            switch (profile) {
                case BluetoothProfile.A2DP://连接蓝牙音响
                    adapter.cancelDiscovery();
                    adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                        @Override
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {//当proxy连接到远程服务时被回调。
                            BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
                            try {
                                Method connectMethod = BluetoothA2dp.class.getMethod(
                                        "connect", BluetoothDevice.class
                                );
                                connectMethod.invoke(a2dp, device);
                            } catch (Exception e) {
                                Toast.makeText(context, "蓝牙连接出错", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onServiceDisconnected(int profile) {
                            Toast.makeText(context, "蓝牙连接断开", Toast.LENGTH_SHORT).show();
                        }
                    }, BluetoothProfile.A2DP);
                    break;


                case BluetoothProfile.HEADSET://连接蓝牙耳机
                    adapter.cancelDiscovery();
                    adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                        @Override
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {
                            BluetoothHeadset headset = (BluetoothHeadset) proxy;
                            try {
                                Method connectMethod = BluetoothHeadset.class.getMethod(
                                        "connect", BluetoothDevice.class);
                                connectMethod.invoke(headset, device);
                            } catch (Exception e) {
                                Toast.makeText(context, "蓝牙连接出错", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onServiceDisconnected(int profile) {
                            Toast.makeText(context, "蓝牙连接断开", Toast.LENGTH_SHORT).show();
                        }
                    }, BluetoothProfile.HEADSET);
                    break;
            }
        }

    }
}
