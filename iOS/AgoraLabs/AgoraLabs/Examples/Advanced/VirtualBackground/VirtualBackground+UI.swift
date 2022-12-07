//
//  VirtualBackground+UI.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import UIKit
import ZLPhotoBrowser
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
        let backgroundColor = "#2a2a3b"
        let bottomView = UIView()
        bottomView.backgroundColor = backgroundColor.hexColor()
        self.view.addSubview(bottomView)
        bottomView.snp.makeConstraints { make in
            make.bottom.left.right.equalToSuperview()
            make.height.equalTo(self.view.safeAreaInsets.bottom+118)
        }
        
        
        let subView = SubCellView()
        originalModel.subView = subView
        subView.setup(model: originalModel)
        bottomView.addSubview(subView)
        subView.snp.makeConstraints { make in
            make.centerY.equalToSuperview()
            make.left.equalToSuperview().offset(4)
            make.size.equalTo(CGSize(width: 72, height: 70))
        }
        subView.clickView {[weak self] sender in
            UINotificationFeedbackGenerator().notificationOccurred(.success)
            self?.originalViewClick()
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
            itemView.setup(model: itemModel)
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
    
        let c0 = backgroundColor.hexColor().withAlphaComponent(0.0)
        let c1 = backgroundColor.hexColor().withAlphaComponent(1)
        self.colorGradient(color0: c0, color1: c1, point0: CGPoint(x: 1, y: 0.5), point1: CGPoint(x: 0, y: 0.5), subView: maskView)
    
    }
    
    func colorGradient(color0:UIColor,color1:UIColor,point0:CGPoint,point1:CGPoint,subView:UIView)  {
        subView.layoutIfNeeded()
        let gradient = CAGradientLayer()
        let colors = [color0.cgColor, color1.cgColor]
        gradient.startPoint = point0
        gradient.endPoint = point1
        gradient.colors = colors.compactMap { $0 }
        gradient.locations = [NSNumber(value: 0.0), NSNumber(value: 1.0)]
        gradient.frame = CGRect(x: 0, y: 0, width: subView.bounds.size.width, height: subView.bounds.size.height)
        subView.layer.insertSublayer(gradient, at: 0)
    }
    
}

extension VirtualBackground {
  
    private func itemViewClick(index:Int) {
        let model:SubCellModel = self.itemModelList[index]
        for item in itemModelList {
            if item.tag == model.tag {
                item.open = !item.open
                item.subView?.setup(model: item)
            }else {
                item.open = false
                item.subView?.setup(model: item)
            }
        }
        if model.name == "Customize Image" {
            self.showImagePickerVC()
        }else{
            self.itemViewClick(model: model)
        }
        
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
    
    func configAddLocalImg(_ imagePath : String) {
        if let model = self.itemModelList.filter({$0.name == "Customize Image"}).first {
            model.value = imagePath
            model.open = true
            self.itemViewClick(model: model)
        }
    }
}
