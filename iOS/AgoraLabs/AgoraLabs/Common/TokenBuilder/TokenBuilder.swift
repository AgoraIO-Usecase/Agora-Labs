//
//  TokenBuilder.swift
//  AgoraEducation
//
//  Created by ZYP on 2021/10/19.
//  Copyright © 2021 Agora. All rights reserved.
//

import Foundation

class TokenBuilder {
    
    typealias FailureCompletion = (Error) -> ()
    typealias SuccessCompletion = (ServerResp) -> ()
    
    
    func buildByAppId(_ appId: String,
                      appCertificate: String,
                      userUuid: String) -> String {
        return RtmTokenTool.token(appId,
                                  appCertificate: appCertificate,
                                  uid: userUuid)
    }
}

extension TokenBuilder {
    struct ServerResp {
        let appId: String
        let userId: String
        let token: String
    }
}
