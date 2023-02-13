//
//  VLRequestRoute.m
//  VoiceOnLine
//

#import "VLRequestRoute.h"
#import "VLNetworkConfig.h"
#import "VLUserCenter.h"
#import "AgoraLabs-Swift.h"

@implementation VLRequestRoute

+ (NSString *)doRoute:(NSString *)route andMethod:(NSString *)method{
    NSString *url = @"";
    if(route && route.length > 0){
        if (method && method.length > 0) {
            url = [NSString stringWithFormat:@"%@?act=%@&op=%@",[self getHostUrl],route,method];
        }else{
           url =  [NSString stringWithFormat:@"%@?act=%@",[self getHostUrl],route];
        }
    }else{
        return [self getRequestUrl:method];
    }
    return url;
}
+ (NSString*)getHostUrl {
    return [NSString stringWithFormat:@"%@", @"https://gateway-fulldemo-staging.agoralab.co"];
}
+ (NSString*)getRequestUrl:(NSString *)url {
    return [NSString stringWithFormat:@"%@%@", @"https://gateway-fulldemo-staging.agoralab.co", url];
}

+ (NSString *)getToken {
    if ([VLUserCenter sharedInstance].isLogin){
        return VLUserCenter.user.token;
    }else{
        return @"";
    }
    return @"";
}

@end
