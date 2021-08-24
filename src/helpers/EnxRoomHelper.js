import { reassignEvents } from "./EnxHelper";
import { isString, isBoolean } from "underscore";

const sanitizeRoomEvents = (events) => {
  try {
    if (typeof events !== "object") {
      return {};
    }

    const customEvents = {
      ios: {
        roomConnected:
          "didRoomConnected" /* Event called on success of room connection. */,
        roomError:
          "didError" /* Event called on error while room connection. */,
        eventError: "didEventError" /* Event called on any event error. */,
        roomDisconnected:
          "didDisconnected" /* Event called on any event error. */,
        streamPublished:
          "didPublishedStream" /* Event called on publish stream success. */,
        streamAdded:
          "didStreamAdded" /* Event called on stream added in the room. */,
        streamSubscribed:
          "didSubscribedStream" /* Event called on subscribe stream success. */,
        activeTalkerList:
          "didActiveTalkerList" /* Event called to get updated active talker list. */,
        roomRecordingOn:
          "didRoomRecordStart" /* Event when recording is started in the room, (either implicitly or explicitly), all connected users are notified that room is being recorded.. */,
        roomRecordingOff:
          "didRoomRecordStop" /* Event when the recording is turned off (either implicitly or explicitly), all connected users are notified that recording has been stopped in the room. */,
        startRecordingEvent:
          "didStartRecordingEvent" /* Event called when recording started by the moderator. */,
        stopRecordingEvent:
          "didStopRecordingEvent" /* Event called when recording stopped by the moderator. */,
        screenShareStarted:
          "didScreenSharedStarted" /* Event called when screen share started. */,
        sceenShareStopped:
          "didScreenShareStopped" /* Event called when screen share stopped. */,
        canvasStarted:
          "didCanvasStarted" /* Event called when screen canvas stopped. */,
        canvasStopped:
          "didCanvasStopped" /* Event called when screen canvas stopped. */,
        floorRequested:
          "didFloorRequested" /* Event for participant on the success of requestFloor. This is for participant only. */,
        processFloorRequested:
          "didProcessFloorRequested" /* Event for Moderator on performing actions like grantFloor, denyFloor, releaseFloor. */,
        floorRequestReceived:
          "didFloorRequestReceived" /* Event for Moderatoron any Floor Request raised by the participant. This is for Moderator only. */,
        grantFloorRequested:
          "didGrantedFloorRequest" /* Event for Participant when the moderator performs action grantFloor. */,
        denyFloorRequested:
          "didDeniedFloorRequest" /* Event for Participant when the moderator performs action denyFloor. */,
        releaseFloorRequested:
          "didReleasedFloorRequest" /* Event for Participant when the moderator performs action releaseFloor. */,
        mutedAllUser:
          "didMutedAllUser" /* Event for called when the room is muted by the moderator. Available to Moderator only. */,
        unmutedAllUser:
          "didUnMutedAllUser" /* Event for called when the room is unmuted by the moderator. Available to Moderator only. */,
        hardMutedAll:
          "didHardMutedAll" /* Event for Participants when room is muted by any moderator. */,
        hardUnmuteAllUser:
          "didHardUnMuteAllUser" /* Event for Participants when room is unmuted by any moderator. */,
        userConnected:
          "userDidConnected" /* Event when a user is connected to a room, all other connected users are notified about the new user. */,
        userDisconnected:
          "userDidDisconnected" /* Event called when a user is disconnected from a room, all other connected users are notified about the users exit. */,
        logUpload:
          "didLogUpload" /* Event called when the log is uploaded successfully to the server. */,
        setTalkerCountResponse:
          "didSetTalkerCount" /* Event called when the user set number of active talker. */,
        getMaxTalkersResponse:
          "didGetMaxTalkers" /* Event to get the maximum number of allowed Active Talkers in the connected room. */,
        getTalkerCountResponse:
          "didGetTalkerCount" /* Event called when the user request to get opted active talker streams set by them. */,
        notifyDeviceUpdate:
          "didNotifyDeviceUpdate" /* Event Will Notify app user for any Audio media changes happen recently(Like :- New device connected/Doisconnected). */,
        receivedStats:
          "didStatsReceive" /* Event Will Notify app user with updated stream stats present in the room. */,
        acknowledgeStats:
          "didAcknowledgeStats" /* Event to called on stats enable or disable at room level. */,
        bandWidthUpdated:
          "didBandWidthUpdated" /* Event will notify if a significant change in remote streams. */,
        shareStateEvent:
          "didShareStreamEvent" /* Event will notify if a significant change in share streams. */,
        canvasStateEvent:
          "didCanvasStreamEvent" /* Event will notify if a significant change in canvas streams. */,
        reconnect: "didReconnect" /* Event will called on reconnect. */,
        userReconnect:
          "didUserReconnectSuccess" /* Event will called on reconnect success. */,
        connectionInterrupted:
          "didConnectionInterrupted" /* Event will notify if there is an interruption in connection. */,
        connectionLost:
          "didConnectionLost" /* Event will notify if the connection has lost. */,
        advancedOptionsUpdate:
          "didAdvanceOptionsUpdate" /* Event will notify advance options update. */,
        getAdvancedOptions: "didGetAdvanceOptions",
        capturedView:
          "didCapturedView" /* Event will provide base64 string of captured screen shot image. */,
        userDataReceived:
          "didUserDataReceived" /* Event called to receive custom signaling event message at room Level. */,
        messageReceived: "didMessageReceived", // Event called to receive message at room Level.
        acknowledgeSendData: "didAcknowledgSendData", // Event called on acknowledge send Data.
        acknowledgeSwitchUserRole: "didSwitchUserRole", // Event called on acknowledge switch user role.
        userRoleChanged: "didUserRoleChanged", // Event called on user role change.
        fileUploaded: "didFileUploaded", // Event called file is upload successfull.
        fileAvailable: "didFileAvailable", // Event called When File available to download.
        fileUploadStarted: "didFileUploadStarted", // Event called When any of the user in same room will start sharing file.
        fileUploadFailed: "didFileUploadFailed", // Event called upload file is failed.
        initFileUpload: "didInitFileUpload", // Event called When self user will start sharing file.
        fileDownloaded: "didFileDownloaded", // Event called When download of file success.
        fileDownloadFailed: "didFileDownloadFailed", // Event called When file download failed.
        availableFiles: "didAvailableFiles", //Event when file is available
        initFileDownload: "didInitFileDownload", // To init file Download
        fileUploadCancelled: "didFileUploadCancelled", // When user canel file upload
        fileDownloadCancelled: "didFileDownloadCancelled", // when user cancel download file
        whoAmI: "whoAmI", //To get self connected user details
        lockedRoom: "didLockRoom", //Event when room is locked
        unLockedRoom: "didUnlockRoom", //Event when room is unlocked
        ackUnLockRoom: "didAckLockRoom", //Event on acknowledge room locked for moderator
        ackLockRoom: "didAckUnlockRoom", //Event on acknowledge room unlocked for moderator
        outBoundCallInitiated: "didOutBoundCallInitiated", //Event when outbound call initiated
        dialStateEvents: "didDialStateEvents", // Event to get state of dial events.
        ackDropUser: "didAckDropUser", //Event on acknowledge drop user for moderator
        ackDestroy: "didAckDestroy", //Event on acknowledge destroy room for moderator
        getReceiveVideoQuality: "getReceiveVideoQuality", //Event to get receive video quality
        annotationStarted: "didAnnotationStarted", //Event on annotation started
        startAnnotationAck: "didStartAnnotationACK", //Event on start annotation acknowledge
        annotationStopped: "didAnnotationStopped", //Event on annotation stopped
        stoppedAnnotationAck: "didStoppedAnnotationACK", //Event on stopped annotation acknowledge
        getRole: "getRole", //Event to get user self role(participant or moderator)
        getClientId: "getClientId", //Event to get user self clientId
        getRoomId: "getRoomId", //Event to get roomId if client is connected
        getClientName: "clientName", //Event to get self clientName
        isRoomActiveTalker: "isRoomActiveTalker", // Event to get room is available with activeTalker event or not
        getUserList: "getUserList", //Event to get all available users in room
        conferencessExtended: "didConferencessExtended", // Event on extend room duration
        conferenceRemainingDuration: "didConferenceRemainingDuration",
        getRoomMetadata: "getRoomMetadata", // Event to get room metaData
        //finishedFloorRequest: "didFinishedFloorRequest", //This event will notify to all available moderator, Once any participant has finished there floor request
        //floorFinished: "didFloorFinished", //This ACK event for Participant , When he/she will finished their request floor after request floor accepted by any moderator
        //cancelledFloorRequest: "didCancelledFloorRequest",// This event method will notify to all available moderator, Once any participant has canceled there floor request
        //floorCancelled: "didFloorCancelled",// This ACK event for Participant , When he/she will cancel their request floor
      },
      android: {
        roomConnected: "onRoomConnected",
        roomError: "onRoomError",
        eventError: "onEventError",
        roomDisconnected: "onRoomDisConnected",
        streamPublished: "onPublishedStream",
        streamAdded: "onStreamAdded",
        streamSubscribed: "onSubscribedStream",
        activeTalkerList: "onActiveTalkerList",
        roomRecordingOn: "onRoomRecordingOn",
        roomRecordingOff: "onRoomRecordingOff",
        startRecordingEvent: "onStartRecordingEvent",
        stopRecordingEvent: "onStopRecordingEvent",
        screenShareStarted: "onScreenSharedStarted",
        sceenShareStopped: "onScreenSharedStopped",
        canvasStarted: "onCanvasStarted",
        canvasStopped: "onCanvasStopped",
        floorRequested: "onFloorRequested",
        processFloorRequested: "onProcessFloorRequested",
        floorRequestReceived: "onFloorRequestReceived",
        grantFloorRequested: "onGrantedFloorRequest",
        denyFloorRequested: "onDeniedFloorRequest",
        releaseFloorRequested: "onReleasedFloorRequest",
        mutedAllUser: "onHardMuted",
        unmutedAllUser: "onHardUnMuted",
        hardMutedAll: "onReceivedHardMute",
        hardUnmuteAllUser: "onReceivedHardUnMute",
        userConnected: "onUserConnected",
        userDisconnected: "onUserDisConnected",
        logUpload: "onLogUploaded",
        setTalkerCountResponse: "onSetTalkerCount",
        getMaxTalkersResponse: "onMaxTalkerCount",
        getTalkerCountResponse: "onGetTalkerCount",
        notifyDeviceUpdate: "onNotifyDeviceUpdate",
        receivedStats: "onReceivedStats",
        acknowledgeStats: "onAcknowledgeStats",
        bandWidthUpdated: "onBandWidthUpdated",
        shareStateEvent: "onShareStreamEvent",
        canvasStateEvent: "onCanvasStreamEvent",
        reconnect: "onReconnect",
        userReconnect: "onUserReconnectSuccess",
        connectionInterrupted: "onConnectionInterrupted",
        connectionLost: "onConnectionLost",
        capturedView: "OnCapturedView",
        getAdvancedOptions: "onGetAdvancedOptions",
        advancedOptionsUpdate: "onAdvancedOptionsUpdate",
        userDataReceived: "onUserDataReceived",
        messageReceived: "onMessageReceived",
        acknowledgeSendData: "onAcknowledgedSendData",
        acknowledgeSwitchUserRole: "onSwitchedUserRole",
        userRoleChanged: "onUserRoleChanged",
        fileUploadStarted: "onFileUploadStarted",
        initFileUpload: "onInitFileUpload",
        fileAvailable: "onFileAvailable",
        fileUploaded: "onFileUploaded",
        fileUploadFailed: "onFileUploadFailed",
        fileDownloaded: "onFileDownloaded",
        fileDownloadFailed: "onFileDownloadFailed",
        availableFiles: "getAvailableFiles",
        initFileDownload: "onInitFileDownload",
        fileUploadCancelled: "onFileUploadCancelled",
        fileDownloadCancelled: "onFileDownloadCancelled",
        lockedRoom: "onLockedRoom",
        unLockedRoom: "onUnLockedRoom",
        ackUnLockRoom: "onAckUnLockRoom",
        ackLockRoom: "onAckLockRoom",
        outBoundCallInitiated: "onOutBoundCallInitiated",
        dialStateEvents: "onDialStateEvents",
        conferencessExtended: "onConferencessExtended",
        conferenceRemainingDuration: "onConferenceRemainingDuration",
        ackDropUser: "onAckDropUser",
        ackDestroy: "onAckDestroy",
        annotationStarted: "onAnnotationStarted",
        startAnnotationAck: "onStartAnnotationAck",
        annotationStopped: "onAnnotationStopped",
        stoppedAnnotationAck: "onStoppedAnnotationAck",
      },
    };
    return reassignEvents("room", customEvents, events);
  } catch (error) {
    console.log("sanitizeRoomEventsError", error);
  }
};

const sanitizeLocalInfoData = (localInfo) => {
  if (typeof localInfo !== "object") {
    return {
      audio: false,
      video: false,
      data: false,
      maxVideoBW: "",
      minVideoBW: "",
      audioMuted: false,
      videoMuted: false,
      name: "",
      minWidth: "",
      minHeight: "",
      maxWidth: "",
      maxHeight: "",
    };
  }
  return {
    audio: validateBoolean(localInfo.audio),
    video: validateBoolean(localInfo.video),
    data: validateBoolean(localInfo.data),
    maxVideoBW: validateString(localInfo.maxVideoBW),
    minVideoBW: validateString(localInfo.minVideoBW),
    audioMuted: validateBoolean(localInfo.audioMuted),
    videoMuted: validateBoolean(localInfo.videoMuted),
    name: validateString(localInfo.name),
    minWidth: validateString(localInfo.minWidth),
    minHeight: validateString(localInfo.minHeight),
    maxWidth: validateString(localInfo.maxWidth),
    maxHeight: validateString(localInfo.maxHeight),
  };
};

const sanitizeRoomData = (roomInfo) => {
  if (typeof roomInfo !== "object") {
    return {
      allow_reconnect: false,
      number_of_attempts: "",
      timeout_interval: "",
      audio_only: false,
    };
  }
  return {
    allow_reconnect: validateBoolean(roomInfo.allow_reconnect),
    number_of_attempts: validateString(roomInfo.number_of_attempts),
    timeout_interval: validateString(roomInfo.timeout_interval),
    audio_only: validateBoolean(roomInfo.audio_only),
  };
};

const sanitizeAdvanceOptions = (advanceOptionsInfo) => {
  if (typeof advanceOptionsInfo !== "object") {
    return [
      { battery_updates: false },
      { notify_video_resolution_change: false },
    ];
  }
  return [
    { battery_updates: validateBoolean(advanceOptionsInfo.battery_updates) },
    {
      notify_video_resolution_change: validateBoolean(
        advanceOptionsInfo.notify_video_resolution_change
      ),
    },
  ];
};

const validateString = (value) => (isString(value) ? value : "");

const validateBoolean = (value) => (isBoolean(value) ? value : false);

const sanitizeBooleanProperty = (property) =>
  property || property === undefined ? true : property;

export {
  sanitizeRoomEvents,
  sanitizeLocalInfoData,
  sanitizeBooleanProperty,
  sanitizeRoomData,
  sanitizeAdvanceOptions,
};
