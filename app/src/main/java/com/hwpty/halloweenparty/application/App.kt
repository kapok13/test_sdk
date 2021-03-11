package com.hwpty.halloweenparty.application

import android.app.Application
import com.facebook.FacebookSdk
import com.hwpty.halloweenparty.R
import com.hwpty.halloweenparty.presentation.activity.MenuActivity
import com.hwpty.halloweenparty.sdk.SDKConfig
import com.hwpty.halloweenparty.sdk.data.ConfigProvider
import com.hwpty.halloweenparty.sdk.data.SettingsStorageImpl

class App : Application() {

    companion object {
        var sdkConfig: SDKConfig = SDKConfig()
    }

    override fun onCreate() {
        sdkConfig.sweetieActivityClass = MenuActivity::class.java
        sdkConfig.setRemoteConfigProvider(ConfigProvider())
        sdkConfig.splashScreenDrawableResId = R.drawable.menu_background
        super.onCreate()
        SettingsStorageImpl(this, sdkConfig.sharedPrefsFilename)
        FacebookSdk.setAutoInitEnabled(false)
    }
}