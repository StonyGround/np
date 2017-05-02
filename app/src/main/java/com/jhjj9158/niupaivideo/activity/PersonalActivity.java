package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.bean.FollowPostBean;
import com.jhjj9158.niupaivideo.bean.PersonalBean;
import com.jhjj9158.niupaivideo.fragment.FragmentFavorite;
import com.jhjj9158.niupaivideo.fragment.FragmentWorks;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.BlurBitmapUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalActivity extends FragmentActivity {

    private static final int GET_OTHERS_INFO = 0;
    private static final int FOLLOW = 1;

    @BindView(R.id.personal_back)
    ImageView personalBack;
    @BindView(R.id.personal_name)
    TextView personalName;
    @BindView(R.id.personal_gender)
    ImageView personalGender;
    @BindView(R.id.personal_more)
    ImageView personalMore;
    @BindView(R.id.personal_follow)
    TextView personalFollow;
    @BindView(R.id.personal_fans)
    TextView personalFans;
    @BindView(R.id.personal_id)
    TextView personalId;
    @BindView(R.id.btn_personal_follow)
    TextView btnPersonalFollow;
    @BindView(R.id.personal_signature)
    TextView personalSignature;
    @BindView(R.id.ll_personal_info)
    LinearLayout llPersonalInfo;
    @BindView(R.id.personal_tab)
    TabLayout personalTab;
    @BindView(R.id.personal_headimg)
    CircleImageView personalHeadimg;
    @BindView(R.id.personal_bg)
    ImageView personalBg;
    @BindView(R.id.personal_viewpager)
    HorizontalScrollViewPager personalViewpager;

    private int buidx;
    private int uidx;
    private int isFollow;
    private int fansNum;

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case GET_OTHERS_INFO:
                    setPersonalInfo(AESUtil.decode(json));
                    break;
                case FOLLOW:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String result = jsonObject.getString("msg");
                        if (result.equals("关注成功")) {
                            isFollow = 1;
                            btnPersonalFollow.setText(R.string.unfollow);
                            fansNum = fansNum + 1;
                            personalFans.setText(" " + "粉丝" + fansNum);
                        } else {
                            isFollow = 0;
                            btnPersonalFollow.setText(R.string.tv_personal_follow);
                            fansNum = fansNum - 1;
                            personalFans.setText(" " + "粉丝" + fansNum);
                        }
                        CommonUtil.showTextToast(result, PersonalActivity
                                .this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setPersonalInfo(String json) {
        Gson gson = new Gson();
        PersonalBean.ResultBean resultBean = gson.fromJson(json, PersonalBean.class).getResult();
        String name = new String(Base64.decode(resultBean.getNickName().getBytes(),
                Base64.DEFAULT));
        String headImage = new String(Base64.decode(resultBean.getHeadphoto().getBytes(),
                Base64.DEFAULT));
        if (!headImage.contains("http")) {
            headImage = "http://" + headImage;
        }
        int followNum = resultBean.getFollowNum();
        fansNum = resultBean.getFansNum();
        String signature = new String(Base64.decode(resultBean.getSignature().getBytes(),
                Base64.DEFAULT));
        int showuidx = resultBean.getShowuidx();
        isFollow = resultBean.getIsFollow();
        int worksNum = resultBean.getVNum();
        int favoriteNum = resultBean.getZanNum();

        personalName.setText(name);
        if (resultBean.getGender() == 2) {
            personalGender.setImageResource(R.drawable.women);
        } else if (resultBean.getGender() == 0) {
            personalGender.setVisibility(View.GONE);
        }
        Picasso.with(this).load(headImage).into(personalHeadimg);
        personalFollow.setText("关注" + followNum + " |");
        personalFans.setText(" " + "粉丝" + fansNum);
        personalSignature.setText(signature);
        personalId.setText("ID:" + showuidx);
        if (isFollow == 1) {
            btnPersonalFollow.setText("取消关注");
        }
        Picasso.with(this).load(headImage).transform(new BlurBitmapUtil.BlurTransformation(this))
                .into(personalBg);

        titles.add("作品" + worksNum);
        titles.add("喜欢" + favoriteNum);
        initTabPager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(99, 00, 00, 00));
        }


        buidx = getIntent().getIntExtra("buidx", 0);
        uidx = CacheUtils.getInt(this, "useridx");
        CacheUtils.setInt(this, "buidx", buidx);


        fragmentList.add(new FragmentWorks());
        fragmentList.add(new FragmentFavorite());


        getPersonalInfo();

    }

    private void initTabPager() {

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter
                (getSupportFragmentManager(), fragmentList, titles);
        personalViewpager.setAdapter(tabFragmentAdapter);
        personalTab.setTabMode(TabLayout.MODE_FIXED);
        personalTab.setupWithViewPager(personalViewpager);
    }

    private FragmentTransaction transaction;

    private void getPersonalInfo() {
        String url = Contact.HOST + Contact.PERSONAL_INFO + "?uidx=" + uidx + "&buidx=" + buidx;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
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
                message.what = GET_OTHERS_INFO;
                handler.sendMessage(message);
            }
        });
    }

    @OnClick({R.id.personal_more, R.id.personal_back, R.id.btn_personal_follow, R.id
            .personal_follow, R.id.personal_fans})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_more:
                break;
            case R.id.btn_personal_follow:
                if (isFollow == 1) {
                    setFollow(2);
                } else {
                    setFollow(1);
                }
                break;
            case R.id.personal_back:
                finish();
                break;
            case R.id.personal_follow:
                Intent intent = new Intent(PersonalActivity.this, FollowActivity.class);
                intent.putExtra("buidx", buidx);
                startActivity(intent);
                break;
            case R.id.personal_fans:
                Intent intentFans = new Intent(PersonalActivity.this, FansActivity.class);
                intentFans.putExtra("buidx", buidx);
                startActivity(intentFans);
                break;
        }
    }

    private void setFollow(int index) {
        FollowPostBean followPostBean = new FollowPostBean();
        followPostBean.setOpcode("FocusonOrDeletecurd_friends");
        followPostBean.setUseridx(uidx);
        followPostBean.setFriendidx(buidx);
        followPostBean.setIndex(index);

        Gson gson = new Gson();
        String json = gson.toJson(followPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(json, Contact.KEY))
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
                message.what = FOLLOW;
                handler.sendMessage(message);
            }
        });
    }
}
