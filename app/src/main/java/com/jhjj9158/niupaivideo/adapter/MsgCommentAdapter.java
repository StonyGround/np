package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MsgCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<MsgCommentBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public MsgCommentAdapter(Context context, List<MsgCommentBean.ResultBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addDatas(List<MsgCommentBean.ResultBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void removeDatas() {
        mDatas.removeAll(mDatas);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_comment,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final MsgCommentBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String name = new String(Base64.decode(data.getNickName().getBytes(),
                    Base64.DEFAULT));
            String comment = new String(Base64.decode(data.getComment().getBytes(),
                    Base64.DEFAULT));
            String reply = new String(Base64.decode(data.getReplycomment().getBytes(),
                    Base64.DEFAULT));
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            String date = new String(Base64.decode(data.getCDate().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String videoPic = new String(Base64.decode(data.getVideoPicUrl().getBytes(),
                    Base64.DEFAULT));
            if (!videoPic.contains("http")) {
                videoPic = "http://" + headImage;
            }

            if(TextUtils.isEmpty(reply)){
                ((Holder) viewHolder).msgCommentReply.setVisibility(View.GONE);
            }
            Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(((MsgCommentAdapter.Holder) viewHolder).msgCommentHeadimg);
            Picasso.with(context).load(videoPic).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).msgCommentVideo);
            ((Holder) viewHolder).msgCommentName.setText(name);
            ((Holder) viewHolder).msgCommentDetail.setText(comment);
            ((Holder) viewHolder).msgCommentReply.setText(reply);
            ((Holder) viewHolder).msgCommentDate.setText(date);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
//        return 5;
    }

    class Holder extends RecyclerView.ViewHolder {
        CircleImageView msgCommentHeadimg;
        TextView msgCommentName;
        TextView msgCommentReply;
        ImageView msgCommentVideo;
        TextView msgCommentDetail;
        TextView msgCommentDate;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            msgCommentHeadimg = (CircleImageView) itemView.findViewById(R.id.msg_comment_headimg);
            msgCommentName = (TextView) itemView.findViewById(R.id.msg_comment_name);
            msgCommentReply = (TextView) itemView.findViewById(R.id.msg_comment_reply);
            msgCommentVideo = (ImageView) itemView.findViewById(R.id.msg_comment_video);
            msgCommentDetail = (TextView) itemView.findViewById(R.id.msg_comment_detail);
            msgCommentDate = (TextView) itemView.findViewById(R.id.msg_comment_date);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, MsgCommentBean.ResultBean data);
    }
}