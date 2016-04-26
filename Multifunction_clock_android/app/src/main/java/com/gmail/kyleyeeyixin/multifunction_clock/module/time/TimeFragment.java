package com.gmail.kyleyeeyixin.multifunction_clock.module.time;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.model.time.Time;
import com.gmail.kyleyeeyixin.multifunction_clock.util.Utils;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;

/**
 * 设置时间
 * Created by yunnnn on 2016/4/11.
 */
public class TimeFragment extends BaseFragment {

    public static final String TIME_BUNDLE = "time_bundle";
    public static final String TIME_ENTER = "time_enter";
    public static final String TIME_SETTING  = "time_setting";

    @Bind(R.id.day)
    TextView mTvDay;
    @Bind(R.id.system)
    Button mSystem;
    @Bind(R.id.custom)
    Button mCustom;

    private int mDay;
    private int mMonth;
    private int mHour;
    private int mMinute;
    private int mSec;
    private int mWeek;
    private int mYear;
    private Intent mIntent;
    private Handler handler = new Handler();
    @Override
    protected int getViewLayoutId() {
        return R.layout.time_fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        initData();
        initListener();
    }

    public void initData() {
        setDay();
    }

    /**
     * 设置天数和周数
     */
    private void setDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;// 获取当前月份
        mDay = calendar.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mSec = calendar.get(Calendar.SECOND);
        mWeek = calendar.get(Calendar.DAY_OF_WEEK);

        mTvDay.setText(mYear + getString(R.string.year) +
                mMonth + getString(R.string.month) +
                mDay + getString(R.string.day));
    }

    /**
     * 获得系统周数
     *
     * @param week
     * @return
     */
    private String getWeek(int week) {
        switch (week) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
        }
        return "";
    }


    public void initListener() {
        //设置系统时间
        mSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(mYear + getString(R.string.year) +
                        mMonth + getString(R.string.month) +
                        mDay + getString(R.string.day));
                builder.setMessage(getString(R.string.is_setting));
                builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //发送时间给服务
                        if (Utils.judgeConnectBluetooth(getActivity()) == null) {
                            Toast.makeText(getContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mIntent = new Intent();
                        mIntent.setAction(AppContent.BLUETOOTH_BROADCAST_TIME);

                        Intent intent = new Intent();
                        intent.setAction(AppContent.BLUETOOTH_BROADCAST_TIME);
                        intent.putExtra(TIME_BUNDLE,TIME_ENTER);
                        getActivity().sendBroadcast(intent);

                        Time time = new Time(mYear, mMonth, mDay, mHour, mMinute,mWeek);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AppContent.EXTRA_TIME, time);
                        mIntent.putExtra(TIME_BUNDLE, bundle);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().sendBroadcast(mIntent);
                            }
                        },1000);
                    }
                });
                builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                dialogFragment.show(getActivity().getFragmentManager(), "Message");
            }
        });

        //设置自定义时间
        mCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTimeActivity.startActivity(getContext());
            }
        });
    }
}
