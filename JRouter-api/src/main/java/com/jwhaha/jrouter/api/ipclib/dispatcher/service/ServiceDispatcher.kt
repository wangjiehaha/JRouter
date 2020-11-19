package com.jwhaha.jrouter.api.ipclib.dispatcher.service

import android.os.IBinder
import com.jwhaha.jrouter.api.bean.BinderBean
import java.util.concurrent.ConcurrentHashMap

class ServiceDispatcher : IServiceDispatcher {
    private val remoteBinderCache = ConcurrentHashMap<String, BinderBean>()

    override fun getTargetBinderLocked(serviceCanonicalName: String): BinderBean? {
        return remoteBinderCache[serviceCanonicalName]
    }

    override fun registerRemoteServiceLocked(
        serviceCanonicalName: String,
        processName: String,
        binder: IBinder
    ) {
        remoteBinderCache[serviceCanonicalName] = BinderBean(binder, processName)
    }

    override fun removeBinderCacheLocked(serviceCanonicalName: String) {
        remoteBinderCache.remove(serviceCanonicalName)
    }
}