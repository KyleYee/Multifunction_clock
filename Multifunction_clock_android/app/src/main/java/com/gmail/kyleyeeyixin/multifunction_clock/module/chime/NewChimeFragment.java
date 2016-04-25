package com.gmail.kyleyeeyixin.multifunction_clock.module.chime;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by kyleYee on 2016/4/25.
 */
public class NewChimeFragment extends BaseFragment {

    public static final String ENTER_NEW_CHIME = "enter_new_chime";
    public static final String NEW_CHIME_STATE = "new_chime_state";
    public static final String SAVE_CHIME_STATE = "save_chime_state";
    public static final String SAVE_CHIME_DATA = "save_chime_data";
    @Bind(R.id.isOpen)
    TextView mIsOpen;
    @Bind(R.id.send)
    Switch mSend;

    private Intent mIntent;
    private SharedPreferences mShare;
    private SharedPreferences.Editor mEditor;
    private Handler mHandler = new Handler();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_CHIME_STATE, mSend.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        if (savedInstanceState != null) {
            boolean state = savedInstanceState.getBoolean(SAVE_CHIME_STATE);
            mSend.setChecked(state);
            mIsOpen.setText(state ? "开启" : "关闭");
        }
        mIntent = new Intent();
        mIntent.setAction(AppContent.BLUETOOTH_BROADCAST_CHIME);
        mShare = getActivity().getSharedPreferences(SAVE_CHIME_STATE, Context.MODE_PRIVATE);
        mEditor = mShare.edit();
    }

    @OnClick(R.id.send)
    public void send(Switch v) {
        final boolean state = v.isChecked();
        if (Utils.judgeConnectBluetooth(getActivity()) == null) {
            Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
            mSend.setChecked(!state);
            mIsOpen.setText(state ? "开启" : "关闭");
            return;
        }
        mIntent.putExtra(ENTER_NEW_CHIME, true);
        getActivity().sendBroadcast(mIntent);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent enterIntent = new Intent();
                enterIntent.setAction(AppContent.BLUETOOTH_BROADCAST_CHIME);
                enterIntent.putExtra(NEW_CHIME_STATE, state);
                getActivity().sendBroadcast(enterIntent);
            }
        }, 500);
        mIsOpen.setText(state ? "开启" : "关闭");
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.new_chime_fragment;
    }
}
