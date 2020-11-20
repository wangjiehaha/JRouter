package com.jwhaha.jrouter.api.ipclib.dispatcher

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.jwhaha.jrouter.api.*
import com.jwhaha.jrouter.api.ipclib.BinderWrapper

class DispatcherService : Service() {
    companion object {
        private const val TAG = "DispatcherService"
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when (action) {
                DISPATCH_REGISTER_SERVICE_ACTION -> {
                    registerRemoteService(intent)
                }
                DISPATCH_UNREGISTER_SERVICE_ACTION -> {
                    unRegisterRemoteService(intent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun registerRemoteService(intent: Intent) {
        val bridgeWrapper = intent.getParcelableExtra<BinderWrapper>(KEY_DISPATCH_BRIDGE_WRAPPER)
        val pid = intent.getIntExtra(KEY_PID, -1)
        val businessWrapper = intent.getParcelableExtra<BinderWrapper>(KEY_BUSINESS_BINDER_WRAPPER)
        val serviceCanonicalName = intent.getStringExtra(KEY_SERVICE_NAME)
        val processName = intent.getStringExtra(KEY_PROCESS_NAME)
        try {
            if (businessWrapper != null && serviceCanonicalName != null && processName != null) {
                Dispatcher.registerRemoteService(
                    serviceCanonicalName,
                    processName,
                    businessWrapper.binder
                )
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            if (bridgeWrapper != null) {
                registerAndReverseRegister(pid, bridgeWrapper.binder)
            }
        }
    }

    private fun unRegisterRemoteService(intent: Intent) {
        val serviceCanonicalName = intent.getStringExtra(KEY_SERVICE_NAME)
        try {
            serviceCanonicalName?.apply {
                Dispatcher.unregisterRemoteService(this)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun registerAndReverseRegister(pid: Int, bridgeBinder: IBinder) {
        val remoteBridge = IRemoteBridge.Stub.asInterface(bridgeBinder)
        Dispatcher.registerRemoteBridge(pid, bridgeBinder)
        try {
            remoteBridge.registerDispatcher(Dispatcher.asBinder())
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}