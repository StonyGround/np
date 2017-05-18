package com.jhjj9158.niupaivideo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.NetworkType;
import com.jhjj9158.niupaivideo.broadcast.NetStateChangeReceiver;
import com.jhjj9158.niupaivideo.observer.NetStateChangeObserver;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {

    Toolbar toolbar;
    LinearLayout llChildContent;
    ImageView toolbar_back;
    TextView toolbar_title;
    LinearLayout ll_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.registerObserver(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(50,00,00,00));
        }

        llChildContent = (LinearLayout) findViewById(R.id.ll_child_content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_back = (ImageView) findViewById(R.id.toolbar_back);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        ll_toolbar = (LinearLayout) findViewById(R.id.ll_toolbar);

        View child = getChildView();
        ButterKnife.bind(this, child);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, -1);
        llChildContent.addView(child, params);
    }

    protected abstract View getChildView();

    protected void initTitle(final Activity activity, String title) {
        toolbar_title.setText(title);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    protected void hintTitle() {
        ll_toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }

    /**
     * 是否需要注册网络变化的Observer,如果不需要监听网络变化,则返回false;否则返回true.默认返回false
     */
    protected boolean needRegisterNetworkChangeObserver() {
        return true;
    }

    @Override
    public void onNetDisconnected() {
        CommonUtil.showTextToast(this, "怎么又没网啦!ヾ(。￣□￣)ﾂ゜゜゜", Toast.LENGTH_LONG);
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
    }
}
