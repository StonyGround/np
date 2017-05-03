package com.jhjj9158.niupaivideo.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_send_comment)
    TextView tvSendComment;
    @BindView(R.id.rl_comment)
    RelativeLayout rlComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(rlComment);
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
