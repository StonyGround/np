package com.jhjj9158.niupaivideo.activity;

import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;

public class WithDrawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        initTitle(this,"提现");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_with_draw, null);
    }
}
