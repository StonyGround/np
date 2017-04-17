package com.jhjj9158.niupaivideo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.AdapterHomeRecyler;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;

import java.io.IOException;
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

public class FragmentDynamic extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private LinearLayoutManager mLinearLayoutManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    Log.e("json", json);
                    setHotData(json);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setHotData(String json) {
        Gson gson = new Gson();
        List<IndexBean.ResultBean> resultBeanList = gson.fromJson(json, IndexBean.class)
                .getResult();
        AdapterHomeRecyler adapterHomeRecyler = new AdapterHomeRecyler(getActivity(),
                resultBeanList);
        recyclerview.setAdapter(adapterHomeRecyler);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_hot, container, false);
        unbinder = ButterKnife.bind(this, view);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(mLinearLayoutManager);

        swipeRefresh.setColorSchemeResources(R.color.button_login_click);
        swipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources()
                        .getDisplayMetrics()));

        Bundle bundle = getArguments();
        String type = "0";
        if (bundle != null) {
            type = bundle.getString("type");
        }

        OkHttpClient mOkHttpClient = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .TAB_DYNAMIC + "?vrtype=" + type + "&uidx=" + CacheUtils.getInt(getActivity(),
                "useridx") + "&begin=1&num=10&vid=0&aes=false");
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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
