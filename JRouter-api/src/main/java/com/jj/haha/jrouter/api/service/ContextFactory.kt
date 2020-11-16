package com.jj.haha.jrouter.api.service

import android.content.Context

/**
 * 使用Context构造
 */
class ContextFactory(private val mContext: Context) : IFactory {
    @Throws(Exception::class)
    override fun <T> create(clazz: Class<T>): T {
        return clazz.getConstructor(Context::class.java).newInstance(mContext)
    }

}