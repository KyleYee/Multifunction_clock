package com.gmail.kyleyeeyixin.multifunction_clock.module.chime;

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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.bluetooth.BluetoothService;
import com.gmail.kyleyeeyixin.multifunction_clock.model.chime.Chime;
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
 * 整点报时
 * Created by yunnnn on 2016/4/19.
 */
public class ChimeFragment extends BaseFragment {
    @Bind(R.id.chime_recycler)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmpty;
    @Bind(R.id.add)
    FloatingActionButton mAdd;
    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;
    private static final String CHIME_LIST = "chime_list";
    public static final String CHIME_DATA = "chime_data";
    public static final String LIST_CHIME_DATA = "chime_list_data";
    public static final String EXTRA_CHIME = "extra_chime";

    private Chime mChime;
    private int mHour;
    private int mMinute;
    private List<Chime> mList;
    private ChimeAdapter mAdapter;
    private LinearLayoutManager mLinearManager;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String mGsonList;
    private String mNewGsonList;
    private Handler handler = new Handler();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CHIME_LIST, (Serializable) mList);
        super.onSaveInstanceState(outState);
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
        super.init(savedInstanceState);

        if (savedInstanceState != null) {
            mList = (List<Chime>) savedInstanceState.get(CHIME_LIST);
        } else {
            mList = new ArrayList<>();
        }
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
        mSharedPreferences = getContext().getSharedPreferences(CHIME_DATA, Context.MODE_PRIVATE);
        mGsonList = mSharedPreferences.getString(LIST_CHIME_DATA, "");
        if (!mGsonList.equals("")) {
            mList = GSonUtil.getObjectFromJson(mGsonList, new TypeToken<List<Chime>>() {
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

        mAdapter = new ChimeAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mEmpty.setVisibility((mList.size() == 0 ? View.VISIBLE : View.GONE));
    }

    public void initListener() {
        //点击更新
        mAdapter.setOnItemClickListener(
                new ChimeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        updateDialog(position);
                    }
                });

        //点击发送数据给蓝牙
        mAdapter.setOnSendListener(new ChimeAdapter.OnSendListener() {
            @Override
            public void onSendClick(int position) {
                mProgressBar.setVisibility(View.VISIBLE);
                mList.get(position);
                if (Utils.judgeConnectBluetooth(getActivity()) == null) {
                    Toast.makeText(getContext(), "请打开wifi", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(AppContent.BLUETOOTH_BROADCAST_CHIME);
                intent.putExtra(EXTRA_CHIME, mList.get(position));
                getActivity().sendBroadcast(intent);

                Toast.makeText(getContext(), mList.get(position).getHour() + ":" +
                        mList.get(position).getMinute(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.add)
    public void onAdd(View v) {
        showDialog();
    }

    /**
     * 更新时间
     *
     * @param mChime
     * @param position
     */
    private void upDataRecycler(Chime mChime, int position) {
        mList.remove(position);
        mList.add(position, mChime);
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
                mChime = new Chime();
                mChime.setHour(mHour);
                mChime.setMinute(mMinute);
                //添加闹钟
                upDataRecycler(mChime, position);
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
                mChime = new Chime();
                mChime.setHour(mHour);
                mChime.setMinute(mMinute);
                //添加闹钟
                mList.add(mChime);
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
        mEditor.remove(LIST_CHIME_DATA);
        mEditor.commit();
        mEditor.putString(LIST_CHIME_DATA, mNewGsonList);
        mEditor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.chime_fragment;
    }
}
