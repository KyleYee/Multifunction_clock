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
    private static final String DEVICE_NAME = "device_name";
    private static final String DEVICE_ADDRESS = "device_address";

    private Set<BluetoothDevice> mDevices;
    ArrayList<String> mNameList = new ArrayList<>();
    ArrayList<String> mAddressList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    public static BluetoothSocket btSocket;

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
        mBluetoothAdapter.startDiscovery();
        mDevices = new HashSet<>();
    }

    private void initView() {
        mAdapter = new ArrayAdapter<String>(ShowBluetoothDeviceActivity.this,
                android.R.layout.simple_list_item_1, mNameList);
        mListView.setAdapter(mAdapter);
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
        mProgressbar.setVisibility(View.VISIBLE);
        mProgressContent.setText(getString(R.string.search_device));
    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();
                String address = mAddressList.get(position);
                connect(address);
                Toast.makeText(ShowBluetoothDeviceActivity.this,
                        mNameList.get(position), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 蓝牙配对
     *
     * @param address 蓝牙mac地址
     */
    private void connect(final String address) {
        mProgressbar.setVisibility(View.VISIBLE);
        mProgressContent.setText(getString(R.string.start_match));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mProgressbar.setVisibility(View.VISIBLE);
                    mProgressContent.setText(getString(R.string.start_match));
                    UUID uuid = UUID.fromString(SPP_UUID);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    Toast.makeText(ShowBluetoothDeviceActivity.this, "开始配对。。。", Toast.LENGTH_LONG).show();
                } catch (final IOException e) {
                    btSocket = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressbar.setVisibility(View.GONE);
                            Toast.makeText(ShowBluetoothDeviceActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressbar.setVisibility(View.GONE);
                        Toast.makeText(ShowBluetoothDeviceActivity.this, "配对成功。。。", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();


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
