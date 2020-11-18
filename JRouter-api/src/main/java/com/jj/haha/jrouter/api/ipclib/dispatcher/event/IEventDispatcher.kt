package com.jj.haha.jrouter.api.ipclib.dispatcher.event

import android.os.IBinder
import android.os.RemoteException
import com.jj.haha.jrouter.api.bean.Event

interface IEventDispatcher {
    fun registerRemoteTransferLocked(pid: Int, remoteBridge: IBinder)

    @Throws(RemoteException::class)
    fun publishLocked(event: Event)

    @Throws(RemoteException::class)
    fun unregisterRemoteServiceLocked(serviceCanonicalName: String?)
}