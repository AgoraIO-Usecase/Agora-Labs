//
//  AlphaVirtualBackground.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2023/4/14.
//  Copyright © 2023 Agora Corp. All rights reserved.
//

import AgoraRtcKit
import UIKit

class AlphaVirtualBackground: BaseViewController {
    
    var currentModel:SubCellModel?
    
    let originalModel = SubCellModel(name: "Original",tag: -1,isSelected: true)
    let greenModel = SubCellModel(name: "Split Green Screen",tag: 1)
    
    var stateProperty:AgoraSegmentationProperty? = nil
    
    let itemModelList:[SubCellModel] = [
        SubCellModel(name: "Scenery",tag: 0, bgImageName: "Image1"),
        SubCellModel(name: "Video",tag: 1, bgImageName: "Image2"),
        SubCellModel(name: "Customize",tag: 2)
    ]
    
    lazy var contentView: UIView = {
        let _contentView = UIView()
        _contentView.backgroundColor = .black
        return _contentView
    }()
    lazy var localVideoView: AGContrastView = {
        let _localVideoView = AGContrastView(type: .top, frame: CGRect.zero)
        _localVideoView.backgroundColor = .black
        _localVideoView.masksToBounds = true
        _localVideoView.title = "发送端".localized
        return _localVideoView
    }()
    lazy var remoteVideoView: UIView = {
        let _remoteVideoView = UIView()
        _remoteVideoView.backgroundColor = .black
        return _remoteVideoView
    }()
    lazy var bottomView: UIView = {
        let _bottomView = UIView()
        _bottomView.backgroundColor = SCREEN_MASK_COLOR.hexColor()
        return _bottomView
    }()
    lazy var mediaPlayer: AgoraRtcMediaPlayerProtocol? = {
        let media = AgoraMediaSource()
        media.url = Bundle.main.path(forResource: "vd1", ofType: "mp4")
        media.autoPlay = true
        let player = agoraKit.createMediaPlayer(with: nil)
        player?.setLoopCount(999)
        player?.open(with: media)
        return player
    }()
    var videoStream:AgoraTranscodingVideoStream?
    var touchRect:CGRect = CGRect(x: 120, y: 200, width: 240, height: 240)
    lazy var touchView: UIView = {
        let _touchView = UIView()
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(didPan(_:)))
        _touchView.addGestureRecognizer(panGesture)
        return _touchView
    }()
    
    var blurSlider:UISlider?
    var agoraKit: AgoraRtcEngineKit!
    var mediaPlayerKit: AgoraRtcMediaPlayerProtocol!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupUI()
        self.setupSendData()
    }
    
    func setupSendData(_ videoConfig:AgoraVideoEncoderConfiguration = AgoraVideoEncoderConfiguration(size: CGSize(width: 480, height: 640), frameRate: .fps15, bitrate: 800, orientationMode: .fixedPortrait, mirrorMode: .auto)) {
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
        
        agoraKit.setLogFile(LogUtils.sdkLogPath())
        agoraKit.setVideoEncoderConfigurationEx(videoConfig, connection: connection)
        
        agoraKit.enableVideo()
        agoraKit.disableAudio()
        // set up local video to render your local camera preview

        let confi = AgoraCameraCapturerConfiguration()
        confi.frameRate = 15
        confi.cameraDirection = .front
        confi.dimensions = videoConfig.dimensions
        agoraKit.startCameraCapture(.camera, config: confi)
        
        let trconfig = self.setupLocalTranscoderConfiguration()
        agoraKit.startLocalVideoTranscoder(trconfig)
        
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.sourceType = .camera
        videoCanvas.uid =  AgoraLabsUser.sendUid
        videoCanvas.view = localVideoView.showView
        videoCanvas.renderMode = .hidden
        agoraKit.setupLocalVideo(videoCanvas)
        
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
        option.publishTranscodedVideoTrack = true
        option.publishMicrophoneTrack = true
        option.clientRoleType = .broadcaster

        AgoraLabsUser.generateToken(channelName: AgoraLabsUser.channelName, uid: AgoraLabsUser.sendUid, tokenType: .token007, type: .rtc) { sendToken in
            
            let result = self.agoraKit.joinChannelEx(byToken: sendToken, connection: connection, delegate: nil, mediaOptions: option) {  channel, uid, elapsed in
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
        option.clientRoleType = .audience
        
        let connection = AgoraRtcConnection()
        connection.localUid = AgoraLabsUser.recvUid
        connection.channelId = AgoraLabsUser.channelName
        
        AgoraLabsUser.generateToken(channelName: AgoraLabsUser.channelName, uid: AgoraLabsUser.recvUid, tokenType: .token007, type: .rtc) { recvToken in
            
            let result = self.agoraKit.joinChannelEx(byToken: recvToken, connection: connection, delegate: nil, mediaOptions: option) {  channel, uid, elapsed in
                print("recvAgoraKit uid=\(uid) joinChannel channel=\(channel)")
                
                let videoCanvas = AgoraRtcVideoCanvas()
                videoCanvas.sourceType = .transCoded
                videoCanvas.uid = AgoraLabsUser.sendUid
                videoCanvas.view = self.remoteVideoView
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
        AgoraRtcEngineKit.destroy()
        self.closeVirtualBackground()
        self.navigationController?.popViewController(animated: true)
    }
        
    //切换摄像头
    @objc func switchBtnDidClick() {
        agoraKit.switchCamera()
    }
    
    //点击原图 取消背景设置
    func originalViewClick() {
        self.closeVirtualBackground()
    }
    
    
    func setupLocalTranscoderConfiguration() -> AgoraLocalTranscoderConfiguration {

        let stream1 = AgoraTranscodingVideoStream()
        stream1.sourceType = .camera
        if let _ = self.videoStream {
            stream1.rect = CGRect(origin: CGPoint(x: 480-self.touchRect.origin.x-self.touchRect.size.width, y: self.touchRect.origin.y), size: self.touchRect.size)
        }else{
            stream1.rect = CGRect(origin: CGPoint.zero, size: CGSize(width: 480, height: 640))
        }
        stream1.mirror = false
        stream1.zOrder = 10
        stream1.alpha = 1
        
        let encoder_config = AgoraVideoEncoderConfiguration()
        encoder_config.codecType = .H264
        encoder_config.dimensions = CGSize(width: 480, height: 640)
        encoder_config.bitrate = 2000
        encoder_config.frameRate = .fps15
        
        let config = AgoraLocalTranscoderConfiguration()
        if let _stream2 = self.videoStream {
            config.videoInputStreams = [stream1,_stream2]
        }else{
            config.videoInputStreams = [stream1]
        }
        
        config.videoOutputConfiguration = encoder_config
        return config
    }

    
    //点击功能
    func itemViewClick(model:SubCellModel) {
        if !model.isSelected {
            self.closeVirtualBackground()
            return
        }
        if model.name == "Scenery" {
            if let path = Bundle.main.path(forResource: "bg3", ofType: "jpeg") {
                self.setVirtualImgBackground(imagePath: path)
            }
        }else if model.name == "Video" {
            if let path = Bundle.main.path(forResource: "vd1", ofType: "mp4") {
                self.setVirtualVideoBackground(videoPath: path)
            }
        }else if model.name == "Customize" {
            self.setVirtualImgBackground(imagePath: model.value as! String)
        }
    }

}

extension AlphaVirtualBackground {

    // Gathering Mode
    func setVirtualImgBackground(imagePath:String) {
        
        let stream2 = AgoraTranscodingVideoStream()
        if imagePath.hasSuffix(".jpg") || imagePath.hasSuffix(".jpeg") {
            stream2.sourceType = .imageJPEG
        } else if imagePath.hasSuffix(".png") {
            stream2.sourceType = .imagePNG
        } else if imagePath.hasSuffix(".gif") {
            stream2.sourceType = .imageGIF
        }
        stream2.sourceType = .imageJPEG
        stream2.rect = CGRect(x: 0, y: 0, width: 480, height: 640)
        stream2.imageUrl = imagePath
        stream2.mirror = false
        stream2.zOrder = 1
        self.videoStream = stream2
        let trconfig = self.setupLocalTranscoderConfiguration()
        agoraKit.updateLocalTranscoderConfiguration(trconfig)
        
        let ret = agoraKit.enableVirtualBackground(true, backData: AgoraVirtualBackgroundSource(), segData: stateProperty)
    
        print("Gathering Mode ----设置返回值：\(ret)")
    }
    
    // Gathering Mode
    func setVirtualVideoBackground(videoPath:String) {
        
        let stream2 = AgoraTranscodingVideoStream()
        stream2.sourceType = .mediaPlayer
        stream2.rect = CGRect(x: 0, y: 0, width: 480, height: 640)
        stream2.mediaPlayerId = UInt(self.mediaPlayer?.getMediaPlayerId() ?? 0)
        stream2.mirror = false
        stream2.zOrder = 1
        self.videoStream = stream2
        let trconfig = self.setupLocalTranscoderConfiguration()
        agoraKit.updateLocalTranscoderConfiguration(trconfig)
        
        let ret = agoraKit.enableVirtualBackground(true, backData: AgoraVirtualBackgroundSource(), segData: stateProperty)
    
        print("Gathering Mode ----设置返回值：\(ret)")
    }

    
    // Close VirtualBackground
    func closeVirtualBackground() {
        
        self.videoStream = nil
        let trconfig = self.setupLocalTranscoderConfiguration()
        agoraKit.updateLocalTranscoderConfiguration(trconfig)
        
        let ret = agoraKit.enableVirtualBackground(false, backData: nil, segData: nil)
        print("Close VirtualBackground ----设置返回值：\(ret)")
    }
}
