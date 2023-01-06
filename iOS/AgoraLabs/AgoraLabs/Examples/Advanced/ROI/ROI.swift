//
//  ROI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/23.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SwiftyJSON
import UIKit

class ROI: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let originalModel = SubCellModel(name: "Original Image",tag: -1)

    let itemModelList:[SubCellModel] = [
        SubCellModel(name: "360P",tag: 0,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 640, height: 360), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 640, height: 360), frameRate: .fps15, bitrate: 560, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "480P",tag: 1,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 640, height: 480), frameRate: .fps15, bitrate: 1220, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 640, height: 480), frameRate: .fps15, bitrate: 854, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "540P",tag: 2,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 960, height: 540), frameRate: .fps15, bitrate: 1470, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 960, height: 540), frameRate: .fps15, bitrate: 1030, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "720P",tag: 3,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 960, height: 720), frameRate: .fps15, bitrate: 2260, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 960, height: 720), frameRate: .fps15, bitrate: 1580, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
    ]
    var isOpenROI:Bool = false
    var blurSlider:UISlider?
    lazy var contentView: UIView = {
        let _contentView = UIView()
        _contentView.backgroundColor = .black
        return _contentView
    }()
    lazy var localVideoView: AGContrastView = {
        let _localVideoView = AGContrastView(type: .top, frame: CGRect.zero)
        _localVideoView.backgroundColor = .black
        _localVideoView.masksToBounds = true
        _localVideoView.showSubTitle = true
        _localVideoView.title = "Original Video".localized
        return _localVideoView
    }()
    lazy var remoteVideoView: AGContrastView = {
        let _remoteVideoView = AGContrastView(type: .bottom, frame: CGRect.zero)
        _remoteVideoView.backgroundColor = .black
        _remoteVideoView.masksToBounds = true
        _remoteVideoView.titleSelected = false
        _remoteVideoView.showSubTitle = true
        _remoteVideoView.selectTitle = "ROIxiaoguo".localized
        _remoteVideoView.title = "ROIxiaoguoweikaiqi".localized
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        //发送端设置
        self.setupSendData()
        //接收端设置
        self.setupRecvData()
    }
    
    func setupSendData(_ videoConfig:AgoraVideoEncoderConfiguration = AgoraVideoEncoderConfiguration(size: CGSize(width: 640, height: 360), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto)) {
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.sendUid
        connection.channelId = AgoraLabsUser.channelName
        
        // set up agora instance when view loadedlet config = AgoraRtcEngineConfig()
        // set up agora instance when view loaded
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.AppId
        config.areaCode = GlobalSettings.shared.area
        config.channelProfile = .liveBroadcasting
        agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        self.setupROI(enabled: false)
        agoraKit.setLogFile(LogUtils.sdkLogPath())
        agoraKit.setVideoEncoderConfigurationEx(videoConfig, connection: connection)
        
        agoraKit.enableVideo()
        agoraKit.enableAudio()
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
        let option = AgoraRtcChannelMediaOptions()
        option.publishCameraTrack = true
        option.publishMicrophoneTrack = true
        option.clientRoleType = .broadcaster
        
       
        let result = agoraKit.joinChannelEx(byToken: KeyCenter.Token, connection: connection, delegate: self, mediaOptions: option) {  channel, uid, elapsed in
            print("sendAgoraKit uid=\(uid) joinChannel channel=\(channel)")
        }
        
        if result != 0 {
            // Usually happens with invalid parameters
            // Error code description can be found at:
            // en: https://docs.agora.io/en/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
            // cn: https://docs.agora.io/cn/Voice/API%20Reference/oc/Constants/AgoraErrorCode.html
            self.showAlert(title: "Error", message: "sendAgoraKit joinChannel call failed: \(result), please check your params")
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
        
        let result = agoraKit.joinChannelEx(byToken: KeyCenter.Token, connection: connection, delegate: self, mediaOptions: option) {  channel, uid, elapsed in
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
    
    @objc func backBtnDidClick() {
        self.agoraKit.leaveChannel(nil)
        AgoraRtcEngineKit.destroy()
        self.navigationController?.popViewController(animated: true)
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
    
    func setupROI(enabled:Bool) {
        if isOpenROI == enabled {
            return
        }
        let json = JSON(["che.video.roiEnable":enabled,
                         "che.hardware_encoding":0]).rawString() ?? ""
        let rt = agoraKit.setParameters(json)
        self.isOpenROI = enabled
        if rt != 0 {
            AGHUD.showFaild(info: "ROIEnable False:\(rt)")
            self.openSwitch.isOn = false
            self.isOpenROI = false
            self.switchOpenChange(self.openSwitch)
        }
    }
}

extension ROI:AgoraRtcEngineDelegate{
    
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
            localVideoView.subTitle = "Bitrate".localized+": \(stats.sentBitrate) kbps"
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
            remoteVideoView.subTitle = "Bitrate".localized+": \(stats.receivedBitrate) kbps"
            //print("recv - receivedBitrate=\(stats.receivedBitrate) receivedFrameRate=\(stats.receivedFrameRate)")
        }
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurWarning warningCode: AgoraWarningCode) {
        LogUtils.log(message: "warning: \(warningCode.description)", level: .warning)
    }
}
