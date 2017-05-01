package com.jhjj9158.niupaivideo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2015/10/10.
 */
public abstract class BaseFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = prepareView(inflater, container);
            //加载数据
            onloadData(view);
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    public abstract View prepareView(LayoutInflater inflater, ViewGroup container);

    public abstract void onloadData(View view);

    @Override
    public void onResume() {
        super.onResume();
    }

    public void resume() {

    }

    public abstract void adapterScreen();
}
