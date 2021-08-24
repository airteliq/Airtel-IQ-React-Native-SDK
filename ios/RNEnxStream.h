//
//  EnxStream.h
//  Enx
//
//  Created by Daljeet Singh on 04/04/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#ifndef EnxStream_h
#define EnxStream_h
#import <React/RCTView.h>

@interface RNEnxStream : RCTView
@property (nonatomic, assign) NSString *streamId;
@property (nonatomic, assign) NSString *isLocal;

@end
#endif /* EnxStream_h */
