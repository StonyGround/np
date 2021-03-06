package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.AccountEditActivity;
import com.jhjj9158.niupaivideo.activity.FansActivity;
import com.jhjj9158.niupaivideo.activity.FavoriteActivity;
import com.jhjj9158.niupaivideo.activity.FollowActivity;
import com.jhjj9158.niupaivideo.activity.MessageActivity;
import com.jhjj9158.niupaivideo.activity.ModifyActivity;
import com.jhjj9158.niupaivideo.activity.SettingActivity;
import com.jhjj9158.niupaivideo.activity.WithDrawActivity;
import com.jhjj9158.niupaivideo.activity.WithdrawRuleActivity;
import com.jhjj9158.niupaivideo.activity.WorksActivity;
import com.jhjj9158.niupaivideo.bean.UserDetailBean;
import com.jhjj9158.niupaivideo.bean.UserInfoBean;
import com.jhjj9158.niupaivideo.bean.UserPostBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.BlurBitmapUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.jhjj9158.niupaivideo.widget.ResizableImageView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pc on 17-4-1.
 */

public class FragmentMy extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.tv_works_num)
    TextView tvWorksNum;
    @BindView(R.id.rl_works)
    RelativeLayout rlWorks;
    @BindView(R.id.tv_favorite_num)
    TextView tvFavoriteNum;
    @BindView(R.id.rl_favorite)
    RelativeLayout rlFavorite;
    @BindView(R.id.tv_follow_num)
    TextView tvFollowNum;
    @BindView(R.id.rl_follow)
    RelativeLayout rlFollow;
    @BindView(R.id.tv_fans_num)
    TextView tvFansNum;
    @BindView(R.id.rl_fans)
    RelativeLayout rlFans;
    @BindView(R.id.tv_make_money)
    TextView tvMakeMoney;
    @BindView(R.id.tv_withdraw)
    TextView tvWithdraw;
    @BindView(R.id.maked_num)
    TextView makedNum;
    @BindView(R.id.rl_daily_reward)
    RelativeLayout rlDailyReward;
    @BindView(R.id.tv_msg_num)
    TextView tvMsgNum;
    @BindView(R.id.rl_msg)
    RelativeLayout rlMsg;
    @BindView(R.id.rl_setting)
    RelativeLayout rlSetting;
    Unbinder unbinder;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_bio)
    TextView tvBio;
    @BindView(R.id.tv_wallte)
    TextView tvWallte;
    @BindView(R.id.fragment_my_bg)
    ResizableImageView fragmentMyBg;
    @BindView(R.id.tv_works)
    TextView tvWorks;
    @BindView(R.id.tv_favorite)
    TextView tvFavorite;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.tv_fans)
    TextView tvFans;
    @BindView(R.id.tv_rmb_sign)
    TextView tvRmbSign;
    @BindView(R.id.icon_daily_reward)
    ImageView iconDailyReward;
    @BindView(R.id.tv_yuan)
    TextView tvYuan;
    @BindView(R.id.iv_reward_more)
    ImageView ivRewardMore;
    @BindView(R.id.icon_msg)
    ImageView iconMsg;
    @BindView(R.id.iv_msg_more)
    ImageView ivMsgMore;
    @BindView(R.id.icon_setting)
    ImageView iconSetting;
    @BindView(R.id.iv_setting_more)
    ImageView ivSettingMore;
    @BindView(R.id.ll_my_info)
    LinearLayout llMyInfo;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    setUserInfo(json);
                    break;
                case 2:
                    setUserData(AESUtil.decode(json));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private UserDetailBean.ResultBean resultBean;

    private void setUserData(String json) {
        Gson gson = new Gson();
        resultBean = gson.fromJson(json, UserDetailBean.class)
                .getResult();
        tvId.setText("ID:" + resultBean.getShowuidx());
        tvWallte.setText(String.valueOf(resultBean.getWallet()));
        tvWorksNum.setText(String.valueOf(resultBean.getVNum()));
        tvFavoriteNum.setText(String.valueOf(resultBean.getLove()));
        tvFollowNum.setText(String.valueOf(resultBean.getFollowNum()));
        tvFansNum.setText(String.valueOf(resultBean.getFansNum()));
        int msgNum = resultBean.getNewmessage();
        if (msgNum > 0) {
            tvMsgNum.setVisibility(View.VISIBLE);
            tvMsgNum.setText(String.valueOf(msgNum));
            if (msgNum > 99) {
                tvMsgNum.setText("99+");
            }
        }
        if (resultBean.getGender() == 1) {
            ivGender.setImageResource(R.drawable.man);
        }
    }

    private void getUserDate() {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        int uid = CacheUtils.getInt(getContext(), "useridx");
        Request.Builder requestBuilder = new Request.Builder()
                .url(Contact.HOST + Contact
                        .GET_USER_INFO + "?uidx=" + uid + "&password=1");
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
                message.what = 2;
                handler.sendMessage(message);
            }
        });
    }

    private void getUserInfo() {

        int uid = CacheUtils.getInt(getContext(), "useridx");
        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("GetUserInfor");
        userPostBean.setUseridx(uid);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
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

    private void setUserInfo(String json) {
        Gson gson = new Gson();
        UserInfoBean userInfoBean = gson.fromJson(json, UserInfoBean.class);
        UserInfoBean.DataBean userInfo = userInfoBean.getData().get(0);
        if (userInfoBean.getCode() == 100) {
            String headImage = userInfo.getHeadimg();
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            Picasso.with(getContext()).load(headImage).placeholder(R.drawable.me_user_admin).into
                    (profileImage);
            try {
                tvName.setText(URLDecoder.decode(userInfo.getNickName(),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            CacheUtils.setString(getActivity(), "userName", "");
            if (userInfo.getUserSex().equals("1")) {
                ivGender.setBackgroundResource(R.drawable.man);
            }
            if (!userInfo.getUserTrueName().equals("")) {
                try {
                    tvBio.setText("签名：" + URLDecoder.decode(userInfo.getUserTrueName(),"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                tvBio.setText(R.string.bio_default);
            }
            Picasso.with(getContext()).load(headImage).placeholder(R.drawable.me_user_admin)
                    .transform(new BlurBitmapUtil
                            .BlurTransformation(getContext())).into(fragmentMyBg);
            CacheUtils.setString(getActivity(), "nickName", userInfo.getNickName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("FragmentMy", "onCreate");
    }

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        Log.e("FragmentMy", "onCreateView");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my, container, false);
            ButterKnife.bind(this, rootView);

            if (CacheUtils.getInt(getActivity(), "useridx") != 0) {
                getUserInfo();
                getUserDate();
                getReward();
                inSeven();
            }
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }


        return rootView;
    }

    private int inSevenResult = 1;

    private void inSeven() {
        String url = Contact.HOST + Contact.IN_SEVEN + "?uidx=" + CacheUtils.getInt(getActivity(), "useridx");
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject object = new JSONObject((String) response);
                    inSevenResult = object.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    private void getReward() {

    }

    @Override
    public void onResume() {
        Log.e("FragmentMy", "onResume");
        if (CacheUtils.getInt(getActivity(), "useridx") != 0) {
            getUserDate();
            getUserInfo();
            inSeven();
        }
        super.onResume();
        MobclickAgent.onPageStart("FragmentMy");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentMy");
    }

    @Override
    public void onDestroyView() {
        Log.e("FragmentMy", "onDestroyView");
        super.onDestroyView();
//        unbinder.unbind();
    }

    @OnClick({R.id.profile_image, R.id.rl_works, R.id.tv_make_money, R.id.tv_withdraw, R.id
            .rl_daily_reward, R.id.rl_msg, R.id.rl_setting, R.id.rl_favorite, R.id.rl_follow, R
            .id.rl_fans, R.id.ll_my_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                break;
            case R.id.rl_works:
                startActivity(new Intent(getActivity(), WorksActivity.class));
                break;
            case R.id.tv_make_money:
                break;
            case R.id.tv_withdraw:
                Intent withDrawintent = new Intent();
                if (resultBean.getWallet() < 100) {
                    withDrawintent.setClass(getActivity(), WithdrawRuleActivity.class);
                    withDrawintent.putExtra("rule", 1);
                } else {
                    if (inSevenResult == 1) {
                        withDrawintent.setClass(getActivity(), WithdrawRuleActivity.class);
                        withDrawintent.putExtra("rule", 2);
                    } else {
                        if (TextUtils.isEmpty(resultBean.getAlipay())) {
                            withDrawintent.setClass(getActivity(), AccountEditActivity.class);
                            withDrawintent.putExtra("money", resultBean.getWallet());
                        } else {
                            withDrawintent.setClass(getActivity(), WithDrawActivity.class);
                            withDrawintent.putExtra("alipay", new String(Base64.decode(resultBean.getAlipay().getBytes(),
                                    Base64.DEFAULT)));
                            withDrawintent.putExtra("alipay_name", new String(Base64.decode(resultBean.getAlipayName().getBytes(),
                                    Base64.DEFAULT)));
                            withDrawintent.putExtra("money", resultBean.getWallet());
                        }
                    }
                }
                startActivity(withDrawintent);
                break;
            case R.id.rl_daily_reward:
                break;
            case R.id.rl_msg:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.rl_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.rl_favorite:
                startActivity(new Intent(getActivity(), FavoriteActivity.class));
                break;
            case R.id.rl_follow:
                startActivity(new Intent(getActivity(), FollowActivity.class));
                break;
            case R.id.rl_fans:
                startActivity(new Intent(getActivity(), FansActivity.class));
                break;
            case R.id.ll_my_info:
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                intent.putExtra("userInfo", resultBean);
                startActivity(intent);
                break;
        }
    }
}
