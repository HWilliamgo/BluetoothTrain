package com.example.admin.bluetoothtrain.Utils;

import android.bluetooth.BluetoothProfile;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUuidHelper {

    static final ParcelUuid[] HEADSET_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.HSP,
            BluetoothUuid.Handsfree,
    };
    static final ParcelUuid[] A2DP_SINK_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.AudioSink,
            BluetoothUuid.AdvAudioDist,
    };
    static final ParcelUuid[] A2DP_SRC_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.AudioSource
    };
    static final ParcelUuid[] OPP_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.ObexObjectPush
    };
    static final ParcelUuid[] HID_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.Hid
    };
    static final ParcelUuid[] PANU_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.PANU
    };
    static final ParcelUuid[] NAP_PROFILE_UUIDS = new ParcelUuid[]{
            BluetoothUuid.NAP
    };

    /**
     *
     * @param uuids 通过BluetoothDevice#fetchUuidsWithSdp()去获取，注册广播接收器来监听系统发出的对应的广播
     * @return Profile数组，用于获取Profile代理来连接远程设备。
     */
    public static List<Integer> uuidsToProfile(ParcelUuid[] uuids) {
        List<Integer> profileList = new ArrayList<>();

        if (BluetoothUuid.containsAnyUuid(uuids, HEADSET_PROFILE_UUIDS)) {
            profileList.add(BluetoothProfile.HEADSET);
        }
        if (BluetoothUuid.containsAnyUuid(uuids, A2DP_SRC_PROFILE_UUIDS)) {
            profileList.add(BluetoothProfile.A2DP);
        }
//        if (BluetoothUuid.containsAnyUuid(uuids, OPP_PROFILE_UUIDS)) {
//            //BluetoothProfile中没有这个对应的常量
//        }
//        if (BluetoothUuid.containsAnyUuid(uuids, HID_PROFILE_UUIDS))
//        {
//            //BluetoothProfile.INPUT_DEVICE被系统隐藏
//        }
//        if (BluetoothUuid.containsAnyUuid(uuids, PANU_PROFILE_UUIDS))
//        {
//            //BluetoothProfile.PAN被系统隐藏
//        }
        return profileList;
    }
}
