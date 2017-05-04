package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.fragment.FragmentHome;
import com.jhjj9158.niupaivideo.fragment.FragmentMy;
import com.jhjj9158.niupaivideo.fragment.TestFragment;
import com.jhjj9158.niupaivideo.utils.CacheUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.fg_main)
    FrameLayout fgMain;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.ll_my)
    LinearLayout llMy;
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.iv_my)
    ImageView ivMy;
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
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            CacheUtils.setInt(this, "useridx", 1628007796);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                getWindow().getDecorView().setSystemUiVisibility(View
                        .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.argb(100, 00, 00, 00));
            }

            inflater = LayoutInflater.from(this);
            tabList = getTabViewList(tabTitles.length);
            initiTabHost();
        }
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
                }else{
                    tabHost.setCurrentTab(1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CacheUtils.getInt(MainActivity.this, "useridx") == 0){
            tabHost.setCurrentTab(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
