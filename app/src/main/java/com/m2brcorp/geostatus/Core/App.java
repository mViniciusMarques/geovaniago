package com.m2brcorp.geostatus.Core;

import android.app.Application;

import com.m2brcorp.geostatus.R;
import com.parse.Parse;

public class App extends Application {

    private static App singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.PARSE_APPLICATION_ID))
                .clientKey(getString(R.string.PARSE_CLIENT_ID))
                .server(getString(R.string.PARSE_SERVER_BACK4APP))
                .build()
        );
    }

    public App getInstance(){
        return singleton;
    }
}
