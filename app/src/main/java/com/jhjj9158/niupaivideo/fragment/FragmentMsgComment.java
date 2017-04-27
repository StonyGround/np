package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.activity.WorksActivity;
import com.jhjj9158.niupaivideo.adapter.MsgCommentAdapter;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by oneki on 2017/4/27.
 */

public class FragmentMsgComment extends Fragment {

    @BindView(R.id.works_nothing)
    TextView worksNothing;
    @BindView(R.id.rv_works)
    RecyclerView rvWorks;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvWorks.setLayoutManager(linearLayoutManager);

        int uidx = CacheUtils.getInt(getActivity(), "useridx");
        String worksUrl = Contact.HOST + Contact.GET_COMMENT + "?uidx=" + uidx + "&cid=0&num=100";
        OkHttpUtils.getOkHttpUtils().get(worksUrl, new OkHttpUtils.MCallBack() {
            @Override
            public void onResponse(String json) {
                String result = AESUtil.decode(json);
                Gson gson = new Gson();
                List<MsgCommentBean.ResultBean> resultBean = gson.fromJson(result, MsgCommentBean
                        .class).getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }
                MsgCommentAdapter adapter = new MsgCommentAdapter(getActivity(), resultBean);
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
}
