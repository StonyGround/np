package com.jhjj9158.niupaivideo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;

public class NetWorkErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_error);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
    }
}
