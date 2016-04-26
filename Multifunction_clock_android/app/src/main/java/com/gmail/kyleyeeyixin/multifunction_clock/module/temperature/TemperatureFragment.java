package com.gmail.kyleyeeyixin.multifunction_clock.module.temperature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 温度显示
 * Created by yunnnn on 2016/4/20.
 */
public class TemperatureFragment extends BaseFragment {

    @Bind(R.id.content)
    TextView mContent;
    public static final String REFRESH_DATA = "refresh_data";
    public static final String REFRESH = "refresh";
    public static final String SAVE_DATA = "save_data";
    public static final String SAVE_SHARE_TEMPERATURE = "temperature";
    private int mGetData;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mContent.setText(msg.what);
        }
    };
    private SharedPreferences mShare;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_DATA, mGetData);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.floating)
    public void refresh(View v) {
        Intent intent = new Intent();
        intent.setAction(AppContent.BLUETOOTH_BROADCAST_TMEPERATURE);
        getActivity().sendBroadcast(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        if (savedInstanceState != null) {
            mGetData = savedInstanceState.getInt(SAVE_DATA);
        }
        mShare = getActivity().getSharedPreferences(SAVE_SHARE_TEMPERATURE, Context.MODE_PRIVATE);
        mGetData = mShare.getInt(SAVE_DATA, -1);
        if (mGetData != -1) {
            mContent.setText(mGetData);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REFRESH);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.temperature_fragment;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mGetData = intent.getIntExtra(REFRESH_DATA, -1);
            if (mGetData != -1) {
                mHandler.sendEmptyMessage(mGetData);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        mEditor.putInt(SAVE_DATA, mGetData);
        mEditor.commit();
    }
}
