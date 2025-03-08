package com.mjh.example.consumer.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.serializer.JdkSerializer;
import com.mjh.rpc.serializer.Serializer;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer =new JdkSerializer();
        //封装rpcRequest
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(UserService.class.getName());
        rpcRequest.setMethodName("getUser");
        rpcRequest.setParameterTypes(new Class[]{User.class});
        rpcRequest.setParams(new Object[]{user});
        //序列化、发请求、收回答、解析response
        try {
            byte[] rpcRequestBytes = serializer.serialize(rpcRequest);
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8090")
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
            return (User) data;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
