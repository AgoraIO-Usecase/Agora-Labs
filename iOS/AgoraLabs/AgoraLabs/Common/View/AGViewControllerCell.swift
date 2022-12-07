//
//  AGViewControllerCell.swift
//  Agora Labs
//
//  Created by LiaoChenliang on 2022/12/1.
//

import UIKit

class AGViewControllerCell: UICollectionViewCell {
    
    lazy var subView: UIView = {
        let _subView = UIView()
        _subView.backgroundColor = UIColor.white
        _subView.layer.masksToBounds = true
        _subView.layer.cornerRadius = 8
        return _subView
    }()
    
    lazy var titleLab: UILabel = {
        let _titleLab = UILabel()
        _titleLab.textColor = SCREEN_TEXT_COLOR.hexColor(alpha: 0.6)
        _titleLab.font = UIFont.systemFont(ofSize: 15)
        _titleLab.textAlignment = .center
        return _titleLab
    }()
    
    
    lazy var icon: UIImageView = {
        let _icon = UIImageView()
//        _icon.backgroundColor = SCREEN_CCC_COLOR.hexColor()
        return _icon
    }()
    
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        initUI()
    }
    
    func initUI() {
        
        contentView.addSubview(subView)
        contentView.addSubview(icon)
        contentView.addSubview(titleLab)
        
        subView.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalToSuperview()
            make.right.equalToSuperview()
        }
        
        icon.snp.makeConstraints { make in
            make.centerX.equalToSuperview()
            make.top.equalTo(subView.snp.top).offset(16)
            make.size.equalTo(CGSize(width: 32, height: 32))
        }
        
        titleLab.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(icon.snp.bottom).offset(8)
        }
    }
        
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

}
