package com.test.module

import com.jj.haha.jrouter.annotation.RouterService
import com.jj.haha.jrouter.api.ipclib.IPCRouter
import com.test.module.sdk.IBuy
import com.test.module.sdk.MODULE1_KEY
import com.test.module.sdk.ModuleApi

@RouterService(
    interfaces = [ModuleApi::class],
    key = [MODULE1_KEY]
)
class ModuleApiImpl : ModuleApi {
    override fun getBuyService(): IBuy? {
        val binder = IPCRouter.getRemoteService(IBuy::class.java) ?: return null
        return IBuy.Stub.asInterface(binder)
    }

}