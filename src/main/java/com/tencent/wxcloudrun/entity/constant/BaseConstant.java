package com.tencent.wxcloudrun.entity.constant;

/**
 * @author yozora
 * description base constant
 **/
public interface BaseConstant {

    String YES = "1";

    String NO = "0";

    String PROCESSING = "2";

    int USER_CREATE_LIMIT = 5;

    int IPV4_LENGTH = 15;

    /**
     * access token valid time (s)
     */
    int ACCESS_TOKEN_TIME = 60 * 60 * 24;

    /**
     * refresh token valid time (s)
     */
    int REFRESH_TOKEN_TIME = 21600;

    /**
     * login verify code valid time (s)
     */
    int LOGIN_TIME_OUT = 10000;

    long MAX_UPLOAD_SIZE = 10 * 1024 * 1024;


}
