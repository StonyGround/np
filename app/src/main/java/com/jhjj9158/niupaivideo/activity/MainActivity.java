package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.GoogleLocationBean;
import com.jhjj9158.niupaivideo.bean.NetworkType;
import com.jhjj9158.niupaivideo.broadcast.NetStateChangeReceiver;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.fragment.FragmentHome;
import com.jhjj9158.niupaivideo.fragment.FragmentMy;
import com.jhjj9158.niupaivideo.observer.NetStateChangeObserver;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NetStateChangeObserver {

    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.tab_host)
    FragmentTabHost tabHost;

    private LayoutInflater inflater;

    private List<View> tabList;
    private Class[] fragmentArray = new Class[]{FragmentHome.class, FragmentMy.class};
    private static final String[] tabTitles = new String[]{"首页", "我的"};
    private int[] imgRes = new int[]{R.drawable.btn_tab_home, R.drawable.btn_tab_my};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
//            CacheUtils.setInt(this, "useridx", 1628007796);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View
                    .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(50, 00, 00, 00));
        }

        boolean isStartMain = CacheUtils.getBoolean(this,
                Contact.IS_START_MAIN);
        if (!isStartMain) {
            startActivity(new Intent(this, GuideActivity.class));
        }

        if (!CommonUtil.checkPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Contact.CHECK_PERMISSION);
        } else {
            CommonUtil.updateInfo(MainActivity.this);
        }

        inflater = LayoutInflater.from(this);
        tabList = getTabViewList(tabTitles.length);
        initiTabHost();

        MobclickAgent.openActivityDurationTrack(false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CommonUtil.updateInfo(MainActivity.this);
                }
                break;
        }
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_main, null);
    }

    private List<View> getTabViewList(int len) {
        List<View> list = new ArrayList<>();
        if (len <= 0) return list;
        for (int i = 0; i < len; i++) {
            list.add(getTabItemView(i));
        }
        return list;
    }

    private View getTabItemView(int tabIndex) {
        View view = inflater.inflate(R.layout.item_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(imgRes[tabIndex]);
        TextView tab_name = (TextView) view.findViewById(R.id.tab_name);
        tab_name.setText(tabTitles[tabIndex]);
        return view;
    }


    private void initiTabHost() {
        int tab = getIntent().getIntExtra("tab", 0);
        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setCurrentTab(tab);
        tabHost.setup(this, getSupportFragmentManager(), R.id.fg_main);
        tabHost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < fragmentArray.length; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabTitles[i]).setIndicator(tabList.get(i));
            //将Tab按钮添加进Tab选项卡中
            tabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        tabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CacheUtils.getInt(MainActivity.this, "useridx") == 0) {
                    startActivity(new Intent(MainActivity.this, QuickLoignActivity
                            .class));
                } else {
                    tabHost.setCurrentTab(1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CacheUtils.getInt(MainActivity.this, "useridx") == 0) {
            tabHost.setCurrentTab(0);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @OnClick(R.id.iv_screen)
    public void onViewClicked() {
        CommonUtil.showTextToast(this, "敬请期待");
    }
}
