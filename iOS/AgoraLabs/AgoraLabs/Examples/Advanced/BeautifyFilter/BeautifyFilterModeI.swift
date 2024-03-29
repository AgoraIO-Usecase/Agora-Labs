//
//  BeautifyFilterModeI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/8.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import UIKit
//import HandyJSON

class BaseModel: NSObject {

    required override init() {}
    
}

class BeautyFuncModel: BaseModel {

    var abilityName = "" //功能名称
    
    var abilityIcon = "" //功能图标
    
    var abilitySelectedIcon = "" //功能选中图标
    
    var abilityId : Int = 0 //功能id
    
    var tag : Int = 0 //view绑定 tag
    
    var isSelected : Bool = false //是否选中
    
    var subView:UIView?
    
    var paramModel = BeautyParamModel()
    
    class func originalData() -> BeautyFuncModel {
        let tempModel = BeautyFuncModel()
        tempModel.abilityName = "yuantu".localized
        tempModel.abilityIcon = "yuantu"
        tempModel.abilityId = 101
        tempModel.paramModel.name = ""
        tempModel.paramModel.currentValue = 0
        return tempModel
    }
    
    class func loadFaceData() -> [BeautyFuncModel] {
         
         var dataArr = [BeautyFuncModel]()
        
         for i in 1..<9 {
             let tempModel = BeautyFuncModel()

             if i == 0 {
                 tempModel.abilityName = "yuantu".localized
                 tempModel.abilityIcon = "yuantu"
                 tempModel.abilityId = 101
                 tempModel.paramModel.name = ""
                 tempModel.paramModel.currentValue = 0
             }
             if i == 1 {
                 tempModel.abilityName = "mopi".localized
                 tempModel.abilityIcon = "mopi"
                 tempModel.abilityId = 102
                 tempModel.paramModel.name = "blur_level" //0.0-6.0，默认6.0
                 tempModel.paramModel.currentValue = 6
                 tempModel.paramModel.exFactor = 6
             }
             if i == 2 {
                 tempModel.abilityName = "meibai".localized
                 tempModel.abilityIcon = "meibai"
                 tempModel.abilityId = 103
                 tempModel.paramModel.name = "color_level" //0.0-2.0,0.0为无效果，2.0为最大效果，默认值0.2
                 tempModel.paramModel.currentValue = 0.2
                 tempModel.paramModel.exFactor = 2
                
             }
             if i == 3 {
                 tempModel.abilityName = "hongrun".localized
                 tempModel.abilityIcon = "hongrun"
                 tempModel.abilityId = 104
                 tempModel.paramModel.name = "red_level"  //取值范围 0.0-2.0,0.0为无效果，2.0为最大效果，默认值0.5
                 tempModel.paramModel.currentValue = 0.5
                 tempModel.paramModel.exFactor = 2
                 
             }
             if i == 4 {
                 tempModel.abilityName = "shoulian".localized
                 tempModel.abilityIcon = "shoulian"
                 tempModel.abilityId = 105
                 tempModel.paramModel.name = "cheek_thinning" //默认0.0,          //瘦脸程度范围0.0-1.0
                 tempModel.paramModel.currentValue = 0
             }
             if i == 5 {
                 tempModel.abilityName = "dayan".localized
                 tempModel.abilityIcon = "dayan"
                 tempModel.abilityId = 106
                 tempModel.paramModel.name = "eye_enlarging" //默认0.5,        //大眼程度范围0.0-1.0
                 tempModel.paramModel.currentValue = 0.5
             }
             if i == 6 {
                 tempModel.abilityName = "quheiyanquan".localized
                 tempModel.abilityIcon = "quheiyanquan"
                 tempModel.abilityId = 107
                 tempModel.paramModel.name = "remove_pouch_strength"  //0.0~1.0 0.0为无效果，1.0最强，默认0.0
                 tempModel.paramModel.currentValue = 0
             }
             if i == 7 {
                 tempModel.abilityName = "meiya".localized
                 tempModel.abilityIcon = "meiya"
                 tempModel.abilityId = 108
                 tempModel.paramModel.name = "tooth_whiten"  //0.0-1.0,0.0为无效果，1.0为最大效果，默认值1.0
                 tempModel.paramModel.currentValue = 1
             }
             if i == 8 {
                 tempModel.abilityName = "qufalingwen".localized
                 tempModel.abilityIcon = "qufalingwen"
                 tempModel.abilityId = 109
                 tempModel.paramModel.name = "remove_nasolabial_folds_strength"  //0.0~1.0 0.0为无效果，1.0最强，默认0.0
             }

             dataArr.append(tempModel)

         }
        
        return dataArr

     }
    
    class func dataBD() -> BeautyFuncModel {
        let tempModel = BeautyFuncModel()
        tempModel.abilityName = "yuantu".localized
        tempModel.abilityIcon = "yuantu"
        tempModel.abilityId = 101
        tempModel.paramModel.name = ""
        tempModel.paramModel.currentValue = 0
        return tempModel
    }
    
    class func loadVolcData() -> [[BeautyFuncModel]]{
        
        var dataArr = [BeautyFuncModel]()
        
        for i in 0..<3 {
            let tempModel = BeautyFuncModel()
            tempModel.paramModel.funcKey = "skinCare"
            
            if i == 0 {
                tempModel.abilityName = "mopi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 100
                tempModel.paramModel.name = "smooth" //强度范围0-1
                tempModel.paramModel.currentValue = 0
            }
            if i == 1 {
                tempModel.abilityName = "meibai".localized
                tempModel.abilityIcon = "meibai"
                tempModel.abilityId = 101
                tempModel.paramModel.name = "whiten" //强度范围0-1
                tempModel.paramModel.currentValue = 0.2
                
            }
            if i == 2 {
                tempModel.abilityName = "ruihua".localized
                tempModel.abilityIcon = "hongrun"
                tempModel.abilityId = 102
                tempModel.paramModel.name = "sharp"  //强度范围0-1
                tempModel.paramModel.currentValue = 0
                
            }
            
            dataArr.append(tempModel)
        }
        
        var dataArr1 = [BeautyFuncModel]()
       
        for i in 0..<7 {
            let tempModel = BeautyFuncModel()
            tempModel.paramModel.funcKey = "microShap"
            
            if i == 0 {
                tempModel.abilityName = "shoulian".localized
                tempModel.abilityIcon = "shoulian"
                tempModel.abilityId = 100
                tempModel.paramModel.name = "Internal_Deform_Overall" //强度范围0-1
                tempModel.paramModel.currentValue = 0
            }
            if i == 1 {
                tempModel.abilityName = "zhailian".localized
                tempModel.abilityIcon = "zhailian"
                tempModel.abilityId = 101
                tempModel.paramModel.name = "Internal_Deform_CutFace"
                tempModel.paramModel.currentValue = 0
            }
            if i == 2 {
                tempModel.abilityName = "quangu".localized
                tempModel.abilityIcon = "quangu"
                tempModel.abilityId = 102
                tempModel.paramModel.name = "Internal_Deform_Zoom_Cheekbone"
                tempModel.paramModel.currentValue = 0
            }
            if i == 3 {
                tempModel.abilityName = "xiahe".localized
                tempModel.abilityIcon = "xiahe"
                tempModel.abilityId = 103
                tempModel.paramModel.name = "Internal_Deform_Zoom_Jawbone"
                tempModel.paramModel.currentValue = 0
            }
            if i == 4 {
                tempModel.abilityName = "yanjing".localized
                tempModel.abilityIcon = "eye"
                tempModel.abilityId = 104
                tempModel.paramModel.name = "Internal_Deform_Eye"
                tempModel.paramModel.currentValue = 0
            }
            if i == 5 {
                tempModel.abilityName = "bizi".localized
                tempModel.abilityIcon = "qufalingwen"
                tempModel.abilityId = 105
                tempModel.paramModel.name = "Internal_Deform_Nose"
                tempModel.paramModel.currentValue = 0
            }
            if i == 6 {
                tempModel.abilityName = "zuiba".localized
                tempModel.abilityIcon = "lip"
                tempModel.abilityId = 106
                tempModel.paramModel.name = "Internal_Deform_ZoomMouth"
                tempModel.paramModel.currentValue = 0
            }
            
            dataArr1.append(tempModel)
            
        }
        
        var dataArr2 = [BeautyFuncModel]()
       
        for i in 0..<17 {
            let tempModel = BeautyFuncModel()
            tempModel.paramModel.funcKey = "oneKeyDress"
            
            if i == 0 {//
                tempModel.abilityName = "aidou".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 100
                tempModel.paramModel.sourcePath = "style_makeup/aidou"
            }
            
            if i == 1 {//
                tempModel.abilityName = "baica".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 101
                tempModel.paramModel.sourcePath = "style_makeup/baicha"
            }
            
            if i == 2 {//
                tempModel.abilityName = "baixi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 102
                tempModel.paramModel.sourcePath = "style_makeup/baixi"
            }
            
            if i == 3 {//
                tempModel.abilityName = "bingchuan".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 103
                tempModel.paramModel.sourcePath = "style_makeup/bingchuan"
            }
            
            if i == 4 {//
                tempModel.abilityName = "Cwei".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 104
                tempModel.paramModel.sourcePath = "style_makeup/cwei"
            }
            
            if i == 5 {//
                tempModel.abilityName = "duanmei".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 105
                tempModel.paramModel.sourcePath = "style_makeup/duanmei"
            }
            
            if i == 6 {//
                tempModel.abilityName = "hanxi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 106
                tempModel.paramModel.sourcePath = "style_makeup/hanxi"
            }
            
            if i == 7 {//
                tempModel.abilityName = "meiyouxiaoxiong".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 107
                tempModel.paramModel.sourcePath = "style_makeup/meiyouxiaoxiong"
            }
            
            if i == 8 {//
                tempModel.abilityName = "nuannan".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 108
                tempModel.paramModel.sourcePath = "style_makeup/nuannan"
            }
            
            if i == 9 {//
                tempModel.abilityName = "oumei".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 109
                tempModel.paramModel.sourcePath = "style_makeup/oumei"
            }
            
            if i == 10 {//
                tempModel.abilityName = "qise".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 110
                tempModel.paramModel.sourcePath = "style_makeup/qise"
            }
            if i == 11 {//
                tempModel.abilityName = "shensui".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 111
                tempModel.paramModel.sourcePath = "style_makeup/shensui"
            }
            if i == 12 {//
                tempModel.abilityName = "tianmei".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 112
                tempModel.paramModel.sourcePath = "style_makeup/tianmei"
            }
            if i == 13 {//
                tempModel.abilityName = "wennuan".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 113
                tempModel.paramModel.sourcePath = "style_makeup/wennuan"
            }
            if i == 14 {//
                tempModel.abilityName = "youya".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 114
                tempModel.paramModel.sourcePath = "style_makeup/youya"
            }
            if i == 15 {//
                tempModel.abilityName = "yuanqi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 115
                tempModel.paramModel.sourcePath = "style_makeup/yuanqi"
            }
            if i == 16 {//
                tempModel.abilityName = "zhigan".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 116
                tempModel.paramModel.sourcePath = "style_makeup/zhigan"
            }
            
            dataArr2.append(tempModel)
            
        }
        
        var dataArr3 = [BeautyFuncModel]()
       
        for i in 0..<14 {
            let tempModel = BeautyFuncModel()
            tempModel.paramModel.funcKey = "sticker"
            
            if i == 0 {//👌
                tempModel.abilityName = "beibei".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 100
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "baby_gan"
            }
            if i == 1 {//👌
                tempModel.abilityName = "biaobaiqixi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 101
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "biaobaiqixi"
            }
            if i == 2 {//👌
                tempModel.abilityName = "chitushaonü".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 102
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "chitushaonv"
            }
            if i == 3 {//👌
                tempModel.abilityName = "dianjita".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 103
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "dianjita"
            }
            if i == 4 {//👌
                tempModel.abilityName = "gongzhumianju".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 104
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "gongzhumianju"
            }
            if i == 5 {//👌
                tempModel.abilityName = "heimaoyanjing".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 105
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "heimaoyanjing"
            }
            if i == 6 {//👌
                tempModel.abilityName = "jiamian".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 106
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "jiamian"
            }
            if i == 7 {//👌
                tempModel.abilityName = "zhaocaimao".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 107
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "zhaocaimao"
            }
            if i == 8 {//👌
                tempModel.abilityName = "meihaoxinqing".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 108
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "meihaoxinqing"
            }
            if i == 9 {//👌
                tempModel.abilityName = "konglongceshi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 109
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "konglongceshi"
            }
            if i == 10 {//👌
                tempModel.abilityName = "shenshi".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 110
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "shenshi"
            }
            if i == 11 {//👌
                tempModel.abilityName = "tiaowuhuoji".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 111
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "tiaowuhuoji"
            }
            if i == 12 {//👌
                tempModel.abilityName = "zhutouzhuer".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 112
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "zhutouzhuer"
            }
            if i == 13 {//👌
                tempModel.abilityName = "zisemeihuo".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 113
                tempModel.paramModel.name = ""
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "zisemeihuo"
            }
            
            dataArr3.append(tempModel)
            
        }
        
        var dataArr4 = [BeautyFuncModel]()
       
        for i in 0..<6 {
            let tempModel = BeautyFuncModel()
            tempModel.paramModel.funcKey = "dressUp"
            
            if i == 0 {//👌
                tempModel.abilityName = "kouhong".localized
                tempModel.abilityIcon = "lip"
                tempModel.abilityId = 100
                tempModel.paramModel.name = "Internal_Makeup_Lips"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "lip/lite/fuguhong"
            }
            
            if i == 1 {
                tempModel.abilityName = "saihong".localized
                tempModel.abilityIcon = "hongrun"
                tempModel.abilityId = 101
                tempModel.paramModel.name = "Internal_Makeup_Blusher"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "blush/lite/weixun"
            }
            
            if i == 2 {
                tempModel.abilityName = "xiurong".localized
                tempModel.abilityIcon = "mopi"
                tempModel.abilityId = 102
                tempModel.paramModel.name = "Internal_Makeup_Facial"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "facial/jingzhi"
            }
            
            if i == 3 {//👌
                tempModel.abilityName = "jiemao".localized
                tempModel.abilityIcon = "eyelash"
                tempModel.abilityId = 103
                tempModel.paramModel.name = "Internal_Makeup_Eyelash"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "eyelashes/chibang"
            }
            
            if i == 4 {
                tempModel.abilityName = "meimao".localized
                tempModel.abilityIcon = "eyebrow"
                tempModel.abilityId = 104
                tempModel.paramModel.name = "Internal_Makeup_Brow"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "eyebrow/lite/BK01"
            }
            
            if i == 5 {//👌
                tempModel.abilityName = "meitong".localized
                tempModel.abilityIcon = "eye"
                tempModel.abilityId = 105
                tempModel.paramModel.name = "Internal_Makeup_Pupil"
                tempModel.paramModel.currentValue = 0
                tempModel.paramModel.sourcePath = "pupil/chujianhui"
            }
            
            dataArr4.append(tempModel)
            
        }
        
        
//        let tmpDic = ["skinCare":dataArr,"microShap":dataArr1,"oneKeyDress":dataArr2,"sticker":dataArr3,"dressUp":dataArr4]
        let tmpDic = [dataArr,dataArr1,dataArr2,dataArr3,dataArr4]
        return tmpDic
    }

    class func loadAgoraData() -> [BeautyFuncModel] {
         
         var dataArr = [BeautyFuncModel]()
        
         for i in 1..<6 {
             let tempModel = BeautyFuncModel()

             if i == 0 {
                 tempModel.abilityName = "yuantu".localized
                 tempModel.abilityIcon = "yuantu"
                 tempModel.abilityId = 101
                 tempModel.paramModel.name = ""
                 tempModel.paramModel.currentValue = 0
             }
             if i == 1 {
                 tempModel.abilityName = "mopi".localized
                 tempModel.abilityIcon = "mopi"
                 tempModel.abilityId = 102
                 tempModel.paramModel.name = "smoothnessLevel" //0.0-1
                 tempModel.paramModel.currentValue = 0.7
                 tempModel.paramModel.exFactor = 1
             }
             if i == 2 {
                 tempModel.abilityName = "meibai".localized
                 tempModel.abilityIcon = "meibai"
                 tempModel.abilityId = 103
                 tempModel.paramModel.name = "lighteningLevel" //0.0-1
                 tempModel.paramModel.currentValue = 0.8
                 tempModel.paramModel.exFactor = 1
                
             }
             if i == 3 {
                 tempModel.abilityName = "hongrun".localized
                 tempModel.abilityIcon = "hongrun"
                 tempModel.abilityId = 104
                 tempModel.paramModel.name = "rednessLevel"  //取值范围 0.0-1
                 tempModel.paramModel.currentValue = 0.5
                 tempModel.paramModel.exFactor = 1
                 
             }
             if i == 4 {
                 tempModel.abilityName = "duibidu".localized
                 tempModel.abilityIcon = "meibai"
                 tempModel.abilityId = 105
                 tempModel.paramModel.name = "lighteningContrastLevel" //默认0.0,
                 tempModel.paramModel.currentValue = 0
             }
             if i == 5 {
                 tempModel.abilityName = "ruihua".localized
                 tempModel.abilityIcon = "mopi"
                 tempModel.abilityId = 106
                 tempModel.paramModel.name = "sharpnessLevel" //默认0.5,
                 tempModel.paramModel.currentValue = 0.3
             }
            
             dataArr.append(tempModel)

         }
        
        return dataArr

     }
}

class BeautyParamModel: BaseModel {

    var name = "" //功能名称
    
    var currentValue : CGFloat = 0.0 //当前值，未处理
    
    var value : CGFloat = 0.0 //处理后的值
    
    var exFactor : CGFloat = 1.0 //扩充系数
    
    var sourcePath = "" //资源路径
    
    var funcKey = "" //美妆类型key,自定义
    
}
