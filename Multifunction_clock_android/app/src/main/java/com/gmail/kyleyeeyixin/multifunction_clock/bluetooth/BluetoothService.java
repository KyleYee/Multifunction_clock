package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 蓝牙通信服务
 * Created by kyleYee on 2016/4/15.
 */
public class BluetoothService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
