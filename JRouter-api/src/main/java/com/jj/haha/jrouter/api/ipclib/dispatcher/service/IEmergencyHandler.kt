package com.jj.haha.jrouter.api.ipclib.dispatcher.service

import android.content.Context

interface IEmergencyHandler {
    fun handleBinderDied(
        context: Context?,
        serverProcessName: String?
    )
}