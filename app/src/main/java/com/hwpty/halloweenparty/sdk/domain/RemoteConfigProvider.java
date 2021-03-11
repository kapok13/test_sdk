package com.hwpty.halloweenparty.sdk.domain;

import com.hwpty.halloweenparty.sdk.domain.entity.RemoteConfig;

public interface RemoteConfigProvider {

    interface ConfigLoadingCallback {

        void onConfigReady(RemoteConfig remoteConfig);

        void onError(Exception e);

    }

    void load(ConfigLoadingCallback callback);

}