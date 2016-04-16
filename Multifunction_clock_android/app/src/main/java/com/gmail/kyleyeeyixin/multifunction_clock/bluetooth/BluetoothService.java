package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙通信服务
 * Created by kyleYee on 2016/4/15.
 */
public class BluetoothService extends Service {


    private BluetoothAdapter mAdapter;
    private BluetoothSocket btSocket;
    private InputStream mTmpIn;
    private OutputStream mTmpOut;

    private boolean isConnect = false;
    //蓝牙状态监听
    private BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Intent intent = new Intent();
            intent.setAction(AppContent.BLUETOOTH_CONNECT_STATE);
            intent.putExtra(AppContent.EXTRA_CONNECT_STATE, newState);
            sendBroadcast(intent);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    //服务广播监听
    private BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("BroadcastReceiver", action);
            switch (action) {
                case AppContent.BLUETOOTH_BROADCAST_CONNECT:
                    //蓝牙链接
                    String address = intent.getStringExtra(ShowBluetoothDeviceActivity.DEVICE_ADDRESS);
                    if (address != null) {
                        connect(address);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("startservice", "==========service start");
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter != null) {
            if (!mAdapter.enable()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_ALARM_CLOCK);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_CHIME);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_CONNECT);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_MEMORIAL_DAY);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_POWER);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_TIME);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_TMEPERATURE);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_STOPWATCH);
        registerReceiver(mServiceBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "==========service start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("onDestroy", "==========service start");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 蓝牙配对
     *
     * @param address 蓝牙mac地址
     */
    private void connect(final String address) {
        new Thread(new Runnable() {
            public void run() {
                InputStream tmpIn;
                OutputStream tmpOut;
                Intent intent = new Intent();
                intent.setAction(ShowBluetoothDeviceActivity.BLUETOOTH_BROADCAST_CONNECT_SHOW);
                try {
                    UUID uuid = UUID.fromString(ShowBluetoothDeviceActivity.SPP_UUID);
                    BluetoothDevice device = mAdapter.getRemoteDevice(address);
                    device.connectGatt(BluetoothService.this, false, mGattCallBack);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    tmpIn = btSocket.getInputStream();
                    tmpOut = btSocket.getOutputStream();
                } catch (Exception e) {
                    Log.e("======", "Error connected to: "
                            + address);
                    isConnect = false;
                    btSocket = null;
                    tmpIn = null;
                    tmpOut = null;
                    intent.putExtra(AppContent.EXTRA_SUCCEED, false);
                    sendBroadcast(intent);
                    return;
                }
                isConnect = true;
                mTmpIn = tmpIn;
                mTmpOut = tmpOut;
                intent.putExtra(AppContent.EXTRA_SUCCEED, true);
                intent.putExtra(AppContent.EXTRA_CONNECT_STATE,2);
                sendBroadcast(intent);
            }
        }).start();
    }
}


