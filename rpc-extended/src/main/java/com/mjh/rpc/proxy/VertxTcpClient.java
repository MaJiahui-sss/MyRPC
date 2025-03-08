package com.mjh.rpc.proxy;

import com.mjh.rpc.RpcApplication;
import com.mjh.rpc.model.RpcRequest;
import com.mjh.rpc.model.RpcResponse;
import com.mjh.rpc.model.ServiceMetaInfo;
import com.mjh.rpc.protocol.ProtocolMessageUtil;
import com.mjh.rpc.protocol.ProtocolSerilizerEnum;
import com.mjh.rpc.protocol.ProtocolTypeEnum;
import com.mjh.rpc.protocol.RpcProtocol;
import com.mjh.rpc.sever.TCPBufferHandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    public static RpcResponse doRequest(ServiceMetaInfo selectedServiceMetaInfo,RpcRequest rpcRequest) throws ExecutionException, InterruptedException {
        CompletableFuture<RpcResponse> future=new CompletableFuture<>();
        //创建tcp客户端，发送rpcRequest，接受rpcResponse
        Vertx vertx = Vertx.vertx();
        NetClientOptions options = new NetClientOptions();
        NetClient client = vertx.createNetClient(options);
        RpcResponse rpcResponse;
        try {
            client.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(), res -> {
                if (res.succeeded()) {
                    System.out.println("—————————————————Consumer已经成功连接到Provider———————————————————————-");
                    NetSocket socket = res.result();
                    //封装protocol协议
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    RpcProtocol.ProtocolHeader rpcProtocolHeader = new RpcProtocol.ProtocolHeader();
                    rpcProtocolHeader.setMagicNum((byte) 1);
                    rpcProtocolHeader.setSerialization(ProtocolSerilizerEnum.getSerilizerEnum(RpcApplication.getRpcConfig().getSerializer()).getSerilizerKey());
                    rpcProtocolHeader.setType(ProtocolTypeEnum.REQUEST.getKey());
                    protocol.setProtocolHeader(rpcProtocolHeader);
                    protocol.setData(rpcRequest);
                    //封装成buffer
                    Buffer sendbuffer = ProtocolMessageUtil.Encode(protocol);
                    socket.write(sendbuffer);


                    TCPBufferHandlerWrapper tcpBufferHandlerWrapper=new TCPBufferHandlerWrapper(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer resultBuffer) {
                            RpcProtocol<RpcResponse> rpcProtocol = ProtocolMessageUtil.Decode(resultBuffer);
                            if (rpcProtocol == null) {
                                System.out.println("消费者收到的协议解析失败");
                                future.complete(null);
                                throw new RuntimeException("消费者收到的协议解析失败");
                            }
                            RpcResponse rpcResponse = rpcProtocol.getData();
                            future.complete(rpcResponse);

                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                    // 处理连接关闭事件
                    socket.closeHandler(v -> {

                        System.out.println("Connection closed");
                    });
                } else {
                    System.out.println("Failed to connect: " + res.cause().getMessage());
                }
            });
            System.out.println("正在堵塞——————————————————————————————");
            rpcResponse = future.get();

            System.out.println("阻塞完成——————————————————————————————————");

        }
        finally{
            client.close();
            vertx.close();
        }
        return rpcResponse;
    }

}
