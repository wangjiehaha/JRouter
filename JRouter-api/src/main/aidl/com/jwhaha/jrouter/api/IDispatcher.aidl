// IDispatcher.aidl
package com.jwhaha.jrouter.api;
import com.jwhaha.jrouter.api.bean.BinderBean;

// Declare any non-default types here with import statements

interface IDispatcher {

    BinderBean getTargetBinder(String serviceCanonicalName);

    void registerRemoteBridge(int pid, IBinder binder);

    void registerRemoteService(String serviceCanonicalName, String processName, IBinder binder);

    void unregisterRemoteService(String serviceCanonicalName);
}
