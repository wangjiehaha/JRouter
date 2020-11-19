package com.jwhaha.jrouter.api

import android.net.Uri
import android.text.TextUtils
import com.jwhaha.jrouter.annotation.Const
import java.util.*

object RouterUtils {
    /**
     * 转成小写
     */
    fun toLowerCase(s: String?): String? {
        return if (TextUtils.isEmpty(s)) s else s!!.toLowerCase(Locale.getDefault())
    }

    /**
     * 转成非null的字符串，如果为null返回空串
     */
    fun toNonNullString(s: String?): String {
        return s ?: ""
    }

    /**
     * 是否为null或长度为0
     */
    fun isEmpty(objects: Array<*>?): Boolean {
        return objects == null || objects.isEmpty()
    }

    /**
     * 根据scheme和host生成字符串
     */
    fun schemeHost(scheme: String?, host: String?): String {
        return toNonNullString(toLowerCase(scheme)) + "://" + toNonNullString(
            toLowerCase(host)
        )
    }

    /**
     * 根据scheme和host生成字符串
     */
    fun schemeHost(uri: Uri?): String? {
        return if (uri == null) null else schemeHost(uri.scheme, uri.host)
    }

    /**
     * 在Uri中添加参数
     *
     * @param uri    原始uri
     * @param params 要添加的参数
     * @return uri    新的uri
     */
    fun appendParams(
        uri: Uri?,
        params: Map<String?, String?>?
    ): Uri? {
        if (uri != null && params != null && params.isNotEmpty()) {
            val builder = uri.buildUpon()
            try {
                for (key in params.keys) {
                    if (TextUtils.isEmpty(key)) continue
                    val `val` = uri.getQueryParameter(key)
                    if (`val` == null) { // 当前没有此参数时，才会添加
                        val value = params[key]
                        builder.appendQueryParameter(key, value)
                    }
                }
                return builder.build()
            } catch (ignored: Exception) {
            }
        }
        return uri
    }

    /**
     * 添加斜线前缀
     */
    fun appendSlash(path: String?): String? {
        var tempPath = path
        if (tempPath != null && !tempPath.startsWith("/")) {
            tempPath = "/$tempPath"
        }
        return tempPath
    }

    fun transferModuleName(fileName: String): String {
        return fileName.replace(
            Const.NAME + Const.SPLITTER,
            ""
        )
    }
}