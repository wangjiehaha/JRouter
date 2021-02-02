package com.jwhaha.jrouter.api.ipclib.dispatcher

import android.os.IBinder
import com.jwhaha.jrouter.api.IDispatcher
import com.jwhaha.jrouter.api.bean.BinderBean
import com.jwhaha.jrouter.api.ipclib.business.DispatcherBridge
import com.jwhaha.jrouter.api.ipclib.dispatcher.event.BridgeDispatcher
import com.jwhaha.jrouter.api.ipclib.dispatcher.service.ServiceDispatcher

/**
 * [Dispatcher] in main process or steady process, every module communicate by [Dispatcher]
 *
 * Dispatcher cache module Binder(provide services) and cache [DispatcherBridge]
 */
object Dispatcher: IDispatcher.Stub() {

    private val serviceDispatcher = ServiceDispatcher()
    private val bridgeDispatcher = BridgeDispatcher()

    override fun registerRemoteBridge(pid: Int, binder: IBinder) {
        if (pid >= 0) {
            bridgeDispatcher.registerRemoteTransferLocked(pid, binder)
        }
    }

    override fun unregisterRemoteService(serviceCanonicalName: String) {
        serviceDispatcher.removeBinderCacheLocked(serviceCanonicalName)
        bridgeDispatcher.unregisterRemoteServiceLocked(serviceCanonicalName)
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