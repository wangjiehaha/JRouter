package com.jwhaha.jrouter.api.service

import com.jwhaha.jrouter.api.RouterComponents.defaultFactory
import java.util.*

/**
 * 单例缓存
 */
object SingletonPool {
    private val CACHE: MutableMap<Class<*>, Any> =
        HashMap()

    @JvmStatic
    @Throws(Exception::class)
    @Suppress("UNCHECKED_CAST")
    operator fun <I, T : I?> get(clazz: Class<I>?, factory: IFactory?): T? {
        var factoryTemp = factory
        if (clazz == null) {
            return null
        }
        if (factoryTemp == null) {
            factoryTemp = defaultFactory
        }
        val instance = getInstance(clazz, factoryTemp)
        return instance as T?
    }

    @Throws(Exception::class)
    private fun getInstance(clazz: Class<*>, factory: IFactory?): Any? {
        var t = CACHE[clazz]
        return if (t != null) {
            t
        } else {
            synchronized(CACHE) {
                t = CACHE[clazz]
                if (t == null) {
                    t = factory!!.create(clazz)
                    if (t != null) {
                        CACHE[clazz] = t!!
                    }
                }
            }
            t
        }
    }
}