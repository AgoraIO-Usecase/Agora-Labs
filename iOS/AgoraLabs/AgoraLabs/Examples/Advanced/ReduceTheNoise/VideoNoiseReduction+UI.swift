//
//  VideoNoiseReduction+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/10.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SnapKitExtend
import UIKit

extension VideoNoiseReduction {
    
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
        button.setTitle("Video Noise Reduction".localized, for: .normal)
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
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+62+62)
        }
        
        bottomView.addSubview(bottomContentView)
        bottomContentView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.bottom.equalToSuperview().offset(-SCREEN_BOTTOM_HEIGHT)
            make.height.equalTo(62)
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
        funNameLabel.text = "qyspjz".localized
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
        
        for i in 0..<multipleModelList.count {
            let itemModel = multipleModelList[i]
            let button = SubButton(alphaNormal: 0.9)
            itemModel.subView = button
            itemModel.isSelected = i == 1
            itemModel.isEnabled = false
            button.tag = i
            button.cornerRadius = 8
            button.masksToBounds = true
            button.alphaSelected = 0.9
            button.isSelected = itemModel.isSelected
            button.isEnabled = itemModel.isEnabled
            button.addBlurEffect(style: .systemThinMaterialDark)
            button.titleLabel?.font = UIFont.boldSystemFont(ofSize: 13)
            button.setTitleColor(.lightGray, for: .disabled)
            button.contentEdgeInsets = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 10)
            button.setTitle(itemModel.name, for: .normal)
            button.clickView {[weak self] sender in
                self?.switchMultipleChange(sender as! SubButton)
            }
            if i == 1 {
                self.currentModel = itemModel
            }
            bottomContentView.addSubview(button)
        }
        let buttonArr = multipleModelList.compactMap({$0.subView})
        buttonArr.snp.distributeViewsAlong(axisType: .horizontal, fixedSpacing: 10, leadSpacing: 10, tailSpacing: 10)
        buttonArr.snp.makeConstraints{
            $0.centerY.equalToSuperview()
            $0.height.equalTo(40)
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
                make.bottom.equalToSuperview().offset(62)
            }
            bottomContentView.isHidden = true
        }
    }
    
    
    @objc func switchOpenChange(_ sender:UISwitch)  {
        print("switchOpenChange - \(sender.isOn)")
        self.remoteVideoView.titleSelected = sender.isOn
        self.multipleModelList.forEach { model in
            guard let itemView = model.subView as? SubButton  else { return  }
            itemView.isEnabled = sender.isOn
        }
        self.setupVideoDenoiser(enabled: sender.isOn)
    }
    
    func switchMultipleChange(_ sender:SubButton)  {
        AGHUD.touchFeedback()
        self.multipleModelList.forEach { model in
            model.isSelected = model.tag == sender.tag
            guard let itemView = model.subView as? SubButton  else { return  }
            itemView.isSelected = model.isSelected
            if model.tag == sender.tag { self.currentModel = model }
        }
        self.setupVideoDenoiser(enabled: self.openSwitch.isOn)
    }
    
}
