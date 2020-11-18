package com.jj.haha.jrouter.api.ipclib.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

object ProcessUtils {
    private var sProcessName: String? = null

    fun isMainProcess(context: Context): Boolean {
        val processName = getProcessName(context)
        return processName == context.packageName
    }

    /**
     * 这是最可靠的一种获取当前进程名称的方式
     *
     */
    fun getProcessName(context: Context): String? {
        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName
        }
        var count = 0
        do {
            val processName = getProcessNameImpl(context)
            if (!TextUtils.isEmpty(processName)) {
                sProcessName = processName
                return processName
            }
        } while (count++ < 3)
        return null
    }

    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun getProcessNameImpl(context: Context): String? { // get by ams
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
                ?: return null
        val processes =
            manager.runningAppProcesses
        if (processes != null) {
            val pid = Process.myPid()
            for (processInfo in manager.runningAppProcesses) {
                if (processInfo.pid == pid && !TextUtils.isEmpty(processInfo.processName)) {
                    return processInfo.processName
                }
            }
        }
        // get from kernel
        val ret = getProcessName(Process.myPid())
        return if (!TextUtils.isEmpty(ret) && ret!!.contains(context.packageName)) {
            ret
        } else null
    }
}