package com.jwhaha.jrouter.api.ipclib.business

import android.content.Context
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
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
            Log.d(TAG, "getIBinderFromCache from stubBinderCache success of $serviceCanonicalName.")
            return BinderBean(stubBinderCache[serviceCanonicalName]!!, ProcessUtils.getProcessName(context))
        }
        if (remoteBinderCache[serviceCanonicalName] != null) {
            val bean = remoteBinderCache[serviceCanonicalName]?.let {
                if (it.binder.pingBinder()) {
                    return@let it
                } else {
                    null
                }
            }
            if (bean != null) {
                Log.d(TAG, "getIBinderFromCache from remoteBinderCache success of $serviceCanonicalName.")
                return bean
            }
        }
        return null
    }

    fun getRemoteBinder(serviceCanonicalName: String, dispatcher: IDispatcher?): BinderBean? {
        try {
            val binderBean = dispatcher?.getTargetBinder(serviceCanonicalName)
            if (binderBean == null) {
                Log.e(TAG, "getRemoteBinder failed $serviceCanonicalName return null")
                return null
            }
            if (!binderBean.binder.pingBinder()) {
                Log.e(TAG, "getRemoteBinder failed $serviceCanonicalName is death.")
                return null
            }
            try {
                binderBean.binder.linkToDeath({
                    Log.e(TAG, "$serviceCanonicalName has death.")
                    remoteBinderCache.remove(serviceCanonicalName)
                }, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            Log.d(TAG, "getRemoteBinder success $serviceCanonicalName")
            remoteBinderCache[serviceCanonicalName] = binderBean
            return binderBean
        } catch (e: RemoteException) {
            Log.e(TAG, "getRemoteBinder failed $serviceCanonicalName has a RemoteException.")
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

    companion object {
        const val TAG = "[Router-ComponentServer]"
    }
}