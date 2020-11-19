package com.jwhaha.jrouter.api;

import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationCallback {

    void attachBaseContext(Context context);

    void onCreate();

    void onConfigurationChanged(Configuration configuration);

    void onLowMemory();

    void onTrimMemory();

    void onTerminate();
}

