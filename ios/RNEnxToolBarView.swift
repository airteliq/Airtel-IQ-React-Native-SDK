//
//  RNEnxToolBar.swift
//  enx-rtc-react-native
//
//  Created by Daljeet Singh on 19/05/20.
//

import Foundation
import EnxRTCiOS
@objc(RNEnxToolBarView)
class RNEnxToolBarView : UIView {
    var toolBar: EnxToolBar!
    override init(frame: CGRect) {
        super.init(frame: frame)
      
    }
       
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        if (self.toolBar == nil){
            self.toolBar = EnxToolBar.init(frame:  CGRect(x:0, y: 0, width:self.bounds.size.width, height:self.bounds.size.height/2) )
                addSubview(self.toolBar)
        }
    }
}
