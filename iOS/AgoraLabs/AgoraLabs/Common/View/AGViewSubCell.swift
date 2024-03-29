//
//  AGViewSubCell.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/6.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

class SubCellModel {
    var name:String = ""
    var bgImageName:String = ""
    var tag:Int = 0
    var value:Any?
    var isSelected:Bool = false     //是否开启
    var isEnabled:Bool = true       //是否可用
    var subView:UIView?
    init(name:String,tag:Int,value:Any = "",bgImageName:String = "",isSelected:Bool = false) {
        self.name = name
        self.tag = tag
        self.value = value
        self.bgImageName = bgImageName
        self.isSelected = isSelected
    }
}

class SubCellView: UIView {
    
    lazy var titleLab: UILabel = {
        let _titleLab = UILabel()
        _titleLab.textColor = UIColor.white
        _titleLab.font = UIFont.systemFont(ofSize: 12)
        _titleLab.adjustsFontSizeToFitWidth = true
        _titleLab.textAlignment = .center
        _titleLab.numberOfLines = 2
        return _titleLab
    }()
    
    lazy var iconView: UIView = {
        let _iconView = UIView()
        _iconView.backgroundColor = UIColor(hex: "323241")
        _iconView.cornerRadius = 24
        _iconView.masksToBounds = true
        _iconView.borderWidth = 1
        _iconView.borderColor = UIColor(hex: "3392ff").cgColor
        return _iconView
    }()
    
    lazy var iconImage: UIImageView = {
        let _iconImage = UIImageView()
        _iconImage.contentMode = .scaleAspectFill
        _iconImage.masksToBounds = true
        return _iconImage
    }()
    
    lazy var bgImage: UIImageView = {
        let _bgImage = UIImageView()
        _bgImage.contentMode = .scaleAspectFill
        _bgImage.masksToBounds = true
        _bgImage.cornerRadius = 24
        return _bgImage
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func initUI() {
        
        self.addSubview(iconView)
        self.addSubview(titleLab)
        
        iconView.snp.makeConstraints { make in
            make.centerX.equalToSuperview()
            make.top.equalToSuperview()
            make.size.equalTo(CGSize(width: 48, height: 48))
        }
        
        titleLab.snp.makeConstraints { make in
            make.left.right.equalToSuperview()
            make.top.equalTo(iconView.snp.bottom)
            make.bottom.equalToSuperview()
        }
        
        iconView.addSubview(bgImage)
        bgImage.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        
        iconView.addSubview(iconImage)
        iconImage.snp.makeConstraints { make in
            make.center.equalToSuperview()
            make.size.equalTo(CGSize(width: 32, height: 32))
        }
    }
        
    func setupSubCellModel(_ model:SubCellModel) {
        titleLab.text = model.name.localized
        if model.bgImageName == "" {
            iconImage.isHidden = false
            iconImage.image = UIImage(named: model.name)
        }else{
            iconImage.isHidden = true
            bgImage.image = UIImage(named: model.bgImageName)
        }
        if model.isEnabled == false {
            iconView.borderWidth = 1
            iconView.borderColor = UIColor(hex: "ffffff", alpha: 0.16).cgColor
            self.alpha = 0.3
        }else {
            if model.isSelected {
                iconView.borderWidth = 2
                iconView.borderColor = UIColor(hex: "91E2FF").cgColor
                self.alpha = 1
            }else{
                iconView.borderWidth = 1
                iconView.borderColor = UIColor(hex: "ffffff", alpha: 0.16).cgColor
                self.alpha = 0.6
            }
        }
    }
    
    
    func setupBeautyFuncModel(_ model:BeautyFuncModel?) {
        titleLab.text = model?.abilityName.localized
        iconImage.image = UIImage(named: model?.abilityIcon ?? "")
        iconImage.isHidden = false
        if model?.isSelected == true {
            iconView.borderWidth = 2
            iconView.borderColor = UIColor(hex: "91E2FF").cgColor
            self.alpha = 1
        }else{
            iconView.borderWidth = 1
            iconView.borderColor = UIColor(hex: "ffffff", alpha: 0.16).cgColor
            self.alpha = 0.6
        }
    }
    
}

class SubButton: UIButton {
    override var isSelected: Bool{
        didSet{
            if isSelected {
                self.borderWidth = 2
                self.borderColor = UIColor(hex: "91E2FF").cgColor
                self.alpha = alphaSelected
            }else{
                self.borderWidth = 1
                self.borderColor = UIColor(hex: "ffffff", alpha: 0.16).cgColor
                self.alpha = alphaNormal
            }
        }
    }
    
    var alphaNormal = 1.0
    var alphaSelected = 1.0
    
    init(alphaNormal:CGFloat) {
        super.init(frame: CGRect.zero)
        self.borderWidth = 1
        self.borderColor = UIColor(hex: "ffffff", alpha: 0.16).cgColor
        self.alphaNormal = alphaNormal
        self.alpha = alphaNormal
    }
    
    func addBlurEffect(style:UIBlurEffect.Style) {
        let blurEffect = UIBlurEffect(style: style)
        let visualEffectView = UIVisualEffectView(effect: blurEffect)
        addSubview(visualEffectView)
        visualEffectView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        visualEffectView.layer.masksToBounds = true
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
