package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.Settings;
import com.jhjj9158.niupaivideo.adapter.CommentAdapter;
import com.jhjj9158.niupaivideo.bean.CommentBean;
import com.jhjj9158.niupaivideo.bean.FollowPostBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.VideoDetailBean;
import com.jhjj9158.niupaivideo.bean.VideoIsFollowBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.dialog.DialogComment;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.InitiView;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.MediaController;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoActivity extends BaseActivity {

    private static final int VIDEO_INFO = 1;
    private static final int IS_FOLLOW = 2;
    private static final int VIDEO_FOLLOW = 3;
    private static final int COMMENT = 4;
    private static final int ADD_COMMENT = 5;
    private static final int FOLLOW = 6;

    @BindView(R.id.video_scrollView)
    ScrollView videoScrollView;
    @BindView(R.id.video_view)
    PLVideoTextureView videoView;
    @BindView(R.id.video_back)
    ImageView videoBack;
    @BindView(R.id.video_heart)
    ImageView videoHeart;
    @BindView(R.id.video_share)
    ImageView videoShare;
    @BindView(R.id.tv_input)
    TextView tvInput;
    @BindView(R.id.iv_headImage)
    CircleImageView ivHeadImage;
    @BindView(R.id.btn_video_bottom)
    TextView btnVideoBottom;
    @BindView(R.id.video_user_name)
    TextView videoUserName;
    @BindView(R.id.video_desc)
    TextView videoDesc;
    @BindView(R.id.video_playnum)
    TextView videoPlaynum;
    @BindView(R.id.video_follow_num)
    TextView videoFollowNum;
    @BindView(R.id.video_comment_num)
    TextView videoCommentNum;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.rv_comment)
    RecyclerView rvComment;
    @BindView(R.id.comment_nothing)
    TextView commentNothing;
    @BindView(R.id.video_from_type)
    TextView videoFromType;
    @BindView(R.id.video_rl_bottom)
    RelativeLayout videoRlBottom;
    @BindView(R.id.video_follow)
    ImageView videoFollow;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.video_view_bottom)
    View videoViewBottom;

    private VideoIsFollowBean isFollowBean;
    private MediaController mediaController;
    private int vid;
    private int videoUserId;
    private int uidx;
    private boolean isShowComment = true;
    private int followNum;
    private int commentNum;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case COMMENT:
                    setComment(AESUtil.decode(json));
                    break;
                case IS_FOLLOW:
                    setIsFollow(AESUtil.decode(json));
                    break;
                case VIDEO_INFO:
                    setVideoData(AESUtil.decode(json));
                    break;
                case VIDEO_FOLLOW:
                    setFollowVideo(AESUtil.decode(json));
                    break;
                case ADD_COMMENT:
                    setAddComment(AESUtil.decode(json));
                case FOLLOW:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String result = jsonObject.getString("msg");
                        CommonUtil.showTextToast(VideoActivity.this, result);
                        videoFollow.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void setAddComment(String json) {
        int result = 0;
        try {
            JSONObject object = new JSONObject(json);
            result = object.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1) {
            CommonUtil.showTextToast(VideoActivity.this, "评论成功");
            commentNum = commentNum + 1;
            videoCommentNum.setText(getString(R.string.comment_num, commentNum));
            getComment();

        } else {
            CommonUtil.showTextToast(VideoActivity.this, "评论失败");
        }
    }

    private IndexBean.ResultBean indexResultBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvComment.setLayoutManager(mLinearLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(50, 00, 00, 00));
        }


        indexResultBean = getIntent().getParcelableExtra("video");
        vid = indexResultBean.getVid();
        videoUserId = indexResultBean.getUidx();
        uidx = CacheUtils.getInt(this, "useridx");

        initVideoView();
        getVideoInfo(vid);

        if (uidx != 0) {
            initIsFollow();
        }
        getComment();
        addPlayNum();
    }

    private void addPlayNum() {
        String url = Contact.HOST + Contact.ADD_PLAY_NUM + "?vid=" + vid + "&loginUidx=" + uidx;
        Log.d("VideoActivity", "url" + url);
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onError(IOException e) {
                Log.e("VideoActivity", String.valueOf(e));
            }

            @Override
            public void onResponse(Object response) {
                Log.d("VideoActivity", "response" + response);
                int result = 0;
                try {
                    JSONObject object = new JSONObject((String) response);
                    result = object.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (result == 1) {
                    playNum++;
                    videoPlaynum.setText(getString(R.string.play_num, playNum));
                }
            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_video, null);
    }

    private void getComment() {
        String url = Contact.HOST + Contact.VIDEO_COMMETN + "?vid=" + vid + "&cid=0&num=100";
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
                message.what = COMMENT;
                handler.sendMessage(message);
            }
        });
    }

    private void initIsFollow() {
        String url = "http://service.quliao.com/works/getGCSByVId?vid=" + vid + "&uidx=" +
                uidx + "&buidx=" + videoUserId;
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
                message.what = IS_FOLLOW;
                handler.sendMessage(message);
            }
        });
    }

    private void getVideoInfo(int vid) {
        String url = "http://service.quliao.com/works/getVideoInfoByVid?uidx=" + uidx + "&vid=" +
                vid;
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
                message.what = VIDEO_INFO;
                handler.sendMessage(message);
            }
        });
    }

    private void initVideoView() {

        String videoUrl = new String(Base64.decode(indexResultBean.getVideoUrl()
                .getBytes(), Base64.DEFAULT));
        String name = new String(Base64.decode(indexResultBean.getNickname()
                .getBytes(), Base64.DEFAULT));
        String desc = null;
        try {
            desc = URLDecoder.decode(new String(Base64.decode(indexResultBean.getDescriptions()
                    .getBytes(), Base64.DEFAULT)), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String date = new String(Base64.decode(indexResultBean.getCreateTime()
                .getBytes(), Base64.DEFAULT));
        String imageScale = indexResultBean.getImgscale();
        if (TextUtils.isEmpty(imageScale) && !TextUtils.isEmpty(indexResultBean.getImgScale())) {
            imageScale = new String(Base64.decode(indexResultBean.getImgScale()
                    .getBytes(), Base64.DEFAULT));
        }
        int fromType = indexResultBean.getFromtype();
        int loginplant = indexResultBean.getLoginplant();

        if (!CommonUtil.checkPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                .ACCESS_COARSE_LOCATION})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Contact.CHECK_PERMISSION);
        } else {
            double longitude = indexResultBean.getLongitude();
            double latitude = indexResultBean.getLatitude();
//            tvDistance.setText(getDistance(longitude, latitude));
            setDistance(latitude, longitude);
        }

        if (uidx == videoUserId) {
            videoFollow.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(desc)) {
            videoUserName.setText(name);
        } else {
            videoUserName.setText(name + "：");
        }
        videoDesc.setText(desc);
        tvDate.setText("发布于:" + date);

        if (fromType == 11) {
            videoFromType.setText("我在水晶直播，快来看~");
        } else if (fromType == 3) {
            videoFromType.setText("我在欢乐直播，快来看~");
        } else {
            if (loginplant == 11) {
                videoFromType.setText("我在水晶直播，快来看~");
            } else if (loginplant == 3) {
                videoFromType.setText("我在欢乐直播，快来看~");
            } else {
                videoRlBottom.setVisibility(View.GONE);
                videoViewBottom.setVisibility(View.GONE);
            }
        }

        int width = CommonUtil.getScreenWidth(this);
        if (TextUtils.isEmpty(imageScale)) {
            imageScale = "0.75";
        }
        double viewHeigh = width / (Double.parseDouble(imageScale));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, (int) viewHeigh);
        videoView.setLayoutParams(layoutParams);

//        mediaController = new MediaController(this, false, false);
//        videoView.setMediaController(mediaController);

        videoView.setBufferingIndicator(progressbar);
//        videoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
//        videoView.setOnCompletionListener(mOnCompletionListener);
//        videoView.setOnErrorListener(mOnErrorListener);

        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();

        videoView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                videoView.start();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    double longitude = indexResultBean.getLongitude();
                    double latitude = indexResultBean.getLatitude();
//                    tvDistance.setText(getDistance(longitude, latitude));
                    setDistance(latitude, longitude);
                } else {
                    new AlertDialog.Builder(this).setMessage("开启定位功能可以查看附近的小伙伴哟~")
                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
        }
    }

    private void setDistance(final double latitude, final double longitude) {
        AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        Log.e("AMapLocation", aMapLocation.getLatitude() + "---" + aMapLocation.getLongitude());
                        double distance = LocationUtil.gps2m(latitude, longitude, aMapLocation.getLatitude(), aMapLocation.getLongitude()
                        ) / 1000;
                        if (distance < 1 && distance > 0) {
                            tvDistance.setText("距你" + (int) (distance * 1000) + "m");
                        } else if (distance > 1 && distance < 1000) {
                            tvDistance.setText("距你" + (int) distance + "km");
                        } else if (distance > 1000) {
                            tvDistance.setText("距你1000km外");
                        }
                    } else {
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("VideoView", "onStop");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.e("VideoView", "onDestroy");
        videoView.stopPlayback();
    }

    @OnClick({R.id.video_back, R.id.video_heart, R.id.video_share, R.id.tv_input, R.id
            .btn_video_bottom, R.id.video_user_name, R.id.iv_headImage, R.id.video_follow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.video_back:
                finish();
                break;
            case R.id.video_heart:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }
                followVideo();
                break;
            case R.id.video_share:
                shareDialog();
                break;
            case R.id.tv_input:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }

                DialogComment dialogComment = new DialogComment(this);
                dialogComment.setNoticeDialogListerner(new DialogComment.NoticeDialogListener() {

                    @Override
                    public void onClick(String comment) {
                        sendComment(videoUserId, comment, 0, 0);
                    }
                });
                InitiView.initiBottomDialog(dialogComment);
                InitiView.setDialogMatchParent(dialogComment);
                dialogComment.show();
                break;
            case R.id.btn_video_bottom:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }
                Intent webIntent = new Intent(this, WebViewActivity.class);
                int fromType = indexResultBean.getFromtype();
                if (fromType == 11 || fromType == 3) {
                    webIntent.putExtra("fromType", fromType);
                } else {
                    webIntent.putExtra("fuidx", indexResultBean.getLoginplant());
                }
                startActivity(webIntent);
                break;
            case R.id.video_user_name:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }

                Intent intent = new Intent(this, PersonalActivity.class);
                intent.putExtra("buidx", videoUserId);
                startActivity(intent);
                break;
            case R.id.iv_headImage:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }
                Intent intentHead = new Intent(this, PersonalActivity.class);
                intentHead.putExtra("buidx", videoUserId);
                intentHead.putExtra("vid", vid);
                startActivity(intentHead);
                break;
            case R.id.video_follow:
                if (uidx == 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                    return;
                }
                setFollow(1);
                break;
        }
    }

    private void setFollow(int index) {
        FollowPostBean followPostBean = new FollowPostBean();
        followPostBean.setOpcode("FocusonOrDeletecurd_friends");
        followPostBean.setUseridx(uidx);
        followPostBean.setFriendidx(videoUserId);
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

    private void shareDialog() {
        String videoPic = new String(Base64.decode(indexResultBean.getVideoPicUrl().getBytes(),
                Base64.DEFAULT));
        if (!videoPic.contains("http")) {
            videoPic = "http://" + videoPic;
        }
        String shareTitle = null;
        try {
            shareTitle = URLDecoder.decode(new String(Base64.decode(indexResultBean
                    .getDescriptions()
                    .getBytes(), Base64.DEFAULT)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String name = CacheUtils.getString(VideoActivity.this, "userName");

        int vid = indexResultBean.getVid();

        List<String> defaultTitles = new ArrayList<String>() {
        };
        defaultTitles.add("这个视频有毒啊，这毒不能让我一个人中，你也来看看");
        defaultTitles.add("这个视频有意思，小帅哥你也来看看");
        defaultTitles.add("发现一个有意思的短片，好看到哭！快来");
        Collections.shuffle(defaultTitles);

        if (TextUtils.isEmpty(shareTitle)) {
            shareTitle = defaultTitles.get(0);
        }
        UMImage image = new UMImage(VideoActivity.this, videoPic);

        UMWeb web = new UMWeb("http://www.quliao.com/mobile/works.aspx?worksId=" + vid +
                "&from=singlemessage&isappinstalled=1");
        web.setTitle(shareTitle);
        if (TextUtils.isEmpty(name)) {
            web.setDescription("分享自牛拍");
        } else {
            web.setDescription("分享自" + name + "的牛拍");
        }
        web.setThumb(image);

        new ShareAction(VideoActivity.this).withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA
                        .WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
                .open();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void sendComment(int buidx, String comment, int identify, int replyCid) {
        comment = CommonUtil.replaceBlank(comment);
        if (TextUtils.isEmpty(comment)) {
            CommonUtil.showTextToast(this, "评论内容不能为空");
            return;
        }

        if (buidx == uidx) {
            replyCid = 0;
        }
        String url = null;
        try {
            url = Contact.HOST + Contact.ADD_COMMENT + "?vid=" + vid + "&uidx=" + uidx +
                    "&buidx=" + buidx + "&comment=" + URLEncoder.encode(URLEncoder.encode(comment, "UTF-8"), "UTF-8") +
                    "&identify=" + identify + "&replyCid=" + replyCid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                message.what = ADD_COMMENT;
                handler.sendMessage(message);
            }
        });
    }

    private void followVideo() {

        String device_id = CommonUtil.getDeviceID(this);

        String url = Contact.HOST + Contact.VIDEO_FOLLOW + "?vid=" + vid + "&uidx=" + uidx +
                "&unique=" + new String(Base64.encode(device_id.getBytes(), Base64.DEFAULT)) +
                "&password=1";
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
                message.what = VIDEO_FOLLOW;
                handler.sendMessage(message);
            }
        });
    }

    private CommentAdapter commentAdapter;

    private void setComment(String json) {
        Gson gson = new Gson();
        List<CommentBean.ResultBean> resultBeanList = gson.fromJson(json, CommentBean.class)
                .getResult();
        if (resultBeanList.size() == 0) {
            commentNothing.setText("你要是主动一点，说不定我们现在都有孩子了~");
            commentNothing.setVisibility(View.VISIBLE);
            return;
        }
        commentNothing.setVisibility(View.GONE);

        commentAdapter = new CommentAdapter(this, resultBeanList);
//        initVideo();
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position, final CommentBean.ResultBean data) {
                String replyName = new String(Base64.decode(data.getNickName().getBytes(),
                        Base64.DEFAULT));
                DialogComment dialogComment = new DialogComment(VideoActivity.this, replyName);
                dialogComment.setNoticeDialogListerner(new DialogComment.NoticeDialogListener() {
                    @Override
                    public void onClick(String comment) {
                        sendComment(data.getUidx(), comment, 1, data.getCid());
                    }
                });
                InitiView.initiBottomDialog(dialogComment);
                InitiView.setDialogMatchParent(dialogComment);
                dialogComment.show();
            }
        });
        rvComment.setAdapter(commentAdapter);
    }


    private void setFollowVideo(String json) {
        int result = 0;
        try {
            JSONObject object = new JSONObject(json);
            result = object.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1) {
            videoHeart.setImageResource(R.drawable.heart1);
            followNum = followNum + 1;
            videoFollowNum.setText(getString(R.string.follow_num, followNum));
        } else if (result == 2) {
            CommonUtil.showTextToast(this, "今日已点赞，请明天再来哟~");
        } else {
            CommonUtil.showTextToast(this, "点赞失败");
        }
    }

    private void setIsFollow(String json) {
        Gson gson = new Gson();
        isFollowBean = gson.fromJson(json, VideoIsFollowBean.class);
        if (isFollowBean.getResult().get(0).getIsfollow() == 1) {
            videoFollow.setVisibility(View.GONE);
        }
    }

    private int playNum;

    private void setVideoData(String json) {
        Gson gson = new Gson();
        VideoDetailBean videoDetailBean = gson.fromJson(json, VideoDetailBean.class);
        if (videoDetailBean.getErrorcode().equals("00000:ok")) {
            VideoDetailBean.ResultBean resultBean = videoDetailBean.getResult();
            if (resultBean.getIsFollow() == 1) {
                videoHeart.setImageResource(R.drawable.heart);
            }
            String headImage = new String(Base64.decode(resultBean.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            Picasso.with(this).load(headImage).placeholder(R.drawable.me_user_admin).into(ivHeadImage);
            followNum = resultBean.getGoodNum();
            commentNum = resultBean.getCNum();
            playNum = resultBean.getPlayNum();

            videoFollowNum.setText(getString(R.string.follow_num, followNum));
            videoCommentNum.setText(getString(R.string.comment_num, commentNum));
            videoPlaynum.setText(getString(R.string.play_num, playNum));
            if (resultBean.getPraiseCount() > 0) {
                videoHeart.setImageResource(R.drawable.heart1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        progressbar.setVisibility(View.VISIBLE);
        uidx = CacheUtils.getInt(this, "useridx");
        Log.e("VideoView", "onResume");
        videoView.start();
        if (CacheUtils.getInt(this, "useridx") != 0) {
            initIsFollow();
        }
        MobclickAgent.onPageStart("VideoActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("VideoView", "onPause");
        videoView.pause();
//        per = videoView.getCurrentPosition();
        MobclickAgent.onPageEnd("VideoActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("VideoView", "onRestart");
    }
}
