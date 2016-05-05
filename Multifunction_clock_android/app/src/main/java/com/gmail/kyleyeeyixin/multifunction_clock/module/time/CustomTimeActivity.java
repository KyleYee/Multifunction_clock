package com.gmail.kyleyeeyixin.multifunction_clock.module.time;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.AppContent;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseActivity;
import com.gmail.kyleyeeyixin.multifunction_clock.model.time.Time;
import com.gmail.kyleyeeyixin.multifunction_clock.util.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

import butterknife.Bind;

/**
 * 自定义时间设置
 * Created by yunnnn on 2016/4/12.
 */
public class CustomTimeActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.calendarView)
    MaterialCalendarView mCalendar;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mWeek;
    private Intent mIntent;
    private Handler handler = new Handler();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CustomTimeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.custom_time_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initToolbar();
    }

    //初始化toolbar
    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.custom_setting_time));
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
    }

    @Override
    protected void initListener() {
        mCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mYear = date.getYear();
                mMonth = date.getMonth() + 1;
                mDay = date.getDay();
                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth - 1, mDay);
                mWeek = calendar.get(Calendar.DAY_OF_WEEK);
                showTimePickerDialog();
                Toast.makeText(CustomTimeActivity.this, date.getYear() + ":" + (date.getMonth() + 1) + ":" + date.getDay(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTimePickerDialog() {
        final TimePicker timePicker = new TimePicker(CustomTimeActivity.this);
        timePicker.setIs24HourView(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomTimeActivity.this);
        builder.setMessage(mYear + getString(R.string.year) +
                mMonth + getString(R.string.month) +
                mDay + getString(R.string.day));
        builder.setView(timePicker);

        builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHour = timePicker.getCurrentHour();
                mMinute = timePicker.getCurrentMinute();
                //发送时间给服务
                if (Utils.judgeConnectBluetooth(CustomTimeActivity.this) == null) {
                    Toast.makeText(CustomTimeActivity.this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
                    return;
                }
                mIntent = new Intent();
                mIntent.setAction(AppContent.BLUETOOTH_BROADCAST_TIME);

                Intent intent = new Intent();
                intent.setAction(AppContent.BLUETOOTH_BROADCAST_TIME);
                intent.putExtra(TimeFragment.TIME_BUNDLE, TimeFragment.TIME_ENTER);
                sendBroadcast(intent);

                Time time = new Time(mYear, mMonth, mDay, mHour, mMinute, mWeek-1);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppContent.EXTRA_TIME, time);
                mIntent.putExtra(TimeFragment.TIME_BUNDLE, bundle);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(mIntent);
                    }
                }, 1000);
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
        dialogFragment.show(getFragmentManager(), "custom_time");
    }
}
