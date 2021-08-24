//
//  EnxToolBarManager.swift
//  enx-rtc-react-native
//
//  Created by Daljeet Singh on 19/05/20.
//

import Foundation

@objc(EnxToolBarSwift)
class EnxToolBarManager: RCTViewManager {
    override func view() -> UIView {
        return RNEnxToolBarView();
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true;
    }
}
