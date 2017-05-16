package com.jhjj9158.niupaivideo;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.v4.app.ActivityCompat;

import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.CrashHandler;
import com.jhjj9158.niupaivideo.utils.PicassoImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
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
        Config.DEBUG = false;
        UMShareAPI.get(this);
        Config.isJumptoAppStore = true;
        regToWx();
        initImagePicker();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setMultiMode(false);
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(500);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(500);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(500);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(500);//保存文件的高度。单位像素
    }
}
