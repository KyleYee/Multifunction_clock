package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.kyleyeeyixin.multifunction_clock.bluetooth.MyBluetoothManager;

import butterknife.ButterKnife;

/**
 * Created by yunnnn on 2016/3/16.
 */
public class BaseActivity extends AppCompatActivity {
    public MyBluetoothManager myBluetoothManager;
    public Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        setContentView(getContentId());
        init(savedInstanceState);
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myBluetoothManager =  MyBluetoothManager.getInstance(mContext);
    }

    protected int getContentId() {
        return 0;
    }

    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    protected void initListener(){

    }
}
