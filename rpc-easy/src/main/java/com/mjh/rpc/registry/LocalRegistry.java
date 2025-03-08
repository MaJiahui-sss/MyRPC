package com.mjh.rpc.registry;

import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    /**
     * 本地服务注册中心
     */
    private static final ConcurrentHashMap<String,Class<?>> registry = new ConcurrentHashMap<String,Class<?>>();

    public static Class<?> getClass(String className) {
        return registry.get(className);
    }
    public static void registerClass(String className, Class<?> clazz) {
        registry.put(className, clazz);
    }
    public static void removeClass(String className) {
        registry.remove(className);
    }

}
