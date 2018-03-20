package com.example.anonymous.smartgat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.content.Intent.getIntent;

/**
 * Created by anonymous on 17/03/18.
 */

public class WebViewFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_webview,container,false);
        WebView webView = view.findViewById(R.id.fragment_webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(LoginActivity.url_to_open);
        return view;
    }

}
