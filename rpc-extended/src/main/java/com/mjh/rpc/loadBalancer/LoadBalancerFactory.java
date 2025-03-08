package com.mjh.rpc.loadBalancer;

import com.mjh.rpc.spi.SpiLoader;

public class LoadBalancerFactory {
    static{
        SpiLoader.load(LoadBalancer.class);

    }
    private static final LoadBalancer DEFAULT_LOADBALANCER=new OrderlyLoadBalancer();

    public static LoadBalancer getInstance(String key){
        LoadBalancer instance =(LoadBalancer) SpiLoader.getInstance(LoadBalancer.class, key);
        if(instance==null){
            System.out.println("指定的负载均衡器不存在，已使用默认负载均衡器");
            return DEFAULT_LOADBALANCER;
        }
        System.out.println("当前的负载均衡器为"+key);
        return instance;
    }

}
