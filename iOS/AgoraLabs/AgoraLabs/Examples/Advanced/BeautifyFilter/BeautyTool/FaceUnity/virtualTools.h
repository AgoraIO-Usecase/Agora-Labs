//
//  virtualTools.h
//  AgoraDemoBox
//
//  Created by wanghaipeng on 2022/9/16.
//

#import <Foundation/Foundation.h>


@interface virtualTools : NSObject


+ (NSArray *)getAuthdata;

+ (NSString *)toJson:(id )dic ;
+ (NSData *)toData:(id )dic ;
@end
