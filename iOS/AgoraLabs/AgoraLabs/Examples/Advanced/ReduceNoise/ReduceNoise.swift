//
//  ReduceNoise.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/10.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import UIKit
//降噪
class ReduceNoise: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let multipleModelList:[SubCellModel] = [
        SubCellModel(name: "dghms".localized,tag: 0,value: 0),
        SubCellModel(name: "jhms".localized,tag: 1,value: 1),
        SubCellModel(name: "qjzms".localized,tag: 2,value: 2)
    ]
    
    lazy var contentView: UIView = {
        let _contentView = UIView()
        _contentView.backgroundColor = .black
        return _contentView
    }()
    lazy var remoteVideoView: AGContrastView = {
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
    
        // Set audio route to speaker
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)

        // start joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. If app certificate is turned on at dashboard, token is needed
        // when joining channel. The channel name and uid used to calculate
        // the token has to match the ones used for channel join
        let option = AgoraRtcChannelMediaOptions()
        option.publishCameraTrack = true
        option.publishMicrophoneTrack = true
        option.clientRoleType = .broadcaster

        AgoraLabsUser.generateToken(channelName: AgoraLabsUser.channelName, uid: AgoraLabsUser.sendUid, tokenType: .token007, type: .rtc) { sendToken in
            
            let result = self.agoraKit.joinChannelEx(byToken: sendToken, connection: connection, delegate: self, mediaOptions: option) {  channel, uid, elapsed in
                print("sendAgoraKit uid=\(uid) joinChannel channel=\(channel)")
            }
            
            if result != 0 {
                // Usually happens with invalid parameters
                // Error code description can be found at:
                // en: https://docs.agora.io/en/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
                // cn: https://docs.agora.io/cn/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
                self.showAlert(title: "Error", message: "sendAgoraKit joinChannel call failed: \(result), please check your params")
            }
            
            //接收端设置
            self.setupRecvData()
        }
        
    }
    
    func setupRecvData() {

        let option = AgoraRtcChannelMediaOptions()
        option.publishCameraTrack = true
        option.publishMicrophoneTrack = true
        option.clientRoleType = .broadcaster
        
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.recvUid
        connection.channelId = AgoraLabsUser.channelName
        
        AgoraLabsUser.generateToken(channelName: AgoraLabsUser.channelName, uid: AgoraLabsUser.recvUid, tokenType: .token007, type: .rtc) { recvToken in
            
            let result = self.agoraKit.joinChannelEx(byToken: recvToken, connection: connection, delegate: self, mediaOptions: option) {  channel, uid, elapsed in
                print("recvAgoraKit uid=\(uid) joinChannel channel=\(channel)")
                let videoCanvas = AgoraRtcVideoCanvas()
                videoCanvas.uid = AgoraLabsUser.sendUid
                videoCanvas.view = self.remoteVideoView.showView
                videoCanvas.mirrorMode = .enabled
                videoCanvas.renderMode = .hidden
                self.agoraKit.setupRemoteVideoEx(videoCanvas, connection: connection)
            }
            
            if result != 0 {
                // Usually happens with invalid parameters
                // Error code description can be found at:
                // en: https://docs.agora.io/en/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
                // cn: https://docs.agora.io/cn/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
                self.showAlert(title: "Error", message: "recv joinChannel call failed: \(result), please check your params")
            }
            
        }
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
        let options = AgoraVideoDenoiserOptions()
        let level = self.currentModel?.value as? Int
        options.level = AgoraVideoDenoiserLevel(rawValue: UInt(level ?? 0)) ?? .highQuality
        options.mode = .manual
        agoraKit.setVideoDenoiserOptions(enabled, options: options)
    }
}

extension ReduceNoise:AgoraMediaFilterEventDelegate,AgoraRtcEngineDelegate{
    func onEvent(_ provider: String?, extension: String?, key: String?, value: String?) {
        print("onEvent ------------ provider:\(provider ?? "")")
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, videoSizeChangedOf sourceType: AgoraVideoSourceType, uid: UInt, size: CGSize, rotation: Int) {
        print("videoSizeChangedOf ------------ ")
    }
    
}

