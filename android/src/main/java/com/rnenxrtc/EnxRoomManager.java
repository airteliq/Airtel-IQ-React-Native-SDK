package com.rnenxrtc;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import enx_rtc_android.Controller.EnxAdvancedOptionsObserver;
import enx_rtc_android.Controller.EnxAnnotationObserver;
import enx_rtc_android.Controller.EnxBandwidthObserver;
import enx_rtc_android.Controller.EnxCanvasObserver;
import enx_rtc_android.Controller.EnxChairControlObserver;
import enx_rtc_android.Controller.EnxFileShareObserver;
import enx_rtc_android.Controller.EnxLockRoomManagementObserver;
import enx_rtc_android.Controller.EnxLogsObserver;
import enx_rtc_android.Controller.EnxMuteAudioStreamObserver;
import enx_rtc_android.Controller.EnxMuteRoomObserver;
import enx_rtc_android.Controller.EnxMuteVideoStreamObserver;
import enx_rtc_android.Controller.EnxNetworkObserever;
import enx_rtc_android.Controller.EnxOutBoundCallObserver;
import enx_rtc_android.Controller.EnxPlayerStatsObserver;
import enx_rtc_android.Controller.EnxPlayerView;
import enx_rtc_android.Controller.EnxReconnectObserver;
import enx_rtc_android.Controller.EnxRecordingObserver;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxScreenShareObserver;
import enx_rtc_android.Controller.EnxScreenShotObserver;
import enx_rtc_android.Controller.EnxStatsObserver;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;
import enx_rtc_android.Controller.EnxTalkerObserver;
import enx_rtc_android.Controller.EnxUtilityManager;

public class EnxRoomManager extends ReactContextBaseJavaModule implements EnxRoomObserver, EnxStreamObserver, EnxRecordingObserver, EnxScreenShareObserver, EnxTalkerObserver, EnxLogsObserver, EnxChairControlObserver, EnxMuteRoomObserver, EnxMuteAudioStreamObserver, EnxMuteVideoStreamObserver, EnxStatsObserver, EnxPlayerStatsObserver, EnxBandwidthObserver, EnxNetworkObserever, EnxReconnectObserver, EnxScreenShotObserver, EnxAdvancedOptionsObserver, EnxCanvasObserver, EnxFileShareObserver, EnxLockRoomManagementObserver, EnxOutBoundCallObserver, EnxAnnotationObserver {
    private ReactApplicationContext mReactContext = null;
    private ArrayList<String> jsEvents = new ArrayList<String>();
    private ArrayList<String> componentEvents = new ArrayList<String>();
    private final String roomPreface = "room:";
    private final String streamPreface = "stream:";
    private EnxRtc enxRtc;
    private EnxStream localStream;
    private EnxRoom mEnxRoom;
    EnxRN sharedState;
    private String localStreamId;

    public EnxRoomManager(ReactApplicationContext reactContext) {
        super(reactContext);
        sharedState = EnxRN.getSharedState();
        mReactContext = reactContext;
    }

    @ReactMethod
    public void setNativeEvents(ReadableArray events) {
        try {
            for (int i = 0; i < events.size(); i++) {
                jsEvents.add(events.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void initRoom() {
        enxRtc = new EnxRtc(mReactContext.getCurrentActivity(), this, this);
    }

    @ReactMethod
    public void removeNativeEvents(ReadableArray events) {
        for (int i = 0; i < events.size(); i++) {
            jsEvents.remove(events.getString(i));
        }
    }

    @ReactMethod
    public void setJSComponentEvents(ReadableArray events) {
        for (int i = 0; i < events.size(); i++) {
            componentEvents.add(events.getString(i));
        }
    }

    @ReactMethod
    public void removeJSComponentEvents(ReadableArray events) {
        for (int i = 0; i < events.size(); i++) {
            componentEvents.remove(events.getString(i));
        }
    }

    @ReactMethod
    public void joinRoom(String token, ReadableMap localStreamInfo, ReadableMap roomInfo, ReadableArray advanceOptions) {
        if (enxRtc != null) {
            try {
                localStream = enxRtc.joinRoom(token, EnxUtils.convertMapToJson(localStreamInfo), EnxUtils.convertMapToJson(roomInfo), getAdvancedOptionsObject(EnxUtils.convertArrayToJson(advanceOptions)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @ReactMethod
    public void muteSelfAudio(String localStreamId, boolean value) {
        ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
        EnxStream stream = mLocalStream.get(localStreamId);
        if (stream != null) {
            stream.muteSelfAudio(value);
        }
    }

    @ReactMethod
    public void muteSelfVideo(String localStreamId, boolean value) {
        ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
        EnxStream stream = mLocalStream.get(localStreamId);
        if (stream != null) {
            stream.muteSelfVideo(value);
        }
    }

    @ReactMethod
    public void initStream(String streamId) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        if (localStream != null) {
            mEnxStream.put(streamId, localStream);
            localStreamId = streamId;
        }
    }

    @ReactMethod
    public void publish() {
        if (localStreamId != null) {
            ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
            EnxStream localStream = mEnxStream.get(localStreamId);
            mEnxRoom.publish(localStream);
            localStream.setMuteAudioStreamObserver(this);
            localStream.setMuteVideoStreamObserver(this);
        }
    }

    @ReactMethod
    public void subscribe(String streamId, Callback callback) {
        ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
        mEnxRoom.subscribe(mSubscriberStreams.get(streamId));
        callback.invoke("Stream subscribed successfully.");
    }

    @ReactMethod
    public void getLocalStreamId(Callback callback) {
        callback.invoke(localStreamId);
    }

    @ReactMethod
    public void switchCamera(String localStreamId) {
        ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
        EnxStream stream = mLocalStream.get(localStreamId);
        if (stream != null) {
            stream.switchCamera();
        }
    }

    @ReactMethod
    public void startRecord() {
        if (mEnxRoom != null) {
            mEnxRoom.startRecord();
        }
    }

    @ReactMethod
    public void stopRecord() {
        if (mEnxRoom != null) {
            mEnxRoom.stopRecord();
        }
    }

    @ReactMethod
    public void getDevices(Callback callback) {
        if (mEnxRoom != null) {
            ArrayList<String> deviceList = (ArrayList<String>) mEnxRoom.getDevices();
            WritableArray array = Arguments.createArray();
            for (int i = 0; i < deviceList.size(); i++) {
                Object value = deviceList.get(i).trim();
                if (value instanceof String) {
                    array.pushString(deviceList.get(i));
                } else if (value == null) {
                    array.pushNull();
                }
            }
            callback.invoke(array);
        }
    }

    @ReactMethod
    public void getSelectedDevice(Callback callback) {
        if (mEnxRoom != null) {
            callback.invoke(mEnxRoom.getSelectedDevice());
        }
    }

    @ReactMethod
    public void getMaxTalkers() {
        if (mEnxRoom != null) {
            mEnxRoom.getMaxTalkers();
        }
    }

    @ReactMethod
    public void getTalkerCount() {
        if (mEnxRoom != null) {
            mEnxRoom.getTalkerCount();
        }
    }

    @ReactMethod
    public void setTalkerCount(int number) {
        if (mEnxRoom != null) {
            mEnxRoom.setTalkerCount(number);
        }
    }

    @ReactMethod
    public void hardMute() {
        if (mEnxRoom != null) {
            mEnxRoom.hardMute();
        }
    }

    @ReactMethod
    public void hardUnmute() {
        if (mEnxRoom != null) {
            mEnxRoom.hardUnMute();
        }
    }

    @ReactMethod
    public void enableLogs(boolean status) {
        EnxUtilityManager enxLogsUtil = EnxUtilityManager.getInstance();
        enxLogsUtil.enableLogs(status);
    }

    @ReactMethod
    public void switchMediaDevice(String audioDevice) {
        if (mEnxRoom != null) {
            mEnxRoom.switchMediaDevice(audioDevice);
        }
    }

    @ReactMethod
    public void requestFloor() {
        if (mEnxRoom != null) {
            mEnxRoom.requestFloor();
        }
    }

    @ReactMethod
    public void grantFloor(String clientId) {
        if (mEnxRoom != null) {
            mEnxRoom.grantFloor(clientId);
        }
    }

    @ReactMethod
    public void denyFloor(String clientId) {
        if (mEnxRoom != null) {
            mEnxRoom.denyFloor(clientId);
        }
    }

    @ReactMethod
    public void releaseFloor(String clientId) {
        if (mEnxRoom != null) {
            mEnxRoom.releaseFloor(clientId);
        }
    }

    @ReactMethod
    public void postClientLogs() {
        if (mEnxRoom != null) {
            mEnxRoom.postClientLogs();
        }
    }

    @ReactMethod
    public void changeToAudioOnly(boolean value) {
        if (mEnxRoom != null) {
            mEnxRoom.changeToAudioOnly(value);
        }
    }

    @ReactMethod
    public void hardMuteAudio(String streamId, String clientId) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        EnxStream localStream = mEnxStream.get(streamId);
        if (localStream != null) {
            localStream.hardMuteAudio(clientId);
        }
    }

    @ReactMethod
    public void hardUnmuteAudio(String streamId, String clientId) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        EnxStream localStream = mEnxStream.get(streamId);
        if (localStream != null) {
            localStream.hardUnMuteAudio(clientId);
        }
    }

    @ReactMethod
    public void hardMuteVideo(String streamId, String clientId) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        EnxStream localStream = mEnxStream.get(streamId);
        if (localStream != null) {
            localStream.hardMuteVideo(clientId);
        }
    }

    @ReactMethod
    public void hardUnmuteVideo(String streamId, String clientId) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        EnxStream localStream = mEnxStream.get(streamId);
        if (localStream != null) {
            localStream.hardUnMuteVideo(clientId);
        }
    }

    @ReactMethod
    public void stopVideoTracksOnApplicationBackground(boolean videoMuteRemoteStream, boolean videoMuteLocalStream) {
        if (mEnxRoom != null) {
            mEnxRoom.stopVideoTracksOnApplicationBackground(videoMuteRemoteStream, videoMuteLocalStream);
        }
    }

    @ReactMethod
    public void startVideoTracksOnApplicationForeground(boolean restoreVideoRemoteStream, boolean restoreVideoLocalStream) {
        if (mEnxRoom != null) {
            mEnxRoom.startVideoTracksOnApplicationForeground(restoreVideoRemoteStream, restoreVideoLocalStream);
        }
    }

    @ReactMethod
    public void changePlayerScaleType(final int mode, final String streamId) {
        if (mReactContext.getCurrentActivity() == null) {
            return;
        }
        mReactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
                if (mPlayers.containsKey(streamId)) {
                    if (mode == 1) {
                        mPlayers.get(streamId).setScalingType(EnxPlayerView.ScalingType.SCALE_ASPECT_FILL);
                    } else {
                        mPlayers.get(streamId).setScalingType(EnxPlayerView.ScalingType.SCALE_ASPECT_FIT);
                    }
                }
            }
        });
    }

    @ReactMethod
    public void setZOrderMediaOverlay(final boolean isOverlay, final String streamId) {
        if (mReactContext.getCurrentActivity() == null) {
            return;
        }
        mReactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
                if (mPlayers.containsKey(streamId)) {
                    mPlayers.get(streamId).setZOrderMediaOverlay(isOverlay);
                }
            }
        });
    }

    @ReactMethod
    public void setConfigureOption(final ReadableMap dataObject, final String streamId) {
        Log.e("setConfigureOption", dataObject.toString());
        if (mReactContext.getCurrentActivity() == null) {
            return;
        }
        mReactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
                if (mPlayers.containsKey(streamId)) {
                    try {
                        mPlayers.get(streamId).setConfigureOption(EnxUtils.convertMapToJson(dataObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @ReactMethod
    public void enableStats(boolean isEnabled) {
        if (mEnxRoom != null) {
            mEnxRoom.enableStats(true, this);
        }
    }

    @ReactMethod
    public void enablePlayerStats(boolean isEnabled, String streamId) {
        ConcurrentHashMap<String, EnxPlayerView> playerView = sharedState.getPlayerView();
        if (playerView.get(streamId) != null) {
            playerView.get(streamId).enablePlayerStats(isEnabled, this);
        }

    }

    @ReactMethod
    public void sendData(String streamId, ReadableMap dataObject) {
        ConcurrentHashMap<String, EnxStream> mEnxStream = sharedState.getLocalStream();
        EnxStream localStream = mEnxStream.get(streamId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", dataObject.getString("message"));
            jsonObject.put("from", dataObject.getString("from"));
            jsonObject.put("timestamp", dataObject.getDynamic("timestamp"));
            if (localStream != null) {
                localStream.sendData(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void sendMessage(String message, boolean broadcast, ReadableArray clientList) {
        if (mEnxRoom != null) {
            mEnxRoom.sendMessage(message, broadcast, Arguments.toList(clientList));
        }
    }

    @ReactMethod
    public void sendUserData(ReadableMap message, boolean broadcast, ReadableArray clientList) throws JSONException {
        if (mEnxRoom != null) {
            mEnxRoom.sendUserData(EnxUtils.convertMapToJson(message), broadcast, Arguments.toList(clientList));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @ReactMethod
    public void sendFiles(final boolean broadcast, final ReadableArray clientList) {
        try {
            mReactContext.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mEnxRoom != null) {
                            mEnxRoom.sendFiles(broadcast, Arguments.toList(clientList), getCurrentActivity());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void downloadFile(ReadableMap data, boolean isAutoSave) throws JSONException {
        if (mEnxRoom != null) {
            mEnxRoom.downloadFile(EnxUtils.convertMapToJson(data), isAutoSave);
        }
    }

    @ReactMethod
    public void getAvailableFiles() throws JSONException {
        if (mEnxRoom != null) {
            JSONObject object = mEnxRoom.getAvailableFiles();
            if (object.optJSONArray("files").length() == 0) {
                sendEventWithString(this.getReactApplicationContext(), roomPreface + "getAvailableFiles", "No files Available");
            } else {
                try {
                    sendEventMap(this.getReactApplicationContext(), roomPreface + "getAvailableFiles", EnxUtils.jsonToReact(mEnxRoom.getAvailableFiles()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @ReactMethod
    public void setAdvancedOptions(ReadableArray array) {
        if (mEnxRoom != null) {
            try {
                mEnxRoom.setAdvancedOptions(EnxUtils.convertArrayToJson(array), EnxRoomManager.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @ReactMethod
    public void getAdvancedOptions() {
        if (mEnxRoom != null) {
            mEnxRoom.getAdvancedOptions();
        }
    }

    @ReactMethod
    public void captureScreenShot(final String streamId) {
        if (mReactContext.getCurrentActivity() == null) {
            return;
        }
        mReactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
                if (mPlayers.containsKey(streamId)) {
                    mPlayers.get(streamId).captureScreenShot(EnxRoomManager.this);
                }
            }
        });
    }

    @ReactMethod
    public void switchUserRole(String clientId) {
        if (mEnxRoom != null) {
            mEnxRoom.switchUserRole(clientId);
        }
    }

    @ReactMethod
    public void cancelUpload(String upJobId) {
        if (mEnxRoom != null) {
            mEnxRoom.cancelUpload(Integer.parseInt(upJobId));
        }
    }

    @ReactMethod
    public void cancelDownload(String jobId) {
        if (mEnxRoom != null) {
            mEnxRoom.cancelDownload(Integer.parseInt(jobId));
        }
    }

    @ReactMethod
    public void cancelAllUploads() {
        if (mEnxRoom != null) {
            mEnxRoom.cancelAllUploads();
        }
    }

    @ReactMethod
    public void cancelAllDownloads() {
        if (mEnxRoom != null) {
            mEnxRoom.cancelAllDownloads();
        }
    }

    @ReactMethod
    public void lockRoom() {
        if (mEnxRoom != null) {
            mEnxRoom.lockRoom();
        }
    }

    @ReactMethod
    public void unLockRoom() {
        if (mEnxRoom != null) {
            mEnxRoom.unLockRoom();
        }
    }

    @ReactMethod
    public void makeOutboundCall(String number) {
        if (mEnxRoom != null) {
            mEnxRoom.makeOutboundCall(number);
        }
    }

    @ReactMethod
    public void extendConferenceDuration() {
        if (mEnxRoom != null) {
            mEnxRoom.extendConferenceDuration();
        }
    }

    @ReactMethod
    public void destroy() {
        if (mEnxRoom != null) {
            mEnxRoom.destroy();
        }
    }

    @ReactMethod
    public void dropUser(ReadableArray clientList) {
        if (mEnxRoom != null) {
            mEnxRoom.dropUser(Arguments.toList(clientList));
        }
    }

    @ReactMethod
    public WritableMap getRoomMetadata() {
        if (mEnxRoom != null) {
            try {
                return EnxUtils.jsonToReact(mEnxRoom.getRoomMetadata());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @ReactMethod
    public WritableMap whoAmI() {
        if (mEnxRoom != null) {
            try {
                return EnxUtils.jsonToReact(mEnxRoom.whoAmI());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @ReactMethod
    public boolean isPublishing() {
        if (mEnxRoom != null) {
            return mEnxRoom.isPublishing();
        }
        return false;
    }

    @ReactMethod
    public boolean isRoomActiveTalker() {
        if (mEnxRoom != null) {
            return mEnxRoom.isRoomActiveTalker();
        }
        return false;
    }

    @ReactMethod
    public void setAudioOnlyMode(boolean status) {
        if (mEnxRoom != null) {
            mEnxRoom.setAudioOnlyMode(status);
        }
    }

    @ReactMethod
    public void enableProximitySensor(boolean status) {
        if (mEnxRoom != null) {
            mEnxRoom.enableProximitySensor(status);
        }
    }

    @ReactMethod
    public void getClientId(Callback callback) {
        if (mEnxRoom != null) {
            callback.invoke(mEnxRoom.getClientId());
        }
    }

    @ReactMethod
    public String getRoomId() {
        if (mEnxRoom != null) {
            return mEnxRoom.getRoomId();
        }
        return "";
    }

    @ReactMethod
    public String getMode() {
        if (mEnxRoom != null) {
            return mEnxRoom.getMode();
        }
        return "";
    }

    @ReactMethod
    public void muteSubscribeStreamsAudio(boolean status) {
        if (mEnxRoom != null) {
            mEnxRoom.muteSubscribeStreamsAudio(status);
        }
    }

    @ReactMethod
    public String getRole() {
        if (mEnxRoom != null) {
            return mEnxRoom.getRole();
        }
        return "";
    }

    @ReactMethod
    public String getClientName() {
        if (mEnxRoom != null) {
            return mEnxRoom.getClientName();
        }
        return "";
    }

    @ReactMethod
    public void setReceiveVideoQuality(ReadableMap map) {
        if (mEnxRoom != null) {
            try {
                mEnxRoom.setReceiveVideoQuality(EnxUtils.convertMapToJson(map));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @ReactMethod
    public String getReceiveVideoQuality(String streamType) {
        if (mEnxRoom != null) {
            return mEnxRoom.getReceiveVideoQuality(streamType);
        }
        return "";
    }

    @ReactMethod
    public WritableArray getUserList(Callback callback) {
        if (mEnxRoom != null) {
            try {
                return EnxUtils.convertJsonToArray(mEnxRoom.getUserList());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @ReactMethod
    public boolean isLocal(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            return true;
        }
        return false;
    }

    @ReactMethod
    public boolean hasData(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.hasData();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.hasData();
            }
        }
        return false;
    }

    @ReactMethod
    public boolean hasScreen(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.hasScreen();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.hasScreen();
            }
        }
        return false;
    }

    @ReactMethod
    public boolean hasAudio(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.hasAudio();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.hasAudio();
            }
        }
        return false;
    }

    @ReactMethod
    public boolean hasVideo(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.hasVideo();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.hasVideo();
            }
        }
        return false;
    }

    @ReactMethod
    public boolean isAudioOnlyStream(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.isAudioOnlyStream();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.isAudioOnlyStream();
            }
        }
        return false;
    }

    @ReactMethod
    public String getReasonForMuteVideo(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.getReasonForMuteVideo();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.getReasonForMuteVideo();
            }
        }
        return "";
    }

    @ReactMethod
    public String getMediaType(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (stream != null) {
                return stream.getMediaType();
            }
        } else {
            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
            EnxStream stream = mSubscriberStreams.get(streamId);
            if (stream != null) {
                return stream.getMediaType();
            }
        }
        return "";
    }

    @ReactMethod
    public void startAnnotation(String streamId) {
        if (streamId.equalsIgnoreCase(localStreamId)) {
            ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
            EnxStream stream = mLocalStream.get(localStreamId);
            if (mEnxRoom != null && stream != null) {
                mEnxRoom.startAnnotation(stream);
            }
        } else {
            if (mEnxRoom.getActiveTalkers() != null && mEnxRoom.getActiveTalkers().size() > 0) {
                EnxStream enxStream = mEnxRoom.getActiveTalkers().get(0);
                mEnxRoom.startAnnotation(/*enxAnnotationsToolbar,*/enxStream);
            } else {
                Log.e("startAnnotation", "No Talkers Present");
            }
        }
//            ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
//            EnxStream stream = mSubscriberStreams.get(streamId);
//            if (mEnxRoom != null && stream != null) {
//                mEnxRoom.startAnnotation(stream);
//            }
    }

    @ReactMethod
    public void stopAnnotation() {
        if (mEnxRoom != null) {
            mEnxRoom.stopAnnotations();
        }
    }


    @ReactMethod
    public void disconnect() {
        if (mEnxRoom != null) {
            ConcurrentHashMap<String, EnxPlayerView> playerView = sharedState.getPlayerView();
            for (String key : playerView.keySet()) {
                if (key.length() > 1) {
                    EnxPlayerView playerView1 = playerView.get(key);
                    if (playerView1 != null) {
                        playerView1.release();
                        playerView1 = null;
                    }
                }
            }
            mEnxRoom.disconnect();
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
        mEnxRoom = enxRoom;
        WritableMap streamInfo = null;
        try {
            streamInfo = EnxUtils.jsonToReact(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onRoomConnected", streamInfo);
        mEnxRoom.setRecordingObserver(this);
        mEnxRoom.setScreenShareObserver(this);
        mEnxRoom.setTalkerObserver(this);
        mEnxRoom.setLogsObserver(this);
        mEnxRoom.setChairControlObserver(this);
        mEnxRoom.setMuteRoomObserver(this);
        mEnxRoom.setBandwidthObserver(this);
        mEnxRoom.setNetworkChangeObserver(this);
        mEnxRoom.setReconnectObserver(this);
        mEnxRoom.setCanvasObserver(this);
        mEnxRoom.setFileShareObserver(this);
        mEnxRoom.setLockRoomManagementObserver(this);
        mEnxRoom.setOutBoundCallObserver(this);
        mEnxRoom.setAnnotationObserver(this);
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
        for (String key : mLocalStream.keySet()) {
            EnxStream stream = mLocalStream.get(key);
            if (stream != null) {
                stream.detachRenderer();
            }
        }
        ConcurrentHashMap<String, EnxStream> mRemoteStream = sharedState.getRemoteStream();
        for (String key : mRemoteStream.keySet()) {
            EnxStream stream = mRemoteStream.get(key);
            if (stream != null) {
                stream.detachRenderer();
            }
        }
        if (mEnxRoom != null) {
            mEnxRoom = null;
        }
        if (enxRtc != null) {
            enxRtc = null;
        }
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onRoomError", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUserConnected", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUserDisConnected", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        WritableMap streamInfo = EnxUtils.customJSONObject("The stream has been published.", "0", localStreamId);
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onPublishedStream", streamInfo);
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
        mSubscriberStreams.put(enxStream.getId(), enxStream);
        WritableMap streamInfo = EnxUtils.prepareJSStreamMap(enxStream);
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onStreamAdded", streamInfo);
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
        ConcurrentHashMap<String, EnxStream> mSubscriberStreams = sharedState.getRemoteStream();
        mSubscriberStreams.put(enxStream.getId(), enxStream);
        WritableMap streamInfo = EnxUtils.prepareJSStreamMap(enxStream);
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onSubscribedStream", streamInfo);
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        ConcurrentHashMap<String, EnxStream> mLocalStream = sharedState.getLocalStream();
        for (String key : mLocalStream.keySet()) {
            EnxStream stream = mLocalStream.get(key);
            if (stream != null) {
                stream.detachRenderer();
            }
        }
        ConcurrentHashMap<String, EnxStream> mRemoteStream = sharedState.getRemoteStream();
        for (String key : mRemoteStream.keySet()) {
            EnxStream stream = mRemoteStream.get(key);
            if (stream != null) {
                stream.detachRenderer();
            }
        }
        if (mEnxRoom != null) {
            mEnxRoom = null;
        }
        if (enxRtc != null) {
            enxRtc = null;
        }
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onRoomDisConnected", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActiveTalkerList(JSONObject jsonObject) {
        try {
            sendEventMapArray(this.getReactApplicationContext(), roomPreface + "onActiveTalkerList", EnxUtils.convertJsonToArray(jsonObject.optJSONArray("activeList")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventError(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onEventError", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onEventInfo", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifyDeviceUpdate(String s) {
        sendEventWithString(this.getReactApplicationContext(), roomPreface + "onNotifyDeviceUpdate", String.valueOf(s));
    }

    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAcknowledgedSendData", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onMessageReceived", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserDataReceived(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUserDataReceived", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCanvasStarted(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onCanvasStarted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCanvasStarted(EnxStream enxStream) {

    }

    @Override
    public void onCanvasStopped(JSONObject jsonObject) {
        ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
        if (mPlayers.containsKey(jsonObject.optString("streamId"))) {
            mPlayers.remove(jsonObject.optString("streamId"));
        }
        ConcurrentHashMap<String, FrameLayout> mLocalStreamViewContainers = sharedState.getStreamViewContainers();
        if (mLocalStreamViewContainers.containsKey(jsonObject.optString("streamId"))) {
            mLocalStreamViewContainers.remove(jsonObject.optString("streamId"));
        }
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onCanvasStopped", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCanvasStopped(EnxStream enxStream) {

    }

    @Override
    public void onStartCanvasAck(JSONObject jsonObject) {

    }

    @Override
    public void onStoppedCanvasAck(JSONObject jsonObject) {

    }

    @Override
    public void onSwitchedUserRole(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onSwitchedUserRole", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserRoleChanged(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUserRoleChanged", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConferencessExtended(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onConferencessExtended", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConferenceRemainingDuration(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onConferenceRemainingDuration", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckDropUser(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAckDropUser", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckDestroy(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAckDestroy", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onAudioEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onVideoEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onReceivedData", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onRemoteStreamAudioMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onRemoteStreamAudioUnMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onRemoteStreamVideoMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onRemoteStreamVideoUnMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartRecordingEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onStartRecordingEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomRecordingOn(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onRoomRecordingOn", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStopRecordingEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onStopRecordingEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomRecordingOff(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onRoomRecordingOff", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScreenSharedStarted(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onScreenSharedStarted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScreenSharedStopped(JSONObject jsonObject) {
        ConcurrentHashMap<String, EnxPlayerView> mPlayers = sharedState.getPlayerView();
        if (mPlayers.containsKey(jsonObject.optString("streamId"))) {
            mPlayers.remove(jsonObject.optString("streamId"));
        }
        ConcurrentHashMap<String, FrameLayout> mLocalStreamViewContainers = sharedState.getStreamViewContainers();
        if (mLocalStreamViewContainers.containsKey(jsonObject.optString("streamId"))) {
            mLocalStreamViewContainers.remove(jsonObject.optString("streamId"));
        }
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onScreenSharedStopped", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScreenSharedStarted(EnxStream enxStream) {

    }

    @Override
    public void onScreenSharedStopped(EnxStream enxStream) {

    }

    @Override
    public void onSetTalkerCount(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onSetTalkerCount", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetTalkerCount(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onGetTalkerCount", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMaxTalkerCount(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onMaxTalkerCount", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogUploaded(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onLogUploaded", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFloorRequested(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFloorRequested", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFloorRequestReceived(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFloorRequestReceived", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProcessFloorRequested(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onProcessFloorRequested", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGrantedFloorRequest(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onGrantedFloorRequest", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeniedFloorRequest(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onDeniedFloorRequest", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReleasedFloorRequest(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onReleasedFloorRequest", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onFloorCancelled(JSONObject jsonObject) {
//        try {
//            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFloorCancelled", EnxUtils.jsonToReact(jsonObject));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onFloorFinished(JSONObject jsonObject) {
//        try {
//            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFloorFinished", EnxUtils.jsonToReact(jsonObject));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onCancelledFloorRequest(JSONObject jsonObject) {
//        try {
//            sendEventMap(this.getReactApplicationContext(), roomPreface + "onCancelledFloorRequest", EnxUtils.jsonToReact(jsonObject));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onFinishedFloorRequest(JSONObject jsonObject) {
//        try {
//            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFinishedFloorRequest", EnxUtils.jsonToReact(jsonObject));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onHardMutedAudio(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onHardMutedAudio", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHardUnMutedAudio(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onHardUnMutedAudio", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardMuteAudio(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onReceivedHardMuteAudio", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardUnMuteAudio(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onReceivedHardUnMuteAudio", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHardMutedVideo(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onHardMutedVideo", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHardUnMutedVideo(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onHardUnMutedVideo", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardMuteVideo(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onReceivedHardMuteVideo", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardUnMuteVideo(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onReceivedHardUnMuteVideo", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHardMuted(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onHardMuted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onReceivedHardMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHardUnMuted(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onHardUnMuted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedHardUnMute(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onReceivedHardUnMute", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAcknowledgeStats(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAcknowledgeStats", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedStats(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onReceivedStats", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerStats(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), streamPreface + "onPlayerStats", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBandWidthUpdated(JSONArray jsonArray) {
        try {
            sendEventMapArray(this.getReactApplicationContext(), roomPreface + "onBandWidthUpdated", EnxUtils.convertJsonToArray(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShareStreamEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onShareStreamEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCanvasStreamEvent(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onCanvasStreamEvent", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionInterrupted(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onConnectionInterrupted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionLost(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onConnectionLost", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReconnect(String s) {
        sendEventWithString(this.getReactApplicationContext(), roomPreface + "onReconnect", String.valueOf(s));
    }

    @Override
    public void onUserReconnectSuccess(EnxRoom enxRoom, JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUserReconnectSuccess", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnCapturedView(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        sendEventWithString(this.getReactApplicationContext(), roomPreface + "OnCapturedView", Base64.encodeToString(byteArray, Base64.DEFAULT));
    }

    @Override
    public void onAdvancedOptionsUpdate(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAdvancedOptionsUpdate", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetAdvancedOptions(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onGetAdvancedOptions", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadStarted(JSONObject jsonObject) {
        Log.e("onFileUploadStarted", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileUploadStarted", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitFileUpload(JSONObject jsonObject) {
        Log.e("OnInitFileUpload", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onInitFileUpload", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileAvailable(JSONObject jsonObject) {
        Log.e("onFileAvailable", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileAvailable", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploaded(JSONObject jsonObject) {
        Log.e("onFileUploaded", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileUploaded", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadCancelled(JSONObject jsonObject) {
        Log.e("onFileUploadCancelled", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileUploadCancelled", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadFailed(JSONObject jsonObject) {
        Log.e("onFileUploadFailed", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileUploadFailed", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileDownloaded(JSONObject jsonObject) {
        Log.e("onFileDownloaded", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileDownloaded", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileDownloadCancelled(JSONObject jsonObject) {
        Log.e("onFileDownloadCancelled", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileDownloadCancelled", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileDownloadFailed(JSONObject jsonObject) {
        Log.e("onFileDownloadFailed", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onFileDownloadFailed", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitFileDownload(JSONObject jsonObject) {
        Log.e("onInitFileDownload", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onInitFileDownload", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckLockRoom(JSONObject jsonObject) {
        Log.e("onAckLockRoom", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAckLockRoom", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckUnLockRoom(JSONObject jsonObject) {
        Log.e("onAckUnLockRoom", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onAckUnLockRoom", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLockedRoom(JSONObject jsonObject) {
        Log.e("onLockedRoom", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onLockedRoom", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnLockedRoom(JSONObject jsonObject) {
        Log.e("onUnLockedRoom", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onUnLockedRoom", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOutBoundCallInitiated(JSONObject jsonObject) {
        Log.e("onOutBoundCallInitiated", jsonObject.toString());
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onOutBoundCallInitiated", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialStateEvents(EnxRoom.EnxOutBoundCallState enxOutBoundCallState) {
        Log.e("onDialStateEvents", enxOutBoundCallState.toString());
        try {
            sendEventWithString(this.getReactApplicationContext(), roomPreface + "onDialStateEvents", String.valueOf(enxOutBoundCallState));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getLocalStreamJsonObject(ReadableMap localStreamInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("audio", localStreamInfo.getBoolean("audio"));
            jsonObject.put("video", localStreamInfo.getBoolean("video"));
            jsonObject.put("data", localStreamInfo.getBoolean("data"));
            jsonObject.put("maxVideoBW", localStreamInfo.getString("maxVideoBW"));
            jsonObject.put("minVideoBW", localStreamInfo.getString("minVideoBW"));
            JSONObject videoSize = new JSONObject();
            videoSize.put("minWidth", localStreamInfo.getString("minWidth"));
            videoSize.put("minHeight", localStreamInfo.getString("minHeight"));
            videoSize.put("maxWidth", localStreamInfo.getString("maxWidth"));
            videoSize.put("maxHeight", localStreamInfo.getString("maxHeight"));
            jsonObject.put("videoSize", videoSize);
            jsonObject.put("audioMuted", localStreamInfo.getBoolean("audioMuted"));
            jsonObject.put("videoMuted", localStreamInfo.getBoolean("videoMuted"));
            jsonObject.put("name", localStreamInfo.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getRoomInfoObject(ReadableMap roomInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("allow_reconnect", roomInfo.getBoolean("allow_reconnect"));
            jsonObject.put("number_of_attempts", roomInfo.getString("number_of_attempts"));
            jsonObject.put("timeout_interval", roomInfo.getString("timeout_interval"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getAdvancedOptionsObject(JSONArray advanceOptions) {
        Log.e("getAdvancedOptions", advanceOptions.toString());
//        [{"battery_updates":false},{"notify_video_resolution_change":false}]
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("options", advanceOptions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    private JSONObject getEventObject(String eventName, boolean value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", eventName);
            jsonObject.put("enable", value);
        } catch (JSONException e) {

        }
        return jsonObject;
    }

    private static boolean contains(ArrayList array, String value) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void sendEventMapArray(ReactContext reactContext, String eventName, @Nullable WritableArray eventData) {
        if (contains(jsEvents, eventName) || contains(componentEvents, eventName)) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, eventData);
        }
    }

    private void sendEventMap(ReactContext reactContext, String eventName, @Nullable WritableMap eventData) {
        if (contains(jsEvents, eventName) || contains(componentEvents, eventName)) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, eventData);
        }
    }

    private void sendEventWithString(ReactContext reactContext, String eventName, String eventString) {
        if (contains(jsEvents, eventName) || contains(componentEvents, eventName)) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, eventString);
        }
    }

    @Override
    public void onAnnotationStarted(EnxStream enxStream) {
        WritableMap streamInfo = EnxUtils.customJSONObject("Annotation has been started.", "0", enxStream.getId().toString());
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onAnnotationStarted", streamInfo);
    }

    @Override
    public void onStartAnnotationAck(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onStartAnnotationAck", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnnotationStopped(EnxStream enxStream) {
        WritableMap streamInfo = EnxUtils.customJSONObject("Annotation has been stopped.", "0", enxStream.getId().toString());
        sendEventMap(this.getReactApplicationContext(), roomPreface + "onAnnotationStopped", streamInfo);
    }

    @Override
    public void onStoppedAnnotationAck(JSONObject jsonObject) {
        try {
            sendEventMap(this.getReactApplicationContext(), roomPreface + "onStoppedAnnotationAck", EnxUtils.jsonToReact(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
