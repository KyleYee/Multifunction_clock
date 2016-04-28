package com.gmail.kyleyeeyixin.multifunction_clock.module.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.temperature.TemperatureFragment;

import butterknife.Bind;
import butterknife.OnClick;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by yunnnn on 2016/4/11.
 */
public class ShowPowerFragment extends BaseFragment {
    @Override
    public void onResume() {
        super.onResume();
    }

    @Bind(R.id.show_power)
    WaveLoadingView mPower;
    public static final String SAVE_DATA = "save_data";
    public static final String SAVE_SHARE_POWER = "power";
    private int mGetData = -1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPower.setCenterTitle(msg.what + "%");
            mPower.setProgressValue(msg.what);
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
            mPower.setCenterTitle(mGetData + "");
        }
        mShare = getActivity().getSharedPreferences(SAVE_SHARE_POWER, Context.MODE_PRIVATE);
        mEditor = mShare.edit();
        mGetData = mShare.getInt(SAVE_DATA, -1);
        if (mGetData != -1) {
            mPower.setCenterTitle(mGetData + "%");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TemperatureFragment.REFRESH);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.power_fragment;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mGetData = intent.getIntExtra(TemperatureFragment.REFRESH_DATA, -1);
            mGetData = mGetData / 16 * 10 + mGetData % 16 % 10;
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
