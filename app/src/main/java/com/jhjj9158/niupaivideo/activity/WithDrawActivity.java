package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class WithDrawActivity extends BaseActivity {

    @BindView(R.id.withdraw_account_name)
    TextView withdrawAccountName;
    @BindView(R.id.withdraw_account_edit)
    ImageView withdrawAccountEdit;
    @BindView(R.id.withdraw_money)
    EditText withdrawMoney;
    @BindView(R.id.withdraw_current_money)
    TextView withdrawCurrentMoney;
    @BindView(R.id.withdraw_confirm)
    TextView withdrawConfirm;
    private boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        initTitle(this, "提现");
        final double currentMoney = getIntent().getDoubleExtra("money", 0);
//        final double currentMoney = 120.35;

        if (TextUtils.isEmpty(CacheUtils.getString(this, "account_alipay"))) {
            withdrawAccountName.setText("添加账户");
            withdrawAccountName.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            withdrawAccountName.setText(CacheUtils.getString(this, "account_zfb"));
        }

        withdrawCurrentMoney.setText("当前钱包余额：" + currentMoney + "元");
        withdrawMoney.addTextChangedListener(new TextWatcher() {
            boolean deleteLastChar;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过三位数值,则删掉最后一位
                    int length = s.length() - s.toString().lastIndexOf(".");
                    // 说明后面有三位数值
                    deleteLastChar = length >= 4;
                }
                userIsEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                if (deleteLastChar) {
                    // 设置新的截取的字符串
                    withdrawMoney.setText(s.toString().substring(0, s.toString().length() - 1));
                    // 光标强制到末尾
                    withdrawMoney.setSelection(withdrawMoney.getText().length());
                }
                // 以小数点开头，前面自动加上 "0"
                if (s.toString().startsWith(".")) {
                    withdrawMoney.setText("0" + s);
                    withdrawMoney.setSelection(withdrawMoney.getText().length());
                }
                if (s.length() > 0) {
                    if (Double.parseDouble(s.toString()) > currentMoney) {
                        withdrawMoney.setText(String.valueOf(currentMoney));
                        withdrawMoney.setSelection(withdrawMoney.getText().length());
                    }
                }
                userIsEmpty();
            }
        });
    }

    public void userIsEmpty() {
        if (!TextUtils.isEmpty(withdrawMoney.getText()) && !TextUtils.isEmpty(CacheUtils.getString(this, "account_alipay"))) {
            isClick = true;
            withdrawConfirm.setBackgroundResource(R.drawable.btn_circle_quit);
        } else {
            isClick = false;
            withdrawConfirm.setBackgroundResource(R.drawable.btn_confirm_unclick);
        }
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_with_draw, null);
    }

    @OnClick({R.id.withdraw_rl_account, R.id.withdraw_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.withdraw_rl_account:
                startActivity(new Intent(this, AccountEditActivity.class));
                break;
            case R.id.withdraw_confirm:
                if (!isClick)
                    return;

                if (Double.parseDouble(withdrawMoney.getText().toString()) < 100) {
                    CommonUtil.showTextToast(this, "提现金额不能少于100元哦~");
                    return;
                }

                String url = Contact.HOST + Contact.WITHDRAW + "?uidx=" + CacheUtils.getInt(this, "useridx") + "&nickName=" +
                        CacheUtils.getString(this, "nickName") + "&alipay=" + CacheUtils.getString(this, "account_alipay") +
                        "&alipayName=" + CacheUtils.getString(this, "account_name") + "&wallet=" + withdrawMoney.getText().toString();
                OkHttpClientManager.get(url, new OKHttpCallback() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject object = new JSONObject((String) response);
                            String errorcode = object.getString("errorcode");
                            if (errorcode.equals("00000:ok")) {
                                CommonUtil.showTextToast(WithDrawActivity.this, "提现申请成功");
                                finish();
                            } else {
                                CommonUtil.showTextToast(WithDrawActivity.this, "提现申请失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(IOException e) {

                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(CacheUtils.getString(this, "account_alipay"))) {
            withdrawAccountName.setText("添加账户");
            withdrawAccountName.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            withdrawAccountName.setText(CacheUtils.getString(this, "account_alipay"));
        }
    }
}
