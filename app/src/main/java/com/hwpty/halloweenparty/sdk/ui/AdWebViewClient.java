package com.hwpty.halloweenparty.sdk.ui;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hwpty.halloweenparty.application.App;
import com.hwpty.halloweenparty.sdk.Utils;


/**
 * This WebView Client allows to open main application's activity from WebView (by navigating to Localhost).
 * Also it allows to inject custom JS for different purposes.
 */
public class AdWebViewClient extends WebViewClient {


    private String mBaseURL = "";

    protected interface ShouldOpenLocalhostListener {

        /**
         * Will be called when in a WebView host redirects to "localhost" occurs
         */
        void shouldOpenLocalhost();

    }

    ShouldOpenLocalhostListener shouldOpenLocalhostListener;

    public AdWebViewClient(ShouldOpenLocalhostListener shouldOpenLocalhostListener) {
        this.shouldOpenLocalhostListener = shouldOpenLocalhostListener;
    }

    public void setBaseURL(String baseURL) {
        this.mBaseURL = baseURL;
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
            App.Companion.getSdkConfig().getSettingsStorage().saveLastVisitedLink(url);
            Utils.debugOutput("LastVisitedLink: " + url);
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    /**
     * Allows to translate different URLs to system intents (in addition to parent's implementation)
     *
     * @param view Source WebView
     * @param url  Current URL
     * @return isUrlInterceptedBySystem
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            //eat Intent if there is no Apps to handle it
            Utils.debugOutput(e.getMessage());
            return true;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Utils.debugOutput("onReceivedHttpError " + request.getUrl());
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        /*
            Эту проверку можно реализовать разными способами:
            - проверять на 404 только базовый УРЛ
            - проверять все запросы и ф=фильтровать (отсекать) файлы
            - сделать запрост= на ОфферУРЛ не в вебвью
        */
        Utils.debugOutput("onReceivedHttpError " + errorResponse.getStatusCode() + " " + request.getUrl());
        if (errorResponse.getStatusCode() == 404
                && request.getUrl().toString().equals(mBaseURL)
                && shouldOpenLocalhostListener != null) {
            shouldOpenLocalhostListener.shouldOpenLocalhost();
        }
    }

}
