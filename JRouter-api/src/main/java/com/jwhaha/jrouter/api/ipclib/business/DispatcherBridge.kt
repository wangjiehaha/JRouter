package com.jwhaha.jrouter.api.ipclib.business

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import android.os.RemoteException
import com.jwhaha.jrouter.api.*
import com.jwhaha.jrouter.api.bean.BinderBean
import com.jwhaha.jrouter.api.ipclib.BinderWrapper
import com.jwhaha.jrouter.api.ipclib.dispatcher.DispatcherCursor
import com.jwhaha.jrouter.api.ipclib.dispatcher.DispatcherProvider
import com.jwhaha.jrouter.api.ipclib.dispatcher.DispatcherService
import com.jwhaha.jrouter.api.ipclib.utils.IOUtils
import com.jwhaha.jrouter.api.ipclib.utils.ServiceUtils
import com.jwhaha.jrouter.api.ipclib.dispatcher.Dispatcher

/**
 * 联通模块(本地进程)与中心binder分配器的桥梁
 *
 * [DispatcherBridge] has [Dispatcher];
 * [Dispatcher] has [DispatcherBridge];
 * [DispatcherBridge] also can notify event (实现了跨进程的事件总线)
 *
 * Remote service(provider) can get [Dispatcher] by [DispatcherBridge];
 *
 */
object DispatcherBridge : IRemoteBridge.Stub() {

    private var dispatcherPoxy: IDispatcher? = null
    private val componentServer = ComponentServer();

    @Synchronized
    private fun requestDispatcherBinder() {
        if (dispatcherPoxy == null) {
            val wrapper = BinderWrapper(this.asBinder())
            Intent(com.jwhaha.jrouter.api.Router.getAppContext(), DispatcherService::class.java).apply {
                action = DISPATCH_REGISTER_SERVICE_ACTION
                putExtra(KEY_REMOTE_TRANSFER_WRAPPER, wrapper)
                putExtra(KEY_PID, android.os.Process.myPid())
                ServiceUtils.startServiceSafely(com.jwhaha.jrouter.api.Router.getAppContext(), this)
            }
        }
    }

    private fun initDispatchProxyLocked() {
        // 优先从 provider 中进行获取dispatcher
        if (dispatcherPoxy == null) {
            val dispatcherBinder = getIBinderFormProvider()
            dispatcherBinder?.apply {
                dispatcherPoxy = IDispatcher.Stub.asInterface(this)
                registerCurrentBridge()
            }
        }
        // 从 provider 中获取失败，那么由dispatcher服务注入
        if (dispatcherPoxy == null) {
            requestDispatcherBinder()
            // todo 这里需要考虑是否需要等待
        }
    }

    private fun registerCurrentBridge() {
        try {
            dispatcherPoxy?.registerRemoteBridge(android.os.Process.myPid(), this.asBinder())
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun getIBinderFormProvider(): IBinder? {
        var cursor: Cursor? = null
        try {
            cursor = Router.getAppContext().contentResolver.query(
                getDispatcherProviderUri(),
                DispatcherProvider.PROJECTION_MAIN,
                null,
                null,
                null
            )
            if (cursor == null) {
                return null
            }
            return DispatcherCursor.stripBinder(cursor)
        } finally {
            IOUtils.closeQuietly(cursor)
        }
    }

    private fun getDispatcherProviderUri(): Uri {
        return Uri.parse("content://" + com.jwhaha.jrouter.api.Router.getAppContext().packageName + "." + DispatcherProvider.URI_SUFFIX + "/main")
    }

    /**
     * 由[Dispatcher]主动调用
     * 因为[Dispatcher]已经移除了Binder，所以也需要通知到各个组件业务的进程将自身缓存的远程Binder移除
     */
    override fun unregisterRemoteService(serviceCanonicalName: String) {
        componentServer.clearRemoteBinderCache(serviceCanonicalName)
    }

    @Synchronized
    fun getRemoteServiceBean(serviceCanonicalName: String): BinderBean? {
        val cacheBinderBean =
            componentServer.getIBinderFromCache(com.jwhaha.jrouter.api.Router.getAppContext(), serviceCanonicalName)
        if (cacheBinderBean != null) {
            return cacheBinderBean
        }
        initDispatchProxyLocked()
        return componentServer.getRemoteBinder(serviceCanonicalName, dispatcherPoxy)
    }

    @Synchronized
    fun registerStubService(serviceCanonicalName: String, iBinder: IBinder) {
        initDispatchProxyLocked()
        componentServer.registerStubService(
            serviceCanonicalName,
            iBinder,
            com.jwhaha.jrouter.api.Router.getAppContext(),
            dispatcherPoxy!!
        )
    }

    fun unregisterStubService(serviceCanonicalName: String) {
        initDispatchProxyLocked()
        componentServer.unregisterStubService(serviceCanonicalName, dispatcherPoxy)
    }

    /**
     * 调用[requestDispatcherBinder]成功后
     * 由[Dispatcher]主动注入
     */
    override fun registerDispatcher(dispatcherBinder: IBinder) {
        dispatcherBinder.linkToDeath({
            dispatcherPoxy = null
        }, 0)
        dispatcherPoxy = IDispatcher.Stub.asInterface(dispatcherBinder)
    }

    override fun notify(event: com.jwhaha.jrouter.api.bean.Event?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}