package com.jhjj9158.niupaivideo.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by pc on 17-4-18.
 */

public class MyDrawLayout extends DrawerLayout {

    private boolean isFirst = true;
    private int height;

    public MyDrawLayout(Context context) {
        super(context);
    }

    public MyDrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("onMeasure", "onMeasure(" + View.MeasureSpec.toString(widthMeasureSpec) + ", "
                + View.MeasureSpec.toString(heightMeasureSpec) + ")");

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec * 1400 / 1080), MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
