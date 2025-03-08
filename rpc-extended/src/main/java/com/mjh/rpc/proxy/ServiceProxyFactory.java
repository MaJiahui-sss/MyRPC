package com.mjh.rpc.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    /**
     * 动态代理工厂
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        System.out.println("通过工厂创建动态代理成功");
        return (T)Proxy.newProxyInstance(serviceClass.getClassLoader()
                ,new Class[]{serviceClass}
                ,new ServiceProxy_TCPClient());
    }
}
