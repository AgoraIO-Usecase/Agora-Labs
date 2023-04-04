//
//  ALLoginViewController.m
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/6.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

#import "ALLoginViewController.h"
#import "Masonry.h"
#import "VLAPIRequest.h"
#import "VLCommonWebViewController.h"
#import "VLLoginInputPhoneView.h"
#import "VLLoginInputVerifyCodeView.h"
#import "VLMacroDefine.h"
#import "VLURLPathConfig.h"
#import "VLPrivacyCustomView.h"
#import "VLPopImageVerifyView.h"
#import "YYCategories.h"
#import "YYText.h"
#import "LEEAlert.h"
#import "LSTPopView.h"
#import "AgoraLabs-Swift.h"

#import "VLLoginModel.h"
#import "VLUserCenter.h"

@interface ALLoginViewController () <VLLoginInputVerifyCodeViewDelegate, VLPrivacyCustomViewDelegate,VLPopImageVerifyViewDelegate>

@property (nonatomic, strong) UILabel *appNameLabel;
@property (nonatomic, strong) VLLoginInputPhoneView *phoneView;
@property (nonatomic, strong) VLLoginInputVerifyCodeView *verifyView;
@property (nonatomic, strong) VLPrivacyCustomView *privacyCustomView;
@property (nonatomic, strong) UIButton *loginButton;
@property (nonatomic, strong) UIButton *agreeButton;    // 同意按钮
@property (nonatomic, strong) YYLabel *privacyLabel;    // 隐私政策
@property (nonatomic, strong) LSTPopView *popView;
@property (nonatomic, assign) bool policyAgreed;

@end

@implementation ALLoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _policyAgreed = NO;
    
    [self.view setBackgroundColor:[UIColor colorWithRGB:0xeff4ff]];
    UIImageView *image = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"ic_main_bg"]];
    [self.view addSubview:image];
    [image mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.view);
    }];
    
    [self setupViews];
    [self setupLayout];
}

- (void)viewDidAppear:(BOOL)animated
{
    [self showAlertPrivacyView];
}

- (void)showAlertPrivacyView
{
    if(_policyAgreed == NO) {
        [self alertPrivacyAlertView:0];
    }
}

- (void)setupViews {
   
    [self.view addSubview:self.appNameLabel];
    [self.view addSubview:self.phoneView];
    [self.view addSubview:self.verifyView];
    [self.view addSubview:self.agreeButton];
    [self.view addSubview:self.privacyLabel];
    [self.view addSubview:self.loginButton];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:true];
}

- (void)setupLayout {
    [self.appNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(30);
        make.top.mas_equalTo(kStatusBarHeight + 125);
        
    }];
    [self.phoneView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.appNameLabel);
        make.right.mas_equalTo(-30);
        make.top.mas_equalTo(self.appNameLabel.mas_bottom).offset(24);
    }];
    
    [self.verifyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.appNameLabel);
        make.right.mas_equalTo(-30);
        make.top.mas_equalTo(self.phoneView.mas_bottom).offset(24);
    }];
    
    [self.agreeButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(30);
        make.top.mas_equalTo(self.verifyView.mas_bottom).offset(20);
    }];
    
    [self.privacyLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.agreeButton.mas_right).offset(5);
        make.centerY.mas_equalTo(self.agreeButton);
    }];
    
    [self.loginButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(30);
        make.right.mas_equalTo(-30);
        make.height.mas_equalTo(48);
        make.top.mas_equalTo(self.verifyView.mas_bottom).offset(64);
    }];
    
    UILabel *versionLabel = [[UILabel alloc]init];
    versionLabel.text = [NSString stringWithFormat:@"Powered by %@ \nv 2.0",AGLocalizedString(@"Agora.io")];
    versionLabel.textAlignment = NSTextAlignmentCenter;
    versionLabel.numberOfLines = 0;
    versionLabel.lineBreakMode = NSLineBreakByWordWrapping;
    [versionLabel setFont:[UIFont systemFontOfSize:13]];
    [versionLabel setTextColor:[UIColor colorWithRGB:0xC9CDD6]];
    [self.view addSubview:versionLabel];
    [versionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.view);
        make.bottom.mas_equalTo(self.view.mas_safeAreaLayoutGuideBottom);
    }];
}

- (void)alertPrivacyAlertView:(int)pass {
    kWeakSelf(self)
    [LEEAlert alert].config
    .LeeMaxWidth(300)
    .LeeMaxHeight(380)
    .LeeHeaderColor([UIColor whiteColor])
    .LeeAddTitle(^(UILabel * _Nonnull label) {
        label.text = @"个人信息保护指引";
        label.textColor = [UIColor colorWithRGB:0x040925];
        label.font = [UIFont systemFontOfSize:16];
    })
    .LeeItemInsets(UIEdgeInsetsMake(20, 10, 10, 0))
    .LeeAddCustomView(^(LEECustomView *custom) {
        kStrongSelf(self)
        self.privacyCustomView = [self getPrivacyCustomView:pass];
        custom.view = self.privacyCustomView;
        custom.view.superview.layer.masksToBounds = NO;
        custom.positionType = LEECustomViewPositionTypeCenter;
    })
    // 想为哪一项设置间距范围 直接在其后面设置即可 ()
    .LeeItemInsets(UIEdgeInsetsMake(10, 10, 20, 10))
    .LeeShow();
}

#pragma mark - Public Methods
- (BOOL)preferredNavigationBarHidden {
    return YES;
}

#pragma mark - Request

- (void)loadVerifyCodeRequest {
    
}

#pragma mark - VLPrivacyCustomViewDelegate

- (void)privacyCustomViewDidClick:(VLPrivacyClickType)type {
    switch (type) {
        case VLPrivacyClickTypeAgree:
            self.agreeButton.selected = NO;
            _policyAgreed = NO;
            [self closePrivaxyAlertView];
            break;
        case VLPrivacyClickTypeDisagree:
            self.agreeButton.selected = NO;
            if(self.privacyCustomView.pass == 0) {
                self.privacyCustomView = nil;
                [self alertPrivacyAlertView:1];
            }
            else if(self.privacyCustomView.pass != 0) {
                //exit(0);
                [self closePrivaxyAlertView];
            }
            break;
        case VLPrivacyClickTypePrivacy:
            [self pushToWebView:kURLPathH5Privacy];
            [self closePrivaxyAlertView];
            break;
        case VLPrivacyClickTypeUserAgreement:
            [self pushToWebView:kURLPathH5UserAgreement];
            [self closePrivaxyAlertView];
            break;
        default:
            break;
    }
}

- (void)closePrivaxyAlertView {
    [LEEAlert closeWithCompletionBlock:^{}];
}

#pragma mark - VLLoginInputVerifyCodeViewDelegate

- (void)verifyCodeViewDidClickSendVerifyCode:(UIButton *)sender{
    if (![self checkPhoneNo]) return;

    NSDictionary *param = @{
        @"phone":self.phoneView.phoneNo
    };
    [AGHUD showNetWork];
    if ([NetworkManager shared].getReachabilityState == false){
        return;
    }
    [VLAPIRequest getRequestURL:kURLPathVerifyCode parameter:param showHUD:YES success:^(VLResponseDataModel * _Nonnull response) {
        if (response.code == 0) {
            [AGHUD disMiss];
            [self.verifyView startTime:sender];
        } else {
            [AGHUD showFaildWithInfo:response.message];
        }
    } failure:^(NSError * _Nullable error, NSURLSessionDataTask * _Nullable task) {
    }];
}

#pragma mark - Action

- (void)loginClick:(UIButton *)button {
    
    if (![self checkPhoneNo]) return;
    if (![self checkPrivacyAgree]) return;
    if (![self checkVerifyCode]) return;
    
    [self popImageVerifyView];
}

-(BOOL)checkVerifyCode {
    
    if (_verifyView.isVerifyCodeSent == NO || [_verifyView.verifyCode isEqualToString:@""] || !_verifyView.verifyCode) {
        [AGHUD showFaildWithInfo:@"请输入验证码"];
        return NO;
    }
    return YES;
}

-(BOOL)checkPrivacyAgree {
    if (!self.agreeButton.selected) {
        [AGHUD showFaildWithInfo:@"请您认真阅读用户协议及隐私政策的条款内容，同意后可开始使用我们的服务"];
        return NO;
    }
    return YES;
}

- (BOOL)isValidateRegularExpression:(NSString *)strDestination byExpression:(NSString *)strExpression {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",strExpression];
    return [predicate evaluateWithObject:strDestination];
}

// 是否手机号码
- (BOOL)isValidateTelNumber:(NSString *)number {
    // 手机号正则表达式
    // NSString *strRegex = @"^1+[3578]+\\d{9}";
    NSString *strRegex = @"^1+\\d{10}";
    BOOL rt = [self isValidateRegularExpression:number byExpression:strRegex];
    if (rt == YES) {
        
        if (number.length < 11 || number.length > 11) {
            rt = NO;
            return rt;
        } else {
            
            if ([number hasPrefix:@"0"]) {
                rt = NO;
                return rt;
            }
        }
    }
    
    return rt;
}

-(BOOL)checkPhoneNo {
    if ([_phoneView.phoneNo isEqualToString:@""] || !_phoneView.phoneNo) {
        [AGHUD showFaildWithInfo:@"请输入手机号"];
        return NO;
    }
    
    if (![self isValidateTelNumber:_phoneView.phoneNo]) {
        [AGHUD showFaildWithInfo:@"手机号码格式错误"];
        return NO;
    }
    
    return YES;
}

- (void)agreeButtonAction {
    self.agreeButton.selected = !self.agreeButton.selected;
}

- (void)navigatorToWebviewOfUserProtocol{
    [self pushToWebView:kURLPathH5UserAgreement];
}

- (void)navigatorToWebviewOfPrivacyProtocol{
    [self pushToWebView:kURLPathH5Privacy];
}

- (void)pushToWebView:(NSString *)url {
    VLCommonWebViewController *webVC = [[VLCommonWebViewController alloc] init];
    webVC.urlString = url;
    if (!self.navigationController) {
        [self presentViewController:webVC animated:YES completion:nil];
    } else {
        [self.navigationController pushViewController:webVC animated:YES];
    }
}

- (void)popImageVerifyView {

    VLPopImageVerifyView *imageVerifyView = [[VLPopImageVerifyView alloc]initWithFrame:CGRectMake(40, 0, kScreenWidth-80, 198+(kScreenWidth-120)*0.65) withDelegate:self];

    LSTPopView *popView = [LSTPopView initWithCustomView:imageVerifyView parentView:self.view popStyle:LSTPopStyleFade dismissStyle:LSTDismissStyleFade];
    popView.hemStyle = LSTHemStyleCenter;
    popView.popDuration = 0.5;
    popView.dismissDuration = 0.5;
    popView.cornerRadius = 16;
    self.popView = popView;
    popView.isClickFeedback = NO;
    [popView pop];
}

#pragma mark --delegate
- (void)closeBtnAction {
    [self.popView dismiss];
}

- (void)slideSuccessAction {
    [self.popView dismiss];
    
    NSDictionary *param = @{
        @"phone" : self.phoneView.phoneNo,
        @"code": self.verifyView.verifyCode
    };
    if ([NetworkManager shared].getReachabilityState == false){
        return;
    }
    [VLAPIRequest getRequestURL:kURLPathLogin parameter:param showHUD:YES success:^(VLResponseDataModel * _Nonnull response) {
        if (response.code == 0) {
            VLLoginModel *model = [VLLoginModel vj_modelWithDictionary:response.data];
            [[VLUserCenter sharedInstance] storeUserInfo:model];
            [self dismissViewControllerAnimated:true completion:nil];
            //[UIApplication.sharedApplication.delegate.window configRootViewController];
        }
        else {
            dispatch_main_async_safe(^{
                [AGHUD showFaildWithInfo:@"验证码校验失败，请重试"];
            })
        }
    } failure:^(NSError * _Nullable error, NSURLSessionDataTask * _Nullable task) {
        dispatch_main_async_safe(^{
            [AGHUD showFaildWithInfo:@"验证码校验失败，请重试"];
        })
    }];
}

#pragma mark - Lazy

- (UILabel *)appNameLabel {
    if (!_appNameLabel) {
        _appNameLabel = [[UILabel alloc] init];
        _appNameLabel.font = [UIFont systemFontOfSize:26];
        _appNameLabel.textColor = [UIColor colorWithRGB:0x040925];
        _appNameLabel.text = AGLocalizedString(@"hytyswfw");
    }
    return _appNameLabel;
}

- (VLLoginInputPhoneView *)phoneView {
    if (!_phoneView) {
        _phoneView = [[VLLoginInputPhoneView alloc] init];
    }
    return _phoneView;
}

- (VLLoginInputVerifyCodeView *)verifyView {
    if (!_verifyView) {
        _verifyView = [[VLLoginInputVerifyCodeView alloc] init];
        _verifyView.delegate = self;
    }
    return _verifyView;
}

- (UIButton *)agreeButton {
    if (!_agreeButton) {
        _agreeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_agreeButton setImage:[UIImage imageNamed:@"online_create_screatNormalIcon"] forState:UIControlStateNormal];
        [_agreeButton setImage:[UIImage imageNamed:@"online_create_screatSelIcon"] forState:UIControlStateSelected];
        [_agreeButton addTarget:self action:@selector(agreeButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _agreeButton;
}

- (YYLabel *)privacyLabel{
    if(_privacyLabel) return _privacyLabel;
    
    _privacyLabel = [[YYLabel alloc] init];
    _privacyLabel.numberOfLines = 0;
    _privacyLabel.preferredMaxLayoutWidth = kScreenWidth - 30 * 2;
    
    NSString *_str4Total = AGLocalizedString(@"wyydbtyfwtkhystk");//@"我已阅读并同意 用户协议 及 隐私政策 ";
    NSString *_str4Highlight1 = AGLocalizedString(@"yhxy1");
    NSString *_str4Highlight2 = AGLocalizedString(@"yszc1");
    NSMutableAttributedString *_mattrStr = [NSMutableAttributedString new];

    [_mattrStr appendAttributedString:[[NSAttributedString alloc] initWithString:_str4Total attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:12], NSForegroundColorAttributeName : [UIColor colorWithRGB:0x6C7192]}]];
    _mattrStr.yy_lineSpacing = 6;
    NSRange range1 = [_str4Total rangeOfString:_str4Highlight1];
    NSRange range2 = [_str4Total rangeOfString:_str4Highlight2];
    [_mattrStr addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:range1];
    [_mattrStr addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:range2];
    if(range1.location != NSNotFound){
        kWeakSelf(self)
        [_mattrStr yy_setTextHighlightRange:range1 color:[UIColor colorWithRGB:0x009FFF]  backgroundColor:[UIColor clearColor] tapAction:^(UIView * _Nonnull containerView, NSAttributedString * _Nonnull text, NSRange range, CGRect rect) {
            [weakself navigatorToWebviewOfUserProtocol];
        }];
    }
    if(range2.location != NSNotFound){
        kWeakSelf(self)
        [_mattrStr yy_setTextHighlightRange:range2 color:[UIColor colorWithRGB:0x009FFF]  backgroundColor:[UIColor clearColor] tapAction:^(UIView * _Nonnull containerView, NSAttributedString * _Nonnull text, NSRange range, CGRect rect) {
            [weakself navigatorToWebviewOfPrivacyProtocol];
        }];
    }
    _privacyLabel.lineBreakMode = NSLineBreakByWordWrapping;
    _privacyLabel.attributedText = _mattrStr;
    return _privacyLabel;
}

- (UIButton *)loginButton {
    if (!_loginButton) {
        _loginButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_loginButton setTitle:AGLocalizedString(@"dl") forState:UIControlStateNormal];
        [_loginButton addTarget:self action:@selector(loginClick:) forControlEvents:UIControlEventTouchUpInside];
        [_loginButton setBackgroundColor:[UIColor colorWithRGB:0x345DFF]];
        CAGradientLayer *gl = [CAGradientLayer layer];
        gl.frame = CGRectMake(30,445,315,48);
        gl.startPoint = CGPointMake(0.43, 0.01);
        gl.endPoint = CGPointMake(0.43, 1);
        gl.colors = @[(__bridge id)[UIColor colorWithRed:33/255.0 green:155/255.0 blue:255/255.0 alpha:1.0].CGColor, (__bridge id)[UIColor colorWithRed:52/255.0 green:93/255.0 blue:255/255.0 alpha:1.0].CGColor];
        gl.locations = @[@(0), @(1.0f)];
        _loginButton.layer.cornerRadius = 24;
        _loginButton.layer.shadowColor = [UIColor colorWithRed:0/255.0 green:139/255.0 blue:250/255.0 alpha:0.2000].CGColor;
        _loginButton.layer.shadowOffset = CGSizeMake(0,4);
        _loginButton.layer.shadowOpacity = 1;
        _loginButton.layer.shadowRadius = 8;
    }
    return _loginButton;
}

- (VLPrivacyCustomView *)getPrivacyCustomView:(int)pass {
    if (!_privacyCustomView) {
        _privacyCustomView = [[VLPrivacyCustomView alloc] initWithPass:pass];
        if(pass == 0) {
            _privacyCustomView.frame = CGRectMake(0, 0, 250, 260);
        }
        else {
            _privacyCustomView.frame = CGRectMake(0, 0, 250, 100);
        }
        _privacyCustomView.backgroundColor = [UIColor whiteColor];
        _privacyCustomView.delegate = self;
    }
    return _privacyCustomView;
}

@end

