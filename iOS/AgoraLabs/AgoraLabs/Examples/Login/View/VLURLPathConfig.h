//
//  VLURLPathConfig.h
//  VoiceOnLine
//

#ifndef VLURLPathConfig_h
#define VLURLPathConfig_h

//通知的字符串
//static NSString * const kExitRoomNotification = @"exitRoomNotification";
static NSString * const kChoosedSongListChangedNotification = @"choosedSongListChangedNotification";

#pragma mark - API
static NSString * const kURLPathUploadImage = @"/api-login/upload"; //上传图片
static NSString * const kURLPathDestroyUser = @"/api-login/users/cancellation"; //注销用户
static NSString * const kURLPathGetUserInfo = @"/api-login/users/getUserInfo"; //获取用户信息
static NSString * const kURLPathLogin = @"/api-login/users/login"; // 登录
static NSString * const kURLPathLogout = @"/api-login/users/logout"; // 退出登录 （接口文档未完成）
static NSString * const kURLPathUploadUserInfo = @"/api-login/users/update";  //修改用户信息
static NSString * const kURLPathVerifyCode = @"/api-login/users/verificationCode"; //发送验证码

static NSString * const kUserLogoutNotify = @"kUserLogoutNotify";

#pragma mark - H5相关
static NSString * const kURLPathH5UserAgreement = @"https://www.shengwang.cn/contracts/lab/service-term/";
static NSString * const kURLPathH5Privacy = @"https://www.shengwang.cn/contracts/lab/privacy-term/";
static NSString * const kURLPathH5AboutUS = @"https://www.agora.io/cn/about-us/";

#endif /* VLURLPathConfig_h */
