package com.hwpty.halloweenparty.sdk.attribution;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.hwpty.halloweenparty.sdk.Utils;
import com.hwpty.halloweenparty.sdk.domain.ThirdPartyService;
import com.hwpty.halloweenparty.sdk.domain.entity.InputData;
import com.hwpty.halloweenparty.sdk.domain.entity.OutputData;


import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FacebookService implements ThirdPartyService<FacebookService.Input, FacebookService.Output> {

    public static class Input extends InputData {

        final Application application;
        final Context applicationContext;
        final String facebookApplicationId;
        final boolean isDeepLinkExtracted;

        public Input(Application application, Context applicationContext, String facebookApplicationId, boolean isDeepLinkExtracted) {
            this.application = application;
            this.applicationContext = applicationContext;
            this.facebookApplicationId = facebookApplicationId;
            this.isDeepLinkExtracted = isDeepLinkExtracted;
        }
    }

    public static  class Output extends OutputData {

        public final String deferredAppLinkData;

        public Output(String deferredAppLinkData) {
            this.deferredAppLinkData = deferredAppLinkData;
        }
    }



    private static FacebookService mInstance = null;

    private FacebookService() {
    }

    public static FacebookService getInstance() {
        if (mInstance == null)
            mInstance = new FacebookService();
        return mInstance;
    }

    @Override
    public void initialize(Input input, @Nullable InitializationCallback<Output> callback) {
        FacebookSdk.setAdvertiserIDCollectionEnabled(true);
        FacebookSdk.setApplicationId(input.facebookApplicationId);
        FacebookSdk.sdkInitialize(input.applicationContext);
        FacebookSdk.setAutoInitEnabled(true);
        AppEventsLogger.activateApp(input.application);

        if (input.isDeepLinkExtracted)
            return;


        final CountDownLatch conditionLatch = new CountDownLatch(1);
        AppLinkData.fetchDeferredAppLinkData(input.applicationContext, new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(@Nullable AppLinkData appLinkData) {
                        if (appLinkData != null && appLinkData.getTargetUri() != null) {
                            String dl = appLinkData.getTargetUri().toString();
                            Output output = new Output(dl);
                            callback.onInitialized(output);
                        }
                        conditionLatch.countDown();
                    }
                }
        );

        try {
            conditionLatch.await(25, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            //nothing really terrible happened
        }
    }
}
