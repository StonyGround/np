package com.jhjj9158.niupaivideo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.CacheUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.setting_feedback)
    RelativeLayout settingFeedback;
    @BindView(R.id.setting_clear)
    RelativeLayout settingClear;
    @BindView(R.id.setting_quit)
    TextView settingQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "设置");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_setting, null);
    }

    @OnClick({R.id.setting_feedback, R.id.setting_clear, R.id.setting_quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_feedback:
                break;
            case R.id.setting_clear:
                break;
            case R.id.setting_quit:
                CacheUtils.setInt(this, "useridx", 0);
                finish();
                break;
        }
    }
}
