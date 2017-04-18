package com.jhjj9158.niupaivideo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.jhjj9158.niupaivideo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    //    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    @BindView(R.id.ll_child_content)
    LinearLayout llChildContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        llChildContent = (LinearLayout) findViewById(R.id.ll_child_content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        View child = getChildView();
        ButterKnife.bind(this, child);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, -1);
        llChildContent.addView(child, params);
    }

    protected abstract View getChildView();

    protected void setTitle(final Activity activity, String title) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_navigate_before);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

}
