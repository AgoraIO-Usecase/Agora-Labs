//
//  EnanceSaturation+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/8.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SnapKitExtend
import UIKit

extension EnanceSaturation {
    
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
        button.setTitle("Enance Saturation".localized, for: .normal)
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
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+62+196)
        }
        
        bottomView.addSubview(bottomContentView)
        bottomContentView.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.bottom.equalToSuperview().offset(-SCREEN_BOTTOM_HEIGHT)
            make.height.equalTo(196)
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
        funNameLabel.text = "Enance Saturation".localized
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
        
        let labkey1 = UILabel()
        labkey1.text = "色彩增强强度"
        labkey1.textColor = .white
        labkey1.font = UIFont.systemFont(ofSize: 15)
        bottomContentView.addSubview(labkey1)
        labkey1.snp.makeConstraints { make in
            make.top.equalTo(funNameLabel.snp.bottom).offset(34)
            make.left.equalToSuperview().offset(16)
        }
        
        let labvalue1 = UILabel()
        labvalue1.text = "0"
        labvalue1.textColor = .white
        labvalue1.font = UIFont.systemFont(ofSize: 15)
        self.colourValueL = labvalue1
        bottomContentView.addSubview(labvalue1)
        labvalue1.snp.makeConstraints { make in
            make.top.equalTo(labkey1)
            make.right.equalToSuperview().offset(-16)
        }
        
        let rangeSlider1 = UISlider()
        rangeSlider1.minimumTrackTintColor = "#4ca7ff".hexColor()
        rangeSlider1.maximumTrackTintColor = "#aeafb5".hexColor()
        rangeSlider1.addTarget(self, action: #selector(self.colourSliderEventValueChanged(_:)), for: .valueChanged)
        self.colourSlider = rangeSlider1
        bottomContentView.addSubview(rangeSlider1)
        rangeSlider1.snp.makeConstraints { make in
            make.top.equalTo(labvalue1.snp.bottom).offset(8)
            make.width.equalTo(SCREEN_WIDTH-60)
            make.centerX.equalToSuperview()
        }
        
        let labkey2 = UILabel()
        labkey2.text = "肤色保护强度"
        labkey2.textColor = .white
        labkey2.font = UIFont.systemFont(ofSize: 15)
        bottomContentView.addSubview(labkey2)
        labkey2.snp.makeConstraints { make in
            make.top.equalTo(rangeSlider1.snp.bottom).offset(34)
            make.left.equalToSuperview().offset(16)
        }

        let labvalue2 = UILabel()
        labvalue2.text = "0"
        labvalue2.textColor = .white
        labvalue2.font = UIFont.systemFont(ofSize: 15)
        self.complexionValueL = labvalue2
        bottomContentView.addSubview(labvalue2)
        labvalue2.snp.makeConstraints { make in
            make.top.equalTo(labkey2)
            make.right.equalToSuperview().offset(-16)
        }
        
        let rangeSlider2 = UISlider()
        rangeSlider2.minimumTrackTintColor = "#4ca7ff".hexColor()
        rangeSlider2.maximumTrackTintColor = "#aeafb5".hexColor()
        rangeSlider2.addTarget(self, action: #selector(self.complexionSliderEventValueChanged(_:)), for: .valueChanged)
        self.complexionSlider = rangeSlider2
        bottomContentView.addSubview(rangeSlider2)
        rangeSlider2.snp.makeConstraints { make in
            make.top.equalTo(labvalue2.snp.bottom).offset(8)
            make.width.equalTo(SCREEN_WIDTH-60)
            make.centerX.equalToSuperview()
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
                make.bottom.equalToSuperview().offset(196)
            }
            bottomContentView.isHidden = true
        }
    }
    
    
    @objc func switchOpenChange(_ sender:UISwitch)  {
        print("switchOpenChange - \(sender.isOn)")
        self.remoteVideoView.titleSelected = sender.isOn
        self.setupEnanceSaturation(enabled: sender.isOn)
    }
    
}

extension EnanceSaturation{
    @objc func complexionSliderEventValueChanged(_ sender:UISlider){
        self.complexionValueL?.text = "\(Int(sender.value * 100))"
    }
    
    @objc func colourSliderEventValueChanged(_ sender:UISlider){
        self.colourValueL?.text = "\(Int(sender.value * 100))"
    }
    
    
}
