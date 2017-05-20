package com.jhjj9158.niupaivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.SearchUserAdapter;
import com.jhjj9158.niupaivideo.bean.FollowBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_content)
    EditText searchContent;
    @BindView(R.id.search_cancel)
    TextView searchCancel;
    @BindView(R.id.search_rv_user)
    RecyclerView searchRvUser;
    @BindView(R.id.search_more)
    ImageView searchMore;
    @BindView(R.id.search_rl_user)
    RelativeLayout searchRlUser;
    @BindView(R.id.search_nothing)
    TextView searchNothing;

    private List<FollowBean.ResultBean> resultBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        searchRvUser.setLayoutManager(linearLayoutManager);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) searchContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(searchContent, 0);
                           }
                       },
                100);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            search();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void search() {

        if (TextUtils.isEmpty(searchContent.getText().toString())) {
            CommonUtil.showTextToast(this, "搜索内容不能为空");
            return;
        }

        String url = null;
        try {
            url = Contact.HOST + Contact.SEARCH_USER + "?uidx=" + CacheUtils.getInt(this, "useridx") + "&skey=" + URLEncoder.encode
                    (searchContent.getText
                            ().toString(), "utf-8") + "&begin=1&num=100";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpClientManager.get(url, new OKHttpCallback<FollowBean>() {
            @Override
            public void onResponse(FollowBean response) {
                searchRlUser.setVisibility(View.VISIBLE);
                resultBean = response.getResult();
                if (resultBean.size() == 0) {
                    searchNothing.setVisibility(View.VISIBLE);
                    return;
                }
                SearchUserAdapter adapter = new SearchUserAdapter(SearchActivity.this, resultBean);
                adapter.setOnItemClickListener(new SearchUserAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, FollowBean.ResultBean data) {
                        Intent intent = new Intent(SearchActivity.this, PersonalActivity.class);
                        intent.putExtra("buidx", data.getUidx());
                        startActivity(intent);
                    }
                });
                searchRvUser.setAdapter(adapter);
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_search, null);
    }

    @OnClick({R.id.search_cancel, R.id.search_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_cancel:
                finish();
                break;
            case R.id.search_more:
                Intent intent = new Intent(SearchActivity.this, SearchUserActivity.class);
                intent.putParcelableArrayListExtra("followBeanList", (ArrayList<? extends Parcelable>) resultBean);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(searchContent.getText().toString())) {
            search();
        }
    }
}
