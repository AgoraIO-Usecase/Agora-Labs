//
//  VLUserCenter.m
//  VoiceOnLine
//

#import "VLCache.h"
#import "VLUserCenter.h"

#import "VLURLPathConfig.h"
#import "VLAPIRequest.h"

@interface VLUserCenter()

@property (nonatomic, strong) VLLoginModel *loginModel;

@end

static NSString *kLocalLoginKey = @"kLocalLoginKey";

@implementation VLUserCenter

+ (VLUserCenter *)sharedInstance{
    static VLUserCenter *instancel = nil;
    static dispatch_once_t oneToken;
    dispatch_once(&oneToken, ^{
        instancel = [[VLUserCenter alloc]init];
    });
    return instancel;
}

- (BOOL)isLogin {
    if (!_loginModel) {
        _loginModel = (VLLoginModel *)[VLCache.system objectForKey:kLocalLoginKey];
    }
    return _loginModel ? YES : NO;
}

- (void)storeUserInfo:(VLLoginModel *)user {
    _loginModel = user;
    [VLCache.system setObject:_loginModel forKey:kLocalLoginKey];
}

- (void)logout {
    NSDictionary *param = @{@"userNo":VLUserCenter.user.userNo ?: @""};
    [VLAPIRequest getRequestURL:kURLPathLogout parameter:param showHUD:YES success:^(VLResponseDataModel * _Nonnull response) {
        [self cleanUserInfo];
    } failure:^(NSError * _Nullable error, NSURLSessionDataTask * _Nullable task) {
        [self cleanUserInfo];
    }];
    
}

- (void)cleanUserInfo {
    _loginModel = nil;
    [VLCache.system removeObjectForKey:kLocalLoginKey];
}

- (void)destroyUser{
    NSDictionary *param = @{@"userNo":VLUserCenter.user.userNo ?: @""};
    [VLAPIRequest getRequestURL:kURLPathDestroyUser parameter:param showHUD:YES success:^(VLResponseDataModel * _Nonnull response) {
        [self cleanUserInfo];
    } failure:^(NSError * _Nullable error, NSURLSessionDataTask * _Nullable task) {
        [self cleanUserInfo];
    }];
}

+ (VLLoginModel *)user {
    return [VLUserCenter sharedInstance].loginModel;
}

+ (void)clearUserRoomInfo {
    VLUserCenter.user.ifMaster = NO;
    [VLCache.system setObject:VLUserCenter.user forKey:kLocalLoginKey];
}

@end
