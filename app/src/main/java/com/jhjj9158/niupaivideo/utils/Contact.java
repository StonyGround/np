package com.jhjj9158.niupaivideo.utils;

import okhttp3.MediaType;

/**
 * Created by pc on 17-4-5.
 */

public class Contact {

    public static final String KEY = "q0m3sd81";

    public static final String AES_KEY = "192c96beaec59d367329c70016e7a50f";

    public static final String USER_INFO = "http://down.tiange.com/NiupatInterface.aspx";

    public static final String LOGIN_SINA = "http://down.tiange" +
            ".com/OAuth/SinaAuthForPhoneHappymoment.aspx";
    public static final String LOGIN_QQ = "http://down.tiange.com/OAuth/QQAuthForPhoneHappymoment" +
            ".aspx";
    public static final String LOGIN_WEIXIN = "http://down.tiange" +
            ".com/OAuth/WeixinAuthForPhoneHappymoment.aspx";

    public static final String HOST = "http://service.quliao.com/";
    public static final String GET_USER_INFO = "user/GetUserInfo";
    public static final String INDEX = "works/getIndexRankVideo";
    public static final String TAB_TITLE = "works/getVRVedioType";
    public static final String TAB_DYNAMIC = "works/getVRVideoInfo";
    public static final String VIDEO_FOLLOW = "works/addPraise";
    public static final String VIDEO_COMMETN = "works/getCommentInfoByVid";
    public static final String ADD_COMMENT = "works/addCommentForDetails";
    public static final String PERSONAL_INFO = "user/getUserByTa";
    public static final String TAB_WORKS = "works/getVideoInfo";
    public static final String TAB_FAVORITE = "works/getPraiseVideoListByTa";
    public static final String GET_FOLLOW = "user/getFollowUserByTa";
    public static final String GET_FANS = "user/getFansList";
    public static final String GET_COMMENT = "works/getCommentInfo_112";
    public static final String GET_NOTICE = "works/GetInform";
    public static final String GET_MOMENTS = "works/getTrendsInfo_112";
    public static final String GET_LIKE = "works/getPraiseInfo_112";
    public static final String GET_REWARD = "works/GetRewardTrendsInfo";
    public static final String GET_BANNER = "Profit/loadAdvertisement";

    public static final String IS_START_MAIN = "is_start_main";


    public static final int BANNER_START_ROLLING = 0;
    public static final int BANNER_KEEP_ROLLING = 1;
    public static final int BANNER_STOP_ROLLING = 2;
    public static final int BANNER_CHANGE_ROLLING = 3;
    public static final int GET_BANNER_DATA = 4;
    public static final int GET_HOT_DATA = 5;
    public static final int GET_NEW_DATA = 6;
    public static final int GET_FOLLOW_DATA = 7;
    public static final int GET_DYNAMIC_DATA = 8;
    public static final int REQUEST_TAKE_PHOTO = 9;
    public static final int REQUEST_PHOTO_ZOOM = 10;
    public static final int REQUEST_PHOTO_RESULT = 11;
}
