package com.hwpty.halloweenparty.sdk;

import android.net.Uri;
import android.util.Log;

import com.hwpty.halloweenparty.BuildConfig;


public class Utils {

    /**
     * Append raw URL string with Facebook DeepLink QueryParameters
     *
     * @param urlSource Raw source URL string (where query parameters will be added)
     * @param urlParams Raw URL string (from where query parameters will be added)
     * @return Updated URL string
     */
    public static String appendQueryParameters(String urlSource, String urlParams) {
        if (urlParams.length() == 0 || !urlParams.contains("://")) return urlSource;

        Uri.Builder uriBuilderSource = Uri.parse(urlSource).buildUpon();
        Uri uriParams = Uri.parse(urlParams);

        for (String paramName : uriParams.getQueryParameterNames()) {
            String val = uriParams.getQueryParameter(paramName);
            if (val == null || val.length() == 0) continue;
            uriBuilderSource = uriBuilderSource.appendQueryParameter(paramName, val);
        }
        return uriBuilderSource.build().toString();
    }

    public static void debugOutput(String m) {
        if (BuildConfig.DEBUG) {
            Log.d("LOG_TAG", m);
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
