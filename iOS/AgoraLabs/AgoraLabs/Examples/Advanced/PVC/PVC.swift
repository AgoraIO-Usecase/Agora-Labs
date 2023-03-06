//
//  PVC.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/23.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import SwiftyJSON
import AAInfographics
import UIKit

class PVC: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let originalModel = SubCellModel(name: "Original Image",tag: -1)

    let itemModelList:[SubCellModel] = [
        SubCellModel(name: "360P",tag: 0,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 360, height: 640), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 360, height: 640), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "480P",tag: 1,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 480, height: 854), frameRate: .fps15, bitrate: 1220, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 480, height: 854), frameRate: .fps15, bitrate: 1220, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "540P",tag: 2,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 540, height: 960), frameRate: .fps15, bitrate: 1470, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 540, height: 960), frameRate: .fps15, bitrate: 1470, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
        SubCellModel(name: "720P",tag: 3,value: [AgoraVideoEncoderConfiguration(size: CGSize(width: 720, height: 1280), frameRate: .fps15, bitrate: 2260, orientationMode: .fixedPortrait, mirrorMode: .auto),AgoraVideoEncoderConfiguration(size: CGSize(width: 720, height: 1280), frameRate: .fps15, bitrate: 2260, orientationMode: .fixedPortrait, mirrorMode: .auto)]),
    ]
    var isOpenPVC:Bool = false
    var blurSlider:UISlider?
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
    lazy var bottomView: UIView = {
        let _bottomView = UIView()
        _bottomView.backgroundColor = SCREEN_MASK_COLOR.hexColor()
        return _bottomView
    }()
    lazy var aaChartLabel1: UIButton = {
        let _aaChartLabel = UIButton()
        _aaChartLabel.isHidden = true
        _aaChartLabel.layer.shadowOpacity = 0.5
        _aaChartLabel.layer.shadowColor = UIColor.black.cgColor
        _aaChartLabel.layer.shadowOffset = CGSize(width: 1, height: 1)
        _aaChartLabel.layer.shadowRadius = 1
        
        _aaChartLabel.setTitleColor(UIColor.white, for: .normal)
        _aaChartLabel.titleLabel?.font = UIFont.systemFont(ofSize: 13)
        _aaChartLabel.addTarget(self, action: #selector(self.aaChartLabel1Click), for: .touchUpInside)
        return _aaChartLabel
    }()
    lazy var aaChartLabel2: UIButton = {
        let _aaChartLabel2 = UIButton()
        _aaChartLabel2.isHidden = false
        _aaChartLabel2.layer.shadowOpacity = 0.5
        _aaChartLabel2.layer.shadowColor = UIColor.black.cgColor
        _aaChartLabel2.layer.shadowOffset = CGSize(width: 1, height: 1)
        _aaChartLabel2.layer.shadowRadius = 1
        
        _aaChartLabel2.setTitleColor(UIColor.white, for: .normal)
        _aaChartLabel2.titleLabel?.font = UIFont.systemFont(ofSize: 13)
        _aaChartLabel2.addTarget(self, action: #selector(self.aaChartLabel2Click), for: .touchUpInside)
        return _aaChartLabel2
    }()
    let aaChartView = AAChartView()
    lazy var chartView: UIView = {
        let _chartView = UIView()
        _chartView.isHidden = false
        _chartView.backgroundColor = UIColor.clear
        _chartView.masksToBounds = true
        _chartView.cornerRadius = 8
        return _chartView
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

    
    var chartXArray = [String]()
    var chartYArray = [Int]()
    var layoutType:Int = 1
    var layoutImage:UIImageView?
    var agoraKit: AgoraRtcEngineKit!
    var videoConfig:AgoraVideoEncoderConfiguration?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        //发送端设置
        self.setupSendData()
    }
    
    func setupSendData(_ videoConfig:AgoraVideoEncoderConfiguration = AgoraVideoEncoderConfiguration(size: CGSize(width: 360, height: 640), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto)) {
        
        self.videoConfig = videoConfig
        
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
        agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        self.setupPVC(enabled: false)
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

    func setupResolution(videoConfig:AgoraVideoEncoderConfiguration?) {
        guard let _videoConfig = videoConfig else { return  }
        self.videoConfig = _videoConfig
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.sendUid
        connection.channelId = AgoraLabsUser.channelName
        agoraKit.setVideoEncoderConfigurationEx(_videoConfig, connection: connection)
    }
    
    func setupPVC(enabled:Bool) {
        if isOpenPVC == enabled {
            return
        }
        let json = JSON(["rtc.video.enable_pvc":enabled]).rawString() ?? ""
        let rt = agoraKit.setParameters(json)
        self.isOpenPVC = enabled
        if rt != 0 {
            AGHUD.showFaild(info: "Enable PVC False:\(rt)")
            self.openSwitch.isOn = false
            self.isOpenPVC = false
            self.switchOpenChange(self.openSwitch)
        }
    }
}

extension PVC:AgoraRtcEngineDelegate{
    
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
            remoteVideoView.subTitle = "Bitrate".localized+": \(stats.receivedBitrate) kbps"
            //print("recv - receivedBitrate=\(stats.receivedBitrate) receivedFrameRate=\(stats.receivedFrameRate)")
            self.aaChartLabel2.setTitle("\(stats.receivedBitrate) kbps", for: .normal)
            self.aaChartLabel1.setTitle("Bitrate".localized+": \(stats.receivedBitrate) kbps", for: .normal)
            self.reloadChartView(Int(stats.receivedBitrate), Int(Date().timeIntervalSince1970))
        }
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurWarning warningCode: AgoraWarningCode) {
        LogUtils.log(message: "warning: \(warningCode.description)", level: .warning)
    }
}
