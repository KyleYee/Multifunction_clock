package com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yunnnn on 2016/3/8.
 */
public class PersonIntroduction extends AppCompatActivity {

    @Bind(R.id.content)
    TextView mContent;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static void StartIntroduction(Activity activity) {
        Intent intent = new Intent(activity, PersonIntroduction.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_introduction_activity);
        ButterKnife.bind(this);
        initToolbar();

        mContent.setText("个人介绍");
    }

    //初始化toolbar
    private void initToolbar() {
        mToolbar.setTitle(getString(R.string.person_introduction));
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
}
