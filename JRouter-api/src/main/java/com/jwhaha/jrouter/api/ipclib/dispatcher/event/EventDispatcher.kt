package com.jwhaha.jrouter.api.ipclib.dispatcher.event

import android.os.IBinder
import android.os.RemoteException
import com.jwhaha.jrouter.api.IRemoteBridge
import java.util.concurrent.ConcurrentHashMap

class EventDispatcher : IEventDispatcher {
    private val dispatcherBridgeBinders: MutableMap<Int, IBinder> = ConcurrentHashMap()

    override fun registerRemoteTransferLocked(
        pid: Int,
        remoteBridge: IBinder
    ) {
        try {
            remoteBridge.linkToDeath({ dispatcherBridgeBinders.remove(pid) }, 0)
        } catch (ex: RemoteException) {
            ex.printStackTrace()
        } finally {
            dispatcherBridgeBinders[pid] = remoteBridge
        }
    }

    @Throws(RemoteException::class)
    override fun publishLocked(event: com.jwhaha.jrouter.api.bean.Event) {
        var e: RemoteException? = null
        for ((_, value) in dispatcherBridgeBinders) {
            IRemoteBridge.Stub.asInterface(value)?.apply {
                try {
                    notify(event)
                } catch (ex: RemoteException) {
                    ex.printStackTrace()
                    e = ex
                }
            }
        }
        if (null != e) {
            throw e as RemoteException
        }
    }

    @Throws(RemoteException::class)
    override fun unregisterRemoteServiceLocked(serviceCanonicalName: String?) {
        var e: RemoteException? = null
        for ((_, value) in dispatcherBridgeBinders) {
            IRemoteBridge.Stub.asInterface(value)?.apply {
                try {
                    unregisterRemoteService(serviceCanonicalName)
                } catch (ex: RemoteException) {
                    ex.printStackTrace()
                    e = ex
                }
            }
        }
        if (null != e) {
            throw e as RemoteException
        }
    }
}