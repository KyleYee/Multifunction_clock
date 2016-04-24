package com.gmail.kyleyeeyixin.multifunction_clock.module.temperature;

import android.os.Bundle;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;

import butterknife.Bind;

/**
 * 温度显示
 * Created by yunnnn on 2016/4/20.
 */
public class TemperatureFragment extends BaseFragment {

    @Bind(R.id.content)
    TextView mContent;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.temperature_fragment;
    }
}
