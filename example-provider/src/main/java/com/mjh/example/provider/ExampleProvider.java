package com.mjh.example.provider;



import com.mjh.common.example.service.UserService;
import com.mjh.example.provider.service.impl.UserServiceImpl;
import com.mjh.rpc.boot.ProviderBoot;
import com.mjh.rpc.model.ServiceRegisteInfo;

import java.util.ArrayList;
import java.util.List;


public class ExampleProvider {
    public static void main(String[] args) throws Exception {

        List<ServiceRegisteInfo> serviceRegisteInfoList = new ArrayList<>();
        ServiceRegisteInfo serviceRegisteInfo = new ServiceRegisteInfo();
        serviceRegisteInfo.setServiceName(UserService.class.getName());
        serviceRegisteInfo.setServiceClass(UserServiceImpl.class);
        serviceRegisteInfoList.add(serviceRegisteInfo);
        ProviderBoot.init(serviceRegisteInfoList);
    }
}
