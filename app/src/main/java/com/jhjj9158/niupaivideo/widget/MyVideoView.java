package com.jhjj9158.niupaivideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.jhjj9158.niupaivideo.activity.VideoActivity;

/**
 * Created by oneki on 2017/5/10.
 */

public class MyVideoView extends VideoView {

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
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
