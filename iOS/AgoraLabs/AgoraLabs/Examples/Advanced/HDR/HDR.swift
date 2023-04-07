//
//  HDR.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/2/8.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SwiftyJSON
import UIKit

class HDR: BaseViewController {
    
    var currentModel:SubCellModel?
    var isFrontCamera = true
    var isOpenHDR:Bool = false
    var blurSlider:UISlider?
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
    var videoConfig:AgoraVideoEncoderConfiguration?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        self.setupAgoraRtcEngine()
    }
    
    func setupAgoraRtcEngine(_ isOpen:Bool = false) {
        
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
        agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        agoraKit.setLogFile(LogUtils.sdkLogPath())
        // make myself a broadcaster
        agoraKit.setClientRole(GlobalSettings.shared.getUserRole())
        // enable video module and set up video encoding configs
        agoraKit.setVideoEncoderConfiguration(AgoraVideoEncoderConfiguration(size: CGSize(width: 1280, height: 720), frameRate:.fps15, bitrate: 2200, orientationMode: .adaptative, mirrorMode: .auto))
        setupHDR(enabled: isOpen)
        agoraKit.enableVideo()
        //agoraKit.enableAudio()
        
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
        
        // start joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. If app certificate is turned on at dashboard, token is needed
        // when joining channel. The channel name and uid used to calculate
        // the token has to match the ones used for channel join
        if !self.isFrontCamera {
            agoraKit.switchCamera()
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
    
    func reJoinChannel(_ isOpen:Bool) {
        self.agoraKit.leaveChannel(nil)
        AgoraRtcEngineKit.destroy()
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.1) {
            self.setupAgoraRtcEngine(isOpen)
        }
    }
    
    //切换摄像头
    @objc func switchBtnDidClick() {
        self.isFrontCamera = !self.isFrontCamera
        agoraKit.switchCamera()
    }
    
    func setupHDR(enabled:Bool) {
        if isOpenHDR == enabled {
            return
        }

        self.isOpenHDR = enabled
    }
}

extension HDR:AgoraRtcEngineDelegate{
    
    /**
     The SDK triggers this callback once every two seconds to report the statistics of the local video stream.

     Parameters
     engine
     AgoraRtcEngineKit object.
     sourceType
     The capture type of the custom video source. See AgoraVideoSourceType.
     stats
     The statistics of the local video stream. See AgoraRtcLocalVideoStats.
     */
    func rtcEngine(_ engine: AgoraRtcEngineKit, localVideoStats stats: AgoraRtcLocalVideoStats, sourceType: AgoraVideoSourceType) {
        if stats.uid ==  AgoraLabsUser.sendUid {
            //localVideoView.subTitle = "Bitrate".localized+": \(stats.sentBitrate) kbps"
            //print("send - sentBitrate=\(stats.sentBitrate) sentFrameRate=\(stats.sentFrameRate)")
        }
    }
    
    /**
     Reports the statistics of the video stream from the remote users. The SDK triggers this callback once every two seconds for each remote user. If a channel has multiple users/hosts sending video streams, the SDK triggers this callback as many times.

     Parameters
     engine
     AgoraRtcEngineKit object.
     stats
     Statistics of the remote video stream. For details, see AgoraRtcRemoteVideoStats.
     */
    func rtcEngine(_ engine: AgoraRtcEngineKit, remoteVideoStats stats: AgoraRtcRemoteVideoStats) {
        if stats.uid ==  AgoraLabsUser.sendUid {
            //remoteVideoView.subTitle = "Bitrate".localized+": \(stats.receivedBitrate) kbps"
            //print("recv - receivedBitrate=\(stats.receivedBitrate) receivedFrameRate=\(stats.receivedFrameRate)")
        }
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurWarning warningCode: AgoraWarningCode) {
        LogUtils.log(message: "warning: \(warningCode.description)", level: .warning)
    }
    
}
