//
//  BeautifyFilter.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/8.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import ASValueTrackingSlider
import AgoraRtcKit
import JXSegmentedView
import UIKit

class BeautifyFilter: BaseViewController {
    
    let filterList:[SubCellModel] = [
        SubCellModel(name: "Faceunity",tag: 0),
        SubCellModel(name: "Agora",tag: 1),
        SubCellModel(name: "Volcengine",tag: 2)
    ]
    //原图
    var originalData = BeautyFuncModel()
    //相芯美颜内容
    var faceDataArr = [BeautyFuncModel]()
    //Agora美颜内容
    var agoraDataArr = [BeautyFuncModel]()
    var agoraBeautyOptions:AgoraBeautyOptions?
    //火山美颜内容
    var volcDataArr = [[BeautyFuncModel]]()
    var dressSourceArr = [String]()
    var oneKeySourceArr = [String]()
    
    var bottomContentView: UIView?
    var segmentedDataSource: JXSegmentedTitleDataSource!
    var segmentedView: JXSegmentedView!
    //开启状态
    var isOpAgora = false
    var isOpByteDance = false
    var isOpFaceUnity = false
    
    var curFilterIndex:Int = -1
    var curSelectModel:BeautyFuncModel?
    var curSlider:ASValueTrackingSlider?
    
    var bottomView:UIView?
    var localVideoView:UIView?
    var agoraKit: AgoraRtcEngineKit!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //初始化-美颜数据模型
        self.originalData   = BeautyFuncModel.originalData()
        self.faceDataArr    = BeautyFuncModel.loadFaceData()
        self.agoraDataArr   = BeautyFuncModel.loadAgoraData()
        self.volcDataArr    = BeautyFuncModel.loadVolcData()
        //初始化-UI
        self.setupUI()
        //初始化-AgoraRtcEngineKit
        self.setupAgoraRtcEngine()
        //初始化-相芯SDK
        self.setupFaceUnityBeauty()
        //初始化-火山SDK
        self.setupByteDanceBeauty()
    }
    
    func setupAgoraRtcEngine() {
        
        // set up agora instance when view loadedlet config = AgoraRtcEngineConfig()
        // set up agora instance when view loade
        agoraKit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: nil)
        
        // set up agora enableExtension FaceUnityEffect and ByteDanceEffect true
        //let faceUnityRet = agoraKit.enableExtension(withVendor: "FaceUnity", extension:"Effect", enabled: true)
        //print("FaceUnity enabled ----返回值：\(faceUnityRet)")
        self.setEnableFaceUnity(isEnabled: true)
        //let byteDanceRet = agoraKit.enableExtension(withVendor: "ByteDance", extension:"Effect", enabled: true)
        //print("ByteDance enabled ----返回值：\(byteDanceRet)")
        self.setEnableVolcUnity(isEnabled: true)
        
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
    
    deinit {
        self.closeBottomView()
    }
}

// MARK: - 声网美颜
extension BeautifyFilter{
    
    //设置相芯-美形参数
    func setAgoraBeauty(_ model: BeautyFuncModel) {
       
        if model.paramModel.name == "lighteningLevel" {
            self.agoraBeautyOptions?.lighteningLevel = Float(model.paramModel.value)
        }else if model.paramModel.name == "smoothnessLevel" {
            self.agoraBeautyOptions?.smoothnessLevel = Float(model.paramModel.value)
        }else if model.paramModel.name == "rednessLevel" {
            self.agoraBeautyOptions?.rednessLevel = Float(model.paramModel.value)
        }else if model.paramModel.name == "sharpnessLevel" {
            self.agoraBeautyOptions?.sharpnessLevel = Float(model.paramModel.value)
        }else if model.paramModel.name == "lighteningContrastLevel" {
            if model.paramModel.value < 1/3 {
                self.agoraBeautyOptions?.lighteningContrastLevel = .low
            }else if model.paramModel.value > 2/3 {
                self.agoraBeautyOptions?.lighteningContrastLevel = .high
            }else {
                self.agoraBeautyOptions?.lighteningContrastLevel = .normal
            }
        }
        agoraKit.setBeautyEffectOptions(true, options: self.agoraBeautyOptions)
    }
    
    //设置声网美颜效果
    private func setAgoraBeautyEffect() {
        if self.agoraBeautyOptions == nil {
            let beautyOptions = AgoraBeautyOptions()
            beautyOptions.lighteningContrastLevel = .high //对比度
            beautyOptions.lighteningLevel = 0.8 //美白程度，取值范围为 [0.0,1.0]
            beautyOptions.smoothnessLevel = 0.7 //磨皮程度，取值范围为 [0.0,1.0]
            beautyOptions.rednessLevel = 0.5    //红润度，取值范围为 [0.0,1.0]
            beautyOptions.sharpnessLevel = 0.3  //锐化程度，取值范围为 [0.0,1.0]
            self.agoraBeautyOptions = beautyOptions
        }
        agoraKit.setBeautyEffectOptions(true, options: self.agoraBeautyOptions)
    }
    
    // 取消声网美颜效果
    private func cancelAgoraBeautyEffect(){
        let beautyOptions = AgoraBeautyOptions()
        agoraKit.setBeautyEffectOptions(false, options: beautyOptions)
    }
    
    //声网美颜-开启/关闭
    func setEnableAgoraBeauty(isEnabled:Bool)  {
        if isOpAgora != isEnabled {
            isOpAgora = isEnabled
            if isOpAgora {
                self.setAgoraBeautyEffect()
            }else{
                self.cancelAgoraBeautyEffect()
            }
        }
    }
}


// MARK: - 相芯美颜
extension BeautifyFilter{

    func setupFaceUnityBeauty(){
        //1. 设置相芯美颜鉴权
        let authdataArray = virtualTools.getAuthdata()
        let param : Dictionary = ["authdata":authdataArray]
        let ret = agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuSetup", value: virtualTools.toJson(param as [AnyHashable : Any]))
        print("FaceUnity fuSetup ----返回值：\(ret)")
        
        //2. 设置相芯初始化参数
        let bundelPath = Bundle.main.path(forResource: "face_beautification", ofType: "bundle")
        let paramDic3 : Dictionary = ["data":bundelPath]
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuCreateItemFromPackage", value: virtualTools.toJson(paramDic3 as Dictionary<String, Any>))
        
        //3. 加载资源
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuLoadAIModelFromPackage", value: virtualTools.toJson([
            "data": Bundle.main.path( forResource: "ai_face_processor", ofType: "bundle") ?? "",
            "type": NSNumber(value: 1 << 10)
        ]))
        
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuLoadAIModelFromPackage", value: virtualTools.toJson([
            "data": Bundle.main.path( forResource: "ai_hand_processor", ofType: "bundle") ?? "",
            "type": NSNumber(value: 1 << 3)
        ]))
        
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuLoadAIModelFromPackage", value: virtualTools.toJson([
            "data": Bundle.main.path( forResource: "ai_human_processor_pc", ofType: "bundle") ?? "",
            "type": NSNumber(value: 1 << 19)
        ]))
        
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuCreateItemFromPackage", value: virtualTools.toJson([
            "data": Bundle.main.path( forResource: "aitype", ofType: "bundle") ?? ""
        ]))
        
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuItemSetParam", value: virtualTools.toJson([
            "obj_handle": Bundle.main.path( forResource: "aitype", ofType: "bundle") ?? "",
            "name": "aitype",
            "value": NSNumber(value: 1 << 10 | 1 << 21 | 1 << 3)
        ]))
        //4. 初始化完成默认关闭
        self.setEnableFaceUnity(isEnabled: false)
    }
    
    //相芯美颜-开启关闭
    func setEnableFaceUnity(isEnabled:Bool)  {
        if isOpFaceUnity != isEnabled {
            isOpFaceUnity = isEnabled
            agoraKit.enableExtension(withVendor: "FaceUnity", extension:"Effect", enabled: isEnabled)
            print("相芯美颜状态 = \(isEnabled)")
        }
    }
    
    //设置相芯-美形参数
    func setFaceUnityBeautyBody(_ model: BeautyFuncModel) {
        guard let bundelPath = Bundle.main.path(forResource: "face_beautification", ofType: "bundle") else { return }

        let paramDic0 : Dictionary = ["obj_handle":bundelPath,"name":"face_shape","value":4] as [String : Any]
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuItemSetParam", value: virtualTools.toJson(paramDic0))

        let paramDic9 : Dictionary = ["obj_handle":bundelPath,"name":model.paramModel.name,"value":model.paramModel.value] as [String : Any]
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuItemSetParam", value: virtualTools.toJson(paramDic9))
        
    }
    
    //设置相芯-美颜参数
    func setFaceUnityBeauty(_ model: BeautyFuncModel) {
        guard let bundelPath = Bundle.main.path(forResource: "face_beautification", ofType: "bundle") else { return }

        let paramDic4 : Dictionary = ["obj_handle":bundelPath,"name":model.paramModel.name,"value":model.paramModel.value] as [String : Any]
        agoraKit.setExtensionPropertyWithVendor("FaceUnity", extension: "Effect", key: "fuItemSetParam", value: virtualTools.toJson(paramDic4))
    }
}


// MARK: - 火山美颜-相关方法
extension BeautifyFilter {
    
    //开启火山美颜-初始化
    func setupByteDanceBeauty(){
        self.oneKeySourceArr = [String]()
        self.dressSourceArr = [String]()
        //1. 设置火山美颜鉴权
        let resourceHelper = BDResourceHelper()
        let param : Dictionary = ["licensePath": resourceHelper.licensePath()]
        let rt = agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_check_license", value: virtualTools.toJson(param as [AnyHashable : Any]))
        print("ByteDance fuSetup ----返回值：\(rt)")
        
        //2. 设置火山初始化参数
        let bundelPath = resourceHelper.modelDirPath()
        let paramDic2 : Dictionary = ["strModelDir":bundelPath,"deviceName": ""]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_init", value: virtualTools.toJson(paramDic2 as Dictionary<String, Any>))

        let paramDic3 : Dictionary = ["mode": NSNumber(value: 1), "orderType": NSNumber(value: 0)]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_set_mode", value: virtualTools.toJson(paramDic3 as Dictionary<String, Any>))

        let paramDic4 = [ resourceHelper.composerNodePath("beauty_IOS_lite"), resourceHelper.composerNodePath("reshape_lite") ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_set_nodes", value: virtualTools.toJson(paramDic4))

        //3. 去除贴图资源
        let paramDic5 : Dictionary = ["strPath": resourceHelper.stickerPath("")]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_set_effect", value: virtualTools.toJson(paramDic5 as Dictionary<String, Any>))

        //4. 初始化完成默认关闭
        self.setEnableVolcUnity(isEnabled: false)
    }
    
    //设置火山-开启/关闭
    func setEnableVolcUnity(isEnabled:Bool)  {
        if isOpByteDance != isEnabled {
            isOpByteDance = isEnabled
            agoraKit.enableExtension(withVendor: "ByteDance", extension:"Effect", enabled: isEnabled)
            print("火山美颜状态 = \(isEnabled)")
        }
    }
    
    //设置火山-美颜参数
    func setByteDanceBeauty(model:BeautyFuncModel)  {
        let paramDic:Dictionary<String, Any> = [
            "nodePath": BDResourceHelper().composerNodePath("beauty_IOS_lite"),
            "nodeTag": model.paramModel.name,
            "value": NSNumber(value: model.paramModel.value)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_update_node", value: virtualTools.toJson(paramDic))
    }
    
    //设置火山-美体美形参数
    func setByteDanceBeautyBody(model:BeautyFuncModel)  {
        let paramDic:Dictionary<String, Any> = [
            "nodePath": BDResourceHelper().composerNodePath("reshape_lite"),
            "nodeTag": model.paramModel.name,
            "value": NSNumber(value: model.paramModel.value)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_update_node", value: virtualTools.toJson(paramDic))
    }
    
    //设置火山-一键美颜
    func setBDOneKeyDressUp(model:BeautyFuncModel)  {
        let tempString = BDResourceHelper().composerNodePath(model.paramModel.sourcePath)
        oneKeySourceArr.removeAll()
        self.oneKeySourceArr.append(tempString)
        
        var tempSourceArr = self.oneKeySourceArr
        tempSourceArr.append(contentsOf: self.dressSourceArr)
        
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_set_nodes", value: virtualTools.toJson(tempSourceArr))
    }
    
    //设置火山-美妆参数
    func setByteDanceDressUp(model:BeautyFuncModel){
        let tempString = BDResourceHelper().composerNodePath(model.paramModel.sourcePath)
        if !self.dressSourceArr.contains(tempString) {
            self.dressSourceArr.append(tempString)
            
            var tempSourceArr = self.oneKeySourceArr
            tempSourceArr.append(contentsOf: self.dressSourceArr)
            
            agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_set_nodes", value: virtualTools.toJson(tempSourceArr))
        }
        
        let paramDic:Dictionary<String, Any> = [
            "nodePath": BDResourceHelper().composerNodePath(model.paramModel.sourcePath),
            "nodeTag": model.paramModel.name,
            "value": NSNumber(value: model.paramModel.value)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_composer_update_node", value: virtualTools.toJson(paramDic))
        
    }
    
    //设置火山-贴纸
    func setStickerDressUp(model:BeautyFuncModel)  {
        let paramDic:Dictionary<String, Any> = [
            "strPath": BDResourceHelper().stickerPath(model.paramModel.sourcePath)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_set_effect", value: virtualTools.toJson(paramDic))
    }
    
    //设置火山-美体滤镜参数
    func setByteDanceFilter(model:BeautyFuncModel)  {
        let paramDic1:Dictionary<String, Any> = [
            "strPath": BDResourceHelper().composerNodePath(model.paramModel.sourcePath)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_set_color_filter_v2", value: virtualTools.toJson(paramDic1))
        
        let paramDic2:Dictionary<String, Any> = [
            "fIntensity": NSNumber(value:model.paramModel.value)
        ]
        agoraKit.setExtensionPropertyWithVendor("ByteDance", extension: "Effect", key: "bef_effect_ai_set_intensity", value: virtualTools.toJson(paramDic2))
    }
}

