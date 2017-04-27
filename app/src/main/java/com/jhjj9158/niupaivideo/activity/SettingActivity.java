package com.jhjj9158.niupaivideo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected View getChildView() {
        return View.inflate(this,R.layout.activity_setting,null);
    }
}
