//
//  SuperResolution+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/15.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SnapKitExtend
import UIKit

extension SuperResolution {
    
    func setupUI() {
        self.setupNavigation()
        self.setupBottom()
        self.setupShowContentView()
        self.setupBottomContentView()
    }
    
    func setupNavigation() {
        self.view.backgroundColor = SCREEN_CCC_COLOR.hexColor()

        let button = UIButton(type: .custom)
        button.frame = CGRect(x:0, y:0, width:65, height:30)
        button.setImage(UIImage(named:"ChevronLeft"), for: .normal)
        button.setImage(UIImage(named:"ChevronLeft"), for: .highlighted)
        button.setTitle("SuperResolution".localized, for: .normal)
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
        
        navigationItem.rightBarButtonItems = [barItemOne]
    }
    
    
    
    func setupShowContentView() {
        self.view.layoutIfNeeded()
        self.view.addSubview(contentView)
        contentView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        
        contentView.addSubview(remoteVideoView)
        remoteVideoView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(contentView.snp.top)
            make.height.equalTo(contentView.snp.height)
        }
        
        self.view.bringSubviewToFront(bottomView)
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
            bottomContentView.isHidden = false
        }else if recognizer.direction == .down {
            
            bottomView.snp.updateConstraints { make in
                make.bottom.equalToSuperview().offset(118)
            }
            bottomContentView.isHidden = true
        }
    }
    
    func switchResolution(tag:Int) {
//        if openSwitch.isOn == false { return }
        self.itemModelList.forEach { model in
            model.isSelected = model.tag == tag
            guard let itemView = model.subView as? SubCellView  else { return  }
            itemView.setupSubCellModel(model)
        }
        guard let videoConfig = self.itemModelList[tag].value as? AgoraVideoEncoderConfiguration else { return }
        self.setupResolution(videoConfig: videoConfig)
        self.setupSuperResolution(enabled: openSwitch.isOn)
    }
    
    @objc func switchOpenChange(_ sender:UISwitch)  {
        print("switchOpenChange - \(sender.isOn)")
        self.remoteVideoView.titleSelected = sender.isOn
        if sender.isOn == false {
            self.isSharpenType = false
            self.isSrType = false
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


