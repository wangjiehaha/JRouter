package com.jj.haha.jrouter.api.lib.dispatcher.service

import android.content.Context

interface IEmergencyHandler {
    fun handleBinderDied(
        context: Context?,
        serverProcessName: String?
    )
}