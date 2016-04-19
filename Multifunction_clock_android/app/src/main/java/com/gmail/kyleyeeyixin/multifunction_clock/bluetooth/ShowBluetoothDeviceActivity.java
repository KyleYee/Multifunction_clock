package com.gmail.kyleyeeyixin.multifunction_clock.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;

/**
 * 显示扫描的蓝牙
 * 并连接
 * Created by yunnnn on 2016/4/14.
 */
public class ShowBluetoothDeviceActivity extends BaseActivity {
    public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String BLUETOOTH_BROADCAST_CONNECT_SHOW = "show_connect";

    private Set<BluetoothDevice> mDevices;
    ArrayList<String> mNameList = new ArrayList<>();
    ArrayList<String> mAddressList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private boolean isConnecting = false;
    private int mConnectedState = 0;

    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.empty)
    TextView mEmpty;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progressbar)
    LinearLayout mProgressbar;
    @Bind(R.id.progressbar_content)
    TextView mProgressContent;

    public static void blueStartActivity(Context context) {
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
        initToolbar();
        initView();
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        //获取未配对的蓝牙信息
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        this.registerReceiver(receiver, intentFilter);
        //注册蓝牙链接广播
        this.registerReceiver(connectReceiver, new IntentFilter(BLUETOOTH_BROADCAST_CONNECT_SHOW));
        mBluetoothAdapter.startDiscovery();
        mDevices = new HashSet<>();
    }

    private void initView() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : mDevices) {
            mNameList.add(device.getName());
            mAddressList.add(device.getAddress());
        }
        mAdapter = new ArrayAdapter<String>(ShowBluetoothDeviceActivity.this,
                android.R.layout.simple_list_item_1, mNameList);
        mListView.setAdapter(mAdapter);
        if (mNameList == null && mNameList.size() == 0) {
            mProgressbar.setVisibility(View.VISIBLE);
            mProgressContent.setText(getString(R.string.search_device));
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mNameList.add(device.getName());
                mAddressList.add(device.getAddress());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressbar.setVisibility(View.GONE);
                        showDevice();
                    }
                });
            }
        }
    };
    //连接广播
    private BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //后台连接
            if (BLUETOOTH_BROADCAST_CONNECT_SHOW.equals(intent.getAction())) {
                final boolean isSuccess = intent.getBooleanExtra(AppContent.EXTRA_SUCCEED, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressbar.setVisibility(View.GONE);
                        if (isSuccess) {
                            Toast.makeText(ShowBluetoothDeviceActivity.this, "连接成功", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(ShowBluetoothDeviceActivity.this, "连接失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    };

    //初始化toolbar
    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.search_device));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); // 设置menu键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ;
    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前连接状态
                if (getConnectState() != AppContent.STATE_DISCONNECTED) {
                    Toast.makeText(ShowBluetoothDeviceActivity.this, "当前已连接了蓝牙", Toast.LENGTH_LONG).show();
                    return;
                } else if (getConnectState() == AppContent.STATE_DISCONNECTED) {
                    if (mBluetoothAdapter != null) {
                        if (!mBluetoothAdapter.enable()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivity(intent);
                        }
                    }
                }
                mProgressbar.setVisibility(View.VISIBLE);
                mProgressContent.setText(getString(R.string.start_match));
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();
                String address = mAddressList.get(position);
                Intent intent = new Intent();
                intent.setAction(AppContent.BLUETOOTH_BROADCAST_CONNECT);
                intent.putExtra(DEVICE_ADDRESS, address);
                sendBroadcast(intent);
                Toast.makeText(ShowBluetoothDeviceActivity.this,
                        mNameList.get(position), Toast.LENGTH_LONG).show();
            }
        });
    }

    //显示设备
    private void showDevice() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
