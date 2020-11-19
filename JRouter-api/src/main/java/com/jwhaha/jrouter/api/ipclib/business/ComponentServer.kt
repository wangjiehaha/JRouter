package com.jwhaha.jrouter.api.ipclib.business

import android.content.Context
import android.os.IBinder
import android.os.RemoteException
import com.jwhaha.jrouter.api.IDispatcher
import com.jwhaha.jrouter.api.bean.BinderBean
import com.jwhaha.jrouter.api.ipclib.utils.ProcessUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * 缓存当前进程的Binder和远程进程的Binder
 */
class ComponentServer {
    private val stubBinderCache = ConcurrentHashMap<String, IBinder>()
    private val remoteBinderCache = ConcurrentHashMap<String, BinderBean>()

    fun registerStubService(
        serviceCanonicalName: String,
        stubBinder: IBinder,
        context: Context,
        dispatcher: IDispatcher?
    ) {
        stubBinderCache[serviceCanonicalName] = stubBinder
        try {
            dispatcher?.registerRemoteService(
                serviceCanonicalName,
                ProcessUtils.getProcessName(context),
                stubBinder
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun unregisterStubService(serviceCanonicalName: String, dispatcher: IDispatcher?) {
        clearStubBinderCache(serviceCanonicalName)
        try {
            dispatcher?.unregisterRemoteService(serviceCanonicalName)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun getIBinderFromCache(context: Context, serviceCanonicalName: String): BinderBean? {
        // 先查本组件进程的binder
        if (stubBinderCache[serviceCanonicalName] != null) {
            return BinderBean(stubBinderCache[serviceCanonicalName]!!, ProcessUtils.getProcessName(context))
        }
        if (remoteBinderCache[serviceCanonicalName] != null) {
            return remoteBinderCache[serviceCanonicalName]
        }
        return null
    }

    fun getRemoteBinder(serviceCanonicalName: String, dispatcher: IDispatcher?): BinderBean? {
        try {
            val binderBean = dispatcher?.getTargetBinder(serviceCanonicalName) ?: return null
            try {
                binderBean.binder.linkToDeath({
                    remoteBinderCache.remove(serviceCanonicalName)
                }, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            remoteBinderCache[serviceCanonicalName] = binderBean
            return binderBean
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return null
    }

    fun clearStubBinderCache(serviceCanonicalName: String) {
        stubBinderCache.remove(serviceCanonicalName)
    }

    fun clearRemoteBinderCache(serviceCanonicalName: String) {
        remoteBinderCache.remove(serviceCanonicalName)
    }
}