package com.test.module

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import com.jwhaha.jrouter.annotation.RouterService
import com.jwhaha.jrouter.api.IApplicationCallback
import com.jwhaha.jrouter.api.ipclib.utils.ServiceUtils

@RouterService(
    interfaces = [IApplicationCallback::class]
)
class Module1Application : IApplicationCallback {

    private lateinit var context: Context

    override fun attachBaseContext(p0: Context?) {
        context = p0!!
    }

    override fun onCreate() {
        ServiceUtils.startServiceSafely(context, Intent(context, BuyService::class.java))
    }

    override fun onLowMemory() {
    }

    override fun onTerminate() {
    }

    override fun onConfigurationChanged(p0: Configuration?) {
    }

    override fun onTrimMemory() {
    }
}