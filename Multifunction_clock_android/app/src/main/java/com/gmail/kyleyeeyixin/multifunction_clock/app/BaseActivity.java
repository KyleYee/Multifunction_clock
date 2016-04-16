package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by yunnnn on 2016/3/16.
 */
public class BaseActivity extends AppCompatActivity {
    public BluetoothAdapter mBluetoothAdapter;
    public Context mContext;
    public int mConnectState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter(AppContent.BLUETOOTH_CONNECT_STATE);
        registerReceiver(registerReceiver, intentFilter);
        setContentView(getContentId());
        init(savedInstanceState);
        initListener();
    }

    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppContent.BLUETOOTH_CONNECT_STATE)) {
                mConnectState = intent.getIntExtra(AppContent.EXTRA_CONNECT_STATE, 0);
            }
        }
    };

    public int getConnectState() {
        return mConnectState;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected int getContentId() {
        return 0;
    }

    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.enable()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        } else {
            Toast.makeText(mContext, "此设备没有蓝牙", Toast.LENGTH_LONG).show();
        }
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    protected void initListener() {

    }
}
