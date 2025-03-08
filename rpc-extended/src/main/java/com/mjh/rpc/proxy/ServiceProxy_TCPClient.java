package com.mjh.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mjh.common.example.service.UserService;
import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.config.RpcConfig;
import com.mjh.rpc.constant.RpcConstant;
import com.mjh.rpc.fault.retry.FixedIntervalRetryStrategy;
import com.mjh.rpc.fault.retry.RetryStrategy;
import com.mjh.rpc.fault.tolerant.TolerantStrategy;
import com.mjh.rpc.fault.tolerant.TolerantStrategyFactory;
import com.mjh.rpc.loadBalancer.*;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.model.ServiceMetaInfo;
import com.mjh.rpc.registry.*;
import com.mjh.rpc.serializer.Serializer;
import com.mjh.rpc.serializer.SerializerFactory;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.config.RpcConfig;
import com.mjh.rpc.constant.ProtocolConstant;
import com.mjh.rpc.constant.RpcConstant;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.model.ServiceMetaInfo;
import com.mjh.rpc.protocol.ProtocolMessageUtil;
import com.mjh.rpc.protocol.ProtocolSerilizerEnum;
import com.mjh.rpc.protocol.ProtocolTypeEnum;
import com.mjh.rpc.protocol.RpcProtocol;
import com.mjh.rpc.registry.Registry;
import com.mjh.rpc.registry.RegistryFactory;
import com.mjh.rpc.serializer.JdkSerializer;
import com.mjh.rpc.serializer.Serializer;
import com.mjh.rpc.serializer.SerializerFactory;
import com.mjh.rpc.sever.TCPBufferHandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

import javax.swing.plaf.basic.BasicTextAreaUI;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy_TCPClient implements InvocationHandler {

    private static  String PROVIDER_URL = "http://localhost:8060";
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("Consumer：启用RPC框架，开始远程调用");
        //指定序列化器
        Serializer serializer = SerializerFactory.getInstance(rpcConfig.getSerializer());
        //封装rpcRequest
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(UserService.class.getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParams(args);
        //获取服务的地址
        // 从注册中心获取服务提供者请求地址

        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
//        //  TODO：不同的负载均衡器
        LoadBalancer loadBalancer= LoadBalancerFactory.getInstance(RpcApplication.getRpcConfig().getLoadBalancer());
        HashMap<String,Object> requsetParam=new HashMap<>();
        requsetParam.put("methodName",rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.selectService(requsetParam,serviceMetaInfoList);
        PROVIDER_URL=selectedServiceMetaInfo.getServiceAddress();
        //创建一个completableFuture对象


        //建立tcp连接，发消息，收消息
        RpcResponse rpcResponse=null;
        try{
            RetryStrategy retryStrategy=new FixedIntervalRetryStrategy();
            rpcResponse = retryStrategy.doRetry(new Callable<RpcResponse>() {
                @Override
                public RpcResponse call() throws Exception {
                    return VertxTcpClient.doRequest(selectedServiceMetaInfo, rpcRequest);

                }
            });

        }catch (Exception e){
            //使用容错策略
            TolerantStrategy tolerantStrategy= TolerantStrategyFactory.getInstance(RpcApplication.getRpcConfig().getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }


        Object data = rpcResponse.getData();
        Class<?> dataType = rpcResponse.getDataType();
        String message = rpcResponse.getMessage();
        Exception exception = rpcResponse.getException();

        //TODO: 如果调用不成功，如何处理。容错处理
        if(data != null)
            return  data;



        return null;
    }
}
