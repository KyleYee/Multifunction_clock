package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.model.alarm_clock.AlarmClock;
import com.gmail.kyleyeeyixin.multifunction_clock.model.chime.Chime;
import com.gmail.kyleyeeyixin.multifunction_clock.model.memory_day.MemoryDay;
import com.gmail.kyleyeeyixin.multifunction_clock.model.time.Time;
import com.gmail.kyleyeeyixin.multifunction_clock.module.alarm_clock.AlarmClockFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.chime.ChimeFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.memoryday.MemoryDayFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.stopwatch.StopWatchFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.time.TimeFragment;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 蓝牙通信服务
 * Created by kyleYee on 2016/4/15.
 */
public class BluetoothService extends Service {
    //连接成功
    private static final int HANDLER_SUCCESS = 0;
    //连接失败
    private static final int HANDLER_FAILED = 1;
    //接收数据
    private static final int HANDLER_RECEIVER = 2;
    //发送数据
    private static final int HANDLER_SEND = 3;
    //发送成功与失败
    public static final String SEND_SUCCESS = "send_success1";
    //是否发送成功
    public static final String EXTRA_IS_SUCCESS = "extra_is_success";
    private BluetoothAdapter mAdapter;
    private BluetoothSocket btSocket;
    private InputStream mTmpIn;
    private OutputStream mTmpOut;

    private boolean isConnect = false;

    //接收数据Handler
    private Handler mReceiveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SUCCESS:
                    //连接成功
                    isConnect = true;
                    new Thread(new Runnable() {
                        public void run() {
                            byte[] bufRecv = new byte[1024];
                            int nRecv = 0;
                            while (isConnect) {
                                try {
                                    nRecv = mTmpIn.read(bufRecv);
                                    if (nRecv < 1) {
                                        Thread.sleep(100);
                                        continue;
                                    }

                                    byte[] nPacket = new byte[nRecv];
                                    System.arraycopy(bufRecv, 0, nPacket, 0, nRecv);
                                    mReceiveHandler.obtainMessage(HANDLER_RECEIVER,
                                            nRecv, -1, nPacket).sendToTarget();
                                    Thread.sleep(100);
                                } catch (Exception e) {

                                    break;
                                }
                            }
                        }
                    }).start();
                    break;
                case HANDLER_FAILED:
                    //连接失败
                    break;
                case HANDLER_RECEIVER:
                    //接收成功
                    byte[] bBuf = (byte[]) msg.obj;
                    Log.e("========", "接收数据: " + bytesToString(bBuf, msg.arg1));
                    break;
            }

        }
    };
    //蓝牙状态监听
    private BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Intent intent = new Intent();
            intent.setAction(AppContent.BLUETOOTH_CONNECT_STATE);
            intent.putExtra(AppContent.EXTRA_CONNECT_STATE, newState);
            sendBroadcast(intent);
        }
    };

    /**
     * 服务广播，用于处理Activity传来的数据
     */
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
                case AppContent.BLUETOOTH_BROADCAST_SEND:
                    //发送数据
                    send(null);
                    break;
                case AppContent.BLUETOOTH_BROADCAST_TIME:
                    //设置时间
                    Bundle timeBundle = intent.getBundleExtra(TimeFragment.TIME_BUNDLE);
                    Time time = (Time) timeBundle.getSerializable(AppContent.EXTRA_TIME);
                    send(time.toString());
                    break;
                case AppContent.BLUETOOTH_BROADCAST_STOPWATCH:
                    //跑表
                    boolean isStart = intent.getBooleanExtra(StopWatchFragment.EXTRA_STOPWATCH, false);
                    if (isStart) {
                        //开始
                        send("1");
                    } else {
                        //复位
                        send("0");
                    }
                    break;
                case AppContent.BLUETOOTH_BROADCAST_ALARM_CLOCK:
                    //闹钟
                    AlarmClock alarmClock = (AlarmClock) intent.getSerializableExtra(AlarmClockFragment.EXTRA_ALARM_CLOCK);
                    if (alarmClock != null) {
                        send(alarmClock.toString());
                    }
                    break;
                case AppContent.BLUETOOTH_BROADCAST_CHIME:
                    //整点报时
                    Chime chime = (Chime) intent.getSerializableExtra(ChimeFragment.EXTRA_CHIME);
                    if (chime != null) {
                        send(chime.toString());
                    }
                    break;
                case AppContent.BLUETOOTH_BROADCAST_MEMORIAL_DAY:
                    MemoryDay memoryDay = (MemoryDay) intent.getSerializableExtra(MemoryDayFragment.EXTRA_MEMORY_DAY);
                    if (memoryDay != null) {
                        send(memoryDay.toString());
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

        registerReceiver(mServiceBroadcastReceiver, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_ALARM_CLOCK);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_CHIME);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_CONNECT);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_MEMORIAL_DAY);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_POWER);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_TIME);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_TMEPERATURE);
        intentFilter.addAction(AppContent.BLUETOOTH_BROADCAST_STOPWATCH);
        return intentFilter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "==========service start");
        return super.onStartCommand(intent, flags, startId);
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
                    btSocket = device.createRfcommSocketToServiceRecord(uuid);
                    // 连接建立之前的先配对
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        Method creMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        Log.e("TAG", "开始配对");
                        creMethod.invoke(device);
                    }
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
                intent.putExtra(AppContent.EXTRA_CONNECT_STATE, 2);
                sendBroadcast(intent);
                mReceiveHandler.sendEmptyMessage(HANDLER_SUCCESS);
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        Log.e("onDestroy", "==========service start");
        closeAndExit();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* DEMO版较为简单，在编写您的应用时，请将此函数放到线程中执行，以免UI不响应 */
    public void send(String strValue) {
        if (!isConnect) {
            Toast.makeText(getApplicationContext(), "请连接蓝牙", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(SEND_SUCCESS);
            intent.putExtra(EXTRA_IS_SUCCESS, false);
            sendBroadcast(intent);
            return;
        }
        try {
            if (mTmpOut == null) {
                Toast.makeText(getApplicationContext(), "请连接蓝牙", Toast.LENGTH_SHORT).show();
                Intent send = new Intent();
                send.setAction(SEND_SUCCESS);
                send.putExtra(EXTRA_IS_SUCCESS, false);
                sendBroadcast(send);
                return;
            }
            mTmpOut.write(strValue.getBytes());
            Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
            Intent success = new Intent();
            success.setAction(SEND_SUCCESS);
            success.putExtra(EXTRA_IS_SUCCESS, true);
            sendBroadcast(success);
            Log.e("========", "发送:" + strValue + "\r\n");
        } catch (Exception e) {
            Intent failed = new Intent();
            failed.setAction(SEND_SUCCESS);
            failed.putExtra(EXTRA_IS_SUCCESS, false);
            sendBroadcast(failed);
            Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static String bytesToString(byte[] b, int length) {
        StringBuffer result = new StringBuffer("");
        for (int i = 0; i < length; i++) {
            result.append((char) (b[i]));
        }

        return result.toString();
    }

    public void closeAndExit() {
        if (isConnect) {
            isConnect = false;

            try {
                Thread.sleep(100);
                if (mTmpOut != null)
                    mTmpIn.close();
                if (mTmpOut != null)
                    mTmpOut.close();
                if (btSocket != null)
                    btSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


