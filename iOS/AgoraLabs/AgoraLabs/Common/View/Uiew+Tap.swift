//
//  Uiew+Tap.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import Foundation
import UIKit
 
 
 
typealias clickViewEventBlock = (_ sender: UIView)->()
 
//作用相当于给view加一个属性：手势的点击事件
private var AssociatedBlockHandle: clickViewEventBlock?

//作用相当于给view加一个属性：手势
private var AssociatedTapHandle: UITapGestureRecognizer?
 
extension UIView {
 
    @objc private func clickViewAction(sender:UITapGestureRecognizer){
 
        //取属性：手势
       guard let block = objc_getAssociatedObject(self, &AssociatedTapHandle) as? clickViewEventBlock else { return }
 
       block(sender.view!)
 
    }
    
    //外部调用
    func clickView(action: clickViewEventBlock?) {
 
        //取属性：手势的点击事件
        var tap = objc_getAssociatedObject(self, &AssociatedBlockHandle)
        
        if tap == nil {
            
            
            tap = UITapGestureRecognizer(target: self, action: #selector(clickViewAction(sender:)))
            
            self.addGestureRecognizer(tap as! UIGestureRecognizer)
            
            //设置属性：手势的点击事件
            objc_setAssociatedObject(self, &AssociatedBlockHandle, tap, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN_NONATOMIC)
 
        }
   
        //设置属性：手势
        objc_setAssociatedObject(self, &AssociatedTapHandle, action, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN_NONATOMIC)
 
    }
    
}
