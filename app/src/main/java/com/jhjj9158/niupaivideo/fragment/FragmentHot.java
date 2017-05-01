package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.adapter.AdapterHomeBanner;
import com.jhjj9158.niupaivideo.adapter.AdapterHomeRecyler;
import com.jhjj9158.niupaivideo.bean.BannerBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.widget.AdaptiveHeightlViewPager;
import com.jhjj9158.niupaivideo.widget.ExStaggeredGridLayoutManager;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;
import com.jhjj9158.niupaivideo.widget.MyLinearLayoutManger;
import com.jhjj9158.niupaivideo.widget.MyStaggeredGridLayoutManager;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.jhjj9158.niupaivideo.widget.StaggeredGridLayoutManagerUnScrollable;

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
 * Created by pc on 17-4-17.
 */

public class FragmentHot extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
        @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.viewpager_banner)
    AdaptiveHeightlViewPager viewpager_banner;
    @BindView(R.id.ll_point_group)
    LinearLayout ll_point_group;

    private int currentItem = Integer.MAX_VALUE / 2;

    private List<BannerBean.ResultBean> bannerList = new ArrayList<>();
    private int preSelectPositon = 0;
    private AdapterHomeRecyler adapterHomeRecyler;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case Contact.GET_HOT_DATA:
                    String j = AESUtil.decode(json);
                    setHotData(j);
                    break;
                case Contact.GET_BANNER_DATA:
                    setBannerData(AESUtil.decode(json));
                    break;
                case Contact.BANNER_START_ROLLING:
                    currentItem++;
                    viewpager_banner.setCurrentItem(currentItem);
                    if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                        handler.removeMessages(Contact.BANNER_START_ROLLING);
                    }
                    handler.postDelayed(new InternalRunnable(), 4000);
                    break;
                case Contact.BANNER_CHANGE_ROLLING:
                    currentItem = msg.arg1;
                    handler.postDelayed(new InternalRunnable(), 4000);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    class InternalRunnable implements Runnable {

        @Override
        public void run() {
            handler.sendEmptyMessage(Contact.BANNER_START_ROLLING);
        }

    }

    private void setHotData(String json) {
        Log.e("setHotData", json);
        Gson gson = new Gson();
        final List<IndexBean.ResultBean> resultBeanList = gson.fromJson(json, IndexBean.class)
                .getResult();
        adapterHomeRecyler = new AdapterHomeRecyler(getActivity(),
                resultBeanList);
        adapterHomeRecyler.setOnItemClickListener(new AdapterHomeRecyler.OnItemClickListener() {
            @Override
            public void onItemClick(int position, IndexBean.ResultBean data) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("video", data);
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(adapterHomeRecyler);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_hot, container, false);
        unbinder = ButterKnife.bind(this, view);

        getBannerData();

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerview.setLayoutManager(layoutManager);

//        recyclerview.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));
        recyclerview.addItemDecoration(new SpaceItemDecoration(5));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);

        swipeRefresh.setEnabled(false);
        swipeRefresh.setColorSchemeResources(R.color.button_login_click);
        swipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources()
                        .getDisplayMetrics()));

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .INDEX + "?type=1&uidx=1&begin=1&num=10&vid=0");
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
                message.what = Contact.GET_HOT_DATA;
                handler.sendMessage(message);
            }
        });

        return view;
    }

    private void getBannerData() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .GET_BANNER + "?stype=1");
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
                message.what = Contact.GET_BANNER_DATA;
                handler.sendMessage(message);
            }
        });
    }

    private void setBannerData(String json) {

        Gson gson = new Gson();
        bannerList = gson.fromJson(json, BannerBean.class).getResult();
        viewpager_banner.setAdapter(new AdapterHomeBanner(getActivity(), bannerList));

        viewpager_banner.setCurrentItem(currentItem);

//        viewpager_banner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return false;
//            }
//        });

        ll_point_group.removeAllViews();
        for (int i = 0; i < bannerList.size(); i++) {
            ImageView point = new ImageView(getActivity());
            point.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    20, 20);
            point.setLayoutParams(params);
            if (i == 0) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
                params.leftMargin = 20;
            }

            // 把点添加到LinearLayout中
            ll_point_group.addView(point);
        }

//        if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
//            handler.removeMessages(Contact.BANNER_START_ROLLING);
//        }
//        handler.postDelayed(new InternalRunnable(), 4000);

//        viewpager_banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
//                    handler.removeMessages(Contact.BANNER_START_ROLLING);
//                }
//                Message message = new Message();
//                message.arg1 = position;
//                message.what = Contact.BANNER_CHANGE_ROLLING;
//                handler.sendMessage(message);
//
//                int diff = (Integer.MAX_VALUE / 2) % (bannerList.size());
//                ll_point_group.getChildAt(preSelectPositon).setEnabled(false);
//                ll_point_group.getChildAt((position - diff) % bannerList.size()).setEnabled(true);
////
//                preSelectPositon = (position - diff) % bannerList.size();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_DRAGGING:
////                        home_swipe.setEnabled(false);
//                        if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
//                            handler.removeMessages(Contact.BANNER_START_ROLLING);
//                        }
//                        break;
//                    case ViewPager.SCROLL_STATE_IDLE:
////                        home_swipe.setEnabled(true);
//                        handler.postDelayed(new InternalRunnable(), 4000);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }

    @Override
    public void onDestroyView() {
//        swipeRefresh.setRefreshing(false);
        super.onDestroyView();
        unbinder.unbind();
    }
}
