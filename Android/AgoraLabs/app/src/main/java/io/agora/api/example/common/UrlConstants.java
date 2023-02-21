package io.agora.api.example.common;

public class UrlConstants {
    //    public static final String BASE_URL = "http://124.220.20.196:8006";
    public static final String BASE_URL = "https://gateway-fulldemo-staging.agoralab.co";
    public static final String BASE_REQUEST_LOGIN_BY_TOKEN = "/api-login";//
    public static final String BASE_REQUEST_ROOM_BY_TOKEN = "/api-room";//

    //发送验证码
    public static final String REQUEST_SEND_V_CODE = BASE_REQUEST_LOGIN_BY_TOKEN + "/users/verificationCode";

    //登录
    public static final String REQUEST_LOGIN = BASE_REQUEST_LOGIN_BY_TOKEN + "/users/login";

    //获取用户信息
    public static final String REQUEST_USER_INFO = BASE_REQUEST_LOGIN_BY_TOKEN + "/users/getUserInfo";

    //注销用户
    public static final String REQUEST_USER_CANCELLATION = BASE_REQUEST_LOGIN_BY_TOKEN + "/users/cancellation";

    //上传头像
    public static final String REQUEST_USER_UPLOAD_PHOTO = BASE_REQUEST_LOGIN_BY_TOKEN + "/upload";

    //修改用户信息
    public static final String REQUEST_USER_UPDATE = BASE_REQUEST_LOGIN_BY_TOKEN + "/users/update";

}
