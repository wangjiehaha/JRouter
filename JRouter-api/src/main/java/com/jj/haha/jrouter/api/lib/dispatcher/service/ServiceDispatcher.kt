package com.jj.haha.jrouter.api.lib.dispatcher.service

import android.os.IBinder
import com.jj.haha.jrouter.api.Router
import com.jj.haha.jrouter.api.bean.BinderBean
import java.util.concurrent.ConcurrentHashMap

class ServiceDispatcher : IServiceDispatcher {
    private val emergencyHandler = EmergencyHandler()
    private val remoteBinderCache = ConcurrentHashMap<String, BinderBean>()

    override fun getTargetBinderLocked(serviceCanonicalName: String): BinderBean? {
        return remoteBinderCache[serviceCanonicalName]
    }

    override fun registerRemoteServiceLocked(
        serviceCanonicalName: String,
        processName: String,
        binder: IBinder
    ) {
        binder.linkToDeath({
            val bean = remoteBinderCache.remove(serviceCanonicalName)
            if (bean != null) {
                emergencyHandler.handleBinderDied(Router.getAppContext(), bean.processName)
            }
        }, 0)
        remoteBinderCache[serviceCanonicalName] = BinderBean(binder, processName)
    }

    override fun removeBinderCacheLocked(serviceCanonicalName: String) {
        remoteBinderCache.remove(serviceCanonicalName)
    }
}