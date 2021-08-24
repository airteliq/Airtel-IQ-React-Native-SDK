import { reassignEvents } from "./EnxHelper";

const sanitizePlayerViewEvents = events => {
  if (typeof events !== 'object') {
    return {};
  }
  const customEvents = {
    ios: {
      audioEvent: 'didAudioEvent', /* Event called on perform audio actions on the streams */
      videoEvent: 'didVideoEvent', /* Event called on perform video actions on the streams. */
      hardMuteAudio: 'didHardMuteAudio', /* Event called on Success of single user mute by moderator. This delegate method is for moderator. */
      hardUnmuteAudio: 'didhardUnmuteAudio', /* Event called on Success of single user unmute by moderator. This delegate method is for moderator. */
      recievedHardMutedAudio: 'didRecievedHardMutedAudio', /* Event called on Success of single user mute by moderator. This delegate method is for participant. */
      recievedHardUnmutedAudio: 'didRecievedHardUnmutedAudio', /* Event called on Success of single user unmute by moderator. This delegate method is for participant. */
      hardVideoMute: 'didHardVideoMute', /* Event called when a hard mute video alert moderator received from server. This delegate is for moderator. */
      hardVideoUnmute: 'didHardVideoUnMute', /* Event called when a hard unmute video alert moderator received from server. This delegate is for moderator. */
      receivehardMuteVideo: 'didReceivehardMuteVideo', /* Event called when a hard mute video alert participant received from server. */
      recivehardUnmuteVideo: 'didRecivehardUnmuteVideo', /* Event called when a hard unmute video alert participant received from server. */
      receiveData: 'didReceiveData', /* Event called when receive data on the streams. */
      remoteStreamAudioMute :'didRemoteStreamAudioMute', /* Event called when a self mute audio alert participant received from server. */
      remoteStreamAudioUnMute:'didRemoteStreamAudioUnMute', /* Event called when a self unmute audio alert participant received from server. */
      remoteStreamVideoMute:'didRemoteStreamVideoMute', /* Event called when a self mute video alert participant received from server. */
      remoteStreamVideoUnMute:'didRemoteStreamVideoUnMute', /* Event called when a self unmute video alert participant received from server. */
      playerStats: 'didPlayerStats',  /* Event called to receive particular player stats */
      isLocal: "isLocal", /* Event to check stream is local or remote*/
      hasScreen: "hasScreen", /* Event to check screen share stream or not*/
      hasAudio: "hasAudio", /* Event to check stream has audio data*/
      hasVideo: "hasVideo", /* Event to check stream has video data*/
      hasData: "hasData", /* Event to check stream has data*/
      isAudioOnlyStream: "isAudioOnlyStream", /* Event to check stream is Audio only mode*/
      getReasonForMuteVideo: "getReasonForMuteVideo", /* Event to get reason of stream muted*/
      getMediaType: "getMediaType", /* Event to get media type of stream*/
      getVideoAspectRatio: "getVideoAspectRatio" /* Event to get video aspect ratio of stream*/
    },
    android: {
      audioEvent: 'onAudioEvent',
      videoEvent: 'onVideoEvent',
      hardMuteAudio: 'onHardMutedAudio',
      hardUnmuteAudio: 'onHardUnMutedAudio',
      recievedHardMutedAudio: 'onReceivedHardMuteAudio',
      recievedHardUnmutedAudio: 'onReceivedHardUnMuteAudio',
      hardVideoMute: 'onHardMutedVideo',
      hardVideoUnmute: 'onHardUnMutedVideo',
      receivehardMuteVideo: 'onReceivedHardMuteVideo',
      recivehardUnmuteVideo: 'onReceivedHardUnMuteVideo',
      receiveData: 'onReceivedData',
      remoteStreamAudioMute :'onRemoteStreamAudioMute',
      remoteStreamAudioUnMute:'onRemoteStreamAudioUnMute',
      remoteStreamVideoMute:'onRemoteStreamVideoMute',
      remoteStreamVideoUnMute:'onRemoteStreamVideoUnMute',
      playerStats:'onPlayerStats',
    }
  };
  return reassignEvents('stream', customEvents, events);
};

export { sanitizePlayerViewEvents };
