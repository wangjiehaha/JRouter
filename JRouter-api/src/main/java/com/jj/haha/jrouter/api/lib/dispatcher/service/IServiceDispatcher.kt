package com.jj.haha.jrouter.api.lib.dispatcher.service

import android.os.IBinder
import android.os.RemoteException
import com.jj.haha.jrouter.api.bean.BinderBean

interface IServiceDispatcher {
    @Throws(RemoteException::class)
    fun getTargetBinderLocked(serviceCanonicalName: String): BinderBean?

    @Throws(RemoteException::class)
    fun registerRemoteServiceLocked(
        serviceCanonicalName: String,
        processName: String,
        binder: IBinder
    )

    fun removeBinderCacheLocked(serviceCanonicalName: String)
}