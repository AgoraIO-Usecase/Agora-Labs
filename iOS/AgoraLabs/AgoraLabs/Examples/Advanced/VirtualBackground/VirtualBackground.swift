//
//  VirtualBackground.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import UIKit

class VirtualBackground: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let originalModel = SubCellModel(name: "Original Image",tag: -1)
    
    var stateProperty:AgoraSegmentationProperty?
    
    let itemModelList:[SubCellModel] = [
        SubCellModel(name: "Background Blur",tag: 0,value: 1),
        SubCellModel(name: "Split Green Screen",tag: 1),
        SubCellModel(name: "Scenery Image",tag: 2, bgImageName: "Image1"),
        SubCellModel(name: "Meeting Image",tag: 3, bgImageName: "Image2"),
        SubCellModel(name: "Customize Image",tag: 4)
    ]
    
    var blurSlider:UISlider?
    
    var localVideoView:UIView?
    var isOpenAgoraGreen:Bool = false
    var agoraKit: AgoraRtcEngineKit!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        self.setupAgoraRtcEngine()
    }
    
    func setupAgoraRtcEngine() {
        
        // set up agora instance when view loadedlet config = AgoraRtcEngineConfig()
        // set up agora instance when view loaded
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.AppId
        config.areaCode = GlobalSettings.shared.area
        config.channelProfile = .liveBroadcasting
        agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        agoraKit.setLogFile(LogUtils.sdkLogPath())

        // get channel name from configs
        guard let resolution = GlobalSettings.shared.getSetting(key: "resolution")?.selectedOption().value as? CGSize,
              let fps = GlobalSettings.shared.getSetting(key: "fps")?.selectedOption().value as? AgoraVideoFrameRate,
              let orientation = GlobalSettings.shared.getSetting(key: "orientation")?.selectedOption().value as? AgoraVideoOutputOrientationMode else {
            LogUtils.log(message: "invalid video configurations, failed to become broadcaster", level: .error)
            return
        }
        
        // make myself a broadcaster
        agoraKit.setClientRole(GlobalSettings.shared.getUserRole())
        // enable video module and set up video encoding configs
        agoraKit.setVideoEncoderConfiguration(AgoraVideoEncoderConfiguration(size: resolution,
                                                                             frameRate: fps,
                                                                             bitrate: AgoraVideoBitrateStandard,
                                                                             orientationMode: orientation, mirrorMode: .auto))
        agoraKit.enableVideo()
        agoraKit.enableAudio()
        
        // set up local video to render your local camera preview
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.uid = 0
        // the view to be binded
        videoCanvas.view = localVideoView
        videoCanvas.renderMode = .hidden
        agoraKit.setupLocalVideo(videoCanvas)
        // you have to call startPreview to see local video
        agoraKit.startPreview()
        
        // Set audio route to speaker
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        
        // start joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. If app certificate is turned on at dashboard, token is needed
        // when joining channel. The channel name and uid used to calculate
        // the token has to match the ones used for channel join
        
    }
    
    @objc func backBtnDidClick() {
        self.navigationController?.popViewController(animated: true)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        AgoraRtcEngineKit.destroy()
    }
    
    //切换摄像头
    @objc func switchBtnDidClick() {
        agoraKit.switchCamera()
    }
    
    //点击原图 取消背景设置
    func originalViewClick() {
        self.closeVirtualBackground()
    }
    
    //绿幕分割开启状态
    func greenScreenState(model:SubCellModel){
        if model.isSelected {
            self.isOpenAgoraGreen = true
            self.setVirtualColorBackground(hexString:"FFFFFF")
        }else{
            self.isOpenAgoraGreen = false
            self.stateProperty = nil
            self.closeVirtualBackground()
        }
        guard let _currentModel = self.currentModel else { return  }
        self.itemViewClick(model: _currentModel)
    }
    
    //点击功能
    func itemViewClick(model:SubCellModel) {
        if !model.isSelected {
            self.closeVirtualBackground()
            return
        }
        if model.name == "Background Blur" {
            self.setupBackgroundBlur(value: model.value as! Int)
        }else if model.name == "Scenery Image" {
            if let path = Bundle.main.path(forResource: "bg3", ofType: "jpeg") {
                self.setVirtualImgBackground(imagePath: path)
            }
        }else if model.name == "Meeting Image" {
            if let path = Bundle.main.path(forResource: "bg4", ofType: "jpeg") {
                self.setVirtualImgBackground(imagePath: path)
            }
        }else if model.name == "Customize Image" {
            self.setVirtualImgBackground(imagePath: model.value as! String)
        }else if model.name == "Video Background" {
            self.closeVirtualBackground()
            AGHUD.showInfo(info: "敬请期待")
        }else if model.name == "Gathering Mode" {
            self.closeVirtualBackground()
            AGHUD.showInfo(info: "敬请期待")
        }
        
    }
    
    deinit {
        self.closeVirtualBackground()
    }
}

extension VirtualBackground {
    // Background Blur
    func setupBackgroundBlur(value:Int) {
        let virtualBG = AgoraVirtualBackgroundSource()
        virtualBG.backgroundSourceType = .blur
        virtualBG.blurDegree = AgoraBlurDegree(rawValue: UInt(value))!
        let ret = agoraKit.enableVirtualBackground(true, backData: virtualBG, segData: stateProperty)
        print("Background Blur ----设置返回值：\(ret)-----档位：\(index)")
    }
    
    // Split Green Screen
    func setVirtualColorBackground(hexString : String) {
        let hexString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        let scanner = Scanner(string: hexString)
        scanner.scanLocation = 0
        var color: UInt32 = 0
        scanner.scanHexInt32(&color)
        
        let virtualBG = AgoraVirtualBackgroundSource()
        virtualBG.backgroundSourceType = .color
        virtualBG.color = UInt(color)
        let tempProperty = AgoraSegmentationProperty()
        tempProperty.modelType = .agoraGreen
        tempProperty.greenCapacity = 0.8
        self.stateProperty = tempProperty
        let ret = agoraKit.enableVirtualBackground(true, backData: virtualBG, segData: stateProperty)
        print("Split Green Screen ----设置返回值：\(ret)")
    }
    
    // Gathering Mode
    func setVirtualImgBackground(imagePath:String) {
        let virtualBG = AgoraVirtualBackgroundSource()
        virtualBG.backgroundSourceType = .img
        virtualBG.source = imagePath
        let ret = agoraKit.enableVirtualBackground(true, backData: virtualBG, segData: stateProperty)
        print("Gathering Mode ----设置返回值：\(ret)")
    }
    
    // Close VirtualBackground
    func closeVirtualBackground() {
        
        let ret = agoraKit.enableVirtualBackground(false, backData: nil, segData: nil)
        if isOpenAgoraGreen  {
            self.setVirtualColorBackground(hexString:"FFFFFF")
        }
        print("Close VirtualBackground ----设置返回值：\(ret)")
    }
}
