package com.jj.haha.jrouter.api.lib.dispatcher.service

import android.content.Context
import com.jj.haha.jrouter.api.lib.utils.ServiceUtils.startServiceSafely
import com.jj.haha.jrouter.api.lib.utils.StubServiceMatcher.matchIntent

class EmergencyHandler : IEmergencyHandler {
    override fun handleBinderDied(
        context: Context?,
        serverProcessName: String?
    ) {
        val intent = matchIntent(context!!, serverProcessName!!)
        intent?.let { startServiceSafely(context, it) }
    }
}