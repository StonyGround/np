package com.jhjj9158.niupaivideo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.Settings;
import com.jhjj9158.niupaivideo.widget.IjkVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class TestActivity extends AppCompatActivity {


    @BindView(R.id.ijkvideo)
    IjkVideoView ijkvideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        Settings mSettings = new Settings(this);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkvideo.setVideoURI(Uri.parse("http://video.quliao.com/20170510/1A9304BC8EE957F3ED3F38DAFCD50216.mp4"));
        ijkvideo.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                ijkvideo.start();
            }
        });
    }
}
