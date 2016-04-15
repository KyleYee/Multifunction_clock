package com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.alarm_clock;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.bluetooth.ShowBluetoothDeviceActivity;
import com.gmail.kyleyeeyixin.multifunction_clock.model.alarm_clock.AlarmClock;
import com.gmail.kyleyeeyixin.multifunction_clock.util.GSonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 闹钟设置
 * Created by yunnnn on 2016/4/13.
 */
public class AlarmClockFragment extends BaseFragment {

    @Bind(R.id.alarm_clock_recycler)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmpty;
    @Bind(R.id.add)
    FloatingActionButton mAdd;
    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    private static final String ALARM_CLOCK_LIST = "alarm_list";
    public static final String ALARM_CLOCK_DATA = "alarm_data";
    public static final String LIST_ALARM_CLOCK_DATA = "alarm_list_data";

    private AlarmClock mAlarmClock;
    private int mHour;
    private int mMinute;
    private List<AlarmClock> mList;
    private AlarmClockAdapter mAdapter;
    private LinearLayoutManager mLinearManager;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String mGsonList;
    private String mNewGsonList;
    private Handler handler = new Handler();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ALARM_CLOCK_LIST, (Serializable) mList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.alarm_clock_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mList = (List<AlarmClock>) savedInstanceState.get(ALARM_CLOCK_LIST);
        } else {
            mList = new ArrayList<>();
        }
        initData();
        initView();
        initListener();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                if (!mBluetoothAdapter.enable()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
            } else {
                ShowBluetoothDeviceActivity.blueStartActivity(getContext());
            }
        } else {
            mBluetoothAdapter.isEnabled();
        }
    }

    public void initData() {
        mSharedPreferences = getContext().getSharedPreferences(ALARM_CLOCK_DATA, Context.MODE_PRIVATE);
        mGsonList = mSharedPreferences.getString(LIST_ALARM_CLOCK_DATA, "");
        if (!mGsonList.equals("")) {
            mList = GSonUtil.getObjectFromJson(mGsonList, new TypeToken<List<AlarmClock>>() {
            }.getType());
        }
    }

    public void initView() {
        mLinearManager = new LinearLayoutManager(getContext());
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearManager);

        mAdapter = new AlarmClockAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mEmpty.setVisibility((mList.size() == 0 ? View.VISIBLE : View.GONE));
    }

    public void initListener() {
        //点击更新
        mAdapter.setOnItemClickListener(new AlarmClockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                updateDialog(position);
            }
        });

        //点击发送数据给蓝牙
        mAdapter.setOnSendListener(new AlarmClockAdapter.OnSendListener() {
            @Override
            public void onSendClick(int position) {
                mProgressBar.setVisibility(View.VISIBLE);
                mList.get(position);
                Toast.makeText(getContext(), mList.get(position).getHour() + ":" +
                        mList.get(position).getMinute(), Toast.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }

    @OnClick(R.id.add)
    public void onAdd(View v) {
        showDialog();
    }

    /**
     * 更新闹钟
     *
     * @param mAlarmClock
     * @param position
     */
    private void upDataRecycler(AlarmClock mAlarmClock, int position) {
        mList.remove(position);
        mList.add(position, mAlarmClock);
        mAdapter.notifyItemChanged(position);
    }

    /**
     * 删除闹钟
     *
     * @param position
     */
    private void deleteRecycler(int position) {
        mList.remove(position);
        mAdapter.notifyItemRemoved(position);
        if (mList.size() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void updateDialog(final int position) {
        final TimePicker timePicker = new TimePicker(getActivity());
        timePicker.setIs24HourView(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(timePicker);

        builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEmpty.setVisibility(View.GONE);
                mHour = timePicker.getCurrentHour();
                mMinute = timePicker.getCurrentMinute();
                mAlarmClock = new AlarmClock();
                mAlarmClock.setHour(mHour);
                mAlarmClock.setMinute(mMinute);
                //添加闹钟
                upDataRecycler(mAlarmClock, position);
            }
        });

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecycler(position);
            }
        });

        final AlertDialog dialog = builder.create();
        DialogFragment dialogFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return dialog;
            }
        };
        dialogFragment.show(getActivity().getFragmentManager(), "custom_time");
    }

    //添加dialog
    public void showDialog() {
        final TimePicker timePicker = new TimePicker(getActivity());
        timePicker.setIs24HourView(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(timePicker);

        builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEmpty.setVisibility(View.GONE);
                mHour = timePicker.getCurrentHour();
                mMinute = timePicker.getCurrentMinute();
                mAlarmClock = new AlarmClock();
                mAlarmClock.setHour(mHour);
                mAlarmClock.setMinute(mMinute);
                //添加闹钟
                mList.add(mAlarmClock);
                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog dialog = builder.create();
        DialogFragment dialogFragment = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return dialog;
            }
        };
        dialogFragment.show(getActivity().getFragmentManager(), "custom_time");
    }

    @Override
    public void onPause() {
        super.onPause();
        mEditor = mSharedPreferences.edit();
        mNewGsonList = new Gson().toJson(mList);
        if (mGsonList.equals(mNewGsonList)) {
            return;
        }
        mEditor.remove(LIST_ALARM_CLOCK_DATA);
        mEditor.commit();
        mEditor.putString(LIST_ALARM_CLOCK_DATA, mNewGsonList);
        mEditor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
