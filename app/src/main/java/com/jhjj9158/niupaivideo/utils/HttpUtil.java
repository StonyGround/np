package com.jhjj9158.niupaivideo.utils;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by oneki on 2017/5/11.
 */

public class HttpUtil {

//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }


//    public void get(String url, OkHttpUtils.MCallBack callBack) {
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        Request.Builder requestBuilder = new Request.Builder().url(url);
//        requestBuilder.method("GET", null);
//        Request request = requestBuilder.build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
////                getJson(json, cls);
//                mainThread(json);
//            }
//        });
//
//        this.mCallBack = callBack;
//    }
}
