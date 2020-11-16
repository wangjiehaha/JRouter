package com.jj.haha.jrouter.api.service

/**
 * 默认的Factory，先尝试Provider，再尝试无参数构造
 */
class DefaultFactory private constructor() : IFactory {

    @Throws(Exception::class)
    override fun <T> create(clazz: Class<T>): T {
        val t = ProviderPool.create(clazz)
        return t ?: clazz.newInstance()
    }

    companion object {
        @JvmField
        val INSTANCE =
            DefaultFactory()
    }
}