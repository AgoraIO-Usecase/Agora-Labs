//
//  ViewController.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

import UIKit
import AgoraRtcKit
import JXSegmentedView
import FDFullscreenPopGesture

struct MenuSection {
    var name: String
    var rows:[MenuItem]
}

struct MenuItem {
    var name: String
    var entry: String = "EntryViewController"
    var storyboard: String = "Main"
    var controller: String
    var note: String = ""
}

class ViewController: UIViewController {

    private let titles: [String] =  [
        "Video".localized,
        "Audio".localized,
        "Network".localized,
        "Algorithms".localized
    ]
    
    private let demoMap:[[[String:Any]]] = [
        [
            [
                "key":"Effects",
                "content":[
                    "Virtual Background",
                    "Beautify Filter"
                ]
            ],
            [
                "key":"Image Quality",
                "content":[
                    "Reduce Noise",
                    "Dim Environment",
                    "Sharpen",
                    "Enance Saturation",
                    "PVC",
                    "ROI",
                    "Resolution",
                    "Image",
                    "HDR"
                ]
            ]
        ],
        [
           [
               "key":"",
               "content":[
                   "Noise Suppression",
                   "AI AEC",
                   "Spacial Audio",
                   "Speach to Text"
               ]
           ]
       ],
        [
            [
                "key":"",
                "content":[
                    "Multipath",
                    "Weak Net"
                ]
            ]
        ],
        [
            [
                "key":"",
                "content":[
                    "Facial Capture",
                    "Motion Capture",
                    "Avatar"
                ]
            ]
        ]
    ]
    
    var doneMap = ["Virtual Background","Beautify Filter",
                   "Resolution","ROI","PVC"]
    
    private var segmentedDataSource: JXSegmentedTitleDataSource!
    private var segmentedView: JXSegmentedView!
    private var contentScrollView: UIScrollView!
    private var listVCArray = [AGViewController]()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("AgoraRtcKit - \(AgoraRtcEngineKit.getSdkVersion())")
        view.backgroundColor = "#eff4ff".hexColor()
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.isTranslucent = true
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        self.fd_prefersNavigationBarHidden = true
        
        segmentedView = JXSegmentedView()
        
        segmentedDataSource = JXSegmentedTitleDataSource()
        segmentedDataSource.isTitleColorGradientEnabled = true
        segmentedDataSource.titleSelectedColor = SCREEN_FFF_COLOR.hexColor()
        segmentedDataSource.titleNormalColor = SCREEN_TEXT_COLOR.hexColor(alpha: 0.6)
        segmentedDataSource.titleNormalFont = UIFont.systemFont(ofSize: 15)
        segmentedView.dataSource = segmentedDataSource
        
        let indicator = JXSegmentedIndicatorBackgroundView()
        indicator.clipsToBounds = true
        indicator.indicatorHeight = 36
        let gradientView = JXSegmentedComponetGradientView()
        gradientView.gradientLayer.endPoint = CGPoint(x: 1, y: 0.5)
        gradientView.gradientLayer.colors = ["#91E2FF".hexColor().cgColor, "#2787FF".hexColor().cgColor]
        gradientView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        indicator.addSubview(gradientView)
        segmentedView.indicators = [indicator]
        view.addSubview(segmentedView)

        contentScrollView = UIScrollView()
        contentScrollView.isPagingEnabled = true
        contentScrollView.showsVerticalScrollIndicator = false
        contentScrollView.showsHorizontalScrollIndicator = false
        contentScrollView.scrollsToTop = false
        contentScrollView.bounces = false
        contentScrollView.contentInsetAdjustmentBehavior = .never
        view.addSubview(contentScrollView)
        
        segmentedView.contentScrollView = contentScrollView

        reloadData()
    }
    
    @objc func reloadData() {
        segmentedDataSource.titles = self.titles
        segmentedView.defaultSelectedIndex = 0
        segmentedView.reloadData()
        
        for vc in listVCArray {
            vc.view.removeFromSuperview()
        }
        listVCArray.removeAll()
        
        for index in 0..<segmentedDataSource.titles.count {
            let vc = AGBasisViewController()
            vc.pageIndex = index
            vc.delegate = self
            vc.dataList = demoMap[index]
            vc.doneMap = doneMap
            contentScrollView.addSubview(vc.view)
            listVCArray.append(vc)
        }
        
        view.setNeedsLayout()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        segmentedView.frame = CGRect(x: 0, y: SCREEN_STATUS_HEIGHT, width: view.bounds.size.width, height: SCREEN_HEAD_HEIGHT)
        contentScrollView.frame = CGRect(x: 0, y: SCREEN_HEAD_HEIGHT+SCREEN_STATUS_HEIGHT, width: view.bounds.size.width, height: view.bounds.size.height - SCREEN_HEAD_HEIGHT - SCREEN_STATUS_HEIGHT)
        contentScrollView.contentSize = CGSize(width: contentScrollView.bounds.size.width*CGFloat(segmentedDataSource.dataSource.count), height: contentScrollView.bounds.size.height)
        for (index, vc) in listVCArray.enumerated() {
            vc.view.frame = CGRect(x: contentScrollView.bounds.size.width*CGFloat(index), y: 0, width: contentScrollView.bounds.size.width, height: contentScrollView.bounds.size.height)
        }
    }
    
}

extension ViewController: AGViewDelegate{
    func collectionViewClick(page: Int, indexPath: IndexPath) {
        guard let dataList = demoMap[page][indexPath.section]["content"] as? [String] else { return }
        let name = dataList[indexPath.row]
        if doneMap.contains(name) {
            let menuItem =  MenuItem(name: name, storyboard: name.removeAllSapce, controller: name.removeAllSapce)
            let storyBoard: UIStoryboard = UIStoryboard(name: menuItem.storyboard, bundle: nil)
            let entryViewController:UIViewController = storyBoard.instantiateViewController(withIdentifier: menuItem.storyboard)
            entryViewController.fd_prefersNavigationBarHidden = false
            entryViewController.fd_interactivePopDisabled = true
            self.navigationController?.pushViewController(entryViewController, animated: true)

        }else{
            AGHUD.showInfo(info: "该功能暂不支持，敬请期待")
        }
    }
}

