// IRemoteBridge.aidl
package com.jwhaha.jrouter.api;
import com.jwhaha.jrouter.api.bean.Event;

// Declare any non-default types here with import statements

interface IRemoteBridge {
    oneway void registerDispatcher(IBinder dispatcherBinder);
    oneway void unregisterRemoteService(String serviceCanonicalName);
    oneway void notify(in Event event);
}
