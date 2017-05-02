package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.FollowAdapter;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.FollowBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class FollowActivity extends BaseActivity {

    @BindView(R.id.works_nothing)
    TextView worksNothing;
    @BindView(R.id.rv_works)
    RecyclerView rvWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "关注");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvWorks.setLayoutManager(linearLayoutManager);

        int buidx = getIntent().getIntExtra("buidx", 0);
        int uidx = CacheUtils.getInt(this, "useridx");
        if (buidx == 0) {
            buidx = uidx;
        }
        String worksUrl = Contact.HOST + Contact.GET_FOLLOW + "?uidx=" + uidx + "&buidx=" + buidx
                + "&begin=1&num=100";
        OkHttpUtils.getOkHttpUtils().get(worksUrl, new OkHttpUtils.MCallBack
                () {
            @Override
            public void onResponse(String json) {
                String result = AESUtil.decode(json);
                Gson gson = new Gson();
                List<FollowBean.ResultBean> resultBean = gson.fromJson(result, FollowBean
                        .class).getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setText("你还没有关注任何人");
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }

                FollowAdapter adapter = new FollowAdapter(FollowActivity.this, resultBean);
                adapter.setOnItemClickListener(new FollowAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, FollowBean.ResultBean data) {
                        Intent intent = new Intent(FollowActivity.this,PersonalActivity.class);
                        intent.putExtra("buidx", data.getUidx());
                        startActivity(intent);
                    }
                });
                rvWorks.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }
}
