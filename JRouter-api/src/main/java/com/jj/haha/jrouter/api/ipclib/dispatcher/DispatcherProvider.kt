package com.jj.haha.jrouter.api.ipclib.dispatcher

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import com.jj.haha.jrouter.api.ipclib.BinderWrapper
import java.util.concurrent.ConcurrentHashMap

class DispatcherProvider: ContentProvider() {

    companion object {
        val PROJECTION_MAIN = Array(1){"main"}
        const val URI_SUFFIX = "api.lib.dispatcher"
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        return DispatcherCursor.generateCursor(Dispatcher.asBinder())
    }

    override fun onCreate(): Boolean {
        return false
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return null
    }
}

class DispatcherCursor(columnNames: Array<out String>?, binder: IBinder) : MatrixCursor(columnNames) {
    private val binderExtras = Bundle()

    init {
        binderExtras.putParcelable(KEY_BINDER_WRAPPER, BinderWrapper(binder))
    }

    override fun getExtras(): Bundle {
        return binderExtras
    }

    companion object {
        private const val KEY_BINDER_WRAPPER = "KeyBinderWrapper"
        private val DEFAULT_COLUMNS = Array<String>(1) {"col"}
        private val cursorCache = ConcurrentHashMap<String, DispatcherCursor>()

        fun generateCursor(binder: IBinder): DispatcherCursor? {
            try {
                var cursor = cursorCache[binder.interfaceDescriptor]
                if (cursor != null) {
                    return cursor
                }
                cursor = DispatcherCursor(DEFAULT_COLUMNS, binder)
                cursorCache[binder.interfaceDescriptor!!] = cursor
                return cursor
            } catch (e: RemoteException) {
                return null
            }
        }

        fun stripBinder(cursor: Cursor?): IBinder? {
            if (cursor == null) {
                return null
            }
            val bundle = cursor.extras
            bundle.classLoader = BinderWrapper::class.java.classLoader
            return bundle.getParcelable<BinderWrapper>(KEY_BINDER_WRAPPER)?.binder
        }
    }
}