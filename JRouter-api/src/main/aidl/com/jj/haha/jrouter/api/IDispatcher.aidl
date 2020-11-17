// IDispatcher.aidl
package com.jj.haha.jrouter.api;
import com.jj.haha.jrouter.api.bean.BinderBean;

// Declare any non-default types here with import statements

interface IDispatcher {

    BinderBean getTargetBinder(String serviceCanonicalName);

    void registerRemoteService(String serviceCanonicalName, String processName, IBinder binder);

    void unregisterRemoteService(String serviceCanonicalName);
}
