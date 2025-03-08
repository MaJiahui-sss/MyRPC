package com.mjh.rpc.fault.tolerant;

import com.mjh.rpc.spi.SpiLoader;


import java.util.Map;

public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }
    public static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailSafeTolerantStrategy();

    public static TolerantStrategy getInstance(String key) {
        TolerantStrategy tolerantStrategy = SpiLoader.getInstance(TolerantStrategy.class,key);
        if(tolerantStrategy == null){
            System.out.println("加载指定容错策略失败，已使用默认容错策略");
            return DEFAULT_TOLERANT_STRATEGY;
        }
        System.out.println("加载指定容错策略成功");
        return tolerantStrategy;
    }
}
