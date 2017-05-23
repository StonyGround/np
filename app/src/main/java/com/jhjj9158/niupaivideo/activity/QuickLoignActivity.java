package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.MyApplication;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.LoginResultBean;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.MD5Util;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuickLoignActivity extends BaseActivity {

    @BindView(R.id.ll_login_wechat)
    LinearLayout llLoginWechat;
    @BindView(R.id.ll_login_qq)
    LinearLayout llLoginQq;
    @BindView(R.id.ll_login_sina)
    LinearLayout llLoginSina;
    @BindView(R.id.tv_login_crystal)
    TextView tvLoginCrystal;
    @BindView(R.id.tv_login_happy)
    TextView tvLoginHappy;
    @BindView(R.id.tv_agressment_1)
    TextView tvAgressment1;
    @BindView(R.id.tv_agressment)
    TextView tvAgressment;

    private UMShareAPI mShareAPI = null;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String jsonLogin = msg.obj.toString();
                    Gson gson = new Gson();
                    LoginResultBean loginResult = gson.fromJson(jsonLogin, LoginResultBean.class);
                    CommonUtil.showTextToast(QuickLoignActivity.this, loginResult.getMsg());
                    if (loginResult.getCode() == 100) {
                        CacheUtils.setInt(QuickLoignActivity.this, "useridx", loginResult
                                .getData().get(0).getUseridx());
                        CacheUtils.setInt(QuickLoignActivity.this, "oldidx", loginResult
                                .getData().get(0).getOldidx());
                        CacheUtils.setString(QuickLoignActivity.this, "oldid", loginResult
                                .getData().get(0).getOldid());
                        String pwd = loginResult.getData().get(0).getPassword();
                        CacheUtils.setString(QuickLoignActivity.this, "password", MD5Util.md5(pwd));
                        CommonUtil.updateInfo(QuickLoignActivity.this);
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "登录");
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);

        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
        UMShareAPI.get(this).setShareConfig(config);
        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_quick_loign, null);
    }

    @OnClick({R.id.ll_login_wechat, R.id.ll_login_qq, R.id.ll_login_sina, R.id.tv_login_crystal, R.id.tv_login_happy, R.id.tv_agressment})
    public void onViewClicked(View view) {
        SHARE_MEDIA platform = null;
        switch (view.getId()) {
            case R.id.ll_login_wechat:
                if (!MyApplication.api.isWXAppInstalled()) {
                    CommonUtil.showTextToast(QuickLoignActivity.this, "未安装微信客户端");
                    return;
                }
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "4146c1c15c8887a3d9916ef8fbcedcd7";
                MyApplication.api.sendReq(req);
//                platform = SHARE_MEDIA.WEIXIN;
//                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                break;
            case R.id.ll_login_qq:
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                break;
            case R.id.ll_login_sina:
                platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                break;
            case R.id.tv_login_crystal:
                Intent intentCrystal = new Intent(QuickLoignActivity.this, LoginCrystalActivity.class);
                intentCrystal.putExtra("platform", 11);
                startActivity(intentCrystal);
                break;
            case R.id.tv_login_happy:
                Intent intentHappy = new Intent(QuickLoignActivity.this, LoginCrystalActivity.class);
                intentHappy.putExtra("platform", 3);
                startActivity(intentHappy);
                break;
            case R.id.tv_agressment:
                Intent webIntent = new Intent(this, WebViewActivity.class);
                webIntent.putExtra("fromType", Contact.WEBVIEW_AGREEMENT);
                webIntent.putExtra("url","file:///android_asset/agreement.html");
                startActivity(webIntent);
                break;
        }

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String media = platform.toString();
            if (media.equals("SINA")) {
                getUserInfoBySina(data);
            } else if (media.equals("WEIXIN")) {
//                getUserInfoByWeixin(data);
            } else if (media.equals("QQ")) {
                getUserInfoByQQ(data);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    private void getUserInfoByQQ(Map<String, String> map) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String openid = null;
        String token = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("openid")) {
                openid = entry.getValue();
            }
            if (entry.getKey().equals("access_token")) {
                token = entry.getValue();
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_QQ + "?platid=0&openid=" + openid + "&token=" + token);
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
    }

    private void getUserInfoBySina(Map<String, String> map) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String openid = null;
        String token = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("uid")) {
                openid = entry.getValue();
            }
            if (entry.getKey().equals("access_token")) {
                token = entry.getValue();
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_SINA + "?platid=0&openid=" + openid + "&token=" + token);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        String code = CacheUtils.getString(QuickLoignActivity.this, "code_weixin");
        if (!code.isEmpty()) {
            CacheUtils.delString(QuickLoignActivity.this, "code_weixin");
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_WEIXIN + "?platid=0&code=" + code);
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
        }
        if (CacheUtils.getInt(QuickLoignActivity.this, "useridx") != 0) {
            finish();
        }
        MobclickAgent.onPageStart("QuickLoginActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("QuickLoginActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
