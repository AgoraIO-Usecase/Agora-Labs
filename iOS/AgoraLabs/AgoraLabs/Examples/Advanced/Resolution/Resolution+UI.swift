//
//  Resolution+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/15.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SnapKitExtend
import UIKit

extension Resolution {
    
    func setupUI() {
        self.setupNavigation()
        self.setupBottom()
        self.setupShowContentView()
        self.setupBottomContentView()
        self.setupMultipleView()
    }
    
    func setupNavigation() {
        self.view.backgroundColor = SCREEN_CCC_COLOR.hexColor()

        let button = UIButton(type: .custom)
        button.frame = CGRect(x:0, y:0, width:65, height:30)
        button.setImage(UIImage(named:"ChevronLeft"), for: .normal)
        button.setImage(UIImage(named:"ChevronLeft"), for: .highlighted)
        button.setTitle("Resolution".localized, for: .normal)
        button.addTarget(self, action: #selector(backBtnDidClick), for: .touchUpInside)
        let leftBarBtn = UIBarButtonItem(customView: button)
        self.navigationItem.leftBarButtonItem = leftBarBtn
        
        
        let imageOne = UIImageView()
        imageOne.image = UIImage.init(named: "CameraFlip")
        imageOne.contentMode = .scaleAspectFit
        imageOne.isUserInteractionEnabled = true
        imageOne.tag = 1000
        let tapGes = UITapGestureRecognizer(target: self, action: #selector(switchBtnDidClick))
        imageOne.addGestureRecognizer(tapGes)
        imageOne.frame = CGRect(x: 0, y: 0, width: 20, height: 15)
        let barItemOne = UIBarButtonItem.init(customView: imageOne)
        
        let imageTwo = UIImageView()
        imageTwo.image = UIImage.init(named: "view1")
        imageTwo.contentMode = .scaleAspectFit
        imageTwo.isUserInteractionEnabled = true
        imageTwo.tag = 1001
        let twoGes = UITapGestureRecognizer(target: self, action: #selector(layoutBtnDidClick))
        imageTwo.addGestureRecognizer(twoGes)
        
        let longGes = UILongPressGestureRecognizer(target: self, action: #selector(layoutPopDidClick))
        imageTwo.addGestureRecognizer(longGes)
        
        imageTwo.frame = CGRect(x: 0, y: 0, width: 20, height: 15)
        self.layoutImage = imageTwo
        let barItemTwo = UIBarButtonItem.init(customView: imageTwo)
        
        navigationItem.rightBarButtonItems = [barItemOne,barItemTwo]
    }
    
    
    
    func setupShowContentView() {
        self.view.layoutIfNeeded()
        self.view.addSubview(contentView)
        contentView.snp.makeConstraints { make in
            make.top.left.right.equalToSuperview()
            make.bottom.equalTo(bottomView.snp.top).offset(SCREEN_RECT_CORNER)
        }
        
        contentView.addSubview(localVideoView)
        localVideoView.snp.makeConstraints { make in
            make.top.left.right.equalToSuperview()
            make.height.equalTo(contentView.snp.height).multipliedBy(0.5)
        }
        
        contentView.addSubview(remoteVideoView)
        remoteVideoView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(localVideoView.snp.bottom)
            make.height.equalTo(contentView.snp.height).multipliedBy(0.5)
        }
        
        self.view.bringSubviewToFront(bottomView)
    }
    
    @objc func layoutPopDidClick(_ sender:UILongPressGestureRecognizer)  {
        
        guard let _layoutImage = self.layoutImage else { return  }
        if XMMenuPopover.shared.isMenuVisible {  return  }
        
        if sender.state == .began {
            AGHUD.touchFeedback()
            
            let itemW = 51
            let itemH = 56
            let view = UIView(frame: CGRect(x: 0, y: 0, width: itemW*3, height: itemH))
            view.backgroundColor = .black
            
            let view1Btn = UIButton(frame: CGRect(x: 0, y: 0, width: itemW, height: itemH))
            view1Btn.setImage(UIImage(named: "view1"), for: .normal)
            view1Btn.clickView {[weak self] sender in
                self?.updataLayoutContentView(tag: 1)
                XMMenuPopover.shared.hideMenu()
            }
            view.addSubview(view1Btn)
            
            let view2Btn = UIButton(frame: CGRect(x: itemW, y: 0, width: itemW, height: itemH))
            view2Btn.setImage(UIImage(named: "view2"), for: .normal)
            view2Btn.clickView {[weak self] sender in
                self?.updataLayoutContentView(tag: 2)
                XMMenuPopover.shared.hideMenu()
            }
            view.addSubview(view2Btn)
            
            let view3Btn = UIButton(frame: CGRect(x: itemW*2, y: 0, width: itemW, height: itemH))
            view3Btn.setImage(UIImage(named: "view3"), for: .normal)
            view3Btn.clickView {[weak self] sender in
                self?.updataLayoutContentView(tag: 3)
                XMMenuPopover.shared.hideMenu()
            }
            view.addSubview(view3Btn)
            
            let popView = XMMenuPopover.shared
            popView.style = .custom
            popView.avoidTopMargin = 0
            popView.customView = view
            popView.show(from:_layoutImage , rect: _layoutImage.bounds,animated: true)        }
    }
    
    @objc func layoutBtnDidClick() {
        self.layoutType = self.layoutType >= 3 ?1:(self.layoutType+1)
        self.updataLayoutContentView(tag: self.layoutType )
    }
    
    @objc func updataLayoutContentView(tag:Int) {
        self.layoutType = tag
        self.layoutImage?.image = UIImage(named: "view\(self.layoutType)")
        AGHUD.touchFeedback()
        if tag == 1 {
            //原视频(上) 远程(下)
            print("原视频(上) 远程(下)")
            localVideoView.reloadPosition(type: .top)
            localVideoView.cornerRadius = 0
            localVideoView.snp.remakeConstraints { make in
                make.top.left.right.equalToSuperview()
                make.height.equalTo(contentView.snp.height).multipliedBy(0.5)
            }
            
            
            remoteVideoView.reloadPosition(type: .bottom)
            remoteVideoView.cornerRadius = 0
            contentView.bringSubviewToFront(remoteVideoView)
            remoteVideoView.snp.remakeConstraints { make in
                make.left.right.equalToSuperview()
                make.top.equalTo(localVideoView.snp.bottom)
                make.height.equalTo(contentView.snp.height).multipliedBy(0.5)
            }
        }else if tag == 2 {
            //原视频(大) 远程(小)
            print("原视频(大) 远程(小)")
            
            localVideoView.reloadPosition(type: .inside)
            localVideoView.cornerRadius = 0
            localVideoView.snp.remakeConstraints { make in
                make.top.left.right.equalToSuperview()
                make.height.equalTo(contentView.snp.height)
            }
            
            remoteVideoView.reloadPosition(type: .outside)
            remoteVideoView.cornerRadius = 8
            contentView.bringSubviewToFront(remoteVideoView)
            remoteVideoView.snp.remakeConstraints { make in
                make.right.equalToSuperview().offset(-16)
                make.bottom.equalTo(multipleView.snp.top).offset(-16)
                make.size.equalTo(CGSize(width: 160, height: 200))
            }
        }else if tag == 3 {
            //原视频(小) 远程(大)
            print("原视频(小) 远程(大)")
            remoteVideoView.reloadPosition(type: .inside)
            remoteVideoView.cornerRadius = 0
            remoteVideoView.snp.remakeConstraints { make in
                make.top.left.right.equalToSuperview()
                make.height.equalTo(contentView.snp.height)
            }
            
            localVideoView.reloadPosition(type: .outside)
            localVideoView.cornerRadius = 8
            contentView.bringSubviewToFront(localVideoView)
            localVideoView.snp.remakeConstraints { make in
                make.right.equalToSuperview().offset(-16)
                make.bottom.equalTo(multipleView.snp.top).offset(-16)
                make.size.equalTo(CGSize(width: 160, height: 200))
            }
        }
        
    }

    
    func setupBottom() {
        
        self.view.addSubview(bottomView)
        bottomView.snp.makeConstraints { make in
            make.bottom.left.right.equalToSuperview()
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+62+118)
        }
        
        bottomView.addSubview(bottomContentView)
        bottomContentView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.bottom.equalToSuperview().offset(-SCREEN_BOTTOM_HEIGHT)
            make.height.equalTo(118)
        }
        
        self.view.addSubview(multipleView)
        multipleView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.bottom.equalTo(bottomView.snp.top)
            make.height.equalTo(56)
        }
        
        let bottomHubView = UIView()
        bottomHubView.backgroundColor = "FFFFFF".hexColor(alpha: 0.3)
        bottomView.addSubview(bottomHubView)
        bottomHubView.snp.makeConstraints { make in
            make.top.equalToSuperview().offset(8)
            make.centerX.equalToSuperview()
            make.size.equalTo(CGSize(width: 37, height: 3))
        }
                
        let upRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeFrom(_:)))
        upRecognizer.direction = .up
        self.bottomView.addGestureRecognizer(upRecognizer)
        
        let downRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeFrom(_:)))
        downRecognizer.direction = .down
        self.bottomView.addGestureRecognizer(downRecognizer)
        
        bottomView.rectCorner(corner: [.topLeft,.topRight], radii:  CGSize(width: 12, height: 12))
    }
        
    func setupBottomContentView() {
        let funNameLabel = UILabel()
        funNameLabel.text = "qiyongchaofen".localized
        funNameLabel.textColor = .white
        funNameLabel.font = UIFont.boldSystemFont(ofSize: 15)
        bottomView.addSubview(funNameLabel)
        funNameLabel.snp.makeConstraints { make in
            make.top.equalToSuperview().offset(26)
            make.left.equalToSuperview().offset(16)
        }
        
        bottomView.addSubview(openSwitch)
        openSwitch.snp.makeConstraints { make in
            make.centerY.equalTo(funNameLabel.snp.centerY)
            make.right.equalToSuperview().offset(-16)
        }
        
        for i in 0..<itemModelList.count {
            let itemModel = itemModelList[i]
            let itemView = SubCellView()
            itemModel.subView = itemView
            itemModel.isSelected = i == 0
            itemModel.isEnabled = openSwitch.isOn
            itemView.tag = i
            itemView.setupSubCellModel(itemModelList[i])
            bottomContentView.addSubview(itemView)
            itemView.clickView {[weak self] sender in
                AGHUD.touchFeedback()
                self?.switchResolution(tag: sender.tag)
            }
        }
        
        let buttonArr = itemModelList.compactMap({$0.subView})
        buttonArr.snp.distributeViewsAlong(axisType: .horizontal, fixedSpacing: 39, leadSpacing: 31, tailSpacing: 31)
        buttonArr.snp.makeConstraints{
            $0.centerY.equalToSuperview()
            $0.height.equalTo(70)
        }
    }
    
    func setupMultipleView()  {
        self.view.bringSubviewToFront(multipleView)
        for i in 0..<multipleModelList.count {
            let itemModel = multipleModelList[i]
            let button = SubButton(alphaNormal: 0.9)
            itemModel.subView = button
            itemModel.isSelected = i == 0
            button.tag = i
            button.cornerRadius = 18
            button.masksToBounds = true
            button.alphaSelected = 0.9
            button.isEnabled = openSwitch.isOn
            button.addBlurEffect(style: .systemThinMaterialDark)
            button.titleLabel?.font = UIFont.boldSystemFont(ofSize: 13)
            button.setTitleColor(.lightGray, for: .disabled)
            button.contentEdgeInsets = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 10)
            button.setTitle("\(itemModel.name)倍", for: .normal)
            button.clickView {[weak self] sender in
                self?.switchMultipleChange(sender as! SubButton)
            }
            multipleView.addSubview(button)
        }
        let buttonArr = multipleModelList.compactMap({$0.subView})
        buttonArr.snp.distributeViewsAlong(axisType: .horizontal, fixedSpacing: 10, leadSpacing: 10, tailSpacing: 10)
        buttonArr.snp.makeConstraints{
            $0.centerY.equalToSuperview()
            $0.height.equalTo(36)
        }
    }
    
    @objc func handleSwipeFrom(_ recognizer:UISwipeGestureRecognizer){
        if recognizer.direction == .up {
            
            bottomView.snp.updateConstraints{ make in
                make.bottom.equalToSuperview()
            }
            multipleView.snp.updateConstraints { make in
                make.bottom.equalTo(bottomView.snp.top)
            }
            multipleView.isHidden = false
            bottomContentView.isHidden = false
        }else if recognizer.direction == .down {
            
            bottomView.snp.updateConstraints { make in
                make.bottom.equalToSuperview().offset(118)
            }
            multipleView.snp.updateConstraints { make in
                make.bottom.equalTo(bottomView.snp.top).offset(56)
            }
            multipleView.isHidden = true
            bottomContentView.isHidden = true
        }
    }
    
    func switchResolution(tag:Int) {
        if openSwitch.isOn == false { return }
        self.itemModelList.forEach { model in
            model.isSelected = model.tag == tag
            guard let itemView = model.subView as? SubCellView  else { return  }
            itemView.setupSubCellModel(model)
        }
        guard let videoConfig = self.itemModelList[tag].value as? AgoraVideoEncoderConfiguration else { return }
        self.setupResolution(videoConfig: videoConfig)
    }
    
    @objc func switchOpenChange(_ sender:UISwitch)  {
        print("switchOpenChange - \(sender.isOn)")
        self.remoteVideoView.titleSelected = sender.isOn
        
        self.multipleModelList.forEach { model in
            guard let itemView = model.subView as? SubButton  else { return  }
            itemView.isSelected = sender.isOn ?model.isSelected:false
            itemView.isEnabled = sender.isOn
            if sender.isOn { self.currentModel = model }
        }
        
        self.itemModelList.forEach { model in
            model.isEnabled = sender.isOn
            guard let itemView = model.subView as? SubCellView  else { return  }
            itemView.setupSubCellModel(model)
        }
        self.setupSuperResolution(enabled: sender.isOn)
    }
    
    @objc func switchMultipleChange(_ sender:SubButton)  {
        if openSwitch.isOn == false { return }
        self.multipleModelList.forEach { model in
            model.isSelected = model.tag == sender.tag
            guard let itemView = model.subView as? SubButton  else { return  }
            itemView.isSelected = model.isSelected
            if model.tag == sender.tag { self.currentModel = model }
        }
        self.setupSuperResolution(enabled: true)
    }
}


