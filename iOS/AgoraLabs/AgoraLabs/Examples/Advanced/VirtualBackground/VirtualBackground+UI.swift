//
//  VirtualBackground+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import UIKit
import ZLPhotoBrowser
import ASValueTrackingSlider
import Photos

extension VirtualBackground {
    
    func setupUI() {
        self.setupNavigation()
        self.setupContentView()
        self.setupBottom()
    }
    
    func setupNavigation() {
        self.view.backgroundColor = SCREEN_CCC_COLOR.hexColor()

        let button = UIButton(type: .custom)
        button.frame = CGRect(x:0, y:0, width:65, height:30)
        button.setImage(UIImage(named:"ChevronLeft"), for: .normal)
        button.setImage(UIImage(named:"ChevronLeft"), for: .highlighted)
        button.setTitle("Virtual Background".localized, for: .normal)
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
        
        navigationItem.rightBarButtonItem = barItemOne
    }
    
    func setupContentView() {
        let localView = UIView()
        localView.backgroundColor = UIColor.black
        self.localVideoView = localView
        self.view.addSubview(localView)
        localView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
    }

    
    func setupBottom() {
        let bottomView = UIView()
        bottomView.backgroundColor = SCREEN_MASK_COLOR.hexColor()
        self.view.addSubview(bottomView)
        bottomView.snp.makeConstraints { make in
            make.bottom.left.right.equalToSuperview()
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+118)
        }
        
        
        let subView = SubCellView()
        originalModel.subView = subView
        subView.setupSubCellModel(originalModel)
        bottomView.addSubview(subView)
        subView.snp.makeConstraints { make in
            make.top.equalToSuperview().offset(24)
            make.left.equalToSuperview().offset(4)
            make.size.equalTo(CGSize(width: 72, height: 70))
        }
        subView.clickView {[weak self] sender in
            self?.itemViewClick(index: -1)
        }

        let scrollView = UIScrollView()
        scrollView.showsHorizontalScrollIndicator = false
        bottomView.addSubview(scrollView)
        scrollView.snp.makeConstraints { make in
            make.top.right.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.height.equalTo(118)
        }
        
        let contentV = UIView()
        scrollView.addSubview(contentV)
        contentV.snp.makeConstraints { (make) in
            make.left.right.top.bottom.height.equalToSuperview()
            make.width.equalTo(80*itemModelList.count)
        }
        
        for i in 0..<itemModelList.count {
            let itemModel = itemModelList[i]
            let itemView = SubCellView()
            itemModel.subView = itemView
            itemView.tag = i
            itemView.setupSubCellModel(itemModel)
            contentV.addSubview(itemView)
            itemView.snp.makeConstraints { (make) in
                make.centerY.equalToSuperview()
                make.left.equalToSuperview().offset(i*(72+8)+8)
                make.size.equalTo(CGSize(width: 72, height: 70))
            }
            itemView.clickView {[weak self] sender in
                UINotificationFeedbackGenerator().notificationOccurred(.success)
                self?.itemViewClick(index: sender.tag)
            }
        }
        
        let maskView = UIView()
        bottomView.addSubview(maskView)
        maskView.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.width.equalTo(16)
        }
    
        let c0 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(0.0)
        let c1 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(1)
        maskView.colorGradient(color0: c0, color1: c1, point0: CGPoint(x: 1, y: 0.5), point1: CGPoint(x: 0, y: 0.5))
        bottomView.rectCorner(corner: [.topLeft,.topRight], radii:  CGSize(width: 12, height: 12))
        
        let rangeSlider = ASValueTrackingSlider()
        rangeSlider.isHidden = true
        rangeSlider.minimumTrackTintColor = "#4ca7ff".hexColor()
        rangeSlider.maximumTrackTintColor = "#aeafb5".hexColor()
        rangeSlider.dataSource = self
        rangeSlider.delegate = self
        rangeSlider.popUpViewWidthPaddingFactor = 1.5
        rangeSlider.popUpViewHeightPaddingFactor = 1.5
        rangeSlider.popUpViewColor = UIColor().setRgba(24, 25, 27, 0.6)
        rangeSlider.popUpViewArrowLength = 0
        rangeSlider.showPopUpView(animated: true)
        rangeSlider.font = UIFont.systemFont(ofSize: 12)
        self.blurSlider = rangeSlider
        self.view.addSubview(rangeSlider)
        rangeSlider.snp.makeConstraints { make in
            make.bottom.equalTo(bottomView.snp.top).offset(-16)
            make.width.equalTo(SCREEN_WIDTH-32)
            make.centerX.equalToSuperview()
        }

    }
    
   
    
}

extension VirtualBackground {
  
    private func itemViewClick(index:Int) {
        
        if index == -1 {
            //原图
            self.currentModel = self.originalModel
            for item in itemModelList {
                item.isSelected =  false
                if let itemView = item.subView as? SubCellView { itemView.setupSubCellModel(item) }
            }
            UINotificationFeedbackGenerator().notificationOccurred(.success)
            self.originalViewClick()
        }else if index == 1 {
            //绿幕分割
            let model:SubCellModel = self.itemModelList[index]
            guard let itemView = model.subView as? SubCellView  else { return  }
            model.isSelected = !model.isSelected
            itemView.setupSubCellModel(model)
            self.greenScreenState(model: model)
        }else {
            let model:SubCellModel = self.itemModelList[index]
            self.currentModel = model
            for item in itemModelList {
                guard let itemView = item.subView as? SubCellView  else { return  }
                if item.tag == 1 { continue }
                if item.tag == model.tag {
                    item.isSelected = !item.isSelected
                    itemView.setupSubCellModel(item)
                }else  {
                    item.isSelected = false
                    itemView.setupSubCellModel(item)
                }
            }
            if model.tag == 4 {
                self.showImagePickerVC()
            }else{
                self.itemViewClick(model: model)
            }
        }
        self.blurSlider?.isHidden = itemModelList.first?.isSelected == false
    }
}

//添加自定义背景图
extension VirtualBackground {
    private func showImagePickerVC(){
        let config = ZLPhotoConfiguration.default()
        config.allowSelectVideo = false
        config.allowTakePhoto = false
        config.maxSelectCount = 1
        let ps = ZLPhotoPreviewSheet()
        ps.selectImageBlock = { [weak self] (images, assets, isOriginal) in
            let path = NSHomeDirectory() + "/Documents/CustomizeImage.png"
            do {
                try images.first?.pngData()?.write(to: URL(fileURLWithPath: path))
                self?.configAddLocalImg(path)
            }catch {
                        
            }
        }
        ps.showPhotoLibrary(sender: self)
    }
    
    private func configAddLocalImg(_ imagePath : String) {
        if let model = self.itemModelList.filter({$0.name == "Customize Image"}).first {
            model.value = imagePath
            model.isSelected = true
            self.itemViewClick(model: model)
        }
    }
}

extension VirtualBackground:ASValueTrackingSliderDataSource,ASValueTrackingSliderDelegate {
    func sliderWillDisplayPopUpView(_ slider: ASValueTrackingSlider!) { }
    
    func sliderViewWillEndTouch(_ slider: ASValueTrackingSlider!) {
        if self.currentModel?.tag != 0 { return }
        var newValue:CGFloat = 0.0
        if slider.value < 1/3 {
            newValue = 0
        }else if slider.value > 2/3 {
            newValue = 1
        }else {
            newValue = 0.5
        }
        self.blurSlider?.value = Float(newValue)
    }
    
    func slider(_ slider: ASValueTrackingSlider!, stringForValue value: Float) -> String! {
        if self.currentModel?.tag != 0 { return "low".localized }
        var newString = "low".localized
        var modelValue = 1
        if value < 1/3 {
            newString = "low".localized
            modelValue = 1
        }else if value > 2/3 {
            newString = "high".localized
            modelValue = 3
        }else {
            newString = "medium".localized
            modelValue = 2
        }
        self.blurSlider?.value = value
        let model:SubCellModel = self.itemModelList[0]
        if let _value = model.value as? Int, _value != modelValue {
            model.value = modelValue
            model.isSelected = true
            UINotificationFeedbackGenerator().notificationOccurred(.success)
            self.itemViewClick(model: model)
        }
        return newString
    }
    

}
