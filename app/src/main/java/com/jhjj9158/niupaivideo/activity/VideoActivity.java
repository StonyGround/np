package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.Settings;
import com.jhjj9158.niupaivideo.adapter.CommentAdapter;
import com.jhjj9158.niupaivideo.bean.CommentBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.VideoDetailBean;
import com.jhjj9158.niupaivideo.bean.VideoIsFollowBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.jhjj9158.niupaivideo.widget.AndroidMediaController;
import com.jhjj9158.niupaivideo.widget.IjkVideoView;
import com.jhjj9158.niupaivideo.widget.MyDrawLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {

    private static final int VIDEO_INFO = 1;
    private static final int IS_FOLLOW = 2;
    private static final int VIDEO_FOLLOW = 3;
    private static final int COMMENT = 4;
    private static final int ADD_COMMENT = 5;

    @BindView(R.id.video_view)
    IjkVideoView videoView;
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
    @BindView(R.id.tv_send_comment)
    TextView tvSendComment;
    @BindView(R.id.rl_comment)
    RelativeLayout rlComment;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.drawer_layout)
    MyDrawLayout drawerLayout;
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

    private Settings mSettings;
    private AndroidMediaController mMediaController;
    private boolean mBackPressed;
    private VideoIsFollowBean isFollowBean;
    private int vid;
    private int videoUserId;
    private int uidx = CacheUtils.getInt(this, "useridx");
    private boolean isShowComment = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case VIDEO_INFO:
                    setVideoData(AESUtil.decode(json));
                    break;
                case IS_FOLLOW:
                    setIsFollow(AESUtil.decode(json));
                    break;
                case VIDEO_FOLLOW:
                    setFollowVideo(json);
                    break;
                case COMMENT:
                    setComment(AESUtil.decode(json));
                    break;
                case ADD_COMMENT:
                    String j = AESUtil.decode(json);
                    int result = 0;
                    try {
                        JSONObject object = new JSONObject(j);
                        result = object.getInt("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (result == 1) {
                        CommonUtil.showTextToast("评论成功", VideoActivity.this);
                        videoHeart.setImageResource(R.drawable.heart1);
                    } else {
                        CommonUtil.showTextToast("评论失败", VideoActivity.this);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setComment(String json) {
        Gson gson = new Gson();
        List<CommentBean.ResultBean> resultBeanList = gson.fromJson(json, CommentBean.class)
                .getResult();
        if (resultBeanList.size() == 0) {
            commentNothing.setVisibility(View.VISIBLE);
            return;
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvComment.setLayoutManager(mLinearLayoutManager);
        rvComment.setAdapter(new CommentAdapter(this, resultBeanList));
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
            CommonUtil.showTextToast("收藏成功", this);
            videoHeart.setImageResource(R.drawable.heart1);
        } else {
            CommonUtil.showTextToast("收藏失败", this);
        }
    }

    private void setIsFollow(String json) {
        Log.e("setIsFollow", json);
        Gson gson = new Gson();
        isFollowBean = gson.fromJson(json, VideoIsFollowBean.class);
        if (isFollowBean.getResult().get(0).getIsfollow() == 1) {
            videoHeart.setImageResource(R.drawable.heart1);
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
            Picasso.with(this).load(headImage).into(ivHeadImage);
            videoFollowNum.setText(resultBean.getGoodNum() + "赞");
            videoCommentNum.setText(resultBean.getCNum() + "评论");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
//        setTitle(this, "播放");

        IndexBean.ResultBean resultBean = getIntent().getParcelableExtra("video");

        initVideoView(resultBean);
        getVideoInfo(vid);

        if (CacheUtils.getInt(this, "useridx") != 0) {
            initIsFollow(vid, videoUserId);
        }
        getComment();
    }

    private void getComment() {
        String url = Contact.HOST + Contact.VIDEO_COMMETN + "?vid=" + vid + "&cid=1&num=100";
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

    private void initIsFollow(int vid, int videoUserId) {
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

    private void initVideoView(IndexBean.ResultBean resultBean) {
        vid = resultBean.getVid();
        videoUserId = resultBean.getUidx();
        String videoUrl = new String(Base64.decode(resultBean.getVideoUrl()
                .getBytes(), Base64.DEFAULT));
        String name = new String(Base64.decode(resultBean.getNickname()
                .getBytes(), Base64.DEFAULT));
        String desc = null;
        try {
            desc = URLDecoder.decode(new String(Base64.decode(resultBean.getDescriptions()
                    .getBytes(), Base64.DEFAULT)), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String date = new String(Base64.decode(resultBean.getCreateTime()
                .getBytes(), Base64.DEFAULT));
        double longitude = resultBean.getLongitude();
        double latitude = resultBean.getLatitude();

        videoUserName.setText(name + "：");
        videoDesc.setText(desc);
        videoPlaynum.setText(resultBean.getPlayNum() + "次播放");
        tvDate.setText("发布于:" + date);
        tvDistance.setText(getDistance(longitude, latitude));

        mSettings = new Settings(this);
        mMediaController = new AndroidMediaController(this, false);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        videoView.setVideoURI(Uri.parse(videoUrl));
//        videoView.setMediaController(mMediaController);
//        videoView.setHudView(hudView);
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                videoView.start();
            }
        });
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
        if (mBackPressed || !videoView.isBackgroundPlayEnabled()) {
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
        } else {
            videoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.video_back, R.id.video_heart, R.id.video_share, R.id.tv_input, R.id
            .btn_video_bottom, R.id.tv_send_comment, R.id.video_user_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.video_back:
                finish();
                break;
            case R.id.video_heart:
                if (uidx != 0) {
                    startActivity(new Intent(this, QuickLoignActivity.class));
                }
                if (isFollowBean.getResult().get(0).getIsfollow() == 0) {
                    setFollowVideo();
                }
                break;
            case R.id.video_share:
                break;
            case R.id.tv_input:
                if (isShowComment) {
                    isShowComment = false;
                    rlComment.setVisibility(View.VISIBLE);
                } else {
                    isShowComment = true;
                    rlComment.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_video_bottom:
                break;
            case R.id.tv_send_comment:
                sendComment();
                break;
            case R.id.video_user_name:
                Intent intent = new Intent(this,PersonalActivity.class);
                intent.putExtra("buidx", videoUserId);
                startActivity(intent);
                break;
        }
    }

    private void sendComment() {
        String url = Contact.HOST + Contact.ADD_COMMENT + "?vid=" + vid + "&uidx=" + uidx +
                "&buidx=" + videoUserId + "&comment=" + etComment.getText().toString() +
                "&identify=0&replyCid=0";
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

    private void setFollowVideo() {

        String device_id = CommonUtil.getDeviceID(this);

        String url = Contact.HOST + Contact.VIDEO_FOLLOW + "?vid=" + vid + "&uidx=" + uidx +
                "&unique=" + device_id + "&password=1";
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
}
