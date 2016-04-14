package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseActivity;

import java.util.ArrayList;
import java.util.Set;

import butterknife.Bind;

/**
 * Created by yunnnn on 2016/4/14.
 */
public class ShowBluetoothDeviceActivity extends BaseActivity {

    private Set<BluetoothDevice> mDevices;

    @Bind(R.id.list_view)
    ListView mListView;
    public static void startActivity(Context context, Set<BluetoothDevice> devices) {
        Intent intent = new Intent(context, ShowBluetoothDeviceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.show_device_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mDevices = myBluetoothManager.getDevice();
    }
}
