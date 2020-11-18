package com.jj.haha.jrouter.api.lib.business

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import android.os.RemoteException
import com.jj.haha.jrouter.api.*
import com.jj.haha.jrouter.api.bean.Event
import com.jj.haha.jrouter.api.lib.BinderWrapper
import com.jj.haha.jrouter.api.lib.dispatcher.DispatcherCursor
import com.jj.haha.jrouter.api.lib.dispatcher.DispatcherProvider
import com.jj.haha.jrouter.api.lib.dispatcher.DispatcherService
import com.jj.haha.jrouter.api.lib.utils.IOUtils
import com.jj.haha.jrouter.api.lib.utils.ServiceUtils
import com.jj.haha.jrouter.api.lib.dispatcher.Dispatcher

/**
 * 联通模块与中心binder分配器的桥梁
 *
 * [RemoteBridge] has [Dispatcher];
 * [Dispatcher] has [RemoteBridge];
 * [RemoteBridge] also can notify event (实现了跨进程的事件总线)
 *
 * Remote service(provider) can get [Dispatcher] by [RemoteBridge];
 *
 */
object RemoteBridge : IRemoteBridge.Stub() {

    const val MAX_WAIT_TIME = 600L

    private var dispatcherPoxy: IDispatcher? = null

    @Synchronized
    private fun requestDispatcherBinder() {
        if (dispatcherPoxy == null) {
            val wrapper = BinderWrapper(this.asBinder())
            Intent(Router.getAppContext(), DispatcherService::class.java).apply {
                action = DISPATCH_REGISTER_SERVICE_ACTION
                putExtra(KEY_REMOTE_TRANSFER_WRAPPER, wrapper)
                putExtra(KEY_PID, android.os.Process.myPid())
                ServiceUtils.startServiceSafely(Router.getAppContext(), this)
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
        return Uri.parse("content://" + Router.getAppContext().packageName + "." + DispatcherProvider.URI_SUFFIX + "/main")
    }

    override fun unregisterRemoteService(serviceCanonicalName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun notify(event: Event?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}