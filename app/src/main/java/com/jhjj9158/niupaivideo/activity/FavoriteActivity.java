package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
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

public class FavoriteActivity extends BaseActivity {

    @BindView(R.id.works_nothing)
    TextView worksNothing;
    @BindView(R.id.rv_works)
    RecyclerView rvWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle(this, "喜欢");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvWorks.setLayoutManager(gridLayoutManager);
        rvWorks.addItemDecoration(new SpaceItemDecoration(2));

        int uidx = CacheUtils.getInt(this, "useridx");
        String worksUrl = Contact.HOST + Contact.TAB_FAVORITE + "?uidx=" + uidx + "&loginUidx=" +
                uidx + "&begin=1&num=100";
        OkHttpUtils.getOkHttpUtils().get(worksUrl, new OkHttpUtils.MCallBack() {
            @Override
            public void onResponse(String json) {
                String result = AESUtil.decode(json);
                Log.e("work", result);
                Gson gson = new Gson();
                List<IndexBean.ResultBean> resultBean = gson.fromJson(result, IndexBean
                        .class).getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }

                WorksAdapter adapter = new WorksAdapter(FavoriteActivity.this, resultBean);
                adapter.setOnItemClickListener(new WorksAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, IndexBean.ResultBean data) {
                        Intent intent = new Intent(FavoriteActivity.this, VideoActivity.class);
                        intent.putExtra("video", data);
                        startActivity(intent);
                    }
                });
                rvWorks.setAdapter(new WorksAdapter(FavoriteActivity.this, resultBean));
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
