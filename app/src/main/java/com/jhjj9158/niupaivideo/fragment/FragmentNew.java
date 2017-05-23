package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.jhjj9158.niupaivideo.widget.AdaptiveHeightlViewPager;
import com.jhjj9158.niupaivideo.widget.GridSpacingItemDecoration;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by pc on 17-4-17.
 */

public class FragmentNew extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Unbinder unbinder;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    AdaptiveHeightlViewPager viewpager_banner;
    LinearLayout ll_point_group;

    private int currentItem = Integer.MAX_VALUE / 2;

    private List<BannerBean.ResultBean> bannerList = new ArrayList<>();
    private int preSelectPositon = 0;
    private int minVid = 0;
    private StaggeredGridLayoutManager layoutManager;
    private AdapterHomeRecyler adapterHomeRecyler;
    private boolean isRefresh = false;
    private View topView;
    private boolean isLoadMore = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Contact.GET_NEW_DATA:
                    String jsonNew = msg.obj.toString();
                    setNewData(AESUtil.decode(jsonNew));
                    break;
                case Contact.GET_BANNER_DATA:
                    String jsonBanner = msg.obj.toString();
                    setBannerData(AESUtil.decode(jsonBanner));
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
                case Contact.NET_ERROR:
                    CommonUtil.showTextToast(getActivity(), "网络请求超时");
                    swipeRefresh.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setNewData(String json) {
        Gson gson = new Gson();
        List<IndexBean.ResultBean> resultBeanList = gson.fromJson(json, IndexBean.class)
                .getResult();
        minVid = CommonUtil.getMinVid(resultBeanList);
        if (isRefresh && adapterHomeRecyler != null) {
            isRefresh = false;
            adapterHomeRecyler.addRefreshDatas(resultBeanList);
            swipeRefresh.setRefreshing(false);
            return;
        }

        if (isLoadMore && adapterHomeRecyler != null) {
            isLoadMore = false;
            adapterHomeRecyler.addDatas(resultBeanList);
            swipeRefresh.setRefreshing(false);
            return;
        }

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
        getBannerData();
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("FragmentNew", "onCreate");

    }

    private int index;

    private void getNewData(int begin) {
        index = begin + 8;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .INDEX + "?type=2&uidx=1&begin=" + begin + "&num=" + 8 + "&vid=" + minVid);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = Contact.NET_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = Contact.GET_NEW_DATA;
                handler.sendMessage(message);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        Log.e("FragmentNew", "onCreateView");
        View view = inflater.inflate(R.layout.tab_hot, container, false);
        unbinder = ButterKnife.bind(this, view);

        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerview.setLayoutManager(layoutManager);

        topView = LayoutInflater.from(getActivity()).inflate(R.layout.home_top, recyclerview,
                false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    Log.d("FragmentHot", "---");
                    int[] lastPositions = new int[layoutManager.getSpanCount()];
                    layoutManager.findLastVisibleItemPositions(lastPositions);

                    Integer[] obj = new Integer[lastPositions.length];
                    for (int i = 0; i < lastPositions.length; i++) {
                        obj[i] = lastPositions[i];
                    }
                    int lastPosition = Collections.max(Arrays.asList(obj));

                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        isLoadMore = true;
                        getNewData(index);
                    }
                }
            }
        });

        swipeRefresh.setColorSchemeResources(R.color.button_login_click);
        swipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources()
                        .getDisplayMetrics()));
        swipeRefresh.setOnRefreshListener(this);
        getNewData(0);
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
                Message message = new Message();
                message.what = Contact.NET_ERROR;
                handler.sendMessage(message);
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

    private class InternalRunnable implements Runnable {

        @Override
        public void run() {
            handler.sendEmptyMessage(Contact.BANNER_START_ROLLING);
        }

    }

    private void setBannerData(String json) {

        Gson gson = new Gson();
        bannerList = gson.fromJson(json, BannerBean.class).getResult();

        viewpager_banner = (AdaptiveHeightlViewPager) topView.findViewById(R.id.viewpager_banner);
        ll_point_group = (LinearLayout) topView.findViewById(R.id.ll_point_group);
        viewpager_banner.setAdapter(new AdapterHomeBanner(getActivity(), bannerList));
        viewpager_banner.setCurrentItem(currentItem);

        viewpager_banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        if (bannerList.size() > 1) {
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
        }

        if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
            handler.removeMessages(Contact.BANNER_START_ROLLING);
        }
        handler.postDelayed(new InternalRunnable(), 4000);

        viewpager_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                    handler.removeMessages(Contact.BANNER_START_ROLLING);
                }
                Message message = new Message();
                message.arg1 = position;
                message.what = Contact.BANNER_CHANGE_ROLLING;
                handler.sendMessage(message);

                int diff = (Integer.MAX_VALUE / 2) % (bannerList.size());
                ll_point_group.getChildAt(preSelectPositon).setEnabled(false);
                ll_point_group.getChildAt((position - diff) % bannerList.size()).setEnabled(true);
//
                preSelectPositon = (position - diff) % bannerList.size();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        swipeRefresh.setEnabled(false);
                        if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                            handler.removeMessages(Contact.BANNER_START_ROLLING);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        swipeRefresh.setEnabled(true);
                        handler.postDelayed(new InternalRunnable(), 4000);
                        break;
                    default:
                        break;
                }
            }
        });
        adapterHomeRecyler.setHeaderView(topView);
        recyclerview.setAdapter(adapterHomeRecyler);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentNew");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentNew");
    }

    @Override
    public void onDestroyView() {
        Log.e("FragmentNew", "onDestroyView");
        swipeRefresh.removeAllViews();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        getNewData(0);
    }
}
