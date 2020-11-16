package com.jj.haha.jrouter.api.service

/**
 * 从Class构造实例
 */
interface IFactory {
    @Throws(Exception::class)
    fun <T> create(clazz: Class<T>): T
}