//
//  AGBasisViewController.swift
//  Agora Labs
//
//  Created by LiaoChenliang on 2022/11/30.
//

import UIKit
import SnapKit

protocol AGViewDelegate: NSObjectProtocol{
    func collectionViewClick(page:Int,indexPath:IndexPath)
}

class AGBasisViewController: BaseViewController{
   
    weak var delegate:AGViewDelegate?
    
    public var pageIndex = 0
    public var dataList = [[String:Any]]()
    
    private let AGViewControllerCellID = "AGViewControllerCell"
    private let AGViewControllerHeadID = "AGViewControllerHead"
    
    lazy var layout: UICollectionViewFlowLayout = {
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: (SCREEN_WIDTH - 16*3) / 2 , height: 92)
        layout.estimatedItemSize = CGSize(width: 0, height: 0)
        layout.minimumInteritemSpacing = 16
        layout.minimumLineSpacing = 16
        if dataList.count <= 1 {
            layout.headerReferenceSize = CGSize(width: 0, height: 0)
        }else{
            layout.headerReferenceSize = CGSize(width: 0, height: 44)
        }
        return layout
    }()
    
    lazy var collectionView: UICollectionView = {
        let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: layout)
        collectionView.backgroundColor = UIColor.clear
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.contentInsetAdjustmentBehavior = .never
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.showsVerticalScrollIndicator = false
        collectionView.register(AGViewControllerCell.self, forCellWithReuseIdentifier: AGViewControllerCellID)
        collectionView.register(AGViewControllerHead.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: AGViewControllerHeadID)
                
        return collectionView
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        self.view.backgroundColor = .clear
        self.view.addSubview(collectionView)
        collectionView.snp.makeConstraints { make in
            make.top.equalToSuperview().offset(dataList.count <= 1 ?0:-8)
            make.bottom.equalToSuperview()
            make.left.equalToSuperview().offset(16)
            make.right.equalToSuperview().offset(-16)
        }
        
    }

}

extension AGBasisViewController: UICollectionViewDataSource, UICollectionViewDelegate  {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return dataList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if dataList.count <= 1 {
            return UICollectionReusableView()
        }else{
            if kind == UICollectionView.elementKindSectionHeader {
                guard let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: AGViewControllerHeadID, for: indexPath) as? AGViewControllerHead else {
                    fatalError()
                }
                let cellName = dataList[indexPath.section]["key"] as? String
                headerView.titleLab.text = cellName?.localized
                return headerView
                
            }
        }
        return UICollectionReusableView()
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        let contentList = dataList[section]["content"] as? [String]
        return contentList?.count ?? 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: AGViewControllerCellID, for: indexPath)
        if let _cell = cell as? AGViewControllerCell {
            let contentList = dataList[indexPath.section]["content"] as? [String]
            _cell.titleLab.text = contentList?[indexPath.row].localized
            _cell.icon.image = UIImage(named: contentList?[indexPath.row].removeAllSapce ?? "")
        }
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        self.delegate?.collectionViewClick(page: pageIndex, indexPath: indexPath)
    }
}

