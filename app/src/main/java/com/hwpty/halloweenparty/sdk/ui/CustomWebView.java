package com.hwpty.halloweenparty.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;


class CustomWebView extends WebView {

    private View customView = null;
    private FrameLayout.LayoutParams matchParentLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    private static Context appcompatIssueFixContext(Context ctx) {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            return ctx.getApplicationContext();
        } else {
            return ctx;
        }
    }

    private void appcompatIssueFixInit() {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
        }
    }

    public CustomWebView(Context context) {
        super(appcompatIssueFixContext(context));
        appcompatIssueFixInit();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(appcompatIssueFixContext(context), attrs);
        init(context);
        appcompatIssueFixInit();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(appcompatIssueFixContext(context), attrs, defStyleAttr);
        appcompatIssueFixInit();
    }

    @Override
    public void onPause() {
        /*
         * There is a bug in AdvancedWebView:
         * onPause() calls pauseTimers() which will stop ALL webview timers within the app
         */
        //super.onPause();
    }

//    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void init(Context context) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);

        //this is needed for full-screen mode:
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
               CustomWebView.this.setVisibility(GONE);
                view.setLayoutParams(matchParentLayout);
                ((FrameLayout) getParent()).addView(view);
                customView = view;
            }

            @Override
            public void onHideCustomView() {
                if (customView != null) {
                    ((FrameLayout) getParent()).removeView(customView);
                } else {
                    ((FrameLayout) getParent()).removeAllViews();
                    ((FrameLayout) getParent()).addView(CustomWebView.this);
                }
                customView = null;
                CustomWebView.this.setVisibility(VISIBLE);
            }
        });
    }
}
