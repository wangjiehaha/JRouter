package com.jj.haha.jrouter.api.lib.dispatcher

import android.os.IBinder
import com.jj.haha.jrouter.api.IDispatcher
import com.jj.haha.jrouter.api.bean.BinderBean
import com.jj.haha.jrouter.api.lib.business.RemoteBridge
import com.jj.haha.jrouter.api.lib.dispatcher.event.EventDispatcher
import com.jj.haha.jrouter.api.lib.dispatcher.service.ServiceDispatcher

/**
 * [Dispatcher] in main process or steady process, every module communicate by [Dispatcher]
 *
 * Dispatcher cache module Binder(provide services) and cache [RemoteBridge]
 */
object Dispatcher: IDispatcher.Stub() {

    private val serviceDispatcher = ServiceDispatcher()
    private val eventDispatcher = EventDispatcher()

    override fun registerRemoteBridge(pid: Int, binder: IBinder) {
        if (pid >= 0) {
            eventDispatcher.registerRemoteTransferLocked(pid, binder)
        }
    }

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