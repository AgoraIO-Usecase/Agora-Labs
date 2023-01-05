//
//  AGContrastView.swift
//  AgoraLabs
//
//  Created by LiaoChenliang on 2022/12/21.
//  Copyright © 2022 Agora Corp. All rights reserved.
//

enum AGContrastPositionType:Int {
    case top
    case bottom
    case inside
    case outside
}

class AGContrastView: UIView {
    public var title:String?{
        didSet{
            titleBtn.setTitle(title, for: .normal)
        }
    }
    
    public var selectTitle:String?{
        didSet{
            titleBtn.setTitle(selectTitle, for: .selected)
        }
    }
    
    public var titleSelected:Bool = false{
        didSet{
            titleBtn.isSelected = titleSelected
        }
    }
    
    public var showSubTitle:Bool = false {
        didSet{
            subTitleBtn.isHidden = !showSubTitle
        }
    }
    
    public var subTitle:String?{
        didSet{
            subTitleBtn.setTitle(subTitle, for: .normal)
        }
    }
    
    public var showView = UIView()
    
    private var positionType:AGContrastPositionType = .top
    
    private lazy var titleBtn: UIButton = {
        let _titleBtn = UIButton()
        _titleBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        _titleBtn.setBackgroundImage("b4b6b8".hexColor(alpha: 1).image(), for: .normal)
        _titleBtn.setBackgroundImage("5eb5ff".hexColor(alpha: 1).image(), for: .selected)
        _titleBtn.contentEdgeInsets = UIEdgeInsets(top: 4, left: 20, bottom: 4, right: 20)
        _titleBtn.cornerRadius = 13
        _titleBtn.masksToBounds = true
        _titleBtn.isUserInteractionEnabled = false
        return _titleBtn
    }()
    
    private lazy var subTitleBtn: UIButton = {
        let _subTitleBtn = UIButton()
        _subTitleBtn.titleLabel?.font = UIFont.systemFont(ofSize: 13)
        _subTitleBtn.setTitleShadowColor(UIColor.black, for: .normal)
        _subTitleBtn.titleLabel?.layer.shadowOpacity = 0.5
        _subTitleBtn.titleLabel?.layer.shadowColor = UIColor.black.cgColor
        _subTitleBtn.titleLabel?.layer.shadowOffset = CGSize(width: 1, height: 1)
        _subTitleBtn.titleLabel?.layer.shadowRadius = 1
        return _subTitleBtn
    }()
    
    init(type:AGContrastPositionType,frame:CGRect) {
        super.init(frame: frame)
        self.positionType = type
        initUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func initUI() {
        self.addSubview(showView)
        self.addSubview(titleBtn)
        self.addSubview(subTitleBtn)
        
        showView.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
        
        self.reloadPosition(type: positionType)
    }
    
    public func reloadPosition(type:AGContrastPositionType) {
        self.positionType = type
        
        if self.positionType == .top {
            titleBtn.snp.remakeConstraints { make in
                make.bottom.equalToSuperview().offset(-8)
                make.centerX.equalToSuperview()
            }
            subTitleBtn.snp.remakeConstraints { make in
                make.right.equalToSuperview().offset(-8)
                make.centerY.equalTo(titleBtn)
            }
        }else if self.positionType == .bottom {
            titleBtn.snp.remakeConstraints { make in
                make.top.equalToSuperview().offset(8)
                make.centerX.equalToSuperview()
            }
            subTitleBtn.snp.remakeConstraints { make in
                make.right.equalToSuperview().offset(-8)
                make.centerY.equalTo(titleBtn)
            }
        }else if self.positionType == .inside {
            titleBtn.snp.remakeConstraints { make in
                make.top.equalToSuperview().offset(SCREEN_NAV_FULL_HEIGHT+8)
                make.centerX.equalToSuperview()
            }
            subTitleBtn.snp.remakeConstraints { make in
                make.right.equalToSuperview().offset(-8)
                make.centerY.equalTo(titleBtn)
            }
        }else if self.positionType == .outside {
            titleBtn.snp.remakeConstraints { make in
                make.top.equalToSuperview().offset(8)
                make.centerX.equalToSuperview()
            }
            subTitleBtn.snp.remakeConstraints { make in
                make.bottom.equalToSuperview().offset(-8)
                make.centerX.equalToSuperview()
            }
        }
    }
}
