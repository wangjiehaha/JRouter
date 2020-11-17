package com.jj.haha.jrouter.api.lib.utils

import android.content.Context
import android.content.Intent
import com.jj.haha.jrouter.api.lib.utils.ProcessUtils.getProcessName

object StubServiceMatcher {
    /**
     * 服务进程的名称，如果是主进程就不用bind了!
     */
    fun matchIntent(
        context: Context,
        serverProcessName: String
    ): Intent? { //如果是对方是主进程，则不需要bind,因为不用担心主进程被杀掉
        if (context.packageName == serverProcessName) {
            return null
        }
        //如果对方跟当前进程是同一进程，也不需要进行bind
        val currentProName = getProcessName(context)
        if (null == currentProName || currentProName == serverProcessName) {
            return null
        }
        var resultProName = serverProcessName
        if (resultProName.startsWith(context.packageName)) {
            val index = resultProName.lastIndexOf(":")
            //要考虑到有些进程名称不包含":"
            if (index > 0) {
                resultProName = resultProName.substring(index)
            }
        }
        val targetObj = getTargetService(resultProName) ?: return null
        val targetServiceClass = targetObj as Class<*>
        return Intent(context, targetServiceClass)
    }

    /**
     * gradle插件会修改这个方法，插入类似如下代码:
     * Map hashMap = new HashMap();
     * hashMap.put(":guard", CommuStubService0.class);
     * hashMap.put(":banana", CommuStubService1.class);
     * hashMap.put("com.android.apple", CommuStubService2.class);
     * hashMap.put(":test4", CommuStubService3.class);
     * hashMap.put("com.android.test5", CommuStubService4.class);
     * hashMap.put(":apple", CommuStubService5.class);
     * hashMap.put(":tea", CommuStubService6.class);
     * hashMap.put("com.android.test6", CommuStubService7.class);
     * hashMap.put(":test3", CommuStubService8.class);
     * hashMap.put(":test2", CommuStubService9.class);
     * hashMap.put(":test1", CommuStubService10.class);
     * if(matchedServices.get($1)!=null)return matchedServices.get($1);
     * return null;
     */
    private fun getTargetService(proName: String): Any? {
        return null
    }
}