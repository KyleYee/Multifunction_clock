package com.gmail.kyleyeeyixin.multifunction_clock.module.Introduction;

import android.os.Bundle;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.app.BaseFragment;
import com.gmail.kyleyeeyixin.multifunction_clock.model.chime.Chime;

import butterknife.Bind;

/**
 * 项目介绍
 * Created by kyleYee on 2016/4/16.
 */
public class ProjectFragment extends BaseFragment {
    @Bind(R.id.content)
    TextView textView;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.project_fragment;
    }
}
