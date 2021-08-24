package com.rnenxrtc;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import enx_rtc_android.annotations.EnxAnnotationsToolbar;


public class EnxToolBarManager extends ViewGroupManager<EnxAnnotationsToolbar> {

    ReactContext mReactContext;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected EnxAnnotationsToolbar createViewInstance(ThemedReactContext reactContext) {
        try {
            return new EnxAnnotationsToolbar(reactContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

