package com.test.module

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.jwhaha.jrouter.api.ipclib.IPCRouter
import com.test.module.sdk.IBuy

class BuyService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        IPCRouter.registerRemoteService(IBuy::class.java, BuyImpl)
    }
}