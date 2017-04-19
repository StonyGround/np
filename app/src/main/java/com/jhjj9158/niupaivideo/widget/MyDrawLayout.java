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
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
//                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);

//        Log.e("heigh", heightMeasureSpec + "");
//
//        if (isFirst) {
//            isFirst = false;
//            height = heightMeasureSpec;
//        }
        Log.i("----", "onMeasure(" + View.MeasureSpec.toString(widthMeasureSpec) + ", "
                + View.MeasureSpec.toString(heightMeasureSpec) + ")");
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(1400), MeasureSpec.EXACTLY);

//
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
