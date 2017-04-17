package com.jhjj9158.niupaivideo.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;

public class VideoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(this, "播放");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_video, null);
    }

}
