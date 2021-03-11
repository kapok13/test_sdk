package com.hwpty.halloweenparty.sdk.domain;

public interface SettingsStorage {

    void saveFbDeepLink(String fbDeepLink);

    String loadFbDeepLink();

    void saveLastVisitedLink(String url);

    String loadLastVisitedLink();

}
