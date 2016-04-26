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
import android.widget.LinearLayout;
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
    @Bind(R.id.progressbar)
    LinearLayout mProgressbar;
    //温度
    public static final String REFRESH_DATA = "refresh_data";
    //湿度
    public static final String DATA = "shidu";
    public static final String REFRESH = "refresh";
    public static final String SAVE_DATA = "save_data";
    public static final String SAVE_SHI_DU = "save_shi_du";
    public static final String SAVE_SHARE_TEMPERATURE = "temperature";
    private int mGetData = -1;
    private int mShidu = -1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mContent.setText("当前温度：" + msg.what + "度\n当前湿度：" + msg.arg1);
            mProgressbar.setVisibility(View.GONE);
        }
    };
    private SharedPreferences mShare;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_DATA, mGetData);
        outState.putInt(SAVE_SHI_DU, mShidu);
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
            mShidu = savedInstanceState.getInt(SAVE_SHI_DU);
        }
        mShare = getActivity().getSharedPreferences(SAVE_SHARE_TEMPERATURE, Context.MODE_PRIVATE);
        mEditor = mShare.edit();
        mGetData = mShare.getInt(SAVE_DATA, -1);
        mShidu = mShare.getInt(SAVE_SHI_DU, -1);
        if (mGetData != -1 && mShidu != -1) {
            mContent.setText("当前温度：" + mGetData + "度\n当前湿度：" + mShidu);
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
            mShidu = intent.getIntExtra(DATA, -1);
            mGetData = mGetData / 16 * 10 + mGetData % 16 % 10;
            mShidu = mShidu / 16 * 10 + mShidu % 16 % 10;
            if (mGetData != -1) {
                Message message = new Message();
                message.what = mGetData;
                message.arg1 = mShidu;
                mHandler.sendMessage(message);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        mEditor.putInt(SAVE_DATA, mGetData);
        mEditor.putInt(SAVE_SHI_DU, mShidu);
        mEditor.commit();
    }
}
