package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.BannerBean;
import com.jhjj9158.niupaivideo.widget.ResizableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
public class AdapterHomeBanner extends PagerAdapter {

    private Context context;

    private List<BannerBean.ResultBean> bannerList;

    public AdapterHomeBanner(Context context, List<BannerBean.ResultBean> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public int getCount() {
//        return Integer.MAX_VALUE;
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {



        ResizableImageView imageView = new ResizableImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final int diff = (Integer.MAX_VALUE / 2) % (bannerList.size());
        String imageUrl = new String(Base64.decode(bannerList.get((position - diff) % bannerList.size()).getAdvImg().getBytes(), Base64.DEFAULT));
        final String link = new String(Base64.decode(bannerList.get((position - diff) % bannerList.size()).getLinkUrl().getBytes(), Base64.DEFAULT));
        Picasso.with(context).load(imageUrl).placeholder(R.drawable.wartfullplacehold).into(imageView);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra("url", link);
//                intent.setClass(context, WebActivity.class);
//                context.startActivity(intent);
//            }
//        });

        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
