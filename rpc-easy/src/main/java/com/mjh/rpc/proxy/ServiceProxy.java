package com.mjh.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.serializer.JdkSerializer;
import com.mjh.rpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    private static final String PROVIDER_URL = "http://localhost:8090";
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Consumer：启用RPC框架，开始远程调用");
        //指定序列化器
        Serializer serializer =new JdkSerializer();
        //封装rpcRequest
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(UserService.class.getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParams(args);
        //序列化、发请求、收回答、解析response
        try {
            byte[] rpcRequestBytes = serializer.serialize(rpcRequest);
            HttpResponse httpResponse = HttpRequest.post(PROVIDER_URL)
                    .body(rpcRequestBytes)
                    .execute();
            byte[] response = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(response, RpcResponse.class);
            Object data = rpcResponse.getData();
            Class<?> dataType = rpcResponse.getDataType();
            String message = rpcResponse.getMessage();
            Exception exception = rpcResponse.getException();
            //TODO: 如果调用不成功，如何处理。容错处理
            if(data != null)
                return  data;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
