package com.jwhaha.jrouter.api.ipclib.dispatcher.event

import android.os.IBinder
import android.os.RemoteException

interface IEventDispatcher {
    fun registerRemoteTransferLocked(pid: Int, remoteBridge: IBinder)

    @Throws(RemoteException::class)
    fun publishLocked(event: com.jwhaha.jrouter.api.bean.Event)

    @Throws(RemoteException::class)
    fun unregisterRemoteServiceLocked(serviceCanonicalName: String?)
}