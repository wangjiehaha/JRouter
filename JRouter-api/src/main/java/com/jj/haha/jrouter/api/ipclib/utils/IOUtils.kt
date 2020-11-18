package com.jj.haha.jrouter.api.ipclib.utils

import android.database.Cursor

object IOUtils {
    fun closeQuietly(cursor: Cursor?) {
        try {
            cursor?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}