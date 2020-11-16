package com.jj.haha.jrouter.api.service

import com.jj.haha.jrouter.annotation.RouterProvider
import com.jj.haha.jrouter.api.RouterUtils.isEmpty
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

/**
 * Provider缓存
 */
object ProviderPool {
    private val CACHE =
        HashMap<Class<*>, Method?>()
    private val NOT_FOUND =
        ProviderPool::class.java.declaredMethods[0]

    fun <T> create(clazz: Class<T>?): T? {
        if (clazz == null) {
            return null
        }
        val provider = getProvider(clazz)
        if (provider === NOT_FOUND) {
            return null
        } else {
            try {
                return provider!!.invoke(null) as T
            } catch (ignored: Exception) {
            }
        }
        return null
    }

    private fun <T> getProvider(clazz: Class<T>): Method? {
        var provider = CACHE[clazz]
        if (provider == null) {
            synchronized(CACHE) {
                provider = CACHE[clazz]
                if (provider == null) {
                    provider = findProvider(clazz)
                    CACHE[clazz] = provider
                }
            }
        }
        return provider
    }

    private fun findProvider(clazz: Class<*>): Method {
        for (method in clazz.declaredMethods) {
            if (method.getAnnotation(RouterProvider::class.java) != null) {
                return if (Modifier.isStatic(method.modifiers) && method.returnType == clazz &&
                    isEmpty(method.parameterTypes)
                ) {
                    method
                } else {
                    NOT_FOUND
                }
            }
        }
        return NOT_FOUND
    }
}