package com.jj.haha.jrouter.api.service;

import android.content.Context;

import com.jj.haha.jrouter.annotation.Const;
import com.jj.haha.jrouter.annotation.RouterProvider;
import com.jj.haha.jrouter.annotation.ServiceImpl;
import com.jj.haha.jrouter.api.LazyInitHelper;
import com.jj.haha.jrouter.api.RouterComponents;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通过接口Class获取实现类
 *
 * @param <I> 接口类型
 */
public class ServiceLoader<I> {

    private static final Map<Class, ServiceLoader> SERVICES = new HashMap<>();
    private HashMap<String, ServiceImpl> mMap = new HashMap<>();
    private final CopyOnWriteArrayList<String> unRegisterService = new CopyOnWriteArrayList<>();
    private final String mInterfaceName;

    private static final LazyInitHelper sInitHelper = new LazyInitHelper("ServiceLoader") {
        @Override
        protected void doInit() {
            try {
                // 反射调用Init类，避免引用的类过多，导致main dex capacity exceeded问题
                Class.forName(Const.SERVICE_LOADER_INIT)
                        .getMethod(Const.INIT_METHOD)
                        .invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public static void lazyInit() {
        sInitHelper.lazyInit();
    }

    /**
     * 提供给InitClass使用的初始化接口
     *
     * @param interfaceClass 接口类
     * @param implementClass 实现类
     */
    public static void put(Class interfaceClass, String key, Class implementClass, boolean singleton, boolean register) {
        ServiceLoader loader = SERVICES.get(interfaceClass);
        if (loader == null) {
            loader = new ServiceLoader(interfaceClass);
            SERVICES.put(interfaceClass, loader);
        }
        loader.putImpl(key, implementClass, singleton);
        if (!register) {
            loader.unRegisterService(key);
        }
    }

    /**
     * 根据接口获取 {@link ServiceLoader}
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceLoader<T> load(Class<T> interfaceClass) {
        sInitHelper.ensureInit();
        if (interfaceClass == null) {
            return EmptyServiceLoader.INSTANCE;
        }
        ServiceLoader service = SERVICES.get(interfaceClass);
        if (service == null) {
            synchronized (SERVICES) {
                service = SERVICES.get(interfaceClass);
                if (service == null) {
                    service = new ServiceLoader(interfaceClass);
                    SERVICES.put(interfaceClass, service);
                }
            }
        }
        return service;
    }

    private ServiceLoader(Class interfaceClass) {
        if (interfaceClass == null) {
            mInterfaceName = "";
        } else {
            mInterfaceName = interfaceClass.getName();
        }
    }

    private void putImpl(String key, Class implementClass, boolean singleton) {
        if (key != null && implementClass != null) {
            mMap.put(key, new ServiceImpl(key, implementClass, singleton));
        }
    }

    public void unRegisterService(String key) {
        synchronized (unRegisterService) {
            unRegisterService.add(key);
        }
    }

    public void registerService(String key) {
        synchronized (unRegisterService) {
            unRegisterService.remove(key);
        }
    }

    /**
     * 创建指定key的实现类实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key) {
        if (unRegisterService.contains(key)) {
            return null;
        }
        return createInstance(mMap.get(key), null);
    }

    /**
     * 创建指定key的实现类实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, Context context) {
        if (unRegisterService.contains(key)) {
            return null;
        }
        return createInstance(mMap.get(key), new ContextFactory(context));
    }

    /**
     * 创建指定key的实现类实例，使用指定的Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, IFactory factory) {
        if (unRegisterService.contains(key)) {
            return null;
        }
        return createInstance(mMap.get(key), factory);
    }

    /**
     * 创建所有实现类的实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public <T extends I> List<T> getAll() {
        return getAll((IFactory) null);
    }

    /**
     * 创建所有实现类的实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public <T extends I> List<T> getAll(Context context) {
        return getAll(new ContextFactory(context));
    }

    /**
     * 创建所有实现类的实例，使用指定Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public <T extends I> List<T> getAll(IFactory factory) {
        Collection<ServiceImpl> services = new ArrayList<>();
        for (Map.Entry<String, ServiceImpl> entry : mMap.entrySet()) {
            if (unRegisterService.contains(entry.getKey())) {
                continue;
            }
            services.add(entry.getValue());
        }
        if (services.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(services.size());
        for (ServiceImpl impl : services) {
            T instance = createInstance(impl, factory);
            if (instance != null) {
                list.add(instance);
            }
        }
        return list;
    }

    /**
     * 获取指定key的实现类。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回null
     */
    @SuppressWarnings("unchecked")
    public <T extends I> Class<T> getClass(String key) {
        if (unRegisterService.contains(key)) {
            return null;
        }
        ServiceImpl impl = mMap.get(key);
        if (impl != null) {
            return (Class<T>) impl.getImplementationClazz();
        }
        return null;
    }

    /**
     * 获取所有实现类的Class。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @SuppressWarnings("unchecked")
    public <T extends I> List<Class<T>> getAllClasses() {
        List<Class<T>> list = new ArrayList<>(mMap.size());
        for (Map.Entry<String, ServiceImpl> entry : mMap.entrySet()) {
            if (unRegisterService.contains(entry.getKey())) {
                continue;
            }
            Class<T> clazz = (Class<T>) entry.getValue().getImplementationClazz();
            if (clazz != null) {
                list.add(clazz);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T extends I> T createInstance(ServiceImpl impl, IFactory factory) {
        if (impl == null) {
            return null;
        }
        Class<T> clazz = (Class<T>) impl.getImplementationClazz();
        if (impl.isSingleton()) {
            try {
                return SingletonPool.get(clazz, factory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (factory == null) {
                    factory = RouterComponents.INSTANCE.getDefaultFactory();
                }
                if (factory != null) {
                    return factory.create(clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return "ServiceLoader (" + mInterfaceName + ")";
    }

    public static class EmptyServiceLoader extends ServiceLoader {

        static final ServiceLoader INSTANCE = new EmptyServiceLoader();

        EmptyServiceLoader() {
            super(null);
        }

        @Override
        public List<Class> getAllClasses() {
            return Collections.emptyList();
        }

        @Override
        public List getAll() {
            return Collections.emptyList();
        }

        @Override
        public List getAll(IFactory factory) {
            return Collections.emptyList();
        }

        @NotNull
        @Override
        public String toString() {
            return "EmptyServiceLoader";
        }
    }
}
