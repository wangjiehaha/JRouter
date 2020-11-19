package com.jwhaha.jrouter.api.ipclib

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.jwhaha.jrouter.annotation.RouterService
import com.jwhaha.jrouter.api.ipclib.business.DispatcherBridge
import com.jwhaha.jrouter.api.ipclib.dispatcher.Dispatcher
import com.jwhaha.jrouter.api.Router
import com.jwhaha.jrouter.api.ipclib.dispatcher.DispatcherService
import com.jwhaha.jrouter.api.ipclib.utils.ServiceUtils

/**
 * 对于跨进程的服务可以通过[IPCRouter]注册到[Dispatcher]中去；
 * 这样其它可以通过[IPCRouter]获取到本进程的服务；
 * 注册必须在自身的进程中进行
 *
 * 对于本地的服务直接通过[RouterService]注解进行注册
 * 使用本地服务可以通[Router]获取即可
 *
 * 规范情况是：
 * 子项目分为 app 和 sdk；
 * 开发阶段提供 sdk 给其它项目使用，sdk 提供获取 app 本地服务的 key，通过[Router]和 key 获取本地服务的api；
 * 如果 app 存在跨进程服务，那么本地服务的api需要提供获取app跨进程的服务方法；
 * 其它项目就不需要知道该服务是否是跨进程服务，实现简单调用，通过[Router]就可以获取app所有对外暴露的功能
 *
 * 这样可以实现 app 完全隐藏自身的功能实现，实现解耦
 */
object IPCRouter {

    fun init(context: Context) {
        val intent = Intent(context, DispatcherService::class.java)
        ServiceUtils.startServiceSafely(context, intent)
    }

    /**
     * 注册远程服务，需要在自身（远程服务）的进程中注册
     */
    fun <T: IBinder> registerRemoteService(serviceClass: Class<*>, serviceImpl: T) {
        DispatcherBridge.registerStubService(serviceClass.canonicalName!!, serviceImpl)
    }

    /**
     * 反注册远程服务
     */
    fun unRegisterRemoteService(serviceClass: Class<*>) {
        DispatcherBridge.unregisterStubService(serviceClass.canonicalName!!)
    }

    /**
     * 获取远程服务；
     * 任意进程中都可以获取到
     */
    fun getRemoteService(serviceClass: Class<*>): IBinder? {
        return DispatcherBridge.getRemoteServiceBean(serviceClass.canonicalName!!)?.binder
    }
}