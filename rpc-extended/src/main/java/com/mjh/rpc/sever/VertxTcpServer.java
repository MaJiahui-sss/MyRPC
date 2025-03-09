package com.mjh.rpc.sever;


import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.protocol.ProtocolMessageUtil;
import com.mjh.rpc.protocol.ProtocolSerilizerEnum;
import com.mjh.rpc.protocol.ProtocolTypeEnum;
import com.mjh.rpc.protocol.RpcProtocol;
import com.mjh.rpc.registry.LocalRegistry;
import com.mjh.rpc.serializer.Serializer;
import com.mjh.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VertxTcpServer implements HttpSever {
    @Override
    public void start(int port) throws Exception {
        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();
        final NetSocket[] socket = new NetSocket[1];
        TCPBufferHandlerWrapper tcpBufferHandlerWrapper=new TCPBufferHandlerWrapper(new Handler<Buffer>() {
            @Override
            public void handle(Buffer resultBuffer) {
                //TODO: resultBuffer的处理逻辑
                RpcResponse rpcResponse = new RpcResponse();
                System.out.println("接受到消息"+new String(resultBuffer.getBytes()));
                RpcProtocol rpcProtocol = ProtocolMessageUtil.Decode(resultBuffer);
                if(rpcProtocol==null){
                    System.out.println("服务端解码失败");
                    rpcResponse.setMessage("消息解码失败");

                    doResponse(socket[0],rpcResponse);
                }
                RpcRequest rpcRequest = (RpcRequest) rpcProtocol.getData();
                if(rpcRequest==null){
                    System.out.println("从protocol中获取rpcRequest失败");

                    doResponse(socket[0],rpcResponse);
                }
                System.out.println("RpcRequest:"+rpcRequest.toString());
                //现在我已经拿到rpcRequest，可以进行后续逻辑
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
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    e.printStackTrace();
                    rpcResponse.setMessage("rpc request failed:method not found");
                    rpcResponse.setException(e);


                }

                doResponse(socket[0],rpcResponse);

            }
        });
        server.connectHandler(new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket netSocket) {
                socket[0] =netSocket;
                netSocket.handler(tcpBufferHandlerWrapper);
            }
        });
        server.listen(RpcApplication.getRpcConfig().getServerPort(), RpcApplication.getRpcConfig().getServerHost(), res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to bind!");
            }
        });
    }
    void doResponse(NetSocket netSocket, RpcResponse rpcResponse) {
        RpcProtocol.ProtocolHeader header = new RpcProtocol.ProtocolHeader();
        RpcProtocol<RpcResponse> rpcProtocol=new RpcProtocol<>();

        header.setMagicNum((byte)1);
        header.setSerialization(ProtocolSerilizerEnum.getSerilizerEnum( RpcApplication.getRpcConfig().getSerializer()).getSerilizerKey());
        header.setType(ProtocolTypeEnum.RESPONSE.getKey());
        rpcProtocol.setProtocolHeader(header);
        rpcProtocol.setData(rpcResponse);
        //封装成buffer
        Buffer buffer = ProtocolMessageUtil.Encode(rpcProtocol);
        netSocket.write(buffer);




    }
}
