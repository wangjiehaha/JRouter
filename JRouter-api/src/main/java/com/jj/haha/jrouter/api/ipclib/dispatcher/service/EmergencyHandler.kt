package com.jj.haha.jrouter.api.ipclib.dispatcher.service

import android.content.Context
import com.jj.haha.jrouter.api.ipclib.utils.ServiceUtils.startServiceSafely
import com.jj.haha.jrouter.api.ipclib.utils.StubServiceMatcher.matchIntent

class EmergencyHandler : IEmergencyHandler {
    override fun handleBinderDied(
        context: Context?,
        serverProcessName: String?
    ) {
        val intent = matchIntent(context!!, serverProcessName!!)
        intent?.let { startServiceSafely(context, it) }
    }
}