//
//  virtualTools.m
//  AgoraDemoBox
//
//  Created by wanghaipeng on 2022/9/16.
//

#import "virtualTools.h"
#import "authpack.h"

@implementation virtualTools


+ (NSArray *)getAuthdata{
    
    NSMutableArray *authdata =
        [[NSMutableArray alloc] initWithCapacity:sizeof(g_auth_package)];
    for (int i = 0; i < sizeof(g_auth_package); i++) {
      [authdata addObject:@(g_auth_package[i])];
    }
    return  authdata;
}

+ (NSString *)toJson:(id )dic {
    
  
  NSError *error;
  NSData *data =
      [NSJSONSerialization dataWithJSONObject:dic
                                      options:NSJSONWritingPrettyPrinted
                                        error:&error];
    NSString *tempString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
  return tempString;
    
}

+ (NSData *)toData:(id)dic{
    NSError *error;
    NSData *data =
        [NSJSONSerialization dataWithJSONObject:dic
                                        options:NSJSONWritingPrettyPrinted
                                          error:&error];
    
    return data;
}


@end
