package com.hwpty.halloweenparty.sdk.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.hwpty.halloweenparty.sdk.Utils;
import com.hwpty.halloweenparty.sdk.domain.SettingsStorage;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SettingsStorageImpl implements SettingsStorage {

    private static SettingsStorageImpl mInstance = null;
    private SharedPreferences mSecuredSharedPreferences = null;
    private static final String KEY_LAST_VISITED_LINK = "lvl";
    private static final String KEY_FACEBOOK_DEEP_LINK = "fbdl";


    public SettingsStorageImpl(Context context, String sharedPrefsFilename) {
        try {
            MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            mSecuredSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    sharedPrefsFilename,
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            mInstance = this;
            Utils.debugOutput("SettingsStorageImpl - initialised.");
        } catch (GeneralSecurityException | IOException e) {
            Utils.debugOutput("SettingsStorageImpl - NOT INITIALISED:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static SettingsStorage getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("SettingsStorageImpl must be initialised before call \"getInstance()\"!");
        }
        return (SettingsStorage) mInstance;
    }

    @Override
    public void saveFbDeepLink(String fbDeepLink) {
        SharedPreferences.Editor ed = mSecuredSharedPreferences.edit();
        ed.putString(KEY_FACEBOOK_DEEP_LINK, fbDeepLink);
        ed.apply();
    }

    @Override
    public String loadFbDeepLink() {
        return mSecuredSharedPreferences.getString(KEY_FACEBOOK_DEEP_LINK, "");
    }

    @Override
    public void saveLastVisitedLink(String url) {
        SharedPreferences.Editor ed = mSecuredSharedPreferences.edit();
        ed.putString(KEY_LAST_VISITED_LINK, url);
        ed.apply();
    }

    @Override
    public String loadLastVisitedLink() {
        return mSecuredSharedPreferences.getString(KEY_LAST_VISITED_LINK, "");
    }

}
