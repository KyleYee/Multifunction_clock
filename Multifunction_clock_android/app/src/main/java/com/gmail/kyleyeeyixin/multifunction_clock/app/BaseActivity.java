package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by yunnnn on 2016/3/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getContentId());
        init(savedInstanceState);
        initView();
        initData();
    }

    protected void init(Bundle savedInstanceState) {
    }

    protected int getContentId() {
        return 0;
    }

    protected void initData() {
    }

    protected void initView() {
    }

}
