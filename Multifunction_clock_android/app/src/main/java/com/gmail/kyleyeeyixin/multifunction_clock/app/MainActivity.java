package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.PersonalIntroduction;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Net.UdpHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.edit)
    EditText editText;
    @Bind(R.id.layout)
    LinearLayout layout;
    private MenuItem mCurrntItem;
    private UdpHelper mUdpHelper;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        ButterKnife.bind(this);
        receiverData();
    }

    @Override
    protected void initData() {
        super.initData();
        mUdpHelper = new UdpHelper();
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar();
        initNavigation();
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    private void receiverData() {
        mUdpHelper.setOnReceiveListener(new UdpHelper.OnReceiveListener() {
            @Override
            public void onReceive(String receiveData) {
                TextView textView = new TextView(MainActivity.this);
                textView.setText(receiveData);
                layout.addView(textView);
            }
        });
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

    @OnClick(R.id.send)
    public void onSend(View v) {
        TextView textView = new TextView(this);
        textView.setText("发送:" + editText.getText().toString().trim());
        layout.addView(textView);
        mUdpHelper.sendUDPdata(editText.getText().toString().trim().getBytes());
    }
}
