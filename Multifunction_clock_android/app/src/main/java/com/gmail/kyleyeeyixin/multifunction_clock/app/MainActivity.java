package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.PersonalIntroduction;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.alarm_clock.AlarmClockFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.time.TimeFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.power.ShowPowerFragment;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    private static final String CURRENT_ITEM_ID = "current_item_id";
    private static final String CURRENT_ITEM_TITLE = "current_item_title";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;

    private int mCurrentItemID;

    private Menu mMenu;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_ITEM_ID, mCurrentItemID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initToolbar();
        initNavigation();
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        if (savedInstanceState != null) {
            mCurrentItemID = savedInstanceState.getInt(CURRENT_ITEM_ID);
            setFragment(mCurrentItemID, mTransaction);
        } else {
            TimeFragment timeFragment = new TimeFragment();
            mTransaction.replace(R.id.fragment, timeFragment);
            mTransaction.commit();
        }


    }

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    //设置侧边栏
    private void initNavigation() {
        //header
        mNavigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalIntroduction.StartIntroduction(MainActivity.this);
            }
        });

    }

    //初始化toolbar
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); // 设置menu键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        setDrawerContent(mNavigationView);
    }


    private void setDrawerContent(NavigationView mNavigationView) {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setCheckable(true);
                mCurrentItemID = item.getItemId();
                FragmentManager mFragmentManager = getSupportFragmentManager();
                FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
                mToolbar.setTitle(item.getTitle());
                setFragment(item.getItemId(), mTransaction);
                mTransaction.commit();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置fragment
     *
     * @param itemId       当前点击的Item的ID
     * @param mTransaction
     */
    private void setFragment(int itemId, FragmentTransaction mTransaction) {
        switch (itemId) {
            case R.id.power:
                //电量显示
                showPower(mTransaction);
                break;
            case R.id.clock:
                //时钟设置
                showTime(mTransaction);
                break;
            case R.id.alarm_clock:
                //闹钟设置
                settingAlarmClock(mTransaction);
                break;
            case R.id.chime:
                //整点报时
                break;
            case R.id.stopwatch:
                //秒表
                break;
            case R.id.temperature:
                //温度显示
                break;
            case R.id.memory_day:
                //纪念日
                break;
            default:
                break;
        }
    }

    //显示时间
    public void showTime(FragmentTransaction mTransaction) {
        TimeFragment timeFragment = new TimeFragment();
        fragmentReplace(mTransaction, timeFragment);
    }

    //显示电量
    public void showPower(FragmentTransaction mTransaction) {
        ShowPowerFragment powerFragment = new ShowPowerFragment();
        fragmentReplace(mTransaction, powerFragment);
    }

    //设置闹钟
    private void settingAlarmClock(FragmentTransaction mTransaction) {
        AlarmClockFragment alarmClockFragment = new AlarmClockFragment();
        fragmentReplace(mTransaction, alarmClockFragment);
    }


    private void fragmentReplace(FragmentTransaction mTransaction, Fragment fragment) {
        mTransaction.replace(R.id.fragment, fragment);
    }
}
