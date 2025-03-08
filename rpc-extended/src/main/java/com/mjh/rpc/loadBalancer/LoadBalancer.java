package com.mjh.rpc.loadBalancer;

import com.mjh.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
    ServiceMetaInfo selectService(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
