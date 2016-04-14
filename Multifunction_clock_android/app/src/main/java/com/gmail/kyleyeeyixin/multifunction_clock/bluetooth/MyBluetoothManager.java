package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.Set;

/**
 * 蓝牙管理
 * Created by yixin on 2016/4/14.
 */
public class MyBluetoothManager {

    public static final int BLUETOOTH_ENABLE_REQUEST_CODE = 0;
    private static MyBluetoothManager mInstance;
    private Context mContext;
    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> mDevice;

    public MyBluetoothManager(Context mContext) {
        this.mContext = mContext;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static MyBluetoothManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyBluetoothManager(context);
        }
        return mInstance;
    }

    public void isEnable(Activity activity) {
        if (!mAdapter.enable()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, BLUETOOTH_ENABLE_REQUEST_CODE);
        }
    }

    public  Set<BluetoothDevice> getDevice(){
        mDevice = mAdapter.getBondedDevices();
        return mDevice;
    }


}
