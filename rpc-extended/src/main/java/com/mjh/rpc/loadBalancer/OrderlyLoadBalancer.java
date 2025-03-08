package com.mjh.rpc.loadBalancer;

import com.mjh.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderlyLoadBalancer implements LoadBalancer {
    private AtomicInteger index = new AtomicInteger(0);
    @Override
    public ServiceMetaInfo selectService(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList == null || serviceMetaInfoList.size() == 0)return null;
        if(serviceMetaInfoList.size() == 1)return serviceMetaInfoList.get(0);
        int serviceIndex = this.index.getAndIncrement();
        return serviceMetaInfoList.get(serviceIndex%serviceMetaInfoList.size());

    }
}
