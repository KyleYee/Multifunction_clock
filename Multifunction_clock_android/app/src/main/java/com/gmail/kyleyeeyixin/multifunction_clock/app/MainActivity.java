package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.PersonalIntroduction;
import com.gmail.kyleyeeyixin.multifunction_clock.module.Net.NetManager;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;

    private MenuItem mCurrntItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initNavigation();
        new Thread() {
            @Override
            public void run() {
                try {
                    byte[] result = NetManager.receiveUDPdata();
                    String resultstr = new String(result, "GB2312");
                    Log.e("服务器", resultstr);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();

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
                String str = "test";
                byte data[];
                data = str.getBytes();
                final byte[] finalData = data;
                new Thread() {
                    @Override
                    public void run() {
                        NetManager.sendUDPdata(finalData);
                    }
                }.start();

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
}
