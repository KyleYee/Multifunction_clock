package com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction.power;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;

import butterknife.Bind;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by yunnnn on 2016/4/11.
 */
public class ShowPowerFragment extends BaseFragment {
    @Override
    public void onResume() {
        super.onResume();
    }

    @Bind(R.id.show_power)
    WaveLoadingView mPower;

    @Override
    protected int getViewLayoutId() {
        return R.layout.power_fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }
}
