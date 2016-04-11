package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.PersonalIntroduction;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.clock.ClockFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.power.ShowPowerFragment;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;

    private MenuItem mCurrntItem;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        ClockFragment clockFragment = new ClockFragment();
        mTransaction.add(R.id.fragment,clockFragment);
        mTransaction.commit();
        initToolbar();
        initNavigation();

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
                mCurrntItem = item;
                mToolbar.setTitle(item.getTitle());
                setFragment(item.getItemId());
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**
     * 设置fragment
     * @param itemId 当前点击的Item的ID
     */
    private void setFragment(int itemId) {
        switch (itemId){
            case R.id.power:
                //电量显示
                showPower();
                break;
            case R.id.clock:
                //时钟设置
                showClock();
                break;
            case R.id.alarm_clock:
                //闹钟设置
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
            default:
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showClock(){
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        ClockFragment clockFragment = new ClockFragment();
        mTransaction.replace(R.id.fragment,clockFragment);
        mTransaction.commit();
    }

    public void showPower(){
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        ShowPowerFragment powerFragment = new ShowPowerFragment();
        mTransaction.replace(R.id.fragment,powerFragment);
        mTransaction.commit();
    }
}
