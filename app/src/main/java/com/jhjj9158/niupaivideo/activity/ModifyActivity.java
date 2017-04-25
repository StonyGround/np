package com.jhjj9158.niupaivideo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;

public class ModifyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this,"修改资料");

    }

    @Override
    protected View getChildView() {
        return View.inflate(this,R.layout.activity_modify,null);
    }
}
