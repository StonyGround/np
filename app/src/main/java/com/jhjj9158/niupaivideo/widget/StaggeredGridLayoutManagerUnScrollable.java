package com.jhjj9158.niupaivideo.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.R.attr.orientation;
import static android.support.v7.recyclerview.R.attr.spanCount;

/**
 * Created by oneki on 2017/5/2.
 */

public class StaggeredGridLayoutManagerUnScrollable extends StaggeredGridLayoutManager {

    private int[] mMeasuredDimension = new int[2];

    private int mSpanCount;

    private int verticalSpacePix;

    public StaggeredGridLayoutManagerUnScrollable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public StaggeredGridLayoutManagerUnScrollable(int spanCount, int orientation) {

        super(spanCount, orientation);

        this.mSpanCount = spanCount;

    }

    public StaggeredGridLayoutManagerUnScrollable(int spanCount, int orientation, int verticalSpacePix) {

        super(spanCount, orientation);

        this.mSpanCount = spanCount;

        this.verticalSpacePix = verticalSpacePix;

    }

    @Override

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        final int widthMode = View.MeasureSpec.getMode(widthSpec);

        final int heightMode = View.MeasureSpec.getMode(heightSpec);

        final int widthSize = View.MeasureSpec.getSize(widthSpec);

        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        Log.e("xc", "onMeasure called.\nwidthMode " + widthMode

                + "\nheightMode " + heightMode

                + "\nwidthSize " + widthSize

                + "\nheightSize " + heightSize

                + "\ngetItemCount() " + getItemCount());

        int width = 0;

        int height = 0;

        int total;

        if (getItemCount() % mSpanCount == 0) {

            total = getItemCount() / mSpanCount;

        } else {

            total = getItemCount() / mSpanCount + 1;

        }

        for (int i = 0; i < total; i++) {

            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);

            if (getOrientation() == HORIZONTAL) {

                width = width + mMeasuredDimension[0];

                if (i == 0) {

                    height = mMeasuredDimension[1];

                }

            } else {

                height = height + mMeasuredDimension[1] + verticalSpacePix;

                if (i == 0) {

                    width = mMeasuredDimension[0];

                }

            }

        }

        switch (widthMode) {

            case View.MeasureSpec.EXACTLY:

                width = widthSize;

                break;

            case View.MeasureSpec.AT_MOST:

            case View.MeasureSpec.UNSPECIFIED:

        }

        switch (heightMode) {

            case View.MeasureSpec.EXACTLY:

                height = heightSize;

                break;

            case View.MeasureSpec.AT_MOST:

            case View.MeasureSpec.UNSPECIFIED:

        }

        setMeasuredDimension(width, height);

    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,

                                   int heightSpec, int[] measuredDimension) {

        try {

            View view = recycler.getViewForPosition(position);//Warning动态添加时报IndexOutOfBoundsException

            if (view != null) {

                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,

                        getPaddingLeft() + getPaddingRight(), p.width);

                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,

                        getPaddingTop() + getPaddingBottom(), p.height);

                view.measure(childWidthSpec, childHeightSpec);

                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;

                measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;

                recycler.recycleView(view);

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

    }

}
