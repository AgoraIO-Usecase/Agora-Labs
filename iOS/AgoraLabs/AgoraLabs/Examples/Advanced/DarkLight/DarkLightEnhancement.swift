//
//  DarkLightEnhancement.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/10.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import UIKit
//暗光
class DarkLightEnhancement: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let multipleModelList:[SubCellModel] = [
        SubCellModel(name: "xnyx".localized,tag: 0,value: 1),
        SubCellModel(name: "hzyx".localized,tag: 1,value: 0)
    ]
    
    lazy var contentView: UIView = {
        let _contentView = UIView()
        _contentView.backgroundColor = .black
        return _contentView
    }()
    lazy var localVideoView: AGContrastView = {
        let _remoteVideoView = AGContrastView(type: .single, frame: CGRect.zero)
        _remoteVideoView.backgroundColor = .black
        _remoteVideoView.masksToBounds = true
        return _remoteVideoView
    }()
    lazy var multipleView: UIView = {
        let _multipleView = UIView()
        _multipleView.backgroundColor = .clear
        return _multipleView
    }()
    lazy var bottomView: UIView = {
        let _bottomView = UIView()
        _bottomView.backgroundColor = SCREEN_MASK_COLOR.hexColor()
        return _bottomView
    }()
    lazy var bottomContentView: UIView = {
        let _bottomView = UIView()
        _bottomView.backgroundColor = SCREEN_MASK_COLOR.hexColor()
        return _bottomView
    }()
    lazy var openSwitch: UISwitch = {
        let _openSwitch = UISwitch()
        _openSwitch.addTarget(self, action: #selector(switchOpenChange(_:)), for: .valueChanged)
        _openSwitch.onTintColor = "099DFD".hexColor()
        _openSwitch.isOn = false
        return _openSwitch
    }()
    
    var layoutType:Int = 1
    var layoutImage:UIImageView?
    var agoraKit: AgoraRtcEngineKit!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        //发送端设置
        self.setupSendData()
    }
    
    func setupSendData(_ videoConfig:AgoraVideoEncoderConfiguration = AgoraVideoEncoderConfiguration(size: CGSize(width: 540, height: 960), frameRate: .fps15, bitrate: 1450, orientationMode: .fixedPortrait, mirrorMode: .auto)) {
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.sendUid
        connection.channelId = AgoraLabsUser.channelName
        
        LBXPermission.authorize(with: .camera) { granted, firstTime in
            if !firstTime && !granted {
                LBXPermissionSetting.showAlertToDislayPrivacySetting(withTitle: "", msg: "xjqx".localized, cancel: "qx".localized, setting: "sz".localized)
            }
        }
        // set up agora instance when view loadedlet config = AgoraRtcEngineConfig()
        // set up agora instance when view loaded
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.AppId
        config.areaCode = GlobalSettings.shared.area
        config.channelProfile = .liveBroadcasting
        config.eventDelegate = self
        agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        
        agoraKit.setLogFile(LogUtils.sdkLogPath())
        agoraKit.setVideoEncoderConfigurationEx(videoConfig, connection: connection)
        
        agoraKit.enableVideo()
        agoraKit.disableAudio()
        // set up local video to render your local camera preview
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.uid = AgoraLabsUser.sendUid
        // the view to be binded
        videoCanvas.view = localVideoView.showView
        videoCanvas.renderMode = .hidden
        agoraKit.setupLocalVideo(videoCanvas)
        // you have to call startPreview to see local video
        agoraKit.startPreview()
        // Set audio route to speaker
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        
    }
    
    @objc func backBtnDidClick() {
        self.navigationController?.popViewController(animated: true)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.agoraKit.leaveChannel(nil)
        AgoraRtcEngineKit.destroy()
    }
    
    //切换摄像头
    @objc func switchBtnDidClick() {
        agoraKit.switchCamera()
    }

    func setupResolution(videoConfig:AgoraVideoEncoderConfiguration) {
        
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.sendUid
        connection.channelId = AgoraLabsUser.channelName
        agoraKit.setVideoEncoderConfigurationEx(videoConfig, connection: connection)
    }
    
    func setupVideoDenoiser(enabled:Bool) {
        let options = AgoraLowlightEnhanceOptions()
        let level = self.currentModel?.value as? UInt
        options.level = AgoraLowlightEnhanceLevel(rawValue: level ?? 0) ?? .quality
        agoraKit.setLowlightEnhanceOptions(enabled, options: options)
    }
}

extension DarkLightEnhancement:AgoraMediaFilterEventDelegate,AgoraRtcEngineDelegate{
    func onEvent(_ provider: String?, extension: String?, key: String?, value: String?) {
        print("onEvent ------------ provider:\(provider ?? "")")
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, videoSizeChangedOf sourceType: AgoraVideoSourceType, uid: UInt, size: CGSize, rotation: Int) {
        print("videoSizeChangedOf ------------ ")
    }
    
}

