package com.gmail.kyleyeeyixin.multifunction_clock.module.alarm_clock;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.bluetooth.BluetoothService;
import com.gmail.kyleyeeyixin.multifunction_clock.bluetooth.ShowBluetoothDeviceActivity;
import com.gmail.kyleyeeyixin.multifunction_clock.model.alarm_clock.AlarmClock;
import com.gmail.kyleyeeyixin.multifunction_clock.util.GSonUtil;
import com.gmail.kyleyeeyixin.multifunction_clock.util.Utils;
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
    public static final String EXTRA_ALARM_CLOCK = "extra_alarm_clock";
    public static final String EXTRA_ALARM_CLOCK_IS_OPEN = "extra_alarm_clock_is_open";
    public static final String ALARM_CLOCK_ENTER = "alarm_clock_enter";

    private static final int OPEN = 1;
    private static final int CLOSE = 0;

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
    private int mDeletePosition = -1;

    private boolean isDelete = false;
    private boolean isUpdate = false;
    private boolean isSending = false;
    private Switch mSwitch;
    private int mPosition = -1;
    private boolean mSwitchState = false;

    @Override
    public void onResume() {
        super.onResume();
    }

    //保存状态
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ALARM_CLOCK_LIST, (Serializable) mList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.alarm_clock_activity;
    }

    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothService.SEND_SUCCESS)) {
                isSendSuccess = intent.getBooleanExtra(BluetoothService.EXTRA_IS_SUCCESS, false);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mList = (List<AlarmClock>) savedInstanceState.get(ALARM_CLOCK_LIST);
        } else {
            mList = new ArrayList<>();
        }
        //发送成功 接收器
        IntentFilter getIntentFilter = new IntentFilter(BluetoothService.SEND_SUCCESS);
        getActivity().registerReceiver(receiver, getIntentFilter);
        initData();
        initView();
        initListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.SEND_SUCCESS);
        getActivity().registerReceiver(registerReceiver, intentFilter);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                if (!mBluetoothAdapter.enable()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
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
                mPosition = position;
                Switch send = (Switch) v.findViewById(R.id.send);
                updateDialog(position, send);
            }
        });

        //点击发送数据给蓝牙   发送按键
        mAdapter.setOnSendListener(new AlarmClockAdapter.OnSendListener() {
            @Override
            public void onSendClick(int position, boolean isOpen, Switch v) {
                isSending = true;
                mSwitch = v;
                mSwitchState = v.isChecked();
                mProgressBar.setVisibility(View.VISIBLE);
                //设置当前状态
                mList.get(position).setType(isOpen);
                if (Utils.judgeConnectBluetooth(getActivity()) == null) {
                    Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
                    return;
                }
                //设置闹钟
                setClock(mList.get(position));
            }
        });
    }

    /**
     * 设置闹钟
     */
    private void setClock(AlarmClock alarmClock) {
        //进入设置闹钟界面 广播
        Intent enterIntent = new Intent();
        enterIntent.setAction(AppContent.BLUETOOTH_BROADCAST_ALARM_CLOCK);
        enterIntent.putExtra(ALARM_CLOCK_ENTER, true);
        getActivity().sendBroadcast(enterIntent);

        //设置闹钟广播
        final Intent intent = new Intent();
        intent.setAction(AppContent.BLUETOOTH_BROADCAST_ALARM_CLOCK);
        //设置当前状态
        intent.putExtra(EXTRA_ALARM_CLOCK, alarmClock);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().sendBroadcast(intent);
            }
        }, 1000);
    }

    @OnClick(R.id.add)
    public void onAdd(View v) {
        if (mList.size() < 5) {
            showDialog();
        } else {
            Toast.makeText(getActivity(), "现在只支持5个哦~~~", Toast.LENGTH_SHORT).show();
        }
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
     * @param send
     */
    private void deleteRecycler(int position, Switch send) {
        mAlarmClock = new AlarmClock();
        mAlarmClock = mList.get(position);
        mAlarmClock.setType(false);
        if (send.isChecked()) {
            setClock(mAlarmClock);
            isDelete = true;
        } else {
            mList.remove(position);
            mAdapter.notifyItemRemoved(position);
            if (mList.size() == 0) {
                mEmpty.setVisibility(View.VISIBLE);
                return;
            }
            isDelete = false;
        }
    }

    //更新
    private void updateDialog(final int position, final Switch send) {
/*        final TimePicker timePicker = new TimePicker(getActivity());
        timePicker.setIs24HourView(true);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        /*builder.setView(timePicker);*/
        builder.setTitle("删除闹钟");
        builder.setMessage("是否删除当前闹钟?");
     /*   builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEmpty.setVisibility(View.GONE);
                mHour = timePicker.getCurrentHour();
                mMinute = timePicker.getCurrentMinute();
                mAlarmClock = new AlarmClock();
                mAlarmClock.setHour(mHour);
                mAlarmClock.setMinute(mMinute);
                mAlarmClock.setType(send.isChecked());
                //更新闹钟
                if (send.isChecked()) {
                    mAlarmClock.setType(false);
                    setClock(mAlarmClock);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAlarmClock.setType(true);
                            setClock(mAlarmClock);
                            mPosition = position;
                            isUpdate = true;
                        }
                    }, 3000);
                    return;
                }
                setClock(mAlarmClock);
                mPosition = position;
                isUpdate = true;
            }
        });*/

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecycler(position, send);
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
                mAlarmClock.setType(false);
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSuccess = intent.getBooleanExtra(BluetoothService.EXTRA_IS_SUCCESS, false);
            if (isSuccess) {
         /*       if (isUpdate) {
                    upDataRecycler(mAlarmClock, mPosition);
                    isUpdate = false;
                }*/
                if (isDelete) {
                    mList.remove(mPosition);
                    mAdapter.notifyItemRemoved(mPosition);
                    if (mList.size() == 0) {
                        mEmpty.setVisibility(View.VISIBLE);
                        isDelete = false;
                        return;
                    }
                    isDelete = false;
                }
            } else {
                if (isSending) {
                    isSending = false;
                    mSwitch.setChecked(!mSwitchState);
                }
                if (isDelete) {
                    isDelete = false;
                    mList.add(mPosition, mAlarmClock);
                    if (mList.size() != 0) {
                        mEmpty.setVisibility(View.GONE);
                        return;
                    }
                }
            }
        }
    };
}
