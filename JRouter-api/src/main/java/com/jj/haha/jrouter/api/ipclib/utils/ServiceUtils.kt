package com.jj.haha.jrouter.api.ipclib.utils

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection

object ServiceUtils {
    /**
     * 考虑到Android 8.0在后台调用startService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    fun startServiceSafely(context: Context?, intent: Intent?) {
        if (null == context) {
            return
        }
        try {
            context.startService(intent)
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }
    }

    fun unbindSafely(
        context: Context?,
        connection: ServiceConnection?
    ) {
        if (context == null || connection == null) {
            return
        }
        try {
            context.unbindService(connection)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}