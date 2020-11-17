package com.jj.haha.jrouter.api.lib.dispatcher

import android.os.IBinder
import com.jj.haha.jrouter.api.IDispatcher
import com.jj.haha.jrouter.api.bean.BinderBean
import com.jj.haha.jrouter.api.lib.dispatcher.service.ServiceDispatcher

object Dispatcher: IDispatcher.Stub() {

    private val serviceDispatcher = ServiceDispatcher()

    override fun unregisterRemoteService(serviceCanonicalName: String) {
        serviceDispatcher.removeBinderCacheLocked(serviceCanonicalName)
    }

    override fun getTargetBinder(serviceCanonicalName: String): BinderBean? {
        return serviceDispatcher.getTargetBinderLocked(serviceCanonicalName)
    }

    override fun registerRemoteService(
        serviceCanonicalName: String,
        processName: String,
        binder: IBinder
    ) {
        serviceDispatcher.registerRemoteServiceLocked(serviceCanonicalName, processName, binder)
    }
}