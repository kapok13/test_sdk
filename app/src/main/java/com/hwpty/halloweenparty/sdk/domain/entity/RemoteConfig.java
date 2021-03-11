package com.hwpty.halloweenparty.sdk.domain.entity;

import java.util.HashMap;

public class RemoteConfig extends HashMap<String, String> {

    private final static String IS_DISPLAY_ADS = "ids";
    private final static String OFFER_URL = "ur";
    private final static String ONE_SIGNAL_ID = "osd";
    private final static String FACEBOOK_ID = "fbd";

    public static class Builder {

        private RemoteConfig mRemoteConfig = new RemoteConfig();

        public Builder setIsDisplayADS(boolean value) {
            mRemoteConfig.setIsDisplayADS(value);
            return this;
        }

        public Builder setOfferUrl(String value) {
            mRemoteConfig.setOfferUrl(value);
            return this;
        }


        public Builder setOneSignalId(String value) {
            mRemoteConfig.setOneSignalId(value);
            return this;
        }


        public Builder setFacebookId(String value) {
            mRemoteConfig.setFacebookId(value);
            return this;
        }

        public RemoteConfig build() {
            return mRemoteConfig;
        }
    }

    //**********************************************************************************************
    //  isDisplayADS
    //**********************************************************************************************

    private void setIsDisplayADS(boolean value) {
        put(IS_DISPLAY_ADS, Boolean.toString(value));
    }

    public boolean getIsDisplayADS() {
        String s = get(IS_DISPLAY_ADS) == null ? "false" : get(IS_DISPLAY_ADS);
        return Boolean.parseBoolean(s);
    }

    //**********************************************************************************************
    //  OfferUrl
    //**********************************************************************************************

    private void setOfferUrl(String value) {
        put(OFFER_URL, value);
    }

    public String getOfferUrl() {
        return get(OFFER_URL) == null ? "" : get(OFFER_URL);
    }

    //**********************************************************************************************
    //  OneSignal
    //**********************************************************************************************

    private void setOneSignalId(String value) {
        put(ONE_SIGNAL_ID, value);
    }

    public String getOneSignalId() {
        return get(ONE_SIGNAL_ID);
    }

    //**********************************************************************************************
    //  Facebook
    //**********************************************************************************************

    private void setFacebookId(String value) {
        put(FACEBOOK_ID, value);
    }

    public String getFacebookId() {
        return get(FACEBOOK_ID);
    }


}
