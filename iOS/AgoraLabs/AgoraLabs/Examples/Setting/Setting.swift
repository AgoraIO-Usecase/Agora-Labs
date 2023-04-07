//
//  Setting.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/10.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SwiftyJSON
import UIKit

class Setting: BaseViewController {

    @IBOutlet weak var flexContentTop: NSLayoutConstraint!
    
    @IBOutlet weak var flexContentHeigh: NSLayoutConstraint!
    
    @IBOutlet weak var contentView: UIView!
    
    @IBOutlet weak var protocolView: UIView!
    @IBOutlet weak var privacyView: UIView!
    @IBOutlet weak var logoutView: UIView!
    @IBOutlet weak var cancelView: UIView!
    
    private var logoutAlertView:VLPrivacyCustomView?
    private var cancelAlertView:VLPrivacyCustomView?
    private var currentAlertView:VLPrivacyCustomView?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
    }
    
    func setupUI() {
        self.setupNavigation()
    }
    
    func setupNavigation() {
        self.view.backgroundColor = SCREEN_CCC_COLOR.hexColor()
        if self.view.height <= 667 {
            flexContentTop.constant = 40
            flexContentHeigh.constant = 240
        }else{
            flexContentTop.constant = 60
            flexContentHeigh.constant = 272
        }
        let button = UIButton(type: .custom)
        button.frame = CGRect(x:0, y:0, width:65, height:30)
        button.setImage(UIImage(named:"navBack_black_new"), for: .normal)
        button.setImage(UIImage(named:"navBack_black_new"), for: .highlighted)
        button.setTitle("返回", for: .normal)
        button.setTitleColor("#eff4ff".hexColor(), for: .normal)
        button.addTarget(self, action: #selector(backBtnDidClick), for: .touchUpInside)
        let leftBarBtn = UIBarButtonItem(customView: button)
        self.navigationItem.leftBarButtonItem = leftBarBtn
    
        self.contentView.cornerRadius = 16
        self.contentView.masksToBounds = true
        
        self.protocolView.clickView {[weak self] sender in
            self?.pushToWebView(url: kURLPathH5UserAgreement)
        }
        
        self.privacyView.clickView {[weak self] sender in
            self?.pushToWebView(url: kURLPathH5Privacy)
        }
        
        self.logoutView.clickView {[weak self]  sender in
            self?.logoutPrepare()
        }
        
        self.cancelView.clickView {[weak self]  sender in
            self?.cancelPrepare()
        }
    }
    
    @objc func backBtnDidClick() {
        self.navigationController?.popViewController(animated: true)
    }
    
    func pushToWebView(url:String)  {
        let webVC = VLCommonWebViewController()
        webVC.urlString = url
        self.navigationController?.pushViewController(webVC, animated: true)
    }

    func logoutPrepare() {
        let alert = LEEAlert.alert()
        _ = alert.config
            .leeMaxWidth(300)
            .leeMaxHeight(380)
            .leeHeaderColor(.white)
            .leeAddCustomView({[weak self] sender in
                let  title = NSMutableAttributedString(string: "tcdlh".localized)
                title.yy_alignment = .center
                title.yy_font = UIFont.systemFont(ofSize: 15)
                
                let  confirmTitle = NSMutableAttributedString(string:"qrtc".localized)
                confirmTitle.yy_alignment = .center
                confirmTitle.yy_font = UIFont.systemFont(ofSize: 15)
                confirmTitle.yy_color = .white
                
                let  cancelTitle = NSMutableAttributedString(string:"qx".localized)
                cancelTitle.yy_alignment = .center
                cancelTitle.yy_font = UIFont.systemFont(ofSize: 15)
                cancelTitle.yy_color = "#0A3D7B".hexColor(alpha: 0.6)
                
                self?.logoutAlertView = VLPrivacyCustomView(contentTitle: title, confirmTitle: confirmTitle, cancelTitle: cancelTitle)
                self?.logoutAlertView?.delegate = self
                self?.currentAlertView = self?.logoutAlertView
                
                self?.logoutAlertView?.frame = CGRect(x: 0, y: 0, width: 250, height: 100);
                sender.view = self?.logoutAlertView
                sender.view?.superview?.layer.masksToBounds = false
                sender.positionType = .center;
            })
            .leeShow()
    }
    
    func cancelPrepare()  {
        let alert = LEEAlert.alert()
        _ = alert.config
            .leeMaxWidth(300)
            .leeMaxHeight(380)
            .leeHeaderColor(.white)
            .leeAddCustomView({[weak self] sender in
                let  title = NSMutableAttributedString(string: "zxh".localized)
                title.yy_alignment = .center
                title.yy_font = UIFont.systemFont(ofSize: 15)
                
                let  confirmTitle = NSMutableAttributedString(string:"zbzx".localized)
                confirmTitle.yy_alignment = .center
                confirmTitle.yy_font = UIFont.systemFont(ofSize: 16)
                confirmTitle.yy_color = .white
                
                let  cancelTitle = NSMutableAttributedString(string:"qrzx".localized)
                cancelTitle.yy_alignment = .center
                cancelTitle.yy_font = UIFont.systemFont(ofSize: 16)
                cancelTitle.yy_color = "#FF0000".hexColor()
                
                self?.cancelAlertView = VLPrivacyCustomView(contentTitle: title, confirmTitle: confirmTitle, cancelTitle: cancelTitle)
                self?.cancelAlertView?.delegate = self
                self?.currentAlertView = self?.cancelAlertView
                
                self?.cancelAlertView?.frame = CGRect(x: 0, y: 0, width: 250, height: 100);
                sender.view = self?.cancelAlertView
                sender.view?.superview?.layer.masksToBounds = false
                sender.positionType = .center;
            })
            .leeShow()
    }
}

extension Setting:VLPrivacyCustomViewDelegate{
    func privacyCustomViewDidClick(_ type: VLPrivacyClickType) {
        LEEAlert.close()
        if self.currentAlertView == self.logoutAlertView {
            if type.rawValue == 0 {
                //退出 - 确认 - 回到首页 弹出登录页
                VLUserCenter.sharedInstance().logout()
                self.navigationController?.popToRootViewController(animated: true)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUserLogoutNotify), object: nil, userInfo: nil)
            }else if type.rawValue == 1 {
                //退出- 取消
            }
        }else if self.currentAlertView == self.cancelAlertView {
            if type.rawValue == 0 {
                //注销- 取消
            }else if type.rawValue == 1 {
                //注销 - 确认 - 注销用户 - 回到首页 弹出登录页
                VLUserCenter.sharedInstance().destroyUser()
                self.navigationController?.popToRootViewController(animated: true)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUserLogoutNotify), object: nil, userInfo: nil)
            }
        }
        
    }
}
