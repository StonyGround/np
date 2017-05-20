package com.jhjj9158.niupaivideo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.feedback_content)
    EditText feedbackContent;
    @BindView(R.id.feedback_confirm)
    TextView feedbackConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        initTitle(this, "意见反馈");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_feedback, null);
    }

    @OnClick(R.id.feedback_confirm)
    public void onViewClicked() {
        String url = Contact.HOST + Contact.FEEDBACK + "?uidx=" + CacheUtils.getInt(this, "useridx") + "&content=" + feedbackContent
                .getText().toString() + "&email=";
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject object = new JSONObject((String) response);
                    int result = object.getInt("result");
                    if (result == 1) {
                        CommonUtil.showTextToast(FeedbackActivity.this, "吐槽成功!");
                        finish();
                    } else {
                        CommonUtil.showTextToast(FeedbackActivity.this, "吐槽失败了~");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }
}
