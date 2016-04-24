package com.gmail.kyleyeeyixin.multifunction_clock.module.stopwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.util.Utils;

import butterknife.OnClick;

/**
 * 跑表控制
 * Created by yunnnn on 2016/4/13.
 */
public class StopWatchFragment extends BaseFragment {
    private Intent mIntent;
    public static final String EXTRA_STOPWATCH = "extra_stopwatch";
    public static final String EXTRA_STOPWATCH_ENTER = "extra_stopwatch_enter";
    public static final String EXTRA_STOPWATCH_PAUSE = "extra_stopwatch_pause";
    public static final String EXTRA_STOPWATCH_START = "extra_stopwatch_start";
    public static final String EXTRA_STOPWATCH_RESET = "extra_stopwatch_reset";

    private Handler handler = new Handler();
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mIntent = new Intent();
        mIntent.setAction(AppContent.BLUETOOTH_BROADCAST_STOPWATCH);
        mIntent.putExtra(EXTRA_STOPWATCH, EXTRA_STOPWATCH_ENTER);
        getActivity().sendBroadcast(mIntent);
    }

    @OnClick(R.id.start)
    public void Start(View v) {
        if (Utils.judgeConnectBluetooth(getActivity()) == null) {
            Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        mIntent.putExtra(EXTRA_STOPWATCH, EXTRA_STOPWATCH_START);
        getActivity().sendBroadcast(mIntent);
    }

    @OnClick(R.id.reset)
    public void Reset(View v) {
        if (Utils.judgeConnectBluetooth(getActivity()) == null) {
            Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        mIntent.putExtra(EXTRA_STOPWATCH, EXTRA_STOPWATCH_RESET);
        getActivity().sendBroadcast(mIntent);
    }

    @OnClick(R.id.pause)
    public void pause(View v) {
        if (Utils.judgeConnectBluetooth(getActivity()) == null) {
            Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        mIntent.putExtra(EXTRA_STOPWATCH, EXTRA_STOPWATCH_PAUSE);
        getActivity().sendBroadcast(mIntent);
    }


    @Override
    protected int getViewLayoutId() {
        return R.layout.stop_watch_fragment;
    }


}
