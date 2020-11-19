package com.jj.haha.jrouter.api;

import android.content.Context;
import android.util.Log;

import com.jj.haha.jrouter.annotation.Const;
import com.jj.haha.jrouter.annotation.RouterProvider;
import com.jj.haha.jrouter.annotation.ServiceImpl;
import com.jj.haha.jrouter.api.ipclib.IPCRouter;
import com.jj.haha.jrouter.api.service.IFactory;
import com.jj.haha.jrouter.api.service.ServiceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Router {

    private final static String TAG = Router.class.getSimpleName();
    private static Context appContext;
    private static AtomicBoolean initFlag = new AtomicBoolean(false);

    public static Context getAppContext(){
        return appContext;
    }

    /**
     * 此初始化方法的调用不是必须的。
     * 使用时会按需初始化；但也可以提前调用并初始化，使用时会等待初始化完成。
     * 本方法线程安全。
     */
    public static void lazyInit(Context context) {
        if (initFlag.get() || context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        IPCRouter.INSTANCE.init(context);
        ServiceLoader.lazyInit();
        initFlag.set(true);
    }

    /**
     * 根据接口获取 {@link ServiceLoader}
     */
    public static <T> ServiceLoader<T> loadService(Class<T> clazz) {
        return ServiceLoader.load(clazz);
    }

    /**
     * 创建 指定的clazz的默认实现类的实例，如果没有任何一个实现类指定了,
     * 则会判断 指定的clazz的实现类是否只有一个，如果只有一个则会使用该实现类构造
     * 如果发现有多个 指定的clazz的实现类，则会抛出异常
     *
     * @return 找不到或获取、构造失败，则返回null
     */
    public static <I, T extends I> I getService(Class<I> clazz) {
        final I service = ServiceLoader.load(clazz).get(ServiceImpl.DEFAULT_IMPL_KEY);
        if (service != null) {
            return service;
        }else {
            final List<I> services = getAllServices(clazz);
            if (services.size() == 1) {
                return services.get(0);
            } else if (services.size() > 1) {
                // todo 抛出异常
                Log.e(TAG, "more than one imp for " + clazz);
            }
        }
        return null;
    }

    /**
     * 创建指定key的实现类实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 找不到或获取、构造失败，则返回null
     */
    public static <I, T extends I> T getService(Class<I> clazz, String key) {
        return ServiceLoader.load(clazz).get(key);
    }

    /**
     * 创建指定key的实现类实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 找不到或获取、构造失败，则返回null
     */
    public static <I, T extends I> T getService(Class<I> clazz, String key, Context context) {
        return ServiceLoader.load(clazz).get(key, context);
    }

    /**
     * 创建指定key的实现类实例，使用指定的Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @param factory 用于从Class构造实例
     * @return 找不到或获取、构造失败，则返回null
     */
    public static <I, T extends I> T getService(Class<I> clazz, String key, IFactory factory) {
        return ServiceLoader.load(clazz).get(key, factory);
    }

    /**
     * 创建所有实现类的实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public static <I, T extends I> List<T> getAllServices(Class<I> clazz) {
        return ServiceLoader.load(clazz).getAll();
    }

    /**
     * 创建所有实现类的实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public static <I, T extends I> List<T> getAllServices(Class<I> clazz, Context context) {
        return ServiceLoader.load(clazz).getAll(context);
    }

    /**
     * 创建所有实现类的实例，使用指定Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public static <I, T extends I> List<T> getAllServices(Class<I> clazz, IFactory factory) {
        return ServiceLoader.load(clazz).getAll(factory);
    }

    /**
     * 根据key获取实现类的Class。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 找不到或获取失败，则返回null
     */
    public static <I, T extends I> Class<T> getServiceClass(Class<I> clazz, String key) {
        return ServiceLoader.load(clazz).getClass(key);
    }

    /**
     * 获取所有实现类的Class。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    public static <I, T extends I> List<Class<T>> getAllServiceClasses(Class<I> clazz) {
        return ServiceLoader.load(clazz).getAllClasses();
    }

    public static <T> void unRegisterService(Class<T> clazz, String key) {
        ServiceLoader.load(clazz).unRegisterService(key);
    }

    public static <T> void registerService(Class<T> clazz, String key) {
        ServiceLoader.load(clazz).registerService(key);
    }

    public List<String> listAllModules(Context context) {
        try {
            ArrayList<String> nameList = new ArrayList<>();
            String[] assetsList = context.getAssets().list("");
            if (assetsList == null) {
                return null;
            }
            for (String name : assetsList) {
                if (name.startsWith(Const.NAME + Const.SPLITTER)) {
                    String moduleName = RouterUtils.INSTANCE.transferModuleName(name);
                    nameList.add(moduleName);
                }
            }
            return nameList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
