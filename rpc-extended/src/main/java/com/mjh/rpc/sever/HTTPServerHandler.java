package com.mjh.rpc.sever;

import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.registry.LocalRegistry;
import com.mjh.rpc.serializer.JdkSerializer;
import com.mjh.rpc.serializer.Serializer;
import com.mjh.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HTTPServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        System.out.println("Provider：接收到远程调用请求");
        //指定序列化器
        //final Serializer serializer =new JdkSerializer();
        // 工厂模式指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //记录日志
        System.out.println("Handle Request "+httpServerRequest.absoluteURI());
        //异步处理http请求
        httpServerRequest.bodyHandler(body->{
            byte[] bodyBytes = body.getBytes();
            RpcRequest rpcRequest =null;
            try {
                rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
            } catch (IOException e) {
                System.out.println("Error deserializing RPC request");
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest==null){
                rpcResponse = new RpcResponse();
                rpcResponse.setMessage("rpc request failed");
                try {
                    doResponse(httpServerRequest,rpcResponse,serializer);
                } catch (IOException e) {
                    System.out.println("Error serializing RPC response from server");
                    e.printStackTrace();
                }

            }
            //解析rpcRequest
            String serviceName = rpcRequest.getServiceName();
            String methodName = rpcRequest.getMethodName();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object[] params = rpcRequest.getParams();
            try {
                //通过反射调用服务
                Class<?> serviceClass = LocalRegistry.getClass(serviceName);
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object reasult = method.invoke(serviceClass.newInstance(), params);
                rpcResponse.setData(reasult);
                rpcResponse.setMessage("executed successfully");
                rpcResponse.setDataType(method.getReturnType());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                rpcResponse.setMessage("rpc request failed:method not found");
                rpcResponse.setException(e);


            }

            try {
                doResponse(httpServerRequest,rpcResponse,serializer);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });
    }
    void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse,Serializer serializer) throws IOException {
        //封装一个httpServerResponse
        HttpServerResponse httpServerResponse = httpServerRequest.response()
                .putHeader("Content-Type", "application/json; charset=utf-8");
        //将rpcResponse封装进去
        try {
            byte[] serialize = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }


    }
}
