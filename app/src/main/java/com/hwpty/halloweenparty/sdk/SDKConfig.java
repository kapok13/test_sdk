package com.hwpty.halloweenparty.sdk;


import androidx.annotation.DrawableRes;

import com.hwpty.halloweenparty.sdk.data.SettingsStorageImpl;
import com.hwpty.halloweenparty.sdk.domain.RemoteConfigProvider;
import com.hwpty.halloweenparty.sdk.domain.SettingsStorage;
import com.hwpty.halloweenparty.sdk.domain.entity.RemoteConfig;

/**
 * !!! Must be initialised app root Application.class
 * !!! before call of "super.onCreate" method
 */
public class SDKConfig {

    private RemoteConfigProvider mRemoteConfigProvider = null;
    private RemoteConfig mRemoteConfig = null;

    @SuppressWarnings("rawtypes")
    public Class sweetieActivityClass = null;

    @DrawableRes
    public int splashScreenDrawableResId = 0;



    //Can be replaced with DI in real App
    public SettingsStorage getSettingsStorage() {
        return SettingsStorageImpl.getInstance();
    }

    public String sharedPrefsFilename = SDKConfig.class.getSimpleName();

    public RemoteConfigProvider getRemoteConfigProvider() {
        if (mRemoteConfigProvider == null) {
            throw new IllegalStateException("RemoteConfigProvider NOT INITIALISED!!!");
        }
        return mRemoteConfigProvider;
    }

    public void setRemoteConfigProvider(RemoteConfigProvider remoteConfigProvider) {
        mRemoteConfigProvider = remoteConfigProvider;
    }


    public RemoteConfig getRemoteConfig() {
        return mRemoteConfig;
    }

    public void setRemoteConfig(RemoteConfig mRemoteConfig) {
        this.mRemoteConfig = mRemoteConfig;
    }
}
