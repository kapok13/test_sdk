package com.hwpty.halloweenparty.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.hwpty.halloweenparty.BuildConfig;
import com.hwpty.halloweenparty.application.App;
import com.hwpty.halloweenparty.sdk.Utils;
import com.hwpty.halloweenparty.sdk.attribution.FacebookService;
import com.hwpty.halloweenparty.sdk.attribution.OneSignalService;
import com.hwpty.halloweenparty.sdk.domain.RemoteConfigProvider;
import com.hwpty.halloweenparty.sdk.domain.entity.RemoteConfig;

import org.jetbrains.annotations.NotNull;

import static com.hwpty.halloweenparty.sdk.Utils.isNullOrEmpty;

@SuppressWarnings("FieldCanBeLocal")
public class AuthActivity extends AppCompatActivity implements AdWebViewClient.ShouldOpenLocalhostListener {


    /**
     * This activity class replaces two separate activities to pack SDK code into single file.
     * EXTRA_AT_ACTIVITY_MODE is an argument to router of this activity.
     * EXTRA_AT_ACTIVITY_MODE=0 or not defined - launcher activity
     * EXTRA_AT_ACTIVITY_MODE=1 - ADs activity
     * Activity stores it's mode in iActivityMode.
     */
    public static final String EXTRA_AT_ACTIVITY_MODE = "mode";
    private final int ACTIVITY_MODE_LAUNCHER = 0;
    private final int ACTIVITY_MODE_ADS = 1;
    private int iActivityMode = 0;

    /**
     * Current remote configuration is stored here
     */
    private Context mSelfContext;
    private Intent intentSweetie;
    private CustomWebView adView;
    private FrameLayout flMain;

    //**********************************************************************************************
    //  Activity Overrides
    //**********************************************************************************************

    /**
     * True onCreate of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelfContext = this;
        intentSweetie = new Intent(mSelfContext, App.Companion.getSdkConfig().sweetieActivityClass);

        //get activity mode (route)
        Intent intent = getIntent();
        iActivityMode = intent.getIntExtra(EXTRA_AT_ACTIVITY_MODE, 0);
        //call corresponding onCreate
        switch (iActivityMode) {
            case ACTIVITY_MODE_ADS:
                Utils.debugOutput("Launching as ADs");
                onCreateAds(savedInstanceState);
                break;
            case ACTIVITY_MODE_LAUNCHER:
            default:
                Utils.debugOutput("Launching in launcher mode");
                onCreateLauncher(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView == null) return;
        adView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView == null) return;
        adView.onPause();
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle outState, @NotNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (adView == null) return;
        adView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (adView == null) return;
        adView.restoreState(savedInstanceState);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        enterFullScreenUiMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterFullScreenUiMode();
        }
    }

    /**
     * 1) Some calls must be passed into AdvancedWebView instance.
     * 2) Eat all others "BackPressed" events to prevent accidentally closing the App
     */
    @Override
    public void onBackPressed() {
        if (adView == null) return;
        if (adView.canGoBack()) {
            adView.goBack();
        }
    }


    //**********************************************************************************************
    //  ADS
    //**********************************************************************************************

    /**
     * This onCreate will be called in ADs mode
     * 1. Show AdvancedWebView with URL provided in remote config.
     * If there is saved deep link data, URL's query parameters will be updated to
     * correspond ones from deep link.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void onCreateAds(Bundle savedInstanceState) {
        //allow WebView debugging for debug builds
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //init WebView
        AdWebViewClient wvClient = new AdWebViewClient(this);
        adView = new CustomWebView(this);
        adView.setWebViewClient(wvClient);
        if (savedInstanceState == null) {
            String sOfferUrl = App.Companion.getSdkConfig().getRemoteConfig().getOfferUrl();
            Utils.debugOutput("Offer URL: " + sOfferUrl);

            //load LastVisitedLink is exist
            String lastVisitedLink = App.Companion.getSdkConfig().getSettingsStorage().loadLastVisitedLink();
            if (!isNullOrEmpty(lastVisitedLink)) {
                Utils.debugOutput("Skipping OfferUrl -> Loading LastVisitedLink");
                wvClient.setBaseURL(lastVisitedLink);
                adView.loadUrl(lastVisitedLink);
            } else {
                //append facebook deep link query parameters
                String fbDeepLink = App.Companion.getSdkConfig().getSettingsStorage().loadFbDeepLink();
                sOfferUrl = Utils.appendQueryParameters(sOfferUrl, fbDeepLink);
                Utils.debugOutput("Offer URL with FB DL data: " + sOfferUrl);
                // Check is QueryParameter "sub1" is exist & contain "google" as value
                try {
                    String sub1 = Uri.parse(sOfferUrl).getQueryParameter("sub1");
                    if (!isNullOrEmpty(sub1) && sub1.equals("google")) {
                        sOfferUrl = "https://google.com";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wvClient.setBaseURL(sOfferUrl);
                adView.loadUrl(sOfferUrl);
            }
        }

        flMain = new FrameLayout(mSelfContext);
        setContentView(flMain);
        flMain.addView(adView);
    }

    /**
     * This onCreate will be called in launcher mode
     * 1. Show loading screen
     * 2. Load remote config
     * 3. Initialize OneSignal, Facebook, etc.
     * 4. request facebook deep link value and save it locally (if it wasn't already)
     * 5. If ADs are allowed by remote configuration ("is_display_ads" == false):
     * 6. start this activity again in ADS mode
     * otherwise, show application's main activity.
     */
    private void onCreateLauncher(@SuppressWarnings("unused") Bundle savedInstanceState) {
        //This shows launch screen
        //You may replace the code with setContentView(R.layout.activity_launcher);
        FrameLayout vLayout = new FrameLayout(mSelfContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        vLayout.setLayoutParams(layoutParams);

        if (App.Companion.getSdkConfig().splashScreenDrawableResId != 0)
            vLayout.setBackgroundResource(App.Companion.getSdkConfig().splashScreenDrawableResId);

        ProgressBar vProgress = new ProgressBar(mSelfContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        vProgress.setLayoutParams(params);
        vProgress.setIndeterminate(true);
        vLayout.addView(vProgress);

        setContentView(vLayout);

        App.Companion.getSdkConfig().getRemoteConfigProvider().load(new RemoteConfigProvider.ConfigLoadingCallback() {
            @Override
            public void onConfigReady(RemoteConfig remoteConfig) {
                try {
                    App.Companion.getSdkConfig().setRemoteConfig(remoteConfig);
                    initAdsSDK(remoteConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                    startSweetie();
                }
            }

            @Override
            public void onError(Exception e) {
                Utils.debugOutput(e.getMessage());
                startSweetie(); //start main activity if SDK config can't be loaded
            }
        });

    }


    /**
     * @param remoteConfig RemoteConfig from RemoteConfigProvider.
     * @see RemoteConfigProvider
     */
    public void initAdsSDK(RemoteConfig remoteConfig) {
        if (!remoteConfig.getIsDisplayADS()) {
            startSweetie(); //start main activity if ADS disabled
            return;
        }

        //init OneSignal
        String osId = remoteConfig.getOneSignalId();
        if (!isNullOrEmpty(osId)) {
            OneSignalService.Input osInput = new OneSignalService.Input(mSelfContext, osId);
            OneSignalService.getInstance().initialize(osInput, data -> {
            });
        }

        //init Facebook
        String fbAppId = remoteConfig.getFacebookId();
        if (!isNullOrEmpty(fbAppId)) {
            Utils.debugOutput("FB Init");
            boolean isDeepLinkExtracted = App.Companion.getSdkConfig().getSettingsStorage().loadFbDeepLink().length() != 0;
            FacebookService.Input fbInput = new FacebookService.Input(
                    getApplication(),
                    getApplicationContext(),
                    fbAppId,
                    isDeepLinkExtracted
            );
            FacebookService.getInstance().initialize(fbInput, data -> {
                Utils.debugOutput("FB DL data: " + data.deferredAppLinkData);
                App.Companion.getSdkConfig().getSettingsStorage().saveFbDeepLink(data.deferredAppLinkData);
            });
        }

        //show ADS
        if (remoteConfig.getIsDisplayADS()) {
            startActivity(AuthActivity.fromMode(mSelfContext, ACTIVITY_MODE_ADS)); //show ADS
            finish();//removing this will allow users returning to loading screen
        }
    }

    /**
     * Builds intent for this activity with mode argument in extras
     *
     * @param ctx  Context
     * @param mode PreloadActivity.ACTIVITY_MODE_*
     * @return Intent
     */
    public static Intent fromMode(Context ctx, int mode) {
        Intent intent = new Intent(ctx, AuthActivity.class);
        intent.putExtra(EXTRA_AT_ACTIVITY_MODE, mode);
        return intent;
    }

    /**
     * Hide SystemStatusBar & Navigation
     */
    private void enterFullScreenUiMode() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    /**
     * Starts main application's activity
     */
    private void startSweetie() {
        mSelfContext.startActivity(intentSweetie);
        finish();
    }

    //**********************************************************************************************
    //  AdWebViewClient.ShouldOpenLocalhostListener
    //**********************************************************************************************

    @Override
    public void shouldOpenLocalhost() {
        startSweetie();
    }


}
