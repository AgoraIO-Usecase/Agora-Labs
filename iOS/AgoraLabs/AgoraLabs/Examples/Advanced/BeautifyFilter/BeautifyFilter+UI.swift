//
//  BeautifyFilter+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/8.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import ASValueTrackingSlider
import JXSegmentedView
import UIKit

extension BeautifyFilter {
    
    func setupUI() {
        self.setupNavigation()
        self.setupContentView()
        self.setupSDKFilterItem()
        self.setupBottom()
    }
    
    func setupNavigation() {
        self.view.backgroundColor = SCREEN_CCC_COLOR.hexColor()

        let button = UIButton(type: .custom)
        button.frame = CGRect(x:0, y:0, width:65, height:30)
        button.setImage(UIImage(named:"ChevronLeft"), for: .normal)
        button.setImage(UIImage(named:"ChevronLeft"), for: .highlighted)
        button.setTitle("Beautify Filter".localized, for: .normal)
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
    
    func setupSDKFilterItem()  {
        for i in 0..<filterList.count {
            let itemModel = filterList[i]
            let itemView = SubButton(type: .custom)
            itemView.title = itemModel.name.localized
            itemView.tag  = i
            itemView.titleLabel?.font = UIFont.systemFont(ofSize: 15)
            itemView.backgroundColor = Color().setRgba(24, 25, 27, 0.6)
            itemView.contentEdgeInsets = UIEdgeInsets(top: 0, left: 16, bottom: 0, right: 16)
            itemView.cornerRadius = 18
            itemView.masksToBounds = true
            itemModel.subView = itemView
            self.view.addSubview(itemView)
            itemView.snp.makeConstraints { (make) in
                make.height.equalTo(36)
                make.right.equalToSuperview().offset(-16)
                make.top.equalToSuperview().offset(Int(SCREEN_NAV_FULL_HEIGHT)+16+40*i)
            }
            itemView.clickView {[weak self] sender in
                self?.filterItemViewClick(index: sender.tag)
            }
        }
    }
    
    func setupBottom() {
        let backgroundColor = "#2a2a3b"
        let bottomView = UIView()
        bottomView.isHidden = true
        bottomView.backgroundColor = backgroundColor.hexColor()
        self.bottomView = bottomView
        self.view.addSubview(bottomView)
        bottomView.snp.makeConstraints { make in
            make.bottom.left.right.equalToSuperview()
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+118)
        }
        
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
        self.curSlider = rangeSlider
        self.view.addSubview(rangeSlider)
        rangeSlider.snp.makeConstraints { make in
            make.bottom.equalTo(bottomView.snp.top).offset(-16)
            make.width.equalTo(SCREEN_WIDTH-32)
            make.centerX.equalToSuperview()
        }
    }
    
    private func setupAgoraView()  {
        self.bottomView?.isHidden = true
        self.curSlider?.isHidden = true
        self.setEnableAgoraBeauty(isEnabled: true)
        self.setEnableFaceUnity(isEnabled: false)
        self.setEnableVolcUnity(isEnabled: false)
    }
        
    func closeBottomView()  {
        self.bottomView?.isHidden = true
        self.curSlider?.isHidden = true
        self.setEnableAgoraBeauty(isEnabled: false)
        self.setEnableFaceUnity(isEnabled: false)
        self.setEnableVolcUnity(isEnabled: false)

        self.dataBeArr.forEach({$0.isSelected = false})
        self.dataBDDic.forEach({$0.forEach({$0.isSelected = false})})
    }
    
    //点击美颜滤镜
    private func beautyItemViewClick(index:Int) {
        if curFilterIndex == 0 {
            //相芯
            self.faceUnityItemViewClick(index: index)
        }else if curFilterIndex == 2 {
            //火山
            self.volcUnityItemViewClick(index: index)
        }
    }
}

// MARK: - 相芯美颜
extension BeautifyFilter{
    
    private func setupFaceUnityView()  {

        guard let _bottomView = self.bottomView else { return }
        guard let _curSlider = self.curSlider else { return }
        _curSlider.isHidden = false
        _bottomView.isHidden = false
        _bottomView.removeAllSubviews()
        
        self.setEnableAgoraBeauty(isEnabled: false)
        self.setEnableVolcUnity(isEnabled: false)
        self.setEnableFaceUnity(isEnabled: true)
        

        bottomView?.snp.updateConstraints({ make in
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+118)
        })
        
        let bottomContentView = UIView()
        bottomView?.addSubview(bottomContentView)
        bottomContentView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        
        let subView = SubCellView()
        oldDataBe.subView = subView
        subView.setupBeautyFuncModel(oldDataBe)
        bottomContentView.addSubview(subView)
        subView.snp.makeConstraints { make in
            make.centerY.equalToSuperview()
            make.left.equalToSuperview().offset(4)
            make.size.equalTo(CGSize(width: 72, height: 70))
        }
        subView.clickView {[weak self] sender in
            self?.beautyItemViewClick(index: -1)
        }
        
        let scrollView = UIScrollView()
        scrollView.showsHorizontalScrollIndicator = false
        bottomContentView.addSubview(scrollView)
        scrollView.snp.makeConstraints { make in
            make.top.right.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.height.equalTo(118)
        }
        
        let contentV = UIView()
        scrollView.addSubview(contentV)
        contentV.snp.makeConstraints { (make) in
            make.left.right.top.bottom.height.equalToSuperview()
            make.width.equalTo(80*dataBeArr.count)
        }
        
        for i in 0..<dataBeArr.count {
            let itemModel = dataBeArr[i]
            let itemView = SubCellView()
            itemModel.subView = itemView
            itemModel.tag = i
            itemView.tag = i
            itemView.setupBeautyFuncModel(itemModel)
            contentV.addSubview(itemView)
            itemView.snp.makeConstraints { (make) in
                make.centerY.equalToSuperview()
                make.left.equalToSuperview().offset(i*(72+8)+8)
                make.size.equalTo(CGSize(width: 72, height: 70))
            }
            itemView.clickView {[weak self] sender in
                UINotificationFeedbackGenerator().notificationOccurred(.success)
                self?.beautyItemViewClick(index: sender.tag)
            }
        }
        
        let maskView = UIView()
        bottomContentView.addSubview(maskView)
        maskView.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.width.equalTo(16)
        }
    
        let c0 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(0.0)
        let c1 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(1)
        maskView.colorGradient(color0: c0, color1: c1, point0: CGPoint(x: 1, y: 0.5), point1: CGPoint(x: 0, y: 0.5))
        _bottomView.rectCorner(corner: [.topLeft,.topRight], radii:  CGSize(width: 12, height: 12))
        self.curSlider?.isHidden = true
    }
    
    func faceUnityItemViewClick(index:Int)  {
        if index == -1 {
            for item in dataBeArr {
                item.isSelected =  false
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }
            UINotificationFeedbackGenerator().notificationOccurred(.success)
            self.setupfaceUnityOriginalClick()
        }else{
            self.setupfaceUnityItem(index: index)
        }
    }
    
    func setupfaceUnityItem(index:Int)  {
        self.curSelectModel = self.dataBeArr[index]
        for item in dataBeArr {
            if item.tag == self.curSelectModel?.tag {
                item.isSelected = true
                self.curSlider?.isHidden = false
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }else {
                item.isSelected = false
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }
        }
        
        let currentValue = self.curSelectModel?.paramModel.currentValue ?? 0
        let exFactor = self.curSelectModel?.paramModel.exFactor ?? 1
        self.curSlider?.value = Float(currentValue/exFactor)
        self.setEnableFaceUnity(isEnabled: true)
    }
    
    func setupfaceUnityOriginalClick()  {
        self.curSlider?.isHidden = true
        self.setEnableFaceUnity(isEnabled: false)
    }
}

// MARK: - 火山美颜
extension BeautifyFilter:JXSegmentedViewDelegate{
    
    func setupVolcEngineView() {
        
        guard let _bottomView = self.bottomView else { return }
        guard let _curSlider = self.curSlider else { return }
        _curSlider.isHidden = false
        _bottomView.isHidden = false
        _bottomView.removeAllSubviews()
        
        self.setEnableAgoraBeauty(isEnabled: false)
        self.setEnableFaceUnity(isEnabled: false)
        self.setEnableVolcUnity(isEnabled: true)
        
        bottomView?.snp.updateConstraints({ make in
            make.height.equalTo(SCREEN_BOTTOM_HEIGHT+118+48)
        })
        
        let tabView = UIView()
        bottomView?.addSubview(tabView)
        tabView.snp.makeConstraints { make in
            make.top.equalToSuperview()
            make.left.right.equalToSuperview()
            make.height.equalTo(48)
        }
                
        segmentedView = JXSegmentedView()
        segmentedView.delegate = self
        tabView.addSubview(self.segmentedView)
        segmentedView.snp.makeConstraints { make in
            make.top.left.right.bottom.equalToSuperview()
        }
        
        segmentedDataSource = JXSegmentedTitleDataSource()
        let funcKey = self.dataBDDic.compactMap({$0.first?.paramModel.funcKey.localized})
        segmentedDataSource.titles = funcKey
        segmentedDataSource.itemSpacing = 16+8
        segmentedDataSource.titleSelectedColor = SCREEN_FFF_COLOR.hexColor()
        segmentedDataSource.titleNormalColor = SCREEN_FFF_COLOR.hexColor()
        segmentedDataSource.titleNormalFont = UIFont.systemFont(ofSize: 15)
        segmentedDataSource.isTitleColorGradientEnabled = true

        segmentedView.dataSource = self.segmentedDataSource
        
        let indicator = JXSegmentedIndicatorLineView()
        indicator.indicatorWidth = 24
        indicator.indicatorColor = SCREEN_FFF_COLOR.hexColor()
        segmentedView.indicators = [indicator]
        
        let bottomContentView = UIView()
        self.bottomContentView = bottomContentView
        bottomView?.addSubview(bottomContentView)
        bottomContentView.snp.makeConstraints { make in
            make.left.right.bottom.equalToSuperview()
            make.top.equalTo(tabView.snp.bottom)
        }
        
        let bottomContentTopLine = UIView()
        bottomContentTopLine.backgroundColor = SCREEN_FFF_COLOR.hexColor(alpha: 0.18)
        tabView.addSubview(bottomContentTopLine)
        bottomContentTopLine.snp.makeConstraints { make in
            make.left.right.bottom.equalToSuperview()
            make.height.equalTo(SCREEN_PIXEL)
        }
        
        self.setupTabBottomContentView(bottomContentView,0)
        self.curSlider?.isHidden = true
        _bottomView.rectCorner(corner: [.topLeft,.topRight], radii:  CGSize(width: 12, height: 12))
    }
    
    func setupTabBottomContentView(_ bottomContentView:UIView,_ tabIndex:Int) {
        
        bottomContentView.removeAllSubviews()
        let dataBDDicArr = dataBDDic[tabIndex]
        let subView = SubCellView()
        oldDataBe.subView = subView
        subView.setupBeautyFuncModel(oldDataBe)
        bottomContentView.addSubview(subView)
        subView.snp.makeConstraints { make in
            make.centerY.equalToSuperview()
            make.left.equalToSuperview().offset(4)
            make.size.equalTo(CGSize(width: 72, height: 70))
        }
        subView.clickView {[weak self] sender in
            self?.beautyItemViewClick(index: -1)
        }
        
        let scrollView = UIScrollView()
        scrollView.showsHorizontalScrollIndicator = false
        bottomContentView.addSubview(scrollView)
        scrollView.snp.makeConstraints { make in
            make.top.right.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.height.equalTo(118)
        }
        
        let contentV = UIView()
        scrollView.addSubview(contentV)
        contentV.snp.makeConstraints { (make) in
            make.left.right.top.bottom.height.equalToSuperview()
            make.width.equalTo(80*dataBDDicArr.count)
        }
        
        for i in 0..<dataBDDicArr.count {
            let itemModel = dataBDDicArr[i]
            let itemView = SubCellView()
            itemModel.subView = itemView
            itemModel.tag = i
            itemView.tag = i
            itemView.setupBeautyFuncModel(itemModel)
            contentV.addSubview(itemView)
            itemView.snp.makeConstraints { (make) in
                make.centerY.equalToSuperview()
                make.left.equalToSuperview().offset(i*(72+8)+8)
                make.size.equalTo(CGSize(width: 72, height: 70))
            }
            itemView.clickView {[weak self] sender in
                UINotificationFeedbackGenerator().notificationOccurred(.success)
                self?.beautyItemViewClick(index: sender.tag)
            }
            if itemModel.isSelected {
                self.setupVolcUnityItem(index: i)
            }
        }
        
        let maskView = UIView()
        bottomContentView.addSubview(maskView)
        maskView.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalTo(subView.snp.right)
            make.width.equalTo(16)
        }
    
        let c0 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(0.0)
        let c1 = SCREEN_MASK_COLOR.hexColor().withAlphaComponent(1)
        maskView.colorGradient(color0: c0, color1: c1, point0: CGPoint(x: 1, y: 0.5), point1: CGPoint(x: 0, y: 0.5))
    }
    
    func volcUnityItemViewClick(index:Int)  {
        let dataBDDicArr = self.dataBDDic[self.segmentedView.selectedIndex]
        if index == -1 {
            for item in dataBDDicArr {
                item.isSelected =  false
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }
            UINotificationFeedbackGenerator().notificationOccurred(.success)
            self.setupVolcUnityOriginalClick()
        }else{
            self.setupVolcUnityItem(index: index)
        }
    }
    
    func setupVolcUnityItem(index:Int)  {
        let dataBDDicArr = self.dataBDDic[self.segmentedView.selectedIndex]
        self.curSelectModel = dataBDDicArr[index]
        for item in dataBDDicArr {
            if item.tag == self.curSelectModel?.tag {
                item.isSelected = true
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }else {
                item.isSelected = false
                if let itemView = item.subView as? SubCellView { itemView.setupBeautyFuncModel(item) }
            }
        }
        
        let currentValue = self.curSelectModel?.paramModel.currentValue ?? 0
        let exFactor = self.curSelectModel?.paramModel.exFactor ?? 1
        self.curSlider?.value = Float(currentValue/exFactor)
        if self.curSelectModel?.paramModel.funcKey == "oneKeyDress" || self.curSelectModel?.paramModel.funcKey == "sticker" {
            self.curSlider?.isHidden = true
        }else{
            self.curSlider?.isHidden = false
        }
          
        self.setEnableVolcUnity(isEnabled: true)
        
        guard let _curSelectModel = self.curSelectModel else { return }
        if _curSelectModel.paramModel.funcKey == "oneKeyDress" {
            self.setBDOneKeyDressUp(model: _curSelectModel)
        }else if _curSelectModel.paramModel.funcKey == "sticker" {
            self.setStickerDressUp(model: _curSelectModel)
        }
    }
    
    func setupVolcUnityOriginalClick()  {
        self.curSlider?.isHidden = true
        self.dataBeArr.forEach({$0.isSelected = false})
        self.dataBDDic.forEach({$0.forEach({$0.isSelected = false})})
        self.setEnableVolcUnity(isEnabled: false)
    }
    
    func segmentedView(_ segmentedView: JXSegmentedView, didSelectedItemAt index: Int) {
        guard let _bottomContentView = self.bottomContentView else { return  }
        self.setupTabBottomContentView(_bottomContentView,index)
    }
    
}

// MARK: - ASValueTrackingSliderDataSource ASValueTrackingSliderDelegate
extension BeautifyFilter:ASValueTrackingSliderDataSource,ASValueTrackingSliderDelegate{
    func slider(_ slider: ASValueTrackingSlider!, stringForValue value: Float) -> String! {
        
        guard let _curSelectModel = self.curSelectModel else { return "" }
        if self.curFilterIndex == 0 {
            //相芯
            let curSelectValue:CGFloat = CGFloat(value)
            let exFactor = _curSelectModel.paramModel.exFactor
            _curSelectModel.paramModel.value = curSelectValue*exFactor//乘以系数，系数默认为1
            _curSelectModel.paramModel.currentValue = _curSelectModel.paramModel.value
            if _curSelectModel.abilityId == 105 || _curSelectModel.abilityId == 106 {//瘦脸和大眼
                self.setFaceUnityBeautyBody(_curSelectModel)
            }else{
                self.setFaceUnityBeauty(_curSelectModel)
            }
        }else if self.curFilterIndex == 2 {
            //火山
            let curSelectValue:CGFloat = CGFloat(value)
            let exFactor = _curSelectModel.paramModel.exFactor
            _curSelectModel.paramModel.value = curSelectValue*exFactor//乘以系数，系数默认为1
            _curSelectModel.paramModel.currentValue = _curSelectModel.paramModel.value
            if _curSelectModel.paramModel.funcKey == "skinCare" {
                self.setByteDanceBeauty(model: _curSelectModel)
            }else if _curSelectModel.paramModel.funcKey == "microShap" {
                self.setByteDanceBeautyBody(model: _curSelectModel)
            }else if _curSelectModel.paramModel.funcKey == "dressUp" {
                self.setByteDanceDressUp(model: _curSelectModel)
            }
        }
        return "\(Int(value*100))"
    }
    
    func sliderWillDisplayPopUpView(_ slider: ASValueTrackingSlider!) {
        
    }
}


extension BeautifyFilter{
    //选择对应的美颜模块
    private func filterItemViewClick(index:Int)  {
        UINotificationFeedbackGenerator().notificationOccurred(.success)
        for i in 0..<filterList.count {
            let filterItem = filterList[i]
            if filterItem.tag == index {
                if let filterButton = filterItem.subView as? SubButton {
                    filterButton.isSelected = !filterButton.isSelected
                    if filterButton.isSelected == false{
                        self.curFilterIndex = -1
                        self.closeBottomView()
                    }else if filterButton.isSelected && filterButton.tag == 0{
                        self.curFilterIndex = 0
                        self.closeBottomView()
                        self.setupFaceUnityView()
                    }else if filterButton.isSelected && filterButton.tag == 1{
                        self.curFilterIndex = 1
                        self.closeBottomView()
                        self.setupAgoraView()
                    }else if filterButton.isSelected && filterButton.tag == 2{
                        self.curFilterIndex = 2
                        self.closeBottomView()
                        self.setupVolcEngineView()
                    }
                }
            }else{
                if let filterButton = filterItem.subView as? SubButton {
                    filterButton.isSelected = false
                }
            }
        }
    }
}
