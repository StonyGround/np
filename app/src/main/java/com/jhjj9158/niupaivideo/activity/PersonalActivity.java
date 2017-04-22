package com.jhjj9158.niupaivideo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jhjj9158.niupaivideo.R;

public class PersonalActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setBackgroundResource(R.color.transparent);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_personal, null);
    }
}
