package com.jj.haha.jrouter.api.ipclib.dispatcher

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.jj.haha.jrouter.api.*
import com.jj.haha.jrouter.api.ipclib.BinderWrapper

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
}