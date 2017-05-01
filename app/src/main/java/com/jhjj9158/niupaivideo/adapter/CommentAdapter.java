package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.CommentBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.LocationUtil;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<CommentBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public CommentAdapter(Context context, List<CommentBean.ResultBean> mDatas) {
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

    public void addDatas(List<CommentBean.ResultBean> datas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final CommentBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String name = new String(Base64.decode(data.getNickName().getBytes(),
                    Base64.DEFAULT));
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String comment = new String(Base64.decode(data.getComment().getBytes(),
                    Base64.DEFAULT));
            String date = new String(Base64.decode(data.getCDate().getBytes(), Base64.DEFAULT));
            double distance = LocationUtil.gps2m(context, data.getLatitude(), data.getLongitude()
            ) / 1000;
            String distance_date = null;
            if (distance < 1) {
                distance_date = (int) (distance * 1000) + "m | " + data;
            } else if (distance > 1 && distance < 1000) {
                distance_date = (int) distance + "km | " + data;
            }

            Picasso.with(context).load(headImage).into(((Holder) viewHolder).comment_headimg);
            ((Holder) viewHolder).comment_name.setText(name);
            ((Holder) viewHolder).comment_distance_date.setText(distance_date);
            ((Holder) viewHolder).comment_detail.setText(comment);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
//        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
        return 10;
    }

    class Holder extends RecyclerView.ViewHolder {

        CircleImageView comment_headimg;
        TextView comment_name;
        TextView comment_distance_date;
        TextView comment_detail;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            comment_headimg = (CircleImageView) itemView.findViewById(R.id.comment_headimg);
            comment_name = (TextView) itemView.findViewById(R.id.comment_name);
            comment_distance_date = (TextView) itemView.findViewById(R.id.comment_distance_date);
            comment_detail = (TextView) itemView.findViewById(R.id.comment_detail);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, CommentBean.ResultBean data);
    }
}