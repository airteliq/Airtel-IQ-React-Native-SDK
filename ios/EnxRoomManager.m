//
//  EnxRoomManagern.m
//  EnxRtc
//
//  Created by Daljeet Singh on 17/03/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTBridgeMethod.h>
#import <React/RCTEventEmitter.h>
@interface RCT_EXTERN_MODULE(EnxRoomManager, RCTEventEmitter)

RCT_EXTERN_METHOD(initRoom)
RCT_EXTERN_METHOD(joinRoom:(NSString *)token localInfo:(NSDictionary *)localInfo roomInfo:(NSDictionary *)roomInfo advanceOptions:(NSArray *)advanceOptions) 
RCT_EXTERN_METHOD(publish)
RCT_EXTERN_METHOD(initStream:
                  (NSString*)streamId)
RCT_EXTERN_METHOD(getLocalStreamId:
                  (RCTResponseSenderBlock*)callback)
RCT_EXTERN_METHOD(subscribe:
                  (NSString*)streamId
                  callback:(RCTResponseSenderBlock*)callback)

RCT_EXTERN_METHOD(switchCamera:
                  (NSString*)streamId)
RCT_EXTERN_METHOD(muteSelfAudio:
                  (NSString*)streamId value:(BOOL)value)
RCT_EXTERN_METHOD(muteSelfVideo:
                  (NSString*)streamId value:(BOOL)value)

RCT_EXTERN_METHOD(startRecord)
RCT_EXTERN_METHOD(stopRecord)

RCT_EXTERN_METHOD(disconnect)

RCT_EXTERN_METHOD(setJSComponentEvents:
                  (NSArray*)events)
RCT_EXTERN_METHOD(removeJSComponentEvents:
                  (NSArray*)events)
RCT_EXTERN_METHOD(setNativeEvents:(NSArray*)events)
RCT_EXTERN_METHOD(removeNativeEvents:
                  (NSArray*)events)
RCT_EXTERN_METHOD(changePlayerScaleType:
                  (int)mode streamId:(NSString*)streamId)
//Chair Control
//For Participant
RCT_EXTERN_METHOD(requestFloor)

//For Moderator
RCT_EXTERN_METHOD(grantFloor:
                  (NSString*)clientId)
RCT_EXTERN_METHOD(denyFloor:
                  (NSString*)clientId)
RCT_EXTERN_METHOD(releaseFloor:
                  (NSString*)clientId)


//Hard Mute

//Audio
RCT_EXTERN_METHOD(hardMuteAudio:(NSString*)streamId clientId:(NSString*)clientId)
RCT_EXTERN_METHOD(hardUnmuteAudio:(NSString*)streamId clientId:(NSString*)clientId)

//Video
RCT_EXTERN_METHOD(hardMuteVideo:(NSString*)streamId clientId:(NSString*)clientId)
RCT_EXTERN_METHOD(hardUnmuteVideo:(NSString*)streamId clientId:(NSString*)clientId)

// Hard Room mute
RCT_EXTERN_METHOD(hardMute)
RCT_EXTERN_METHOD(hardUnmute)


//Send Data method
RCT_EXTERN_METHOD (sendData:(NSString *)streamId data:(NSDictionary *)data)


//Post Client Logs
RCT_EXTERN_METHOD(enableLogs:(BOOL)value)
RCT_EXTERN_METHOD(postClientLogs)

//Set and get Active Talker
RCT_EXTERN_METHOD(setTalkerCount:(Int)number)
RCT_EXTERN_METHOD(getTalkerCount)
RCT_EXTERN_METHOD(getMaxTalkers)

//changeToAudioOnly
RCT_EXTERN_METHOD(changeToAudioOnly:(BOOL)value)

//To stop video tracks on backgroung and foreground.

RCT_EXTERN_METHOD(stopVideoTracksOnApplicationBackground:(BOOL)value (BOOL)videoMutelocalStream)
RCT_EXTERN_METHOD(startVideoTracksOnApplicationForeground:(BOOL)value (BOOL)videoMutelocalStream)


//Audio Device methods
RCT_EXTERN_METHOD(switchMediaDevice:(NSString *)mediaName)
RCT_EXTERN_METHOD(getSelectedDevice:(RCTResponseSenderBlock*)callback)
RCT_EXTERN_METHOD(getDevices:(RCTResponseSenderBlock*)callback)

//Stats Methods
RCT_EXTERN_METHOD(enablePlayerStats:(BOOL)value streamId:(NSString *)streamId)
RCT_EXTERN_METHOD(enableStats:(BOOL)value)

// Set player configuration option.
RCT_EXTERN_METHOD(setConfigureOption:(NSDictionary *)options streamId:(NSString *)streamId)

//Set Advance Options
RCT_EXTERN_METHOD(setAdvancedOptions:(NSArray *)options)

RCT_EXTERN_METHOD(getAdvancedOptions)

// To capture player view screen shot
RCT_EXTERN_METHOD(captureScreenShot:(NSString *)streamId)


// Send message at Room level.

RCT_EXTERN_METHOD(sendMessage:(NSString *)data broadcast:(BOOL)broadcast clientIds:(NSArray *)clientIds)
RCT_EXTERN_METHOD(sendUserData:(NSDictionary *)data broadcast:(BOOL)broadcast clientIds:(NSArray *)clientIds)

// To switch user role.
RCT_EXTERN_METHOD(switchUserRole:(NSString *)clientId)

RCT_EXTERN_METHOD(sendFiles:(NSString *)position isBroadcast:(BOOL)isBroadcast clientIds:(NSArray *)clientIds)

RCT_EXTERN_METHOD(downloadFile:(NSDictionary *)file autoSave:(BOOL)autoSave)

//To get available files
RCT_EXTERN_METHOD(getAvailableFiles)

//To cancel file upload with job Id.
RCT_EXTERN_METHOD(cancelUpload:(NSString *)jobId)

//To cancel file download with job Id.
RCT_EXTERN_METHOD(cancelDownload:(NSString *)jobId)

//To cancel all upload files
RCT_EXTERN_METHOD(cancelAllUploads)

//To cancal all download files.
RCT_EXTERN_METHOD(cancelAllDownloads)

//To get self client details
RCT_EXTERN_METHOD(whoAmI)

//To lock room. No particpannt can join when room is lock
RCT_EXTERN_METHOD(lockRoom)

//To unlock room
RCT_EXTERN_METHOD(unlockRoom)

//To make outbound call
RCT_EXTERN_METHOD(makeOutboundCall:(NSString *)number)

//To drop any client from the connected room
RCT_EXTERN_METHOD(dropUser:(NSArray *)clientIds)

//To destroy the connected room. With this method all participant will get disconnected
RCT_EXTERN_METHOD(destroy)


//To enable proximity sensor
RCT_EXTERN_METHOD(enableProximitySensor:(BOOL)value)

//To set Audio Only mode
RCT_EXTERN_METHOD(setAudioOnlyMode:(BOOL)audioOnly)

//To set Receive video quality
RCT_EXTERN_METHOD(setReceiveVideoQuality:(NSDictionary *)opt)

// To get receive video quality
RCT_EXTERN_METHOD(getReceiveVideoQuality:(NSString *)streamType)

//To start annotation
RCT_EXTERN_METHOD(startAnnotation:(NSString *)streamId)

//To stop annotation
RCT_EXTERN_METHOD(stopAnnotation)

//To mute subscribers stream audio
RCT_EXTERN_METHOD(muteSubscribeStreamsAudio:(BOOL)flag)

// To get self client role
RCT_EXTERN_METHOD(getRole)

// To get self client Id
RCT_EXTERN_METHOD(getClientId)

//To get connected room Id
RCT_EXTERN_METHOD(getRoomId)

// To get client self name
RCT_EXTERN_METHOD(getClientName)

//To check room is with active talker or not
RCT_EXTERN_METHOD(isRoomActiveTalker)

//To get user list
RCT_EXTERN_METHOD(getUserList)

//To extend conference duration
RCT_EXTERN_METHOD(extendConferenceDuration)

//To get room meta data
RCT_EXTERN_METHOD(getRoomMetadata)

//To set zoom factor of remote client local stream.
RCT_EXTERN_METHOD(setZoomFactor:(CGFloat)value clientIds(NSArray *)clientIds)

// To get stream is local or not
RCT_EXTERN_METHOD(isLocal:(NSString *)streamId)

//To get stream has data or not
RCT_EXTERN_METHOD(hasData:(NSString *)streamId)

//To get stream has audio or not
RCT_EXTERN_METHOD(hasAudio:(NSString *)streamId)

//To get stream has video or not
RCT_EXTERN_METHOD(hasVideo:(NSString *)streamId)

//To get stream is screen share or not
RCT_EXTERN_METHOD(hasScreen:(NSString *)streamId)

//To get stream is audio only stream
RCT_EXTERN_METHOD(isAudioOnlyStream:(NSString *)streamId)

//To get reason for video mute for stream
RCT_EXTERN_METHOD(getReasonForMuteVideo:(NSString *)streamId)

//To get stream media type
RCT_EXTERN_METHOD(getMediaType:(NSString *)streamId)

//To get stream video aspect ratio
RCT_EXTERN_METHOD(getVideoAspectRatio:(NSString *)streamId)

@end
