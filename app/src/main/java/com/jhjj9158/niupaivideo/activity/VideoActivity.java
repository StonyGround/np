package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.Settings;
import com.jhjj9158.niupaivideo.adapter.CommentAdapter;
import com.jhjj9158.niupaivideo.bean.CommentBean;
import com.jhjj9158.niupaivideo.bean.FollowPostBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.VideoDetailBean;
import com.jhjj9158.niupaivideo.bean.VideoIsFollowBean;
import com.jhjj9158.niupaivideo.dialog.DialogComment;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.InitiView;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.widget.AndroidMediaController;
import com.jhjj9158.niupaivideo.widget.IjkVideoView;
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
import java.util.ArrayList;
import java.util.Collections;
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

public class VideoActivity extends AppCompatActivity {

    private static final int VIDEO_INFO = 1;
    private static final int IS_FOLLOW = 2;
    private static final int VIDEO_FOLLOW = 3;
    private static final int COMMENT = 4;
    private static final int ADD_COMMENT = 5;
    private static final int FOLLOW = 6;

    @BindView(R.id.video_view)
    VideoView videoView;
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

    private Settings mSettings;
    private AndroidMediaController mMediaController;
    private boolean mBackPressed;
    private VideoIsFollowBean isFollowBean;
    private int vid;
    private int videoUserId;
    private int uidx;
    private boolean isShowComment = true;
    private int followNum;
    private int commentNum;

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
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvComment.setLayoutManager(mLinearLayoutManager);
//        setTitle(this, "播放");

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

        initVideoView();
        getVideoInfo(vid);

        if (CacheUtils.getInt(this, "useridx") != 0) {
            initIsFollow();
        }
        getComment();
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
        uidx = CacheUtils.getInt(this, "useridx");
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

        if (LocationUtil.getLocation(this) != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Contact.CHECK_PERMISSION);
            } else {
                double longitude = indexResultBean.getLongitude();
                double latitude = indexResultBean.getLatitude();
                tvDistance.setText(getDistance(longitude, latitude));
            }
        }

        if (TextUtils.isEmpty(desc)) {
            videoUserName.setText(name);
        } else {
            videoUserName.setText(name + "：");
        }
        videoDesc.setText(desc);
        videoPlaynum.setText(getString(R.string.play_num, indexResultBean.getPlayNum()));
        tvDate.setText("发布于:" + date);

        if (fromType == 11) {
            videoFromType.setText("主播在水晶直播等你哟~");
        } else if (fromType == 3) {
            videoFromType.setText("主播在欢乐直播等你哟~");
        } else {
            if (loginplant == 11) {
                videoFromType.setText("主播在水晶直播等你哟~");
            } else if (loginplant == 3) {
                videoFromType.setText("主播在欢乐直播等你哟~");
            } else {
                videoRlBottom.setVisibility(View.GONE);
            }
        }

//        mSettings = new Settings(this);
//        mMediaController = new AndroidMediaController(this, false);
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");


//        videoView.setMediaController(mMediaController);
//        videoView.setHudView(hudView);
        int width = CommonUtil.getScreenWidth(this);
        if (TextUtils.isEmpty(imageScale)) {
            imageScale = "0.75";
        }
        double heigh = width / (Double.parseDouble(imageScale));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, (int) heigh);
        videoView.setLayoutParams(layoutParams);
        videoView.setVideoURI(Uri.parse(videoUrl));
//        videoView.setMediaController(new MediaController(this));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressbar.setVisibility(View.GONE);
            }
        });
//        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(IMediaPlayer mp) {
//                videoView.start();
//            }
//        });
//        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(IMediaPlayer iMediaPlayer) {
//                videoView.start();
//            }
//        });
    }

    private IjkVideoView ijkVideoView;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    double longitude = indexResultBean.getLongitude();
                    double latitude = indexResultBean.getLatitude();
                    tvDistance.setText(getDistance(longitude, latitude));
                } else {
//                    new AlertDialog.Builder(this).setMessage("请允许牛拍获取您的相机、相册权限，否则无法更换新的头像。")
//                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).show();
                }
        }
    }

    private String getDistance(double longitude, double latitude) {
        String tv_distance = null;
        double distance = LocationUtil.gps2m(this, latitude, longitude) / 1000;
        if (distance < 1) {
            tv_distance = "距你" + (int) (distance * 1000) + "m";
        } else if (distance > 1 && distance < 1000) {
            tv_distance = "距你" + (int) distance + "km";
        } else {
            tv_distance = "距你1000km外";
        }
        return tv_distance;
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("VideoView", "onStop");
//        videoView.stopPlayback();
//        if (mBackPressed || !videoView.isBackgroundPlayEnabled()) {
//            videoView.stopPlayback();
//            videoView.release(true);
//            videoView.stopBackgroundPlay();
//        } else {
//            videoView.enterBackground();
//        }
//        IjkMediaPlayer.native_profileEnd();

//        if (mBackPressed || !ijkVideoView.isBackgroundPlayEnabled()) {
//            ijkVideoView.stopPlayback();
//            ijkVideoView.release(true);
//            ijkVideoView.stopBackgroundPlay();
//        } else {
//            ijkVideoView.enterBackground();
//        }
//        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("VideoView", "onDestroy");
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
        if (TextUtils.isEmpty(comment)) {
            CommonUtil.showTextToast(this, "评论内容不能为空");
            return;
        }

        if (buidx == uidx) {
            replyCid = 0;
        }
        String url = Contact.HOST + Contact.ADD_COMMENT + "?vid=" + vid + "&uidx=" + uidx +
                "&buidx=" + buidx + "&comment=" + comment +
                "&identify=" + identify + "&replyCid=" + replyCid;
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
            CommonUtil.showTextToast(this, "今日点赞已满10次，请明天再来哟~");
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
            videoFollowNum.setText(getString(R.string.follow_num, followNum));
            videoCommentNum.setText(getString(R.string.comment_num, commentNum));
            if (resultBean.getPraiseCount() > 0) {
                videoHeart.setImageResource(R.drawable.heart1);
            }
        }
    }

    private int per;

    @Override
    public void onResume() {
        super.onResume();
//        progressbar.setVisibility(View.VISIBLE);
        uidx = CacheUtils.getInt(this, "useridx");
        Log.e("VideoView", "onResume");
//        initVideoView();
        videoView.seekTo(per);
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
        per = videoView.getCurrentPosition();
        MobclickAgent.onPageEnd("VideoActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("VideoView", "onRestart");
    }
}
