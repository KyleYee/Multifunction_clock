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
        initAll();
        onCreateView(inflater, savedInstanceState);
        return v;
    }

    public void onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {

    }

    protected int getViewLayoutId() {
        return 0;
    }

    private void initAll() {
        initData();
        initView();
        initListener();
    }
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getViewLayoutId(), container, false);
    }



    /**
     * 初始化数据（example：从intent中获取数据）
     */
    protected void initData() {
    }

    /**
     * 初始化view
     */
    protected void initView() {
    }

    /**
     * 绑定监听事件(Butternife不支持的简体事件才需要在这里面绑定)
     */
    protected void initListener() {
    }

}
