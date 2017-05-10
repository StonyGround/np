package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by pc on 17-4-24.
 */

public class FragmentFavorite extends Fragment {

    @BindView(R.id.rv_works)
    RecyclerView rvWorks;
    Unbinder unbinder;
    @BindView(R.id.works_nothing)
    TextView worksNothing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        Log.e("FragmentFavorite", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        unbinder = ButterKnife.bind(this, view);

        int buidx = CacheUtils.getInt(getActivity(), "buidx");
        String faovriteUrl = Contact.HOST + Contact.TAB_FAVORITE + "?uidx=" + buidx +
                "&loginUidx=" + buidx + "&begin=1&num=100";
        OkHttpUtils.getOkHttpUtils().get(faovriteUrl, new OkHttpUtils.MCallBack() {
            @Override
            public void onResponse(String json) {
                String result = AESUtil.decode(json);
                Log.e("faovrite", result);
                Gson gson = new Gson();
                List<IndexBean.ResultBean> resultBean = gson.fromJson(result, IndexBean
                        .class).getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
                rvWorks.setLayoutManager(gridLayoutManager);
                rvWorks.addItemDecoration(new SpaceItemDecoration(2));
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
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentFavorite");
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentFavorite");
    }
}
