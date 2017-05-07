package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.CacheUtils;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int fromType = intent.getIntExtra("fromType", 0);
        int fuidx = intent.getIntExtra("fuidx", 0);
        int oldidx = CacheUtils.getInt(this, "oldidx");
        String oldid = CacheUtils.getString(this, "oldid");
        String pwd = CacheUtils.getString(this, "password");

        if (fromType == 3) {
            initTitle(this, "欢乐直播登录");
        } else {
            initTitle(this, "水晶直播登录");
        }

        String url = "http://liveh5.happy88.com/event170406/niupaijump.aspx?uidx=" + oldidx + "&plattype=" + fromType +
                "&type=30&usename=" + oldid + "&pwd=" + pwd + "&fuidx=" + fuidx;

        webView.loadUrl(url);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_web_view, null);
    }
}
