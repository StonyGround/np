package com.jhjj9158.niupaivideo.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.DataCleanUtil;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.setting_feedback)
    RelativeLayout settingFeedback;
    @BindView(R.id.setting_clear)
    RelativeLayout settingClear;
    @BindView(R.id.setting_quit)
    TextView settingQuit;
    @BindView(R.id.setting_clear_size)
    TextView settingClearSize;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        initTitle(this, "设置");
//        try {
//            settingClearSize.setText(getString(R.string.cache_size, DataCleanUtil
//                    .getTotalCacheSize(SettingActivity.this)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        builder = new AlertDialog.Builder(this);
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
                builder.setMessage("确定清除缓存吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanUtil.cleanApplicationData(SettingActivity.this);
                                CommonUtil.showTextToast(SettingActivity.this,"清除成功");
//                                settingClearSize.setText(getString(R.string.cache_size, "0M"));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.setting_quit:
                builder.setMessage("确定退出当前帐号吗？")
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CacheUtils.setInt(SettingActivity.this, "useridx", 0);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }
}
