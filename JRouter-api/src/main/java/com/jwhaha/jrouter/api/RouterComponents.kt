package com.jwhaha.jrouter.api

import com.jwhaha.jrouter.api.service.DefaultFactory
import com.jwhaha.jrouter.api.service.IFactory


/**
 * 用于配置组件
 *
 */
object RouterComponents {
    private var sDefaultFactory: IFactory = DefaultFactory.INSTANCE

    var defaultFactory: IFactory?
        get() = sDefaultFactory
        set(factory) {
            sDefaultFactory =
                factory ?: DefaultFactory.INSTANCE
        }
}