//
//  AGViewControllerHead.swift
//  Agora Labs
//
//  Created by LiaoChenliang on 2022/12/1.
//

import UIKit

class AGViewControllerHead: UICollectionReusableView {
        
    lazy var titleLab: UILabel = {
        let _titleLab = UILabel()
        _titleLab.textColor = SCREEN_TEXT_COLOR.hexColor(alpha: 0.6)
        _titleLab.font = UIFont.systemFont(ofSize: 18)
        _titleLab.numberOfLines = 0
        return _titleLab
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.addSubview(titleLab)
        titleLab.snp.makeConstraints { make in
            make.top.equalToSuperview().offset(16)
            make.bottom.equalToSuperview().offset(-8)
            make.right.equalToSuperview()
            make.left.equalToSuperview().offset(10)
        }
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
}
