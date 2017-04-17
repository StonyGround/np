package com.jhjj9158.niupaivideo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.bean.TabTitleBean;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by pc on 17-4-1.
 */

public class FragmentHome extends Fragment {

    @BindView(R.id.viewpager)
    HorizontalScrollViewPager viewpager;
    @BindView(R.id.tablLayout)
    TabLayout tabLayout;
    Unbinder unbinder;

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<TabTitleBean.ResultBean> resultBeanList = new ArrayList<>();

    private FragmentTransaction transaction;
    private FragmentDynamic fragmentDynamic;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    Gson gson = new Gson();
                    resultBeanList = gson.fromJson(json, TabTitleBean.class).getResult();
                    for (int i = 0; i < resultBeanList.size(); i++) {
                        titles.add(new String(Base64.decode(resultBeanList.get(0).getVrname()
                                .getBytes(), Base64.DEFAULT)));

                        fragmentDynamic = new FragmentDynamic();
                        transaction = getFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        Log.e("FragmentHome",new String(Base64.decode(resultBeanList.get(0)
                                        .getVrid().getBytes(), Base64.DEFAULT)));
                        bundle.putString("type", new String(Base64.decode(resultBeanList.get(0)
                                .getVrid().getBytes(), Base64.DEFAULT)));
                        fragmentDynamic.setArguments(bundle);
                        transaction.commit();
                        fragmentList.add(fragmentDynamic);
                    }
                    TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(getFragmentManager(),
                            fragmentList, titles);
                    viewpager.setAdapter(tabFragmentAdapter);
                    tabLayout.setupWithViewPager(viewpager);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles.add("hot");
        titles.add("new");
        titles.add("follow");
        fragmentList.add(new FragmentHot());
        fragmentList.add(new FragmentNew());
        fragmentList.add(new FragmentFollow());


        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .TAB_TITLE);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                setIndicator(tabLayout,10,10);
//            }
//        });

        return view;
    }
//
//    public void setIndicator (TabLayout tabs,int leftDip,int rightDip) {
//        Class<?> tabLayout = tabs.getClass();
//        Field tabStrip = null;
//        try {
//            tabStrip = tabLayout.getDeclaredField("mTabStrip");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        tabStrip.setAccessible(true);
//        LinearLayout llTab = null;
//        try {
//            llTab = (LinearLayout) tabStrip.get(tabs);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip,
// Resources.getSystem().getDisplayMetrics());
//        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip,
// Resources.getSystem().getDisplayMetrics());
//
//        for (int i = 0; i < llTab.getChildCount(); i++) {
//            View child = llTab.getChildAt(i);
//            child.setPadding(0, 0, 0, 0);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout
// .LayoutParams.MATCH_PARENT, 1);
//            params.leftMargin = left;
//            params.rightMargin = right;
//            child.setLayoutParams(params);
//            child.invalidate();
//        }
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.e("FragmentHome", "onDestroyView");
    }
}
