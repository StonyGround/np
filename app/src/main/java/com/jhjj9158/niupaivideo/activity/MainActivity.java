package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.fragment.FragmentHome;
import com.jhjj9158.niupaivideo.fragment.FragmentMy;
import com.jhjj9158.niupaivideo.utils.CacheUtils;

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

    private FragmentHome fragmentHome;
    private FragmentMy fragmentMy;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ivHome.setBackgroundResource(R.drawable.icon_home_selected);

        fragmentHome = new FragmentHome();
        fragmentMy = new FragmentMy();
        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fg_main, fragmentMy).hide(fragmentMy).add(R.id.fg_main,
                    fragmentHome);
            transaction.commit();
        }
    }

    @OnClick({R.id.ll_home, R.id.ll_my})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                ivHome.setBackgroundResource(R.drawable.icon_home_selected);
                ivMy.setBackgroundResource(R.drawable.icon_my);
                switchFragment(fragmentMy, fragmentHome);
                break;
            case R.id.ll_my:
                ivHome.setBackgroundResource(R.drawable.icon_home);
                ivMy.setBackgroundResource(R.drawable.icon_my_selected);
                switchFragment(fragmentHome, fragmentMy);
                if (CacheUtils.getInt(MainActivity.this, "useridx") == 0) {
                    Log.e("useridx",String.valueOf(CacheUtils.getInt(MainActivity.this, "useridx")));
                    startActivity(new Intent(MainActivity.this, QuickLoignActivity.class));
                }
                break;
        }
    }

    /**
     * 切换Fragment,不能使用replace,会导致ViewPagerIndicator失效
     *
     * @param from
     * @param to
     */
    public void switchFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from).add(R.id.fg_main, to).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy");
    }
}
