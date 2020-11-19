package com.jwhaha.jrouter.api

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration

open class BaseRouterApplication: Application() {

    private lateinit var mAppCallbacks: MutableList<com.jwhaha.jrouter.api.IApplicationCallback>

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (isMainProcess()) {
            mAppCallbacks = com.jwhaha.jrouter.api.Router.getAllServices(
                com.jwhaha.jrouter.api.IApplicationCallback::class.java
            )
            mAppCallbacks.forEach{ it.attachBaseContext(this) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        com.jwhaha.jrouter.api.Router.lazyInit(this)
        if (isMainProcess()) {
            mAppCallbacks.forEach { it.onCreate() }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isMainProcess()) {
            mAppCallbacks.forEach { it.onConfigurationChanged(newConfig) }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (isMainProcess()) {
            mAppCallbacks.forEach { it.onLowMemory() }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (isMainProcess()) {
            mAppCallbacks.forEach { it.onTrimMemory() }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        if (isMainProcess()) {
            mAppCallbacks.forEach { it.onTerminate() }
        }
    }

    private fun isMainProcess(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses
        val pid = android.os.Process.myPid()
        for (processInfo in runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName == packageName
            }
        }
        return false
    }
}