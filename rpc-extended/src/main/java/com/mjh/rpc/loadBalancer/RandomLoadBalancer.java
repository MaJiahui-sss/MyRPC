package com.mjh.rpc.loadBalancer;

import com.mjh.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private final Random random=new Random();
    @Override
    public ServiceMetaInfo selectService(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList == null || serviceMetaInfoList.size() == 0)return null;
        if(serviceMetaInfoList.size() == 1)return serviceMetaInfoList.get(0);
        int size=serviceMetaInfoList.size();
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
