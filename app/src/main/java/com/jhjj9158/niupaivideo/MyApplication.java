package com.jhjj9158.niupaivideo;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by pc on 17-4-10.
 */

public class MyApplication extends Application {

    {
        PlatformConfig.setWeixin("wx17181f643ff9a6c8", "6b16d435a31070b3ddb0cf0000e04445");
        PlatformConfig.setQQZone("1105995205", "UjRJFeXvj6EyH1Bq");
        PlatformConfig.setSinaWeibo("1694884006", "cd1be2e8b6f78d3d17b422473e47244c", "http://www.quliao.com/");
    }

    private static final String WEIXIN_APP_ID = "wx17181f643ff9a6c8";
    public static IWXAPI api;

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
        api.registerApp(WEIXIN_APP_ID);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Config.DEBUG = true;
        UMShareAPI.get(this);
        Config.isJumptoAppStore = true;
        regToWx();
    }

}
