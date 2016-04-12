package com.gmail.kyleyeeyixin.multifunction_clock.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by yunnnn on 2016/4/11.
 */
public class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getContentView(inflater, container);
        ButterKnife.bind(this, v);
        init(savedInstanceState);
        onCreateView(inflater, savedInstanceState);
        return v;
    }

    protected void init(Bundle savedInstanceState) {

    }

    public void onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {

    }

    protected int getViewLayoutId() {
        return 0;
    }


    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getViewLayoutId(), container, false);
    }


}
