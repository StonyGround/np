package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;

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
 * Created by pc on 17-4-24.
 */

public class FragmentWorks extends Fragment {

    @BindView(R.id.rv_works)
    RecyclerView rvWorks;
    Unbinder unbinder;
    @BindView(R.id.works_nothing)
    TextView worksNothing;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    String result = AESUtil.decode(json);
                    Gson gson = new Gson();
                    List<IndexBean.ResultBean> resultBean = gson.fromJson(result, IndexBean
                            .class).getResult();
                    if (resultBean.size() == 0) {
                        worksNothing.setVisibility(View.VISIBLE);
                        return;
                    }
                    WorksAdapter adapter = new WorksAdapter(getContext(), resultBean);
                    adapter.setOnItemClickListener(new WorksAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, IndexBean.ResultBean data) {
                            Intent intent = new Intent(getActivity(), VideoActivity.class);
                            intent.putExtra("video", data);
                            startActivity(intent);
                        }
                    });
                    rvWorks.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        Log.e("FragmentWorks", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        unbinder = ButterKnife.bind(this, view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvWorks.setLayoutManager(gridLayoutManager);
        rvWorks.addItemDecoration(new SpaceItemDecoration(2));

//        String url = getArguments().getString("url");
        int buidx = CacheUtils.getInt(getActivity(), "buidx");
        String worksUrl = Contact.HOST + Contact.TAB_WORKS + "?uidx=" + buidx + "&loginUidx=" +
                buidx + "&begin=1&num=100";
        Log.e("work", worksUrl);
//        OkHttpUtils.getOkHttpUtils().get(worksUrl, new OkHttpUtils.MCallBack
//                () {
//            @Override
//            public void onResponse(String json) {
//                String result = AESUtil.decode(json);
//                Log.e("work", result);
//                Gson gson = new Gson();
//                List<UserWorksBean.ResultBean> resultBean = gson.fromJson(result, UserWorksBean
//                        .class).getResult();
//                if (resultBean.size() == 0) {
//                    worksNothing.setVisibility(View.VISIBLE);
//                    return;
//                }
//                rvWorks.setAdapter(new WorksAdapter(getContext(), resultBean));
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//        });

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(worksUrl);
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
