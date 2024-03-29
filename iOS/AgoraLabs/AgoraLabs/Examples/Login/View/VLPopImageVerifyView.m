//
//  VLPopImageVerifyView.m
//  VoiceOnLine
//

#import "VLPopImageVerifyView.h"
#import "WMZCodeView.h"
#import "YYCategories.h"
#import "VLMacroDefine.h"
#import "VLHotSpotBtn.h"
//#import "MenuUtils.h"
//@import QMUIKit;
//@import YYCategories;

@interface VLPopImageVerifyView ()

@property(nonatomic, weak) id <VLPopImageVerifyViewDelegate>delegate;
@property(nonatomic,strong)WMZCodeView *codeView;
@property(nonatomic, assign) int verifyFailTimes;
@end

@implementation VLPopImageVerifyView

- (instancetype)initWithFrame:(CGRect)frame withDelegate:(id<VLPopImageVerifyViewDelegate>)delegate {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor whiteColor];
        self.delegate = delegate;
        [self setupView];
        _verifyFailTimes = 0;
    }
    return self;
}

- (void)setupView {
    VL(weakSelf);
    UIView *bgView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.width, self.height)];
    bgView.backgroundColor = [UIColor whiteColor];
    bgView.layer.cornerRadius = 16;
    bgView.layer.masksToBounds = YES;
    [self addSubview:bgView];
    
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, 22, 180, 20)];
    titleLabel.text = AGLocalizedString(@"wcyz");
    titleLabel.font = [UIFont systemFontOfSize:14];
    titleLabel.textColor = [UIColor colorWithRGB:0x6C7192];
    [self addSubview:titleLabel];
    
    VLHotSpotBtn *closeBtn = [[VLHotSpotBtn alloc]initWithFrame:CGRectMake(self.width-24-15, titleLabel.centerY-12, 24, 24)];
    [closeBtn setImage:[UIImage imageNamed:@"login_pop_closeIcon"] forState:UIControlStateNormal];
    [closeBtn addTarget:self action:@selector(closeBtnClickEvent) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:closeBtn];

    UILabel *slideLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, titleLabel.bottom+5, 270, 22)];
    slideLabel.text = AGLocalizedString(@"tdxfhkwcpt");
    slideLabel.font = [UIFont systemFontOfSize:16];
    slideLabel.numberOfLines = 0;
    slideLabel.lineBreakMode = NSLineBreakByCharWrapping;
    slideLabel.textColor = [UIColor colorWithRGB:0x040925];
    [self addSubview:slideLabel];
    
    //使用方法
    self.codeView = [[WMZCodeView sharedInstance] addCodeViewWithFrame:CGRectMake(10, slideLabel.bottom+15, self.width-20, (self.width-20)*0.64+63)  withBlock:^(BOOL success) {
        if (success) {
            if (weakSelf.delegate && [weakSelf.delegate respondsToSelector:@selector(slideSuccessAction)]) {
                [weakSelf.delegate slideSuccessAction];
            }
        }
        else {
            [AGHUD showFaildWithInfo:AGLocalizedString(@"yzsb")];
            weakSelf.verifyFailTimes += 1;
            if(weakSelf.verifyFailTimes >= 3) {
                weakSelf.verifyFailTimes = 0;
                [weakSelf changeImage];
            }
        }
    }];
    
    [self addSubview:self.codeView];
    
}

- (void)changeImage
{
    [self.codeView refreshAction];
}

- (void)closeBtnClickEvent{
    if (self.delegate && [self.delegate respondsToSelector:@selector(closeBtnAction)]) {
        [self.delegate closeBtnAction];
    }
}

@end
