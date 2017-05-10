package com.jhjj9158.niupaivideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/19.
 */
public class DialogComment extends Dialog {

    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static String imageDir = "temp.jpg";
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_send_comment)
    TextView tvSendComment;

    private Context context;

    public DialogComment(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);
        ButterKnife.bind(this);
    }

    NoticeDialogListener mListener;

    public void setNoticeDialogListerner(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    public interface NoticeDialogListener {
        void onClick(String comment);
    }

    @OnClick(R.id.tv_send_comment)
    public void onViewClicked() {
        mListener.onClick(etComment.getText().toString());
        DialogComment.this.dismiss();
    }
}
