package com.jwhaha.jrouter.api

/**
 * 初始化辅助工具类。
 *
 * 一些初始化任务可以在使用时按需初始化(通常在主线程)；
 * 但也可以提前调用并初始化(通常在后台线程)，使用时等待初始化完成。
 */
abstract class LazyInitHelper(private val mTag: String) {
    private var mHasInit = false
    /**
     * 此初始化方法的调用不是必须的。
     * 使用时会按需初始化；但也可以提前调用并初始化，使用时会等待初始化完成。
     * 本方法线程安全。
     */
    fun lazyInit() {
        performInit()
    }

    /**
     * 使用时确保已经初始化；如果正在初始化，则等待完成。
     */
    fun ensureInit() {
        performInit()
    }

    private fun performInit() {
        if (!mHasInit) {
            synchronized(this) {
                if (!mHasInit) {
                    try {
                        doInit()
                        mHasInit = true
                    } catch (ignored: Throwable) {
                    }
                }
            }
        }
    }

    protected abstract fun doInit()

}