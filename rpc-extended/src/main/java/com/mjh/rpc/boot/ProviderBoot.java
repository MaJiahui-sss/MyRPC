package com.mjh.rpc.boot;

import com.mjh.common.example.service.UserService;
import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.config.RpcConfig;
import com.mjh.rpc.model.ServiceMetaInfo;
import com.mjh.rpc.model.ServiceRegisteInfo;
import com.mjh.rpc.registry.LocalRegistry;
import com.mjh.rpc.registry.Registry;
import com.mjh.rpc.registry.RegistryFactory;
import com.mjh.rpc.sever.VertxTcpServer;

import java.util.List;

import static com.mjh.rpc.constant.RpcConstant.DEFAULT_SERVICE_VERSION;

public class ProviderBoot {
    public static void init(List<ServiceRegisteInfo> serviceRegisteInfoList){
        //读取Rpc配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println(rpcConfig);
        for(ServiceRegisteInfo serviceRegisteInfo : serviceRegisteInfoList){
            //本地注册服务 服务名和实现类的映射
            LocalRegistry.registerClass(serviceRegisteInfo.getServiceName(),serviceRegisteInfo.getServiceClass());
            //注册中心住粗 服务名和服务提供地址的映射
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceRegisteInfo.getServiceName());
            serviceMetaInfo.setServiceVersion(DEFAULT_SERVICE_VERSION);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                System.out.println(serviceRegisteInfo.getServiceName()+"服务注册失败");
            }
        }


//
        VertxTcpServer vertxTCPServer =new VertxTcpServer();

        try {
            vertxTCPServer.start(rpcConfig.getServerPort());
        } catch (Exception e) {
            System.out.println( "服务器启动失败");
            throw new RuntimeException(e);
        }


    }
}
