package com.jhjj9158.niupaivideo.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.Arrays;
import java.util.Collections;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by Administrator on 2016/7/7.
 */
public abstract class MyRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private StaggeredGridLayoutManager layoutManager;

    public MyRecyclerOnScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        super.onScrollStateChanged(recyclerView, newState);
    }

    public abstract void onLoadMore();
}
