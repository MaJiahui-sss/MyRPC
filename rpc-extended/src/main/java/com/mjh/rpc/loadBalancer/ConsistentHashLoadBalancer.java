package com.mjh.rpc.loadBalancer;

import com.mjh.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer{
    private static final TreeMap<Integer,ServiceMetaInfo> virtualNodes=new TreeMap<Integer,ServiceMetaInfo>();
    private static final Integer NODES_MAX_NUMBER=100;
    @Override
    public ServiceMetaInfo selectService(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList==null || serviceMetaInfoList.size()==0)
            return null;
        if(serviceMetaInfoList.size()==1)return serviceMetaInfoList.get(0);

        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < NODES_MAX_NUMBER; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取调用请求的 hash 值
        int hash = getHash(requestParams);

        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // 如果没有大于等于调用请求 hash 值的虚拟节点，则返回环首部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();

    }

    private int getHash(Object key) {
        return key.hashCode();
    }
}
