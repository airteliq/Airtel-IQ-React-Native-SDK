//
//  EnxRoomManager.swift
//  EnxRtc
//
//  Created by Daljeet Singh on 17/03/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

import Foundation
import EnxRTCiOS

@objc(EnxRoomManager)
class EnxRoomManager: RCTEventEmitter {
    var localStream : EnxStream!
    var localStreamId : String!
    var objectJoin: EnxRtc!
    var remoteRoom : EnxRoom!
    var componentEvents: [String] = [];
    var jsEvents: [String] = [];
    
    @objc override func supportedEvents() -> [String] {
        let allEvents = getSupportedEvents();
        return jsEvents + allEvents
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true;
    }
    
    @objc func initRoom(){
        objectJoin = EnxRtc()
    }
   
    @objc func changePlayerScaleType(_ mode: Int, streamId:String?)
    {
      DispatchQueue.main.async {
        guard let player = EnxRN.sharedState.players[streamId!] else {
            return;
        }
        var contentMode = UIView.ContentMode.scaleAspectFit
        if mode == 1{
           contentMode = UIView.ContentMode.scaleAspectFill
        }
        player.contentMode = contentMode
      }
    }
    
    @objc func joinRoom(_ token: String, localInfo: NSDictionary, roomInfo: NSDictionary, advanceOptions: NSArray){
        
        
        DispatchQueue.main.async {
            let localStreamInfo : NSDictionary = localInfo
            
            guard let localStreamObject =    self.objectJoin.joinRoom(token, delegate: self, publishStreamInfo: (localStreamInfo as! [AnyHashable : Any]), roomInfo: (roomInfo as! [AnyHashable : Any]), advanceOptions: advanceOptions as? [Any]) else{
                return
            }
            self.localStream = localStreamObject
            self.localStream.delegate = self as EnxStreamDelegate
        }
    }
    
    
    @objc func publish(){
        
        guard localStream != nil else{
            return
        }
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.publish(localStream)
    }
    
    @objc func initStream(_ streamId:String?){
        guard streamId != nil else{
            return
        }
        localStreamId = streamId
        DispatchQueue.main.async {
            if(self.localStream != nil) {
                EnxRN.sharedState.publishStreams.updateValue(self.localStream, forKey: (streamId)!)
                guard let player = EnxRN.sharedState.players[streamId!] else{
                    return;
                }
                self.localStream.attachRenderer(player)
            }
        }
    }
    
    
    @objc func getLocalStreamId(_ callback: @escaping RCTResponseSenderBlock) -> Void {
        let streamDict = EnxRN.sharedState.publishStreams
        var keyString: String = ""
        for (key, _) in streamDict {
            keyString = key
        }
        callback([keyString])
    }
    
    @objc func subscribe(_ streamId: String?, callback: @escaping RCTResponseSenderBlock) -> Void {
        DispatchQueue.main.async{
            if(streamId == nil || streamId?.count == 0){
                callback(["Error: Invalid streamId to subscribe."])
            }
            guard let stream = EnxRN.sharedState.room?.streamsByStreamId?[streamId!] as? EnxStream else{
                return;
            }
            EnxRN.sharedState.room!.subscribe(stream)
        }
    }
    
    
    @objc func switchCamera(_ streamId: String?){
        
        guard let stream = EnxRN.sharedState.publishStreams[streamId!] else{
            return;
        }
        stream.switchCamera()
    }
    
    @objc func muteSelfAudio(_ streamId: String, value: Bool){
        
        guard let stream = EnxRN.sharedState.publishStreams[streamId] else{
            return;
        }
        stream.muteSelfAudio(value)
        
        
    }
    
    @objc func muteSelfVideo(_ streamId: String?, value: Bool){
        
        guard let stream = EnxRN.sharedState.publishStreams[streamId!] else{
            return;
        }
        stream.muteSelfVideo(value)
    }
    
    
    @objc func startRecord(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.startRecord()
        
    }
    
    @objc func stopRecord(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.stopRecord()
        
    }
    
    //Chair Control
    //For Participant
    @objc func requestFloor(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.requestFloor()
    }
    
    //For Moderator
    @objc func grantFloor(_ clientId: String){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        if clientId.count > 0{
            room.grantFloor(clientId)
        }
    }
    
    @objc func denyFloor(_ clientId: String){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        if clientId.count > 0{
            room.denyFloor(clientId)
        }
    }
    
    @objc func releaseFloor(_ clientId: String){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        if clientId.count > 0{
            room.releaseFloor(clientId)
        }
    }
    
    //For Participant to finish floor
//    @objc func finishFloor(){
//        guard let room = EnxRN.sharedState.room else{
//            return
//        }
//        room.finishFloor()
//    }
    
    //For Participant to cancel floor
//    @objc func cancelFloor(){
//      guard let room = EnxRN.sharedState.room else{
//            return
//      }
//      room.cancelFloor()
//    }
//    
    
    //Hard Mute
    
    @objc func hardMuteAudio(_ streamId: String?, _ clientId: String){
        guard let stream = EnxRN.sharedState.publishStreams[streamId!] else{
            return;
        }
        if clientId.count > 0{
          stream.hardMuteAudio(clientId)
        }
    }
    
    @objc func hardUnmuteAudio(_ streamId: String?, _ clientId: String){
        guard let stream = EnxRN.sharedState.publishStreams[streamId!] else{
            return;
        }
        
        if clientId.count > 0{
            stream.hardUnMuteAudio(clientId)
        }
    }
    
    @objc func hardMuteVideo(_ streamId: String, _ clientId: String){
        guard let stream = EnxRN.sharedState.publishStreams[streamId] else{
            return;
        }
        
        if clientId.count > 0{
            stream.hardMuteVideo(clientId)
        }
    }
    
    @objc func hardUnmuteVideo(_ streamId: String, _ clientId: String) {
        guard let stream = EnxRN.sharedState.publishStreams[streamId] else{
            return;
        }
        if clientId.count > 0{
            stream.hardUnMuteVideo(clientId)
        }
    }
    
    //Hard Room mute
    @objc func hardMute(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.hardMute()
    }
    
    @objc func hardUnmute(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.hardUnMute()
    }
    
    @objc func sendData(_ streamId: String, _ data: NSDictionary){
        guard let stream = EnxRN.sharedState.publishStreams[streamId] else{
            return;
        }
            stream.sendData(data as! [AnyHashable : Any])
    }
    
    //Post Client Logs
    // To enble Enx logs to write in the file.
    @objc func enableLogs(_ value: Bool)
    {
        if(value){
            let enxLog = EnxUtilityManager.shareInstance()
            enxLog?.startLog()
        }
        else{
            
        }
    }
    
    @objc func postClientLogs(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.postClientLogs()
    }
    
  
    
    // Set Active Talker Count
    @objc func setTalkerCount(_ number: Int){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.setTalkerCount(number)
    }
    
    @objc func getTalkerCount(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.getTalkerCount()
    }
    
    @objc func getMaxTalkers(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.getMaxTalkers()
    }
    
    
    @objc func changeToAudioOnly(_ value:Bool){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.change(toAudioOnly: value)
    }
    
    @objc func stopVideoTracksOnApplicationBackground(_ value: Bool, _ videoMuteLocalStream: Bool){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.stopVideoTracks(onApplicationBackground: value)
    }
    
    @objc func startVideoTracksOnApplicationForeground(_ value: Bool, _ videoMuteLocalStream: Bool){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.startVideoTracks(onApplicationForeground: value)
    }
    
    //Audio Device methods
    @objc func switchMediaDevice(_ mediaName: String){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.switchMediaDevice(mediaName)
    }
    
    @objc func getSelectedDevice(_ callback: @escaping RCTResponseSenderBlock) -> Void {
        guard let room = EnxRN.sharedState.room else{
            return
        }
        
        let selectedDeviceString : String = room.getSelectedDevice()
        let selectedDeviceArray = [selectedDeviceString]
        callback(selectedDeviceArray)
    }
    
    @objc func getDevices(_ callback: @escaping RCTResponseSenderBlock) -> Void {
        guard let room = EnxRN.sharedState.room else{
            return
        }
        let deviceArray = room.getDevices()
        callback(deviceArray)
        
    }
    
    //Stats Method
    @objc func enableStats(_ value: Bool){
        guard let room = EnxRN.sharedState.room else {
            return
        }
        room.enableStats(value)
    }
    
    //Send Message
    @objc func sendMessage(_ data: String, broadcast: Bool, clientIds: [Any]?){
        guard let room = EnxRN.sharedState.room else {
            return
        }
        room.sendMessage(data, isBroadCast: broadcast, recipientIDs: clientIds)
       // room.sendMessage(data, broadCast: broadcast, clientIds: clientIds)
    }
    
    //Send User Data
    @objc func sendUserData(_ data: NSDictionary, broadcast: Bool, clientIds: [Any]?) {
        guard let room = EnxRN.sharedState.room else {
            return
        }
        room.sendUserData(data as! [AnyHashable : Any], isBroadCast: broadcast, recipientIDs: clientIds)
        //room.sendUserData(data, broadCast: broadcast, clientIds: clientIds)
    }
    
    //TO switch user role.
    @objc func switchUserRole(_ clientId: String){
      
        guard let room = EnxRN.sharedState.room else {
            return
        }
        room.switchUserRole(clientId)
    }
    @objc func sendFiles(_ position: NSString, isBroadcast: Bool, clientIds: [Any]?){
        guard let room = EnxRN.sharedState.room else {
            return
        }
        DispatchQueue.main.async {
            room.sendFiles(isBroadcast, clientIds: clientIds)
        }
    }
    
    @objc func downloadFile(_ file: NSDictionary, autoSave: Bool){
        guard let room = EnxRN.sharedState.room else {
            return
        }
        room.downloadFile(file as! [AnyHashable : Any], autoSave: autoSave)
     }
    
    @objc func getAvailableFiles(){
        guard let room = EnxRN.sharedState.room else {
            return
        }
        let array  = room.getAvailableFiles()
            self.emitEvent(event: "room:didAvailableFiles", data: array)
    }

    @objc func cancelUpload(_ jobId: NSString!){
           guard let room = EnxRN.sharedState.room else {
               return
           }
        
          room.cancelUpload(jobId.intValue)
        
    }
    
    @objc func cancelDownload(_ jobId: NSString!){
          guard let room = EnxRN.sharedState.room else {
               return
           }
        room.cancelDownload(jobId.intValue)
    }
    
    @objc func cancelAllUploads(){
           guard let room = EnxRN.sharedState.room else {
               return
           }
        room.cancelAllUploads()
    }
    
    @objc func cancelAllDownloads(){
           guard let room = EnxRN.sharedState.room else {
               return
           }
        room.cancelAllDownloads()
    }
   
    //To get self connected user details
    @objc func whoAmI(){
       guard let room = EnxRN.sharedState.room else {
           return
        }
        guard let resDict = room.whoami() as? [String : Any] else{
          return
        }
       self.emitEvent(event: "room:whoAmI", data:resDict)
    }
   
    //To lock room
    @objc func lockRoom(){
        guard let room = EnxRN.sharedState.room else {
          return
        }
        room.lock()
    }
    
    //To unlock Room
    @objc func unlockRoom(){
        guard let room = EnxRN.sharedState.room else {
         return
        }
        room.unlock()
    }
    
    //To destroy Room
    @objc func destroy(){
        guard let room = EnxRN.sharedState.room else {
         return
        }
        room.destroy()
    }
    
    //To start out bound call
    @objc func makeOutboundCall(_ number: NSString){
        guard let room = EnxRN.sharedState.room else {
         return
        }
        room.makeOutboundCall(number as String)
    }

    
    @objc func dropUser(_ clientIds: NSArray){
       guard let room = EnxRN.sharedState.room else {
         return
       }
        room.dropUser(clientIds as! [Any])
    }
    
     // To enableProximitySensor
    @objc func enableProximitySensor(_ value: Bool){
        guard let room = EnxRN.sharedState.room else {
          return
        }
         room.enableProximitySensor(value)
    }
    
    //To setAudioOnlyMode
    @objc func setAudioOnlyMode(_ audioOnly: Bool){
       guard let room = EnxRN.sharedState.room else {
          return
        }
         room.setAudioOnlyMode(audioOnly)
    }
    
    //To setReceiveVideoQuality
    @objc func setReceiveVideoQuality(_ opt: NSDictionary){
      guard let room = EnxRN.sharedState.room else {
        return
      }
        room.setReceiveVideoQuality(opt as! [String : Any])
    }
   
    //To getReceiveVideoQuality
    @objc func getReceiveVideoQuality(_ streamType: NSString){
      guard let room = EnxRN.sharedState.room else {
         return
      }
        let type = room.getReceiveVideoQuality(streamType as String)
        self.emitEvent(event: "room:getReceiveVideoQuality", data:type)
    }
    
    @objc func startAnnotation(_ streamId: NSString){
       guard let room = EnxRN.sharedState.room else {
          return
        }
        var stream = EnxRN.sharedState.publishStreams[streamId as String]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId as String]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        
        room.startAnnotation(stream!)
    }
    
    @objc func stopAnnotation(){
       guard let room = EnxRN.sharedState.room else {
          return
       }
        room.stopAnnotation()
    }
    
    @objc func muteSubscribeStreamsAudio(_ flag: Bool){
        guard let room = EnxRN.sharedState.room else {
          return
        }
        room.muteSubscribeStreamsAudio(flag)
    }
    
    @objc func getRole(){
       guard let room = EnxRN.sharedState.room else {
          return
        }
       guard room.userRole as? String != nil else{
            return;
        }
       self.emitEvent(event: "room:getRole", data:room.userRole)

    }
    
    @objc func getClientId(){
        guard let room = EnxRN.sharedState.room else {
          return
        }
        guard room.clientId as String? != nil else{
            return;
        }
        self.emitEvent(event: "room:getClientId", data:room.clientId as Any)

    }
    
    @objc func getRoomId(){
        guard let room = EnxRN.sharedState.room else {
         return
        }
        guard room.roomId as String? != nil else{
        return;
        }
        self.emitEvent(event: "room:getRoomId", data:room.roomId as Any)
    }
    
    @objc func getClientName(){
        guard let room = EnxRN.sharedState.room else {
          return
        }
        guard room.clientName as String? != nil else{
         return;
        }
        self.emitEvent(event: "room:clientName", data:room.clientName as Any)
    }
    
    @objc func isRoomActiveTalker(){
     guard let room = EnxRN.sharedState.room else {
        return
     }
      self.emitEvent(event: "room:isRoomActiveTalker", data:room.isRoomActiveTalker)
    }
    
    @objc func setZoomFactor(_ value:CGFloat ,clientIds: NSArray){
        guard let room = EnxRN.sharedState.room else {
              return
        }
        room.setZoomFactor(value, clientId: clientIds as! [Any])
    }
    
    
    @objc func getUserList(){
     guard let room = EnxRN.sharedState.room else {
       return
     }
     guard let userArray = room.getUserList() as? [Any] else{
          return
     }
     self.emitEvent(event: "room:getUserList", data:userArray)
    }
    
    @objc func extendConferenceDuration(){
     guard let room = EnxRN.sharedState.room else {
      return
     }
     room.extendConferenceDuration()
    }
    
    @objc func getRoomMetadata(){
     guard let room = EnxRN.sharedState.room else {
        return
     }
        guard let metaData = room.roomMetadata as? [String:Any] else{
                 return
        }
        self.emitEvent(event: "room:getRoomMetadata", data:metaData)
    }
    
    @objc func isLocal(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        self.emitEvent(event: "stream:isLocal", data:stream?.isLocal as Any)
    }
    
    @objc func hasAudio(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        self.emitEvent(event: "stream:hasAudio", data:stream?.hasAudio() as Any)
    }
    
    @objc func hasData(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        self.emitEvent(event: "stream:hasData", data:stream?.hasData() as Any)
    }
    
    @objc func hasVideo(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        self.emitEvent(event: "stream:hasVideo", data:stream?.hasVideo() as Any)
    }
    
    @objc func hasScreen(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        self.emitEvent(event: "stream:hasScreen", data:stream?.screen as Any)
    }
    
    @objc func isAudioOnlyStream(_ streamId: String){
     var stream = EnxRN.sharedState.publishStreams[streamId]
      if(stream == nil){
        stream = EnxRN.sharedState.subscriberStreams[streamId]
      }
      guard stream?.enxPlayerView != nil else{
        return;
      }
        self.emitEvent(event: "stream:isAudioOnlyStream", data:stream?.isAudioOnlyStream as Any)
    }
    
    @objc func getReasonForMuteVideo(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
          stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
          return;
        }
        guard stream?.reasonForMuteVideo != nil else{
          return;
        }
        self.emitEvent(event: "stream:getReasonForMuteVideo", data:stream?.reasonForMuteVideo as Any)
    }
    
    @objc func getMediaType(_ streamId: String){
     var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
          stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
          return;
        }
        guard stream?.mediaType != nil else{
          return;
        }
        self.emitEvent(event: "stream:getMediaType", data:stream?.mediaType as Any)
    }
    
    @objc func getVideoAspectRatio(_ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
          stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
          return;
        }
        guard stream?.videoAspectRatio != nil else{
          return;
        }
      self.emitEvent(event: "stream:getVideoAspectRatio", data:stream?.videoAspectRatio as Any)
    }
    
    
    
    //To enable particular player stream stats.
    @objc func enablePlayerStats(_ value: Bool, _ streamId: String){
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
        stream?.enxPlayerView?.delegate = self
        stream?.enxPlayerView?.enablePlayerStats(value)
    }
    
    @objc func setAdvancedOptions(_ options: NSArray ){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.setAdvanceOptions(options as! [Any])
    }
    
    @objc func setConfigureOption(_ options: NSDictionary, streamId: String){
        guard let stream = EnxRN.sharedState.subscriberStreams[streamId] else{
            return;
        }
        guard stream.enxPlayerView != nil else{
            return;
        }
        stream.enxPlayerView?.setConfigureOption(options as! [String : Any])
    }
    
    @objc func getAdvancedOptions(){
        guard let room = EnxRN.sharedState.room else{
            return
        }
        room.getAdvanceOptions()
    }
    
    @objc func captureScreenShot(_ streamId: String){
        
        var stream = EnxRN.sharedState.publishStreams[streamId]
        if(stream == nil){
            stream = EnxRN.sharedState.subscriberStreams[streamId]
        }
        guard stream?.enxPlayerView != nil else{
            return;
        }
         
         DispatchQueue.main.async {
            stream?.enxPlayerView?.delegate = self
            stream?.enxPlayerView?.captureScreenShot()
        }
    }
    
    @objc func disconnect(){
        
        guard let room = EnxRN.sharedState.room else{
            return
        }
        DispatchQueue.main.async {
           room.disconnect()
        }
        
    }
    
    @objc func setNativeEvents(_ events: Array<String>) -> Void {
        for event in events {
            if (!self.jsEvents.contains(event)) {
                self.jsEvents.append(event);
            }
        }
    }
    
    @objc func removeNativeEvents(_ events: Array<String>) -> Void {
        for event in events {
            if let i = self.jsEvents.index(of: event) {
                self.jsEvents.remove(at: i)
            }
        }
    }
    
    
    @objc func setJSComponentEvents(_ events: Array<String>) -> Void {
        for event in events {
            self.componentEvents.append(event);
        }
    }
    
    @objc func removeJSComponentEvents(_ events: Array<String>) -> Void {
        for event in events {
            if let i = self.componentEvents.index(of: event) {
                self.componentEvents.remove(at: i)
            }
        }
    }
    
    
    func emitEvent(event : String , data: Any) -> Void {
        if (self.jsEvents.contains(event) || self.componentEvents.contains(event)) {
            self.sendEvent(withName: event, body: data);
        }
    }
}

extension EnxRoomManager : EnxRoomDelegate
{
    func getSupportedEvents() -> [String] {
        
        return ["room:didActiveTalkerList","room:didScreenSharedStarted","room:didScreenShareStopped","room:didCanvasStarted","room:didCanvasStopped","room:didRoomRecordStart","room:didRoomRecordStop","room:didFloorRequested","room:didLogUpload","room:didSetTalkerCount","room:didGetMaxTalkers","room:didGetTalkerCount","room:userDidConnected","room:userDidDisconnected","room:didHardUnMuteAllUser","room:didHardMutedAll","room:didUnMutedAllUser","room:didMutedAllUser","room:didProcessFloorRequested","room:didFloorRequestReceived","room:didReleasedFloorRequest","room:didDeniedFloorRequest","room:didGrantedFloorRequest","room:didStopRecordingEvent","room:didStartRecordingEvent","room:didSubscribedStream","room:didDisconnected","room:didStreamAdded","room:didEventError","room:didError","room:didPublishedStream","room:didNotifyDeviceUpdate","room:didStatsReceive","room:didAcknowledgeStats","room:didBandWidthUpdated","room:didShareStreamEvent","room:didRoomConnected","room:didReconnect","room:didUserReconnectSuccess","room:didConnectionInterrupted","room:didConnectionLost","room:didCanvasStreamEvent","room:didAdvanceOptionsUpdate","room:didGetAdvanceOptions","room:didCapturedView","room:didMessageReceived","room:didUserDataReceived","room:didAcknowledgSendData","room:didSwitchUserRole","room:didFileUploaded","room:didFileAvailable","room:didFileUploadStarted","room:didInitFileUpload","room:didFileUploadFailed","didFileDownloaded","room:didFileDownloadFailed","room:didAvailableFiles","room:didFileUploadCancelled","room:didInitFileDownload","room:didFileDownloadCancelled","room:whoAmI","room:didLockRoom","room:didUnlockRoom","room:didAckLockRoom","room:didAckUnlockRoom","room:didOutBoundCallInitiated","room:didDialStateEvents","room:didAckDropUser","room:didAckDestroy","room:getReceiveVideoQuality","room:didAnnotationStarted","room:didStartAnnotationACK","room:didAnnotationStopped","room:didStoppedAnnotationACK","room:getRole","room:getClientId","room:getRoomId","room:clientName","room:getUserList","room:didConferencessExtended","room:didConferenceRemainingDuration","room:getRoomMetadata","stream:didAudioEvent","stream:didVideoEvent","stream:didhardMuteAudio","stream:didhardUnmuteAudio","stream:didRemoteStreamAudioMute","stream:didRemoteStreamAudioUnMute","stream:didRemoteStreamVideoMute","stream:didRecievedHardMutedAudio","stream:didRecievedHardUnmutedAudio","stream:didRemoteStreamVideoUnMute","stream:didHardVideoMute","stream:didHardVideoUnMute","stream:didReceivehardMuteVideo","stream:didRecivehardUnmuteVideo","stream:didReceiveData","stream:didPlayerStats","stream:isLocal","stream:hasScreen","stream:hasAudio","stream:hasVideo","stream:hasData","stream:isAudioOnlyStream","stream:getMediaType","stream:getVideoAspectRatio"];
    }
    
    
    func room(_ room: EnxRoom?, didConnect roomMetadata: [AnyHashable : Any]?) {
        EnxRN.sharedState.room = room
        self.emitEvent(event: "room:didRoomConnected", data: roomMetadata as Any)
    }
    
    func room(_ room: EnxRoom?, didError reason: [Any]?) {
        guard let errorVal = reason else{
            return
        }
        self.emitEvent(event: "room:didError", data: errorVal)
    }
    
    func room(_ room: EnxRoom?, didEventError reason: [Any]?) {
        guard let resDict = reason?[0] as? [String : Any], reason!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didEventError", data:resDict)
    }
    
    func room(_ room: EnxRoom?, didReconnect reason: String?) {
        guard let reasonString = reason else {
            return
        }
        self.emitEvent(event: "room:didReconnect", data:reasonString)
    }
    
    func room(_ room: EnxRoom, didUserReconnectSuccess data: [AnyHashable : Any]) {
        
        self.emitEvent(event: "room:didUserReconnectSuccess", data:data)
    }
    
    func room(_ room: EnxRoom, didConnectionInterrupted data: [Any]) {
        guard let dataDict = data[0] as? [String : Any], data.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didConnectionInterrupted", data:dataDict)
    }
    
    func room(_ room: EnxRoom, didConnectionLost data: [Any]) {
        guard let dataDict = data[0] as? [String : Any], data.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didConnectionLost", data:dataDict)
    }
    
    func room(_ room: EnxRoom?, didPublishStream stream: EnxStream?) {
        if(localStreamId == nil){
            localStreamId = ""
        }
        let resultDict : NSDictionary = ["result" : 0 ,"message" : "Stream has been published." ,"streamId" :localStreamId]
        
        self.emitEvent(event: "room:didPublishedStream", data: resultDict)
    }
    
    func room(_ room: EnxRoom?, didAddedStream stream: EnxStream?) {
        
        guard stream != nil else{
            return
        }
        EnxRN.sharedState.subscriberStreams.updateValue(stream!, forKey: stream?.streamId as! String)
        let resultDict : NSDictionary = ["streamId" : stream!.streamId as Any ,"hasData" : stream?.hasData() as Any  ,"hasScreen" :stream?.screen as Any]
        self.emitEvent(event: "room:didStreamAdded", data: resultDict)
    }
    
    func room(_ room: EnxRoom?, didRemovedStream stream: EnxStream?) {
        guard stream != nil else{
            return
        }
        guard let subscribeStream = EnxRN.sharedState.subscriberStreams[(stream?.streamId)!] else {
            return
        }
        let resultDict : NSDictionary = ["streamId" : subscribeStream.streamId as Any,"msg": "Stream has removed."]
        self.emitEvent(event: "room:didRemoveStream", data: resultDict)
         EnxRN.sharedState.subscriberStreams.removeValue(forKey: subscribeStream.streamId!)

    }
    
    func room(_ room: EnxRoom?, didSubscribeStream stream: EnxStream?) {
        guard let player = EnxRN.sharedState.players[(stream?.streamId)!] else {
            return
        }
        stream?.attachRenderer(player)
        self.emitEvent(event: "room:didSubscribedStream", data: "")
    }
    
    func room(_ room: EnxRoom?, activeTalkerList Data: [Any]?) {
        
        guard let tempDict = Data?[0] as? [String : Any], Data!.count>0 else {
            
            return
        }
        let activeListArray = tempDict["activeList"] as? [Any]
        self.emitEvent(event: "room:didActiveTalkerList", data: activeListArray!)
        
        for (_,active) in (activeListArray?.enumerated())! {
            // Do this
            let remoteStreamDict = EnxRN.sharedState.room!.streamsByStreamId as! [String : Any]
            let mostActiveDict = active as! [String : Any]
            let streamId = String(mostActiveDict["streamId"] as! Int)
            let stream = remoteStreamDict[streamId] as! EnxStream
            
            guard let player = EnxRN.sharedState.players[streamId] else{
                return
            }
            stream.attachRenderer(player)
        }
    }
    
    //Screen Share Delegates
    func room(_ room: EnxRoom?, screenSharedStarted Data: [Any]?) {
        guard let shareDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didScreenSharedStarted", data:shareDict)
        let streamId = String (shareDict["streamId"] as! Int)
        guard let shareStream = EnxRN.sharedState.room!.streamsByStreamId![streamId] as? EnxStream else{
            return
        }
        guard let player = EnxRN.sharedState.players[streamId] else{
            return
        }
        shareStream.attachRenderer(player)
        
    }
    
    func room(_ room: EnxRoom?, screenShareStopped Data: [Any]?) {
        guard let shareDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        let streamId = String (shareDict["streamId"] as! Int)
        guard EnxRN.sharedState.players[streamId] != nil else{
            return
        }
        EnxRN.sharedState.players.removeValue(forKey: streamId)
        self.emitEvent(event: "room:didScreenShareStopped", data: shareDict)
    }
    
    //Canvas Delegates
    func room(_ room: EnxRoom?, canvasStarted Data: [Any]?) {
        guard let canvasDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didCanvasStarted", data:canvasDict)
        let streamId = String (canvasDict["streamId"] as! Int)
        guard let canvasStream = EnxRN.sharedState.room!.streamsByStreamId![streamId] as? EnxStream else{
            return
        }
        guard let player = EnxRN.sharedState.players[streamId] else{
            return
        }
        canvasStream.attachRenderer(player)
    }
    
    func room(_ room: EnxRoom?, canvasStopped Data: [Any]?) {
        guard let canvasDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        let streamId = String (canvasDict["streamId"] as! Int)
        guard EnxRN.sharedState.players[streamId] != nil else{
            return
        }
        EnxRN.sharedState.players.removeValue(forKey: streamId)
        self.emitEvent(event: "room:didCanvasStopped", data: canvasDict)
    }
    
    func roomDidDisconnected(_ status: EnxRoomStatus) {
        self.emitEvent(event: "room:didDisconnected", data: status)
    }
    
    /* Recording Delegate */
    /* This delegate called when recording started by the moderator. */
    func startRecordingEvent(_ response: [Any]?) {
        guard let responseDict = response?[0] as? [String : Any], response!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didStartRecordingEvent", data: responseDict)
    }
    /* This delegate called when recording stopped by the moderator. */
    func stopRecordingEvent(_ response: [Any]?) {
        guard let responseDict = response?[0] as? [String : Any], response!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didStopRecordingEvent", data: responseDict)
    }
    /* When recording is started in the room, (either implicitly or explicitly), all connected users are notified that room is being recorded.(For Participant) */
    func roomRecord(on Data: [Any]?) {
        guard let responseDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didRoomRecordStart", data: responseDict)
    }
    
    /* When the recording is turned off (either implicitly or explicitly), all connected users are notified that recording has been stopped in the room.(For Participant) */
    func roomRecordOff(_ Data: [Any]?) {
        guard let responseDict = Data?[0] as? [String : Any], Data!.count > 0 else{
            return
        }
        self.emitEvent(event: "room:didRoomRecordStop", data: responseDict)
    }
    
    /* Chair control Delegates */
    //Participant receives on the success of requestFloor. This is for participant only.
    func didFloorRequested(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didFloorRequested", data: dataDict)
    }
    
    /* Participant receives when the moderator performs action grantFloor. */
    func didGrantedFloorRequest(_ Data: [Any]?) {
      guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didGrantedFloorRequest", data: dataDict)
    }
   
    /* Participant receives when the moderator performs action denyFloor. */
    func didDeniedFloorRequest(_ Data: [Any]?) {
       guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didDeniedFloorRequest", data: dataDict)
    }
    
    
    /* Participant receives when the moderator performs action releaseFloor. */
    func didReleasedFloorRequest(_ Data: [Any]?) {
      guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didReleasedFloorRequest", data: dataDict)
    }
    
    /* Moderator receives any Floor Request raised by the participant. This is for Moderator only. */
    func didFloorRequestReceived(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didFloorRequestReceived", data: dataDict)
    }
    
    /* Moderator receives acknowledgment on performing actions like grantFloor, denyFloor, releaseFloor. */
    func didProcessFloorRequested(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didProcessFloorRequested", data: dataDict)
    }
    
    /*
       This delegate method will notify to all available moderator, Once any participant has finished there floor request
    */
//    func didFinishedFloorRequest(_ Data: [Any]?) {
//      guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
//            return
//        }
//        self.emitEvent(event: "room:didFinishedFloorRequest", data: dataDict)
//    }
    
    /*
    This ACK method for Participant , When he/she will finished their request floor after request floor accepted by any moderator */
//    func didFloorFinished(_ Data: [Any]?) {
//      guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
//            return
//        }
//        self.emitEvent(event: "room:didFloorFinished", data: dataDict)
//    }
    
    /*
       This delegate method will notify to all available moderator, Once any participant has canceled there floor request
    */
//    func didCancelledFloorRequest(_ Data: [Any]?) {
//      guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
//            return
//        }
//        self.emitEvent(event: "room:didCancelledFloorRequest", data: dataDict)
//    }
    
    /*
    This ACK method for Participant , When he/she will cancel their request floor*/
//    func didFloorCancelled(_ Data: [Any]?) {
//     guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
//            return
//        }
//        self.emitEvent(event: "room:didFloorCancelled", data: dataDict)
//    }
//
    
    //Room mute Delegates
    /* This delegate called when the room is muted by the moderator. Available to Moderator only. */
    func didhardMute(_ Data: [Any]?) {
    guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didMutedAllUser", data: dataDict)
    }
    
    /* This delegate called when the room is unmuted by the moderator. Available to Moderator only. */
    func didhardUnMute(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didUnMutedAllUser", data: dataDict)
        
    }
    
    /* Participants notified when room is muted by any moderator. */
    func didHardMuteRecived(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didHardMutedAll", data: dataDict)
    }
    
    /*  */
    func didHardunMuteRecived(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didHardUnMuteAllUser", data: dataDict)
    }
    
    /* This delegate called when a user is connected to a room, all other connected users are notified about the new user. */
    func room(_ room: EnxRoom?, userDidJoined Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:userDidConnected", data: dataDict)
    }
    
    /* When a user is disconnected from a room, all other connected users are notified about the users exit. */
    func room(_ room: EnxRoom?, userDidDisconnected Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:userDidDisconnected", data: dataDict)
    }
    
    //logs upload delegate
    func didLogUpload(_ data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didLogUpload", data: dataDict)
    }
    
    
    //Set and Get Active talker Delegates.
    /* Client endpoint receives when the user set number of active talker. */
    func room(_ room: EnxRoom?, didSetTalkerCount Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didSetTalkerCount", data: dataDict)
    }
    
    /* Client endpoint will get the maximum number of allowed Active Talkers in the connected room. */
    func room(_ room: EnxRoom?, didGetMaxTalkers Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didGetMaxTalkers", data: dataDict)
    }
    
    /* Client endpoint receives when the user request to get opted active talker streams set by them. */
    func room(_ room: EnxRoom?, didGetTalkerCount Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "room:didGetTalkerCount", data: dataDict)
    }
    
    /**
     This delegate Method Will Notify app user for any Audio media changes happen recentaly(Like :- New device connected/Doisconnected).
     */
    func didNotifyDeviceUpdate(_ updates: String) {
        self.emitEvent(event: "room:didNotifyDeviceUpdate", data: updates)
      
    }
    
    /*
     This method will update once stats enable and update to app user for stats
     @param statsData has all stats information.
     */
    func didStatsReceive(_ statsData: [Any]) {
        self.emitEvent(event: "room:didStatsReceive", data: statsData)
    }
    
    func didAcknowledgeStats(_ subUnSubResponse: [Any]) {
        self.emitEvent(event: "room:didAcknowledgeStats", data: subUnSubResponse)
    }
    
    //ABWD delegates
    func room(_ room: EnxRoom?, didBandWidthUpdated data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didBandWidthUpdated", data: Data)
    }
    
    func room(_ room: EnxRoom?, didCanvasStreamEvent data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didCanvasStreamEvent", data: Data)
    }
    
    func room(_ room: EnxRoom?, didShareStreamEvent data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didShareStreamEvent", data: Data)
    }
    
    func room(_ room: EnxRoom, didMessageReceived data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didMessageReceived", data: Data)
    }
    
    func room(_ room: EnxRoom, didUserDataReceived data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didUserDataReceived", data: Data)
    }
    
    func room(_ room: EnxRoom?, didAcknowledgSendData data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didAcknowledgSendData", data: Data)
    }
    
    func room(_ room: EnxRoom?, didAdvanceOptionsUpdate data: [AnyHashable : Any]? = nil) {
        self.emitEvent(event: "room:didAdvanceOptionsUpdate", data: data as Any)

    }
    
    func room(_ room: EnxRoom?, didGetAdvanceOptions data: [Any]?) {
        if(data!.count > 0){
            print(data![0])
            self.emitEvent(event: "room:didGetAdvanceOptions", data: data![0] )
            
        }
    }
    
    func room(_ room: EnxRoom?, didSwitchUserRole data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didSwitchUserRole", data: Data)
    }
    
    func room(_ room: EnxRoom?, didUserRoleChanged data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didUserRoleChanged", data: Data)
    }
    
    /*
     This delegate method called When any of the user in same room will start sharing file.
     */
    func room(_ room: EnxRoom, didFileUploadStarted data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileUploadStarted", data: Data)
    }
    
    /*
     This delegate method called When self user will start sharing file.
     */
    func room(_ room: EnxRoom, didInitFileUpload data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didInitFileUpload", data: Data)
    }
    
    /*
     This delegate method called When File available to download.
     */
    func room(_ room: EnxRoom, didFileAvailable data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileAvailable", data: Data)
    }
    
    /*
     This delegate method called upload file is success.
     */
    func room(_ room: EnxRoom, didFileUploaded data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileUploaded", data: Data)
    }
   
    /*
     This delegate method called upload file is failed.
     */
    func room(_ room: EnxRoom, didFileUploadFailed data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileUploadFailed", data: Data)
    }
    
    /*
     This delegate method called When download of file success.
     */
    func room(_ room: EnxRoom, didFileDownloaded data: String?) {
//        guard let data == nil else {
//            return
//        }
        self.emitEvent(event: "room:didFileDownloaded", data: data!)
    }
    
    /*
     This delegate method called When file download failed.
     */
    func room(_ room: EnxRoom, didFileDownloadFailed data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileDownloadFailed", data: Data)
    }
    
    func room(_ room: EnxRoom, didFileUploadCancelled data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileUploadCancelled", data: Data)
    }
    
    func room(_ room: EnxRoom, didFileDownloadCancelled data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didFileDownloadCancelled", data: Data)
    }
    
    func room(_ room: EnxRoom, didInitFileDownload data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didInitFileDownload", data: Data)
    }
    
    func room(_ room: EnxRoom?, didLockRoom data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didLockRoom", data: Data)
    }
    
    func room(_ room: EnxRoom?, didUnlockRoom data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didUnlockRoom", data: Data)
    }
    
    func room(_ room: EnxRoom?, didAckLockRoom data: [Any]?) {
       guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didAckLockRoom", data: Data)
    }
    
    func room(_ room: EnxRoom?, didAckUnlockRoom data: [Any]?) {
       guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didAckUnlockRoom", data: Data)
    }
    
    func room(_ room: EnxRoom?, didOutBoundCallInitiated data: [Any]?)
    {
        guard let Data = data?[0] as? [String : Any] else {
            return
        }
        self.emitEvent(event: "room:didOutBoundCallInitiated", data: Data)
    }
    
    func room(_ room: EnxRoom?, didDialStateEvents state: EnxOutBoundCallState) {
      self.emitEvent(event: "room:didDialStateEvents", data: state)
    }
    
    func room(_ room: EnxRoom?, didAckDropUser data: [Any]?) {
     guard let Data = data?[0] as? [String : Any] else {
        return
      }
      self.emitEvent(event: "room:didAckDropUser", data: Data)
    }
    
    func room(_ room: EnxRoom?, didAckDestroy data: [Any]?) {
     guard let Data = data?[0] as? [String : Any] else {
          return
     }
      self.emitEvent(event: "room:didAckDestroy", data: Data)
    }
    
    
    func room(_ room: EnxRoom?, didAnnotationStarted Data: [Any]?) {
      guard let data = Data?[0] as? [String : Any] else {
        return
      }
      self.emitEvent(event: "room:didAnnotationStarted", data: data)
    }
    
    func room(_ room: EnxRoom?, didStartAnnotationACK Data: [Any]?) {
     guard let data = Data?[0] as? [String : Any] else {
       return
     }
     self.emitEvent(event: "room:didStartAnnotationACK", data: data)
    }
    
    func room(_ room: EnxRoom?, didAnnotationStopped Data: [Any]?) {
     guard let data = Data?[0] as? [String : Any] else {
       return
      }
      self.emitEvent(event: "room:didAnnotationStopped", data: data)
    }
    
    func room(_ room: EnxRoom?, didStoppedAnnotationACK Data: [Any]?) {
      guard let data = Data?[0] as? [String : Any] else {
        return
      }
      self.emitEvent(event: "room:didStoppedAnnotationACK", data: data)
    }
    
    func room(_ room: EnxRoom?, didConferencessExtended data: [Any]?) {
     guard let Data = data?[0] as? [String : Any] else {
        return
     }
    self.emitEvent(event: "room:didConferencessExtended", data: Data)
    }
    
    func room(_ room: EnxRoom?, didConferenceRemainingDuration data: [Any]?) {
        guard let Data = data?[0] as? [String : Any] else {
            return
         }
        self.emitEvent(event: "room:didConferenceRemainingDuration", data: Data)
    }
    
    
}

extension EnxRoomManager :  EnxStreamDelegate
{
    /* Stream Delegates */
    
    func didAudioEvents(_ data: [AnyHashable : Any]?) {
        self.emitEvent(event: "stream:didAudioEvent", data: data as Any)
    }
    
    func didVideoEvents(_ data: [AnyHashable : Any]?) {
        self.emitEvent(event: "stream:didVideoEvent", data: data as Any)
    }
    
    //Receive all other users
    func stream(_ stream: EnxStream?, didSelfMuteAudio data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRemoteStreamAudioMute", data: dataDict)
    }
    
    //Receive all other users
    func stream(_ stream: EnxStream?, didSelfUnmuteAudio data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRemoteStreamAudioUnMute", data: dataDict)
    }
    
    //Receive all other users
    func stream(_ stream: EnxStream?, didSelfMuteVideo data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRemoteStreamVideoMute", data: dataDict)
    }
    
    // Receive all other users
    func stream(_ stream: EnxStream?, didSelfUnmuteVideo data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRemoteStreamVideoUnMute", data: dataDict)
    }
    
    //Hard mute Delegate
    /* On Success of single user mute by moderator. This delegate method is for moderator.*/
    func didhardMuteAudio(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didhardMuteAudio", data: dataDict)
    }
    
    /*On Success of single user unmute by moderator. This delegate method is for moderator.*/
    func didhardUnMuteAudio(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didhardUnmuteAudio", data: dataDict)
    }
    
    /*On Success of single user mute by moderator. This delegate method is for participant.*/
    func didRecievedHardMutedAudio(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRecievedHardMutedAudio", data: dataDict)
        
    }
    
    /*On Success of single user unmute by moderator. This delegate method is for participant.*/
    func didRecievedHardUnmutedAudio(_ Data: [Any]?) {
        guard let dataDict = Data?[0] as? [String : Any], Data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRecievedHardUnmutedAudio", data: dataDict)
        
    }
    
    //For Video
    /* This delegate called when a hard mute video alert moderator received from server. This delegate is for moderator. */
    func stream(_ stream: EnxStream?, didHardVideoMute data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didHardVideoMute", data: dataDict)
    }
    
    /* This delegate called when a hard unmute video alert moderator received from server. This delegate is for moderator. */
    func stream(_ stream: EnxStream?, didHardVideoUnMute data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didHardVideoUnMute", data: dataDict)
    }
    
    /* This delegate called when a hard mute video alert participant received from server. */
    func stream(_ stream: EnxStream?, didReceivehardMuteVideo data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didReceivehardMuteVideo", data: dataDict)
    }
    
    /* This delegate called when a hard unmute video alert participant received from server. */
    func stream(_ stream: EnxStream?, didRecivehardUnmuteVideo data: [Any]?) {
        guard let dataDict = data?[0] as? [String : Any], data!.count>0 else {
            return
        }
        self.emitEvent(event: "stream:didRecivehardUnmuteVideo", data: dataDict)
    }
    
    //Receive data API
    func didReceiveData(_ data: [AnyHashable : Any]?) {
        if data != nil{
            self.emitEvent(event: "stream:didReceiveData", data: data as Any)
        }
    }
  }


extension EnxRoomManager : EnxPlayerDelegate
{
    func didPlayerStats(_ data: [AnyHashable : Any]) {
        self.emitEvent(event: "stream:didPlayerStats", data: data)
    }
    
    func didCapturedView(_ snapShot: UIImage) {
        guard let imageData = snapShot.pngData() else {
            return;
        }
        let base64String = imageData.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))
        self.emitEvent(event: "room:didCapturedView", data: base64String)
    }
}
