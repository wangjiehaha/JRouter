package com.jwhaha.jrouter.api.ipclib.dispatcher.service

import android.os.IBinder
import android.util.Log
import com.jwhaha.jrouter.api.bean.BinderBean
import java.util.concurrent.ConcurrentHashMap

class ServiceDispatcher : IServiceDispatcher {
    private val remoteBinderCache = ConcurrentHashMap<String, BinderBean>()

    override fun getTargetBinderLocked(serviceCanonicalName: String): BinderBean? {
        Log.d(TAG, "getTargetBinderLocked $serviceCanonicalName")
        return remoteBinderCache[serviceCanonicalName]
    }

    override fun registerRemoteServiceLocked(
        serviceCanonicalName: String,
        processName: String,
        binder: IBinder
    ) {
        Log.d(TAG, "registerRemoteServiceLocked $serviceCanonicalName")
        remoteBinderCache[serviceCanonicalName] = BinderBean(binder, processName)
    }

    override fun removeBinderCacheLocked(serviceCanonicalName: String) {
        Log.d(TAG, "removeBinderCacheLocked $serviceCanonicalName")
        remoteBinderCache.remove(serviceCanonicalName)
    }

    companion object {
        const val TAG = "[Router-ServiceDispatcher]"
    }
}